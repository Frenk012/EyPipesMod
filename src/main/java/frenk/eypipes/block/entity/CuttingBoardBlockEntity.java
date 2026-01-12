package frenk.eypipes.block.entity;

import frenk.eypipes.registries.ModBlockEntities;
import frenk.eypipes.registries.ModDataComponents;
import frenk.eypipes.registries.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * Herb Cutting Table block entity - Stores a single herb item that can be cut.
 * Preserves fermentation properties when cutting dried herbs into cutted herbs.
 * The leaf is consumed after cutting.
 */
public class CuttingBoardBlockEntity extends BlockEntity {

    private ItemStack storedItem = ItemStack.EMPTY;

    public CuttingBoardBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CUTTING_BOARD.get(), pos, state);
    }

    public boolean isEmpty() {
        return storedItem.isEmpty();
    }

    public ItemStack getStoredItem() {
        return storedItem;
    }

    /**
     * Check if the given item can be placed on the cutting board (dried herbs only).
     */
    public static boolean canBeCut(ItemStack stack) {
        if (stack.isEmpty()) return false;
        Item item = stack.getItem();
        return item == ModItems.ERBAPIPA_DRIED.get() ||
               item == ModItems.VALERIANA_DRIED.get() ||
               item == ModItems.GINSENG_DRIED.get() ||
               item == ModItems.SALVIA_DRIED.get();
    }

    /**
     * Get the cutted result for a dried herb.
     */
    @Nullable
    public static ItemStack getCuttedResult(ItemStack input) {
        if (input.is(ModItems.ERBAPIPA_DRIED.get())) {
            return new ItemStack(ModItems.ERBAPIPA_CUTTED.get(), 4);
        }
        if (input.is(ModItems.VALERIANA_DRIED.get())) {
            return new ItemStack(ModItems.VALERIANA_CUTTED.get(), 4);
        }
        if (input.is(ModItems.GINSENG_DRIED.get())) {
            return new ItemStack(ModItems.GINSENG_CUTTED.get(), 4);
        }
        if (input.is(ModItems.SALVIA_DRIED.get())) {
            return new ItemStack(ModItems.SALVIA_CUTTED.get(), 4);
        }
        return null;
    }

    /**
     * Place an item on the cutting table.
     */
    public boolean placeItem(ItemStack stack) {
        if (!storedItem.isEmpty()) return false;
        if (!canBeCut(stack)) return false;

        storedItem = stack.copyWithCount(1);
        setChanged();
        syncToClient();
        return true;
    }

    /**
     * Remove the item from the cutting table.
     */
    public ItemStack removeItem() {
        ItemStack removed = storedItem.copy();
        storedItem = ItemStack.EMPTY;
        setChanged();
        syncToClient();
        return removed;
    }

    /**
     * Cut the stored item with a knife.
     * Returns the cutted result with preserved fermentation properties.
     * The leaf is consumed after cutting.
     */
    @Nullable
    public ItemStack cutItem() {
        if (storedItem.isEmpty()) return null;

        ItemStack result = getCuttedResult(storedItem);
        if (result == null) return null;

        // Copy fermentation properties from input to output
        Integer fermentationLevel = storedItem.get(ModDataComponents.FERMENTATION_LEVEL.get());
        if (fermentationLevel != null) {
            result.set(ModDataComponents.FERMENTATION_LEVEL.get(), fermentationLevel);
        }

        Long fermentationStart = storedItem.get(ModDataComponents.FERMENTATION_START.get());
        if (fermentationStart != null) {
            result.set(ModDataComponents.FERMENTATION_START.get(), fermentationStart);
        }

        // Consume the leaf after cutting
        storedItem = ItemStack.EMPTY;
        setChanged();
        syncToClient();

        return result;
    }

    private void syncToClient() {
        if (level != null) {
            BlockState state = getBlockState();
            level.sendBlockUpdated(worldPosition, state, state, Block.UPDATE_ALL);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (!storedItem.isEmpty()) {
            tag.put("StoredItem", storedItem.save(registries));
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("StoredItem")) {
            storedItem = ItemStack.parse(registries, tag.getCompound("StoredItem")).orElse(ItemStack.EMPTY);
        } else {
            storedItem = ItemStack.EMPTY;
        }
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag, registries);
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider registries) {
        loadAdditional(tag, registries);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt, HolderLookup.Provider registries) {
        CompoundTag tag = pkt.getTag();
        if (tag != null) {
            loadAdditional(tag, registries);
        } else {
            // Empty tag means empty item
            storedItem = ItemStack.EMPTY;
        }
    }
}
