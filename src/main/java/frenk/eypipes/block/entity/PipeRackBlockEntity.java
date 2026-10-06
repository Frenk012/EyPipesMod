package frenk.eypipes.block.entity;

import frenk.eypipes.compat.Nbt;
//? if >=1.21.6 {
/*import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
*///?}
import net.minecraft.world.level.Level;
import net.minecraft.world.Containers;
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
        return ModItems.isPipe(stack);
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

    /**
     * Drop everything stored here on the ground. Called when the block is removed:
     * from the block's onRemove hook before 1.21.5, and from preRemoveSideEffects after,
     * because affectNeighborsAfterRemoval runs once the block entity is already gone.
     */
    public void dropContents(Level level, BlockPos pos) {
        for (ItemStack stack : items) {
            if (!stack.isEmpty()) {
                Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), stack);
            }
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
    //? if <1.20.5 {
    /*protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        HolderLookup.Provider registries = null;
    *///?} else {
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
    //?}

        for (int i = 0; i < SLOT_COUNT; i++) {
            Nbt.putStack(tag, "Pipe" + i, items[i], registries);
        }
    }

    @Override
    //? if <1.20.5 {
    /*public void load(CompoundTag tag) {
        super.load(tag);
        HolderLookup.Provider registries = null;
    *///?} else {
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
    //?}

        for (int i = 0; i < SLOT_COUNT; i++) {
            items[i] = Nbt.getStack(tag, "Pipe" + i, registries);
        }
    }
    //?} else {
    /*@Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        for (int i = 0; i < SLOT_COUNT; i++) {
            Nbt.putStack(output, "Pipe" + i, items[i]);
        }
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        for (int i = 0; i < SLOT_COUNT; i++) {
            items[i] = Nbt.getStack(input, "Pipe" + i);
        }
    }
    *///?}

    // Client synchronization methods
    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    //? if <1.21.6 {
    @Override
    //? if <1.20.5 {
    /*public CompoundTag getUpdateTag() {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag);
    *///?} else {
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag, registries);
        // Without (Neo)Forge's hooks, vanilla skips an empty update tag, so an emptied block
        // would keep showing its last item. The marker keeps the tag from ever being empty.
        //? if fabric
        //tag.putBoolean("eypipes_sync", true);
    //?}
        return tag;
    }

    // (Neo)Forge hooks. Fabric needs neither: vanilla already loads both through loadAdditional.
    //? if !fabric {
    @Override
    //? if <1.20.5 {
    /*public void handleUpdateTag(CompoundTag tag) {
        load(tag);
    *///?} else {
    public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider registries) {
        loadAdditional(tag, registries);
    //?}
    }

    @Override
    //? if <1.20.5 {
    /*public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        CompoundTag tag = pkt.getTag();
        if (tag != null) {
            load(tag);
    *///?} else {
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt, HolderLookup.Provider registries) {
        CompoundTag tag = pkt.getTag();
        if (tag != null) {
            loadAdditional(tag, registries);
    //?}
        }
    }
    //?}
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
