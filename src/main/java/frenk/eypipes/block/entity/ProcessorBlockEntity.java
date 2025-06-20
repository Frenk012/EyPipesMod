package frenk.eypipes.block.entity;

import frenk.eypipes.PipesEntities;
import frenk.eypipes.screen.ProcessorScreenHandler;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class ProcessorBlockEntity extends BlockEntity implements NamedScreenHandlerFactory, SidedInventory {
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(9, ItemStack.EMPTY);
    private int processingTime = 0;
    private int maxProcessingTime = 200; // 10 seconds at 20 ticks per second

    public ProcessorBlockEntity(BlockPos pos, BlockState state) {
        super(PipesEntities.PROCESSOR_ENTITY, pos, state);
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable("block.eypipes.processor");
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new ProcessorScreenHandler(syncId, inv, this);
    }

    @Override
    public int size() {
        return inventory.size();
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack itemStack : inventory) {
            if (!itemStack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getStack(int slot) {
        return inventory.get(slot);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        ItemStack result = Inventories.splitStack(inventory, slot, amount);
        if (!result.isEmpty()) {
            markDirty();
        }
        return result;
    }

    @Override
    public ItemStack removeStack(int slot) {
        return Inventories.removeStack(inventory, slot);
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        inventory.set(slot, stack);
        if (stack.getCount() > getMaxCountPerStack()) {
            stack.setCount(getMaxCountPerStack());
        }
        markDirty();
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        if (this.world.getBlockEntity(this.pos) != this) {
            return false;
        } else {
            return player.squaredDistanceTo((double)this.pos.getX() + 0.5D, (double)this.pos.getY() + 0.5D, (double)this.pos.getZ() + 0.5D) <= 64.0D;
        }
    }

    @Override
    public void clear() {
        inventory.clear();
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        Inventories.readNbt(nbt, inventory);
        processingTime = nbt.getInt("ProcessingTime");
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        Inventories.writeNbt(nbt, inventory);
        nbt.putInt("ProcessingTime", processingTime);
    }

    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }

    public static void tick(World world, BlockPos pos, BlockState state, ProcessorBlockEntity entity) {
        if (world.isClient()) return;

        // Simple processing logic - can be expanded later
        if (!entity.getStack(0).isEmpty() && entity.getStack(8).isEmpty()) {
            entity.processingTime++;
            if (entity.processingTime >= entity.maxProcessingTime) {
                // Process item (placeholder logic)
                ItemStack input = entity.getStack(0);
                entity.setStack(8, input.copy());
                entity.removeStack(0, 1);
                entity.processingTime = 0;
                entity.markDirty();
            }
        } else {
            entity.processingTime = 0;
        }
    }

    public void dropInventory(World world, BlockPos pos) {
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.get(i);
            if (!stack.isEmpty()) {
                net.minecraft.entity.ItemEntity itemEntity = new net.minecraft.entity.ItemEntity(
                    world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, stack
                );
                world.spawnEntity(itemEntity);
            }
        }
    }

    // SidedInventory implementation
    @Override
    public int[] getAvailableSlots(Direction side) {
        // Input slots: 0-7, Output slot: 8
        if (side == Direction.DOWN) {
            return new int[]{8}; // Output only
        } else if (side == Direction.UP) {
            return new int[]{0, 1, 2, 3, 4, 5, 6, 7}; // Input only
        } else {
            return new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8}; // All slots
        }
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        return slot < 8; // Only input slots
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return slot == 8; // Only output slot
    }

    public int getProcessingTime() {
        return processingTime;
    }

    public int getMaxProcessingTime() {
        return maxProcessingTime;
    }
}