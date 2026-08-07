package frenk.eypipes.block.entity;

import frenk.eypipes.compat.Nbt;
//? if >=1.21.6 {
/*import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
*///?}
import net.minecraft.world.level.Level;
import net.minecraft.world.Containers;
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

    /**
     * Drop everything stored here on the ground. Called when the block is removed:
     * from the block's onRemove hook before 1.21.5, and from preRemoveSideEffects after,
     * because affectNeighborsAfterRemoval runs once the block entity is already gone.
     */
    public void dropContents(Level level, BlockPos pos) {
        if (!storedItem.isEmpty()) {
            Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), storedItem);
        }
    }

    //? if >=1.21.5 {
    /*@Override
    public void preRemoveSideEffects(BlockPos pos, BlockState state) {
        if (this.level != null) {
            dropContents(this.level, pos);
        }
    }
    *///?}

    //? if <1.21.6 {
    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        Nbt.putStack(tag, "StoredItem", storedItem, registries);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        storedItem = Nbt.getStack(tag, "StoredItem", registries);
    }
    //?} else {
    /*@Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        Nbt.putStack(output, "StoredItem", storedItem);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        storedItem = Nbt.getStack(input, "StoredItem");
    }
    *///?}

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    //? if <1.21.6 {
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
    //?} else {
    /*@Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveCustomOnly(registries);
    }

    @Override
    public void handleUpdateTag(ValueInput input) {
        loadAdditional(input);
    }

    @Override
    public void onDataPacket(Connection net, ValueInput input) {
        // An absent key already reads back as empty, so the old null-tag branch is moot.
        loadAdditional(input);
    }
    *///?}
}
