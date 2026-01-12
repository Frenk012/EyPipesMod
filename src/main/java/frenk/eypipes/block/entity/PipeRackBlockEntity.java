package frenk.eypipes.block.entity;

import frenk.eypipes.registries.ModBlockEntities;
import frenk.eypipes.registries.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * Pipe Rack block entity - Manages pipes displayed on the rack.
 * Stores up to 4 pipes which are rendered on the rack.
 */
public class PipeRackBlockEntity extends BlockEntity {

    private static final int SLOT_COUNT = 4;

    private final ItemStack[] items = new ItemStack[SLOT_COUNT];

    public PipeRackBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.PIPE_RACK.get(), pos, state);
        for (int i = 0; i < SLOT_COUNT; i++) {
            items[i] = ItemStack.EMPTY;
        }
    }

    public boolean isEmpty() {
        for (ItemStack item : items) {
            if (!item.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public int getItemCount() {
        int count = 0;
        for (ItemStack item : items) {
            if (!item.isEmpty()) {
                count++;
            }
        }
        return count;
    }

    public ItemStack getItem(int slot) {
        if (slot < 0 || slot >= SLOT_COUNT) return ItemStack.EMPTY;
        return items[slot];
    }

    public ItemStack[] getAllItems() {
        return items;
    }

    /**
     * Check if an item is a pipe that can be placed on the rack.
     */
    public static boolean isPipeItem(ItemStack stack) {
        return stack.is(ModItems.PIPE.get()) ||
               stack.is(ModItems.WOODEN_PIPE.get()) ||
               stack.is(ModItems.CLAY_PIPE.get()) ||
               stack.is(ModItems.CORN_COB_PIPE.get()) ||
               stack.is(ModItems.MEERSCHAUM_PIPE.get()) ||
               stack.is(ModItems.BRIAR_PIPE.get()) ||
               stack.is(ModItems.CHERRY_PIPE.get()) ||
               stack.is(ModItems.CALABASH_PIPE.get()) ||
               stack.is(ModItems.CHURCHWARD_PIPE.get()) ||
               stack.is(ModItems.BENT_PIPE.get());
    }

    public boolean insertItem(ItemStack stack) {
        for (int i = 0; i < SLOT_COUNT; i++) {
            if (items[i].isEmpty()) {
                // Store the entire item including damage/NBT
                items[i] = stack.copy();
                items[i].setCount(1);
                setChanged();

                // Update the client about the change
                if (level != null) {
                    BlockState state = getBlockState();
                    level.sendBlockUpdated(worldPosition, state, state, Block.UPDATE_ALL);
                }
                return true;
            }
        }
        return false; // No empty slot found
    }

    public ItemStack removeItem(int slot) {
        if (slot < 0 || slot >= SLOT_COUNT) return ItemStack.EMPTY;

        ItemStack out = items[slot].copy();
        items[slot] = ItemStack.EMPTY;
        setChanged();

        // Update the client about the change
        if (level != null) {
            BlockState state = getBlockState();
            level.sendBlockUpdated(worldPosition, state, state, Block.UPDATE_ALL);
        }

        return out;
    }

    public ItemStack removeFirstItem() {
        for (int i = 0; i < SLOT_COUNT; i++) {
            if (!items[i].isEmpty()) {
                return removeItem(i);
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);

        for (int i = 0; i < SLOT_COUNT; i++) {
            if (!items[i].isEmpty()) {
                tag.put("Pipe" + i, items[i].save(registries));
            }
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);

        for (int i = 0; i < SLOT_COUNT; i++) {
            if (tag.contains("Pipe" + i)) {
                items[i] = ItemStack.parse(registries, tag.getCompound("Pipe" + i)).orElse(ItemStack.EMPTY);
            } else {
                items[i] = ItemStack.EMPTY;
            }
        }
    }

    // Client synchronization methods
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
        }
    }
}
