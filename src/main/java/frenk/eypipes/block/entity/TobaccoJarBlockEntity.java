package frenk.eypipes.block.entity;

import frenk.eypipes.compat.Nbt;
//? if >=1.21.6 {
/*import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
*///?}
import net.minecraft.world.Containers;
import frenk.eypipes.registries.ModBlockEntities;
import frenk.eypipes.registries.ModDataComponents;
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
 * Tobacco Jar block entity - Manages herb fermentation.
 * Stores up to 6 items and ferments them over time.
 *
 * Fermentation timeline:
 * - 1 MC day (24000 ticks) = Aged quality
 * - 3 MC days (72000 ticks) = Fermented quality
 */
public class TobaccoJarBlockEntity extends BlockEntity {

    private static final int SLOT_COUNT = 6;

    // Fermentation time in ticks (1 MC day = 24000 ticks)
    private static final long AGED_TIME = 24000L;
    private static final long FERMENTED_TIME = 72000L; // 3 MC days

    private final ItemStack[] items = new ItemStack[SLOT_COUNT];
    private final long[] fermentationStartTimes = new long[SLOT_COUNT];

    public TobaccoJarBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.TOBACCO_JAR.get(), pos, state);
        for (int i = 0; i < SLOT_COUNT; i++) {
            items[i] = ItemStack.EMPTY;
            fermentationStartTimes[i] = 0;
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
     * Check if an item can be fermented in the tobacco jar.
     * Accepts dried and cutted herbs.
     */
    public static boolean canFerment(ItemStack stack) {
        return stack.is(ModItems.ERBAPIPA_DRIED.get()) ||
               stack.is(ModItems.ERBAPIPA_CUTTED.get()) ||
               stack.is(ModItems.VALERIANA_DRIED.get()) ||
               stack.is(ModItems.VALERIANA_CUTTED.get()) ||
               stack.is(ModItems.GINSENG_DRIED.get()) ||
               stack.is(ModItems.GINSENG_CUTTED.get()) ||
               stack.is(ModItems.SALVIA_DRIED.get()) ||
               stack.is(ModItems.SALVIA_CUTTED.get());
    }

    public boolean insertItem(ItemStack stack) {
        for (int i = 0; i < SLOT_COUNT; i++) {
            if (items[i].isEmpty()) {
                // Copy the stack to preserve all data components
                ItemStack inserted = stack.copyWithCount(1);

                // If no fermentation level exists, set it based on item type
                if (!ModDataComponents.FERMENTATION_LEVEL.has(inserted)) {
                    // Dried items start at DRIED quality, cutted at FRESH
                    int quality = isDriedHerb(stack) ? ModDataComponents.QUALITY_DRIED : ModDataComponents.QUALITY_FRESH;
                    ModDataComponents.FERMENTATION_LEVEL.set(inserted, quality);
                }

                items[i] = inserted;
                fermentationStartTimes[i] = level != null ? level.getGameTime() : 0;
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

    private static boolean isDriedHerb(ItemStack stack) {
        return stack.is(ModItems.ERBAPIPA_DRIED.get()) ||
               stack.is(ModItems.VALERIANA_DRIED.get()) ||
               stack.is(ModItems.GINSENG_DRIED.get()) ||
               stack.is(ModItems.SALVIA_DRIED.get());
    }

    public ItemStack removeItem(int slot) {
        if (slot < 0 || slot >= SLOT_COUNT) return ItemStack.EMPTY;

        ItemStack out = items[slot].copy();
        items[slot] = ItemStack.EMPTY;
        fermentationStartTimes[slot] = 0;
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
     * Get the average fermentation level of all items in the jar.
     * Returns -1 if empty.
     */
    public int getAverageFermentationLevel() {
        int total = 0;
        int count = 0;
        for (ItemStack item : items) {
            if (!item.isEmpty()) {
                Integer level = ModDataComponents.FERMENTATION_LEVEL.get(item);
                total += (level != null) ? level : ModDataComponents.QUALITY_DRIED;
                count++;
            }
        }
        return count > 0 ? total / count : -1;
    }

    // Optimization: Only check fermentation every N ticks (server) and particles every M ticks (client)
    private static final int SERVER_TICK_INTERVAL = 200; // Check every 10 seconds
    private static final int CLIENT_PARTICLE_INTERVAL = 60; // Particles every 3 seconds

    /**
     * Static ticker method called every game tick.
     * OPTIMIZED: Server checks only every 200 ticks, client particles every 60 ticks.
     */
    public static void tick(Level level, BlockPos pos, BlockState state, TobaccoJarBlockEntity entity) {
        long gameTime = level.getGameTime();

        // Client-side: spawn ambient particles colored by fermentation state
        if (level.isClientSide()) {
            int fillLevel = entity.getItemCount();
            // OPTIMIZED: Particles every 60 ticks (3 seconds) instead of 20
            if (fillLevel > 0 && gameTime % CLIENT_PARTICLE_INTERVAL == 0) {
                int avgFermentation = entity.getAverageFermentationLevel();
                int particleCount = Math.min(fillLevel, 3); // Cap particles at 3

                for (int i = 0; i < particleCount; i++) {
                    double x = pos.getX() + 0.3 + level.random.nextDouble() * 0.4;
                    double y = pos.getY() + 0.7 + level.random.nextDouble() * 0.1;
                    double z = pos.getZ() + 0.3 + level.random.nextDouble() * 0.4;

                    // Different particles based on fermentation level
                    net.minecraft.core.particles.ParticleOptions particle;
                    if (avgFermentation >= ModDataComponents.QUALITY_FERMENTED) {
                        // Fermented: Gold/orange flame-like particles
                        particle = net.minecraft.core.particles.ParticleTypes.FLAME;
                    } else if (avgFermentation >= ModDataComponents.QUALITY_AGED) {
                        // Aged: Yellow/green sparkle particles
                        particle = net.minecraft.core.particles.ParticleTypes.HAPPY_VILLAGER;
                    } else {
                        // Fresh/Dried: Gray smoke
                        particle = net.minecraft.core.particles.ParticleTypes.SMOKE;
                    }

                    level.addParticle(
                            particle,
                            x, y, z,
                            0.0, 0.015 + level.random.nextDouble() * 0.01, 0.0
                    );
                }
            }
            return;
        }

        // OPTIMIZED: Server-side fermentation check only every 200 ticks (10 seconds)
        // Fermentation takes 24000-72000 ticks, so checking every 200 is plenty accurate
        if (gameTime % SERVER_TICK_INTERVAL != 0) {
            return;
        }

        boolean changed = false;

        for (int i = 0; i < SLOT_COUNT; i++) {
            if (entity.items[i].isEmpty()) continue;

            ItemStack stack = entity.items[i];
            Integer currentLevel = ModDataComponents.FERMENTATION_LEVEL.get(stack);
            if (currentLevel == null) {
                currentLevel = ModDataComponents.QUALITY_DRIED;
            }

            // Only ferment if not already at max quality
            if (currentLevel < ModDataComponents.QUALITY_FERMENTED) {
                long startTime = entity.fermentationStartTimes[i];
                long elapsedTime = gameTime - startTime;

                int newLevel = currentLevel;
                if (elapsedTime >= FERMENTED_TIME && currentLevel < ModDataComponents.QUALITY_FERMENTED) {
                    newLevel = ModDataComponents.QUALITY_FERMENTED;
                } else if (elapsedTime >= AGED_TIME && currentLevel < ModDataComponents.QUALITY_AGED) {
                    newLevel = ModDataComponents.QUALITY_AGED;
                }

                if (newLevel != currentLevel) {
                    ModDataComponents.FERMENTATION_LEVEL.set(stack, newLevel);
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
            Nbt.putStack(tag, "Item" + i, items[i], registries);
            Nbt.putLong(tag, "FermentStart" + i, fermentationStartTimes[i]);
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
            items[i] = Nbt.getStack(tag, "Item" + i, registries);
            fermentationStartTimes[i] = Nbt.getLong(tag, "FermentStart" + i, 0);
        }
    }
    //?} else {
    /*@Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        for (int i = 0; i < SLOT_COUNT; i++) {
            Nbt.putStack(output, "Item" + i, items[i]);
            Nbt.putLong(output, "FermentStart" + i, fermentationStartTimes[i]);
        }
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        for (int i = 0; i < SLOT_COUNT; i++) {
            items[i] = Nbt.getStack(input, "Item" + i);
            fermentationStartTimes[i] = Nbt.getLong(input, "FermentStart" + i, 0);
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
    //?}
        return tag;
    }

    @Override
    //? if <1.20.5 {
    /*public void handleUpdateTag(CompoundTag tag) {
        load(tag);
    *///?} else {
    public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider registries) {
        loadAdditional(tag, registries);
    //?}
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
    *///?}
}
