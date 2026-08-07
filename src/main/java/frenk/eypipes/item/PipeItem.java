package frenk.eypipes.item;

import frenk.eypipes.compat.Interactions;
import frenk.eypipes.client.SmokeClientEffects;
import frenk.eypipes.compat.Cooldowns;
import frenk.eypipes.compat.Nbt;
import frenk.eypipes.compat.ServerParticles;
import frenk.eypipes.config.EyPipesConfig;
import frenk.eypipes.registries.ModItems;
import frenk.eypipes.registries.ModParticles;
import frenk.eypipes.registries.ModSounds;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
//? if <1.21.5 {
import net.minecraft.world.InteractionResultHolder;
//?} else
/*import net.minecraft.world.InteractionResult;*/
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
//? if <1.21.5 {
import net.minecraft.world.item.UseAnim;
//?} else
/*import net.minecraft.world.item.ItemUseAnimation;*/
import net.minecraft.world.level.Level;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import java.util.List;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
//? if >=1.21.9 {
/*import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.animatable.processing.AnimationController;
*///?}
import software.bernie.geckolib.util.GeckoLibUtil;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

/**
 * Pipe item - An animated smoking pipe with GeckoLib 4 and Curios integration.
 * Features durability, smoke particles, and repair mechanics.
 * Ported from Fabric 1.19.2 (GeckoLib 3 + Trinkets) to NeoForge 1.21.1 (GeckoLib 4 + Curios)
 */
public class PipeItem extends Item implements GeoItem, ICurioItem {
    private static final Logger LOGGER = LoggerFactory.getLogger("EyPipes");

    // Animation constants - use thenPlay for proper animation reset
    private static final RawAnimation SMOKE_ANIM = RawAnimation.begin().thenPlay("animation.pipe.smoke");

    // GeckoLib 4 cache
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    // Usage settings
    private static final int USAGE_TIME = 60;
    private static final String SMOKING_KEY = "smoking";
    private static final String HERB_TYPE_KEY = "herb_type";
    private static final String QUALITY_KEY = "quality_level";

    // Herb type constants
    public static final int HERB_ERBAPIPA = 0;
    public static final int HERB_VALERIANA = 1;
    public static final int HERB_GINSENG = 2;
    public static final int HERB_SALVIA = 3;

    // Scheduled executor for delayed particle spawning - daemon threads die with JVM, no server-stop leak
    private static final ScheduledExecutorService PARTICLE_EXECUTOR = Executors.newScheduledThreadPool(2, r -> {
        Thread t = new Thread(r, "eypipes-particle-scheduler");
        t.setDaemon(true);
        return t;
    });

    public PipeItem(Properties properties) {
        super(properties);
        // Register as singleton animatable for GeckoLib 4
        SingletonGeoAnimatable.registerSyncedAnimatable(this);
    }

    // NBT helpers for smoking state (using custom data in 1.21.1)
    private boolean isSmoking(ItemStack stack) {
        if (!stack.has(net.minecraft.core.component.DataComponents.CUSTOM_DATA)) {
            return false;
        }
        return Nbt.getBoolean(stack.get(net.minecraft.core.component.DataComponents.CUSTOM_DATA).copyTag(), SMOKING_KEY, false);
    }

    private void setSmoking(ItemStack stack, boolean smoking) {
        stack.update(net.minecraft.core.component.DataComponents.CUSTOM_DATA,
                net.minecraft.world.item.component.CustomData.EMPTY,
                data -> data.update(tag -> tag.putBoolean(SMOKING_KEY, smoking)));
    }

    // Herb type helpers
    private int getHerbType(ItemStack stack) {
        if (!stack.has(net.minecraft.core.component.DataComponents.CUSTOM_DATA)) {
            return HERB_ERBAPIPA;
        }
        return Nbt.getInt(stack.get(net.minecraft.core.component.DataComponents.CUSTOM_DATA).copyTag(), HERB_TYPE_KEY, HERB_ERBAPIPA);
    }

    private void setHerbType(ItemStack stack, int herbType) {
        stack.update(net.minecraft.core.component.DataComponents.CUSTOM_DATA,
                net.minecraft.world.item.component.CustomData.EMPTY,
                data -> data.update(tag -> tag.putInt(HERB_TYPE_KEY, herbType)));
    }

