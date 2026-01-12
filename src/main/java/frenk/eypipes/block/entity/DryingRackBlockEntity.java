package frenk.eypipes.block.entity;

import frenk.eypipes.config.EyPipesConfig;
import frenk.eypipes.registries.ModBlockEntities;
import frenk.eypipes.registries.ModItems;
import frenk.eypipes.registries.ModParticles;
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
 * OPTIMIZED: Uses time-based drying instead of tick-counting to reduce CPU usage.
 * Server checks only every 100 ticks instead of every tick.
 * Ported from Fabric 1.19.2 to NeoForge 1.21.1
 */
public class DryingRackBlockEntity extends BlockEntity {

    private static final int SLOT_COUNT = 3;

    // Optimization: Only check drying progress every N ticks
    private static final int SERVER_TICK_INTERVAL = 100; // Check every 5 seconds
    private static final int CLIENT_PARTICLE_INTERVAL = 40; // Particles every 2 seconds

    private final ItemStack[] items = new ItemStack[SLOT_COUNT];
    // OPTIMIZED: Store start time instead of incrementing counter every tick
    private final long[] dryingStartTimes = new long[SLOT_COUNT];

    public DryingRackBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.DRYING_RACK.get(), pos, state);
        for (int i = 0; i < SLOT_COUNT; i++) {
            items[i] = ItemStack.EMPTY;
            dryingStartTimes[i] = 0;
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

    /**
     * Get drying progress for a slot (0.0 to 1.0).
     * Used for rendering progress indicators.
     */
    public float getDryingProgress(int slot) {
        if (slot < 0 || slot >= SLOT_COUNT) return 0;
        if (items[slot].isEmpty() || dryingStartTimes[slot] == 0) return 0;
        if (level == null) return 0;

        long elapsed = level.getGameTime() - dryingStartTimes[slot];
        int dryingTime = EyPipesConfig.COMMON.dryingTimeTicks.get();
        return Math.min(1.0f, (float) elapsed / dryingTime);
    }

    public boolean insertItem(ItemStack stack) {
        for (int i = 0; i < SLOT_COUNT; i++) {
            if (items[i].isEmpty()) {
                items[i] = new ItemStack(stack.getItem(), 1);
                // OPTIMIZED: Store start time instead of 0
                dryingStartTimes[i] = level != null ? level.getGameTime() : 0;
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
        dryingStartTimes[slot] = 0;
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
     * OPTIMIZED: Server only checks every 100 ticks, client particles every 40 ticks.
     * Uses time-based calculation instead of incrementing counter every tick.
     */
    public static void tick(Level level, BlockPos pos, BlockState state, DryingRackBlockEntity entity) {
        long gameTime = level.getGameTime();

        // Client-side: spawn steam particles during drying
        if (level.isClientSide()) {
            // OPTIMIZED: Particles every 40 ticks (2 seconds) instead of 10
            if (gameTime % CLIENT_PARTICLE_INTERVAL == 0) {
                for (int i = 0; i < SLOT_COUNT; i++) {
                    if (!entity.items[i].isEmpty() && canBeDried(entity.items[i])) {
                        // Calculate position for each slot (spread across the rack)
                        double offsetX = (i - 1) * 0.25; // -0.25, 0, 0.25 for slots 0, 1, 2
                        double x = pos.getX() + 0.5 + offsetX;
                        double y = pos.getY() + 0.6;
                        double z = pos.getZ() + 0.5;

                        // Spawn steam particle rising upward
                        level.addParticle(ModParticles.STEAM.get(), x, y, z,
                                (level.random.nextDouble() - 0.5) * 0.02, // slight x drift
                                0.02 + level.random.nextDouble() * 0.01,   // upward velocity
                                (level.random.nextDouble() - 0.5) * 0.02); // slight z drift
                    }
                }
            }
            return;
        }

        // OPTIMIZED: Server-side drying check only every 100 ticks (5 seconds)
        // Drying takes thousands of ticks, so checking every 100 is plenty accurate
        if (gameTime % SERVER_TICK_INTERVAL != 0) {
            return;
        }

        boolean changed = false;
        int dryingTime = EyPipesConfig.COMMON.dryingTimeTicks.get();

        for (int i = 0; i < SLOT_COUNT; i++) {
            if (entity.items[i].isEmpty()) continue;

            ItemStack driedResult = getDriedResult(entity.items[i]);
            if (driedResult != null) {
                // OPTIMIZED: Calculate elapsed time instead of incrementing counter
                long startTime = entity.dryingStartTimes[i];
                if (startTime == 0) {
                    // Fix: set start time if it wasn't set (loaded from old save)
                    entity.dryingStartTimes[i] = gameTime;
                    continue;
                }

                long elapsedTime = gameTime - startTime;

                if (elapsedTime >= dryingTime) {
                    entity.items[i] = driedResult.copy();
                    entity.dryingStartTimes[i] = 0;
                    changed = true;
                }
            }
        }

        if (changed) {
            entity.setChanged();
            level.sendBlockUpdated(pos, state, state, Block.UPDATE_ALL);
        }
    }

    /**
     * Get the dried result for a fresh herb, or null if item cannot be dried.
     */
    @Nullable
    private static ItemStack getDriedResult(ItemStack input) {
        if (input.is(ModItems.ERBAPIPA.get())) {
            return new ItemStack(ModItems.ERBAPIPA_DRIED.get());
        }
        if (input.is(ModItems.VALERIANA.get())) {
            return new ItemStack(ModItems.VALERIANA_DRIED.get());
        }
        if (input.is(ModItems.GINSENG.get())) {
            return new ItemStack(ModItems.GINSENG_DRIED.get());
        }
        if (input.is(ModItems.SALVIA.get())) {
            return new ItemStack(ModItems.SALVIA_DRIED.get());
        }
        return null;
    }

    /**
     * Check if an item can be dried in the drying rack.
     */
    public static boolean canBeDried(ItemStack stack) {
        return getDriedResult(stack) != null;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);

        for (int i = 0; i < SLOT_COUNT; i++) {
            if (!items[i].isEmpty()) {
                tag.put("Item" + i, items[i].save(registries));
            }
            // Save start time instead of elapsed time
            tag.putLong("DryingStart" + i, dryingStartTimes[i]);
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

            // Load start time (handle migration from old format)
            if (tag.contains("DryingStart" + i)) {
                dryingStartTimes[i] = tag.getLong("DryingStart" + i);
            } else if (tag.contains("DryingTime" + i)) {
                // Migration: old format stored elapsed ticks, convert to start time
                // We'll set start time to 0 and let the tick method fix it
                dryingStartTimes[i] = 0;
            } else {
                dryingStartTimes[i] = 0;
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
}
