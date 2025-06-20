package frenk.eypipes;

import frenk.eypipes.item.EyPipesItems;
import frenk.eypipes.config.EyPipesConfig;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class DryingRackErbBlockEntity extends BlockEntity {

    private ItemStack[] items = new ItemStack[3];
    private int[] dryingTimes = new int[3];
    
    public DryingRackErbBlockEntity(BlockPos pos, BlockState state) {
        super(PipesEntities.DRYING_RACK_ENTITY, pos, state);
        for (int i = 0; i < 3; i++) {
            items[i] = ItemStack.EMPTY;
            dryingTimes[i] = 0;
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
    
    public boolean isSlotEmpty(int slot) {
        if (slot < 0 || slot >= 3) return true;
        return items[slot].isEmpty();
    }

    public ItemStack getItem(int slot) {
        if (slot < 0 || slot >= 3) return ItemStack.EMPTY;
        return items[slot];
    }
    
    public ItemStack[] getAllItems() {
        return items;
    }

    public boolean insertItem(ItemStack stack) {
        for (int i = 0; i < 3; i++) {
            if (items[i].isEmpty()) {
                items[i] = new ItemStack(stack.getItem(), 1);
                dryingTimes[i] = 0;
                markDirty();
                
                // Update the client about the change
                if (world != null) {
                    BlockState state = getCachedState();
                    world.updateListeners(pos, state, state, Block.NOTIFY_ALL);
                }
                return true;
            }
        }
        return false; // No empty slot found
    }

    public ItemStack removeItem(int slot) {
        if (slot < 0 || slot >= 3) return ItemStack.EMPTY;
        
        ItemStack out = items[slot].copy();
        items[slot] = ItemStack.EMPTY;
        dryingTimes[slot] = 0;
        markDirty();
        
        // Update the client about the change
        if (world != null) {
            BlockState state = getCachedState();
            world.updateListeners(pos, state, state, Block.NOTIFY_ALL);
        }
        
        return out;
    }
    
    public ItemStack removeFirstItem() {
        for (int i = 0; i < 3; i++) {
            if (!items[i].isEmpty()) {
                return removeItem(i);
            }
        }
        return ItemStack.EMPTY;
    }

    public static void tick(World world, BlockPos pos, BlockState state, DryingRackErbBlockEntity entity) {
        
        for (int i = 0; i < 3; i++) {
            if (!entity.items[i].isEmpty() && entity.items[i].getItem() == EyPipesItems.ERBAPIPA) {
                entity.dryingTimes[i]++;
                
                if (entity.dryingTimes[i] >= EyPipesConfig.DRYING_TIME_TICKS) {
                    entity.items[i] = new ItemStack(EyPipesItems.ERBAPIPA_DRIED);
                    entity.dryingTimes[i] = 0;
                    entity.markDirty();
                    world.updateListeners(pos, state, state, Block.NOTIFY_ALL);
                }
            }
        }
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        
        for (int i = 0; i < 3; i++) {
            nbt.put("Item" + i, items[i].writeNbt(new NbtCompound()));
            nbt.putInt("DryingTime" + i, dryingTimes[i]);
        }
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        
        for (int i = 0; i < 3; i++) {
            items[i] = ItemStack.fromNbt(nbt.getCompound("Item" + i));
            dryingTimes[i] = nbt.getInt("DryingTime" + i);
        }
    }
    
    // Client synchronization methods
    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }
    
    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }
}