    // Quality level helpers (for fermented tobacco effects)
    private int getQualityLevel(ItemStack stack) {
        if (!stack.has(net.minecraft.core.component.DataComponents.CUSTOM_DATA)) {
            return 1; // Default: dried quality
        }
        // Absent means dried (1.0x), not fresh (0.5x), so the fallback is spelled out
        return Nbt.getInt(stack.get(net.minecraft.core.component.DataComponents.CUSTOM_DATA).copyTag(), QUALITY_KEY, 1);
    }

    private void setQualityLevel(ItemStack stack, int qualityLevel) {
        stack.update(net.minecraft.core.component.DataComponents.CUSTOM_DATA,
                net.minecraft.world.item.component.CustomData.EMPTY,
                data -> data.update(tag -> tag.putInt(QUALITY_KEY, qualityLevel)));
    }

    /**
     * Get quality level from an herb item (reads from its data component).
     * Default quality for cutted herbs is DRIED (1).
     */
    private int getQualityFromHerb(ItemStack herbStack) {
        if (herbStack.has(frenk.eypipes.registries.ModDataComponents.FERMENTATION_LEVEL.get())) {
            return herbStack.get(frenk.eypipes.registries.ModDataComponents.FERMENTATION_LEVEL.get());
        }
        return 1; // Default: dried quality
    }

    /**
     * Determine herb type from an ItemStack.
     * Returns -1 if the item is not a valid cutted herb.
     */
    private int getHerbTypeFromItem(ItemStack itemStack) {
        if (itemStack.is(ModItems.ERBAPIPA_CUTTED.get())) return HERB_ERBAPIPA;
        if (itemStack.is(ModItems.VALERIANA_CUTTED.get())) return HERB_VALERIANA;
        if (itemStack.is(ModItems.GINSENG_CUTTED.get())) return HERB_GINSENG;
        if (itemStack.is(ModItems.SALVIA_CUTTED.get())) return HERB_SALVIA;
        return -1; // Not a valid herb
    }

    //? if <1.21.9 {
    @Override
    public void onCraftedBy(ItemStack stack, Level level, Player player) {
        if (!level.isClientSide()) {
            // Start with pipe nearly empty (requires refill)
            stack.setDamageValue(stack.getMaxDamage() - 1);
        }
    }
    //?} else {
    /*@Override
    public void onCraftedPostProcess(ItemStack stack, Level level) {
        if (!level.isClientSide()) {
            // Start with pipe nearly empty (requires refill)
            stack.setDamageValue(stack.getMaxDamage() - 1);
        }
    }
    *///?}

    @Override
    public int getBarColor(ItemStack stack) {
        return 0x01e81b0; // Green color for durability bar
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        if (stack.isDamageableItem()) {
            return Math.round(13.0F - (float) stack.getDamageValue() * 13.0F / (float) stack.getMaxDamage());
        }
        return super.getBarWidth(stack);
    }

    @Override
    //? if <1.21.5 {
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
    //?} else
    /*public InteractionResult use(Level level, Player player, InteractionHand hand) {*/
        ItemStack itemStack = player.getItemInHand(hand);

        // Check for shift+right-click with any cutted herb in off-hand for refill
        if (player.isShiftKeyDown() && hand == InteractionHand.MAIN_HAND) {
            ItemStack offHandStack = player.getItemInHand(InteractionHand.OFF_HAND);
            int herbType = getHerbTypeFromItem(offHandStack);

            if (herbType >= 0 && itemStack.isDamageableItem()) {
                // Check if pipe is already at maximum durability
                if (itemStack.getDamageValue() <= 1) {
                    return Interactions.useFail(itemStack);
                }

                int currentDamage = itemStack.getDamageValue();
                int remainingUses = itemStack.getMaxDamage() - currentDamage;

                // If pipe has herbs loaded, only allow same herb type
                if (remainingUses > 0) {
                    int currentHerbType = getHerbType(itemStack);
                    if (currentHerbType != herbType) {
                        // Different herb type - cannot mix, pipe must be empty first
                        return Interactions.useFail(itemStack);
                    }
                }

                int maxInsertable = currentDamage - 1;
                int insertCount = player.isCreative() ? maxInsertable : Math.min(maxInsertable, offHandStack.getCount());
                if (insertCount <= 0) {
                    return Interactions.useFail(itemStack);
                }

                itemStack.setDamageValue(currentDamage - insertCount);

                // Store the herb type and quality level that was loaded
                // (only changes type if pipe was empty, otherwise keeps same type)
                setHerbType(itemStack, herbType);
                setQualityLevel(itemStack, getQualityFromHerb(offHandStack));

                // Consume herbs
                if (!player.isCreative()) {
                    offHandStack.shrink(insertCount);
                }

                // Play refill sound and set cooldown
                level.playSound(null, player.getX(), player.getY(), player.getZ(),
                        ModSounds.PIPE_REFILL.get(), SoundSource.PLAYERS, 1.0F, 1.2F);
                Cooldowns.add(player, itemStack, 20);

                return Interactions.useSuccess(itemStack);
            }
        }

