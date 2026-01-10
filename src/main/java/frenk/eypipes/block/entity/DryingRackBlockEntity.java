package frenk.eypipes.block.entity;

import frenk.eypipes.config.EyPipesConfig;
import frenk.eypipes.registries.ModBlockEntities;
import frenk.eypipes.registries.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * Drying Rack block entity - Manages items being dried in the drying rack.
 * Converts erbapipa to erbapipa_dried over time.
 * Ported from Fabric 1.19.2 to NeoForge 1.21.1
 */
public class DryingRackBlockEntity extends BlockEntity {

    private static final int SLOT_COUNT = 3;

    private final ItemStack[] items = new ItemStack[SLOT_COUNT];
    private final int[] dryingTimes = new int[SLOT_COUNT];

    public DryingRackBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.DRYING_RACK.get(), pos, state);
        for (int i = 0; i < SLOT_COUNT; i++) {
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
        if (slot < 0 || slot >= SLOT_COUNT) return true;
        return items[slot].isEmpty();
    }

    public ItemStack getItem(int slot) {
        if (slot < 0 || slot >= SLOT_COUNT) return ItemStack.EMPTY;
        return items[slot];
    }

    public ItemStack[] getAllItems() {
        return items;
    }

    public int getDryingTime(int slot) {
        if (slot < 0 || slot >= SLOT_COUNT) return 0;
        return dryingTimes[slot];
    }

    public boolean insertItem(ItemStack stack) {
        for (int i = 0; i < SLOT_COUNT; i++) {
            if (items[i].isEmpty()) {
                items[i] = new ItemStack(stack.getItem(), 1);
                dryingTimes[i] = 0;
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
        dryingTimes[slot] = 0;
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
     * Static ticker method called every game tick.
     * Processes drying of erbapipa into erbapipa_dried.
     */
    public static void tick(Level level, BlockPos pos, BlockState state, DryingRackBlockEntity entity) {
        if (level.isClientSide()) return;

        boolean changed = false;
        int dryingTime = EyPipesConfig.COMMON.dryingTimeTicks.get();

        for (int i = 0; i < SLOT_COUNT; i++) {
            if (!entity.items[i].isEmpty() && entity.items[i].is(ModItems.ERBAPIPA.get())) {
                entity.dryingTimes[i]++;

                if (entity.dryingTimes[i] >= dryingTime) {
                    entity.items[i] = new ItemStack(ModItems.ERBAPIPA_DRIED.get());
                    entity.dryingTimes[i] = 0;
                    changed = true;
                }
            }
        }

        if (changed) {
            entity.setChanged();
            level.sendBlockUpdated(pos, state, state, Block.UPDATE_ALL);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);

        for (int i = 0; i < SLOT_COUNT; i++) {
            if (!items[i].isEmpty()) {
                tag.put("Item" + i, items[i].save(registries));
            }
            tag.putInt("DryingTime" + i, dryingTimes[i]);
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);

        for (int i = 0; i < SLOT_COUNT; i++) {
            if (tag.contains("Item" + i)) {
                items[i] = ItemStack.parse(registries, tag.getCompound("Item" + i)).orElse(ItemStack.EMPTY);
            } else {
                items[i] = ItemStack.EMPTY;
            }
            dryingTimes[i] = tag.getInt("DryingTime" + i);
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
}