        // Check if pipe is empty (no tobacco left) - cannot smoke
        if (itemStack.isDamageableItem() && itemStack.getDamageValue() >= itemStack.getMaxDamage()) {
            // Pipe is empty - need to refill using shift+right-click with erbapipa_cutted
            return Interactions.useFail(itemStack);
        }

        // Start smoking
        player.startUsingItem(hand);
        setSmoking(itemStack, true);
        LOGGER.debug("Pipe use started for player {}", player.getName().getString());

        // Notify burning tobacco layer (client-side only)
        if (level.isClientSide()) {
            SmokeClientEffects.onStartSmoking(player, itemStack);
        }

        // Animation is handled by PipeGeoRenderer's custom transformation
        // GeckoLib animation disabled due to inconsistent behavior between dev/production

        player.awardStat(Stats.ITEM_USED.get(this));

        return Interactions.useConsume(itemStack);
    }

    @Override
    //? if <1.21.5 {
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeCharged) {
    //?} else
    /*public boolean releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeCharged) {*/
        if (isSmoking(stack)) {
            finishUsing(stack, level, entity);
        }
        setSmoking(stack, false);

        // Notify burning tobacco layer to start afterglow (client-side only)
        if (level.isClientSide() && entity instanceof Player player) {
            SmokeClientEffects.onStopSmoking(player, stack, level.getGameTime());
        }

        // Animation handled by PipeGeoRenderer - no GeckoLib animation stop needed

        if (entity instanceof Player player) {
            Cooldowns.add(player, stack, 20);
        }
        //? if >=1.21.5 {
        /*return false;
        *///?}
    }

    @Override
    public void onUseTick(Level level, LivingEntity entity, ItemStack stack, int remainingUseTicks) {
        if (!isSmoking(stack)) return;

        int totalTicks = USAGE_TIME - remainingUseTicks;
        int frequency = Math.max(4, (int) Math.pow((USAGE_TIME - totalTicks) / 10.0, 2));

        // Play tobacco crackling sound every 40 ticks (2 seconds)
        if (totalTicks > 0 && totalTicks % 40 == 0 && !level.isClientSide()) {
            level.playSound(null, entity.getX(), entity.getY(), entity.getZ(),
                    ModSounds.TOBACCO_CRACKLE.get(), SoundSource.PLAYERS,
                    0.3F, 0.9F + level.random.nextFloat() * 0.2F);
        }

        if (remainingUseTicks % frequency == 0) {
            float intensity = 1.0f - ((float) remainingUseTicks / USAGE_TIME);

            if (level.isClientSide()) {
                if (SmokeClientEffects.isFirstPersonPipeView(entity)) {
                    // FIRST-PERSON: Use locator-based position for discrete particles
                    SmokeClientEffects.spawnFirstPersonBowl(level, intensity);
                } else {
                    // THIRD-PERSON or not local player: Use original eye-based position
                    boolean isLeftHand = entity.getUsedItemHand() == InteractionHand.OFF_HAND;
                    Vec3[] positions = calculateParticlePosition(entity, isLeftHand);
                    SmokeClientEffects.spawnThirdPersonBowl(level, positions[1], intensity);
                }
            } else if (level instanceof ServerLevel serverLevel) {
                // Server-side particles for other players (always third-person)
                boolean isLeftHand = entity.getUsedItemHand() == InteractionHand.OFF_HAND;
                Vec3[] positions = calculateParticlePosition(entity, isLeftHand);
                Vec3 thirdPersonPos = positions[1];
                for (ServerPlayer player : serverLevel.players()) {
                    if (player != entity && player.distanceTo(entity) <= 32.0) {
                        ServerParticles.sendTo(serverLevel, player, ModParticles.SMOKE_STREAM.get(),
                                thirdPersonPos.x + (level.random.nextGaussian() * 0.02),
                                thirdPersonPos.y + (level.random.nextGaussian() * 0.02),
                                thirdPersonPos.z + (level.random.nextGaussian() * 0.02),
                                10, 0.001, 0.01, 0.001, 0.0);
                    }
                }
            }
        }
    }

    public ItemStack finishUsing(ItemStack item, Level level, LivingEntity entity) {
        spawnSmoke(entity, level);

        if (!level.isClientSide()) {
            level.playSound(null, entity.getX(), entity.getY(), entity.getZ(),
                    ModSounds.PIPE_EXHALE.get(), SoundSource.PLAYERS, 0.8F, 1.0F);

            // Apply herb effects based on what was loaded and quality level
            applySmokingEffects(entity, getHerbType(item), getQualityLevel(item));
        }

        item.setDamageValue(item.getDamageValue() + 1);
        setSmoking(item, false);

        // Notify burning tobacco layer to start afterglow (client-side only)
        if (level.isClientSide() && entity instanceof Player player) {
            SmokeClientEffects.onStopSmoking(player, item, level.getGameTime());
        }

        if (entity instanceof Player player) {
            Cooldowns.add(player, item, 20);
        }

        return item;
    }

    /**
     * Apply potion effects based on the herb type that was smoked.
     * Duration is multiplied by quality level:
     * - Fresh (0): 0.5x duration
     * - Dried (1): 1.0x duration
     * - Aged (2): 1.5x duration
     * - Fermented (3): 2.0x duration
     *
     * Effects vary by herb:
     * - Erbapipa: No special effect (relaxation)
     * - Valeriana: Calming - Slowness I + Night Vision (base 30s)
     * - Ginseng: Energizing - Speed I + Haste I (base 30s)
     * - Salvia: Visions - Night Vision II + Glowing (base 20s)
     */
    private void applySmokingEffects(LivingEntity entity, int herbType, int qualityLevel) {
        float multiplier = frenk.eypipes.registries.ModDataComponents.getQualityMultiplier(qualityLevel);

        switch (herbType) {
            case HERB_VALERIANA -> {
                // Calming effect: slower movement but better night vision
                int duration = (int) (600 * multiplier); // base 30 seconds
                //? if <1.21.5 {
                entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, duration, 0));
                //?} else
                /*entity.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, duration, 0));*/
                entity.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, duration, 0));
            }
            case HERB_GINSENG -> {
                // Energizing effect: faster movement and mining
                int duration = (int) (600 * multiplier); // base 30 seconds
                //? if <1.21.5 {
                entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, duration, 0));
                //?} else
                /*entity.addEffect(new MobEffectInstance(MobEffects.SPEED, duration, 0));*/
                //? if <1.21.5 {
                entity.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, duration, 0));
                //?} else
                /*entity.addEffect(new MobEffectInstance(MobEffects.HASTE, duration, 0));*/
            }
            case HERB_SALVIA -> {
                // Vision effect: enhanced sight but you glow
                int duration = (int) (400 * multiplier); // base 20 seconds
                entity.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, duration, 1));
                entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, duration, 0));
            }
            // HERB_ERBAPIPA (default) - no special effects, just relaxation
        }
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return USAGE_TIME;
    }

    public void spawnSmoke(LivingEntity entity, Level level) {
        float velocityMultiplier = USAGE_TIME / 600.0f;

        // Delay between smoke rings in milliseconds (matching SMOKE_RING_DELAY_TICKS = 12 * 50ms)
        final long RING_DELAY_MS = 600L;

        if (level.isClientSide()) {
            // Client-side smoke rings with delayed spawning at current player position
            for (int i = 0; i < 3; i++) {
                final int particleIndex = i;
                final double offsetMultiplier = 0.3 + (particleIndex * 0.1);
                final float velScale = velocityMultiplier * (0.7f + particleIndex * 0.2f);

                PARTICLE_EXECUTOR.schedule(() ->
                    // Dispatch back to render thread - addParticle is not thread-safe
                    SmokeClientEffects.runOnRenderThread(() -> {
                        if (entity.isAlive()) {
                            SmokeClientEffects.spawnSmokeRing(entity, level, offsetMultiplier, velScale);
                        }
                    }), particleIndex * RING_DELAY_MS, TimeUnit.MILLISECONDS);
            }
        } else if (level instanceof ServerLevel serverLevel) {
            // Server-side smoke rings for other players with delayed spawning
            for (int i = 0; i < 3; i++) {
                final int particleIndex = i;
                final double offsetMultiplier = 0.3 + (particleIndex * 0.1);
                final float velScale = velocityMultiplier * (0.7f + particleIndex * 0.2f);

                PARTICLE_EXECUTOR.schedule(() ->
                    // Dispatch back to server thread - players() and sendParticles() are not thread-safe
                    serverLevel.getServer().execute(() -> {
                        if (entity.isAlive()) {
                            Vec3 vec = entity.getViewVector(1.0F);
                            for (ServerPlayer player : serverLevel.players()) {
                                if (player != entity) {
                                    ServerParticles.sendTo(serverLevel, player, ModParticles.RING_OF_SMOKE.get(),
                                            entity.getX() + vec.x * offsetMultiplier,
                                            entity.getY() + entity.getEyeHeight() + vec.y * offsetMultiplier,
                                            entity.getZ() + vec.z * offsetMultiplier,
                                            1,
                                            vec.x * velScale,
                                            vec.y * velScale,
                                            vec.z * velScale,
                                            1.0);
                                }
                            }
                        }
                    }), particleIndex * RING_DELAY_MS, TimeUnit.MILLISECONDS);
            }
        }
    }

    @Override
    //? if <1.21.5 {
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.NONE;
    }
    //?} else {
    /*public ItemUseAnimation getUseAnimation(ItemStack stack) {
        return ItemUseAnimation.NONE;
    }
    *///?}

    // From 1.21.9 repairability is a data component; an item that never sets one
    // cannot be repaired anyway, which is what this override was for.
    //? if <1.21.9 {
    @Override
    public boolean isRepairable(ItemStack stack) {
        return false; // Disable anvil repair - use erbapipa_cutted instead
    }
    //?}

    //? if <1.21.5 {
    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        addEyPipesTooltip(stack, tooltipComponents::add);
    }
    //?} else {
    /*@Override
    public void appendHoverText(ItemStack stack, TooltipContext context, net.minecraft.world.item.component.TooltipDisplay display, java.util.function.Consumer<Component> tooltipAdder, TooltipFlag flag) {
        super.appendHoverText(stack, context, display, tooltipAdder, flag);
        addEyPipesTooltip(stack, tooltipAdder);
    }
    *///?}

    /** The mod's own tooltip lines, independent of how the game asks for them. */
    private void addEyPipesTooltip(ItemStack stack, java.util.function.Consumer<Component> lines) {

        // Calculate remaining uses
        int remainingUses = stack.getMaxDamage() - stack.getDamageValue();

        // Show loaded herb info if there are remaining uses
        if (remainingUses > 0) {
            int herbType = getHerbType(stack);
            int qualityLevel = getQualityLevel(stack);
            String herbTranslationKey = getHerbTranslationKey(herbType);

            // Show herb name and remaining count
            lines.accept(Component.translatable("tooltip.eypipes.loaded_herb",
                    Component.translatable(herbTranslationKey).withStyle(getHerbColor(herbType)),
                    remainingUses)
                    .withStyle(ChatFormatting.GRAY));

            // Show quality level with color based on fermentation
            String qualityName = frenk.eypipes.registries.ModDataComponents.getQualityName(qualityLevel);
            ChatFormatting qualityColor = getQualityColor(qualityLevel);
            lines.accept(Component.translatable("tooltip.eypipes.quality",
                    Component.translatable("tooltip.eypipes.quality." + qualityName.toLowerCase()).withStyle(qualityColor))
                    .withStyle(ChatFormatting.GRAY));
        } else {
            // Pipe is empty
            lines.accept(Component.translatable("tooltip.eypipes.pipe_empty")
                    .withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC));
        }

        // Show mod name
        lines.accept(Component.translatable("itemGroup.eypipes.eypipes_tab")
                .withStyle(ChatFormatting.BLUE, ChatFormatting.ITALIC));
    }

    /**
     * Get the translation key for a herb type.
     */
    private String getHerbTranslationKey(int herbType) {
        return switch (herbType) {
            case HERB_VALERIANA -> "item.eypipes.valeriana_cutted";
            case HERB_GINSENG -> "item.eypipes.ginseng_cutted";
            case HERB_SALVIA -> "item.eypipes.salvia_cutted";
            default -> "item.eypipes.erbapipa_cutted";
        };
    }

    /**
     * Get the color formatting for a herb type.
     */
    private ChatFormatting getHerbColor(int herbType) {
        return switch (herbType) {
            case HERB_VALERIANA -> ChatFormatting.LIGHT_PURPLE;
            case HERB_GINSENG -> ChatFormatting.GOLD;
            case HERB_SALVIA -> ChatFormatting.DARK_GREEN;
            default -> ChatFormatting.GREEN;
        };
    }

    /**
     * Get the color formatting for a quality level.
     */
    private ChatFormatting getQualityColor(int qualityLevel) {
        return switch (qualityLevel) {
            case 0 -> ChatFormatting.GRAY;           // Fresh
            case 2 -> ChatFormatting.YELLOW;         // Aged
            case 3 -> ChatFormatting.GOLD;           // Fermented
            default -> ChatFormatting.WHITE;         // Dried
        };
    }

    // GeckoLib 4 methods
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "smokeController", 0, state -> {
            // Only play animation when smoking
            return PlayState.STOP;
        }).triggerableAnim("smoke", SMOKE_ANIM));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    /**
     * Calculate smoke particle spawn position for first-person and third-person views.
     * Returns array: [0] = first person position, [1] = third person position
     */
    private Vec3[] calculateParticlePosition(LivingEntity entity, boolean isLeftHand) {
        Vec3 lookVec = entity.getViewVector(1.0F);

        Vec3 basePos = new Vec3(
                entity.getX(),
                entity.getY() + entity.getEyeHeight(),
                entity.getZ()
        );

        // Third-person: vectors that follow view direction including pitch
        Vec3 horizontalLookVec = new Vec3(lookVec.x, 0, lookVec.z).normalize();
        Vec3 rightVecThird = new Vec3(-horizontalLookVec.z, 0, horizontalLookVec.x).normalize();
        Vec3 upVecThird = new Vec3(0, 1, 0);

        // Use default values on server side (config CLIENT is only available on client)
        float offsetX = 0.0f;
        float offsetY = 0.0f;
        float offsetZ = 0.0f;
        try {
            offsetX = EyPipesConfig.CLIENT.particleOffsetThirdViewX.get().floatValue();
            offsetY = EyPipesConfig.CLIENT.particleOffsetThirdViewY.get().floatValue();
            offsetZ = EyPipesConfig.CLIENT.particleOffsetThirdViewZ.get().floatValue();
        } catch (IllegalStateException e) {
            // Config not loaded (server-side), use default values
        }

        // Flip the X offset for left hand
        float handMultiplier = isLeftHand ? -1.0f : 1.0f;

        Vec3 resultThird = basePos
                .add(lookVec.scale(0.5))  // Use full lookVec to follow vertical direction
                .add(rightVecThird.scale((offsetX + 0.2f) * handMultiplier))
                .add(upVecThird.scale(-offsetY + 0.2f));  // Lowered spawn point

        // First-person: full look direction
        Vec3 rightVecFirst = lookVec.cross(new Vec3(0, 1, 0)).normalize();
        Vec3 upVecFirst = rightVecFirst.cross(lookVec).normalize();

        Vec3 resultFirst = basePos
                .add(lookVec.scale(0.4))
                .add(rightVecFirst.scale(0.33 * handMultiplier))
                .add(upVecFirst.scale(0.0));

        return new Vec3[]{resultFirst, resultThird};
    }
}
