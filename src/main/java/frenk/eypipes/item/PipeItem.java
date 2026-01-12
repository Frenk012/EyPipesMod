package frenk.eypipes.item;

import frenk.eypipes.client.renderer.BasePipeRenderer;
import frenk.eypipes.config.EyPipesConfig;
import frenk.eypipes.particle.EnhancedParticleHelper;
import frenk.eypipes.registries.ModItems;
import net.minecraft.client.Minecraft;
import frenk.eypipes.registries.ModParticles;
import frenk.eypipes.registries.ModSounds;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
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

    // Scheduled executor for delayed particle spawning
    private static final ScheduledExecutorService PARTICLE_EXECUTOR = Executors.newScheduledThreadPool(2);

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
        return stack.get(net.minecraft.core.component.DataComponents.CUSTOM_DATA)
                .copyTag().getBoolean(SMOKING_KEY);
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
        return stack.get(net.minecraft.core.component.DataComponents.CUSTOM_DATA)
                .copyTag().getInt(HERB_TYPE_KEY);
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
        return stack.get(net.minecraft.core.component.DataComponents.CUSTOM_DATA)
                .copyTag().getInt(QUALITY_KEY);
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

    @Override
    public void onCraftedBy(ItemStack stack, Level level, Player player) {
        if (!level.isClientSide()) {
            // Start with pipe nearly empty (requires refill)
            stack.setDamageValue(stack.getMaxDamage() - 1);
        }
    }

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
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);

        // Check for shift+right-click with any cutted herb in off-hand for refill
        if (player.isShiftKeyDown() && hand == InteractionHand.MAIN_HAND) {
            ItemStack offHandStack = player.getItemInHand(InteractionHand.OFF_HAND);
            int herbType = getHerbTypeFromItem(offHandStack);

            if (herbType >= 0 && itemStack.isDamageableItem()) {
                // Check if pipe is already at maximum durability
                if (itemStack.getDamageValue() <= 1) {
                    return InteractionResultHolder.fail(itemStack);
                }

                int currentDamage = itemStack.getDamageValue();
                int maxInsertable = currentDamage - 1;
                int insertCount = player.isCreative() ? maxInsertable : Math.min(maxInsertable, offHandStack.getCount());
                if (insertCount <= 0) {
                    return InteractionResultHolder.fail(itemStack);
                }

                itemStack.setDamageValue(currentDamage - insertCount);

                // Store the herb type and quality level that was loaded
                setHerbType(itemStack, herbType);
                setQualityLevel(itemStack, getQualityFromHerb(offHandStack));

                // Consume herbs
                if (!player.isCreative()) {
                    offHandStack.shrink(insertCount);
                }

                // Play refill sound and set cooldown
                level.playSound(null, player.getX(), player.getY(), player.getZ(),
                        ModSounds.PIPE_REFILL.get(), SoundSource.PLAYERS, 1.0F, 1.2F);
                player.getCooldowns().addCooldown(this, 20);

                return InteractionResultHolder.success(itemStack);
            }
        }

        // Check if pipe is empty (no tobacco left) - cannot smoke
        if (itemStack.isDamageableItem() && itemStack.getDamageValue() >= itemStack.getMaxDamage()) {
            // Pipe is empty - need to refill using shift+right-click with erbapipa_cutted
            return InteractionResultHolder.fail(itemStack);
        }

        // Start smoking
        player.startUsingItem(hand);
        setSmoking(itemStack, true);
        LOGGER.debug("Pipe use started for player {}", player.getName().getString());

        // Trigger animation
        if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
            triggerAnim(serverPlayer, GeoItem.getOrAssignId(itemStack, (ServerLevel) level), "smokeController", "smoke");
        }

        player.awardStat(Stats.ITEM_USED.get(this));

        return InteractionResultHolder.consume(itemStack);
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeCharged) {
        if (isSmoking(stack)) {
            finishUsing(stack, level, entity);
        }
        setSmoking(stack, false);

        // Stop animation when releasing
        if (!level.isClientSide() && entity instanceof ServerPlayer serverPlayer) {
            stopTriggeredAnim(serverPlayer, GeoItem.getOrAssignId(stack, (ServerLevel) level), "smokeController", "smoke");
        }

        if (entity instanceof Player player) {
            player.getCooldowns().addCooldown(this, 20);
        }
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
                // Client-side particles - check if first-person with locator position
                boolean isLocalPlayer = entity == Minecraft.getInstance().player;
                boolean isFirstPerson = Minecraft.getInstance().options.getCameraType().isFirstPerson();

                if (isLocalPlayer && isFirstPerson && BasePipeRenderer.isCurrentlySmokingFirstPerson()) {
                    // FIRST-PERSON: Use locator-based position for discrete particles
                    Vec3 locatorPos = BasePipeRenderer.getLastLocatorWorldPos();
                    if (locatorPos != Vec3.ZERO) {
                        // Spawn smaller, discrete first-person particles at bowl locator
                        EnhancedParticleHelper.spawnFirstPersonBowlSmoke(level, locatorPos, intensity);
                        EnhancedParticleHelper.spawnFirstPersonBowlEmbers(level, locatorPos, intensity);
                    }
                } else {
                    // THIRD-PERSON or not local player: Use original eye-based position
                    Vec3[] positions = calculateParticlePosition(entity);
                    Vec3 thirdPersonPos = positions[1];

                    for (int i = 0; i < 10; i++) {
                        level.addParticle(ModParticles.SMOKE_STREAM.get(),
                                thirdPersonPos.x + (level.random.nextGaussian() * 0.02),
                                thirdPersonPos.y + (level.random.nextGaussian() * 0.02),
                                thirdPersonPos.z + (level.random.nextGaussian() * 0.02),
                                0.001, 0.01, 0.001);
                    }

                    EnhancedParticleHelper.spawnBowlEmbers(level, thirdPersonPos, intensity);
                }
            } else if (level instanceof ServerLevel serverLevel) {
                // Server-side particles for other players (always third-person)
                Vec3[] positions = calculateParticlePosition(entity);
                Vec3 thirdPersonPos = positions[1];
                for (ServerPlayer player : serverLevel.players()) {
                    if (player != entity && player.distanceTo(entity) <= 32.0) {
                        serverLevel.sendParticles(player, ModParticles.SMOKE_STREAM.get(),
                                false,
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

        if (entity instanceof Player player) {
            player.getCooldowns().addCooldown(this, 20);
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
                entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, duration, 0));
                entity.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, duration, 0));
            }
            case HERB_GINSENG -> {
                // Energizing effect: faster movement and mining
                int duration = (int) (600 * multiplier); // base 30 seconds
                entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, duration, 0));
                entity.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, duration, 0));
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

                PARTICLE_EXECUTOR.schedule(() -> {
                    if (entity.isAlive()) {
                        // Get CURRENT position and direction when spawning
                        Vec3 vec = entity.getViewVector(1.0F);

                        level.addParticle(ModParticles.RING_OF_SMOKE.get(),
                                entity.getX() + vec.x * offsetMultiplier,
                                entity.getY() + entity.getEyeHeight() + vec.y * offsetMultiplier,
                                entity.getZ() + vec.z * offsetMultiplier,
                                vec.x * velScale,
                                vec.y * velScale,
                                vec.z * velScale);
                        // Smoke wisps removed - keep only the smoke rings
                    }
                }, particleIndex * RING_DELAY_MS, TimeUnit.MILLISECONDS);
            }
        } else if (level instanceof ServerLevel serverLevel) {
            // Server-side smoke rings for other players with delayed spawning
            for (int i = 0; i < 3; i++) {
                final int particleIndex = i;
                final double offsetMultiplier = 0.3 + (particleIndex * 0.1);
                final float velScale = velocityMultiplier * (0.7f + particleIndex * 0.2f);

                PARTICLE_EXECUTOR.schedule(() -> {
                    if (entity.isAlive()) {
                        // Get CURRENT position and direction when spawning
                        Vec3 vec = entity.getViewVector(1.0F);

                        for (ServerPlayer player : serverLevel.players()) {
                            if (player != entity) {
                                serverLevel.sendParticles(player, ModParticles.RING_OF_SMOKE.get(),
                                        false,
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
                }, particleIndex * RING_DELAY_MS, TimeUnit.MILLISECONDS);
            }
        }
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.NONE;
    }

    @Override
    public boolean isRepairable(ItemStack stack) {
        return false; // Disable anvil repair - use erbapipa_cutted instead
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);

        // Show mod name
        tooltipComponents.add(Component.translatable("itemGroup.eypipes.eypipes_tab")
                .withStyle(ChatFormatting.BLUE, ChatFormatting.ITALIC));
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
    private Vec3[] calculateParticlePosition(LivingEntity entity) {
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

        float offsetX = EyPipesConfig.CLIENT.particleOffsetThirdViewX.get().floatValue();
        float offsetY = EyPipesConfig.CLIENT.particleOffsetThirdViewY.get().floatValue();
        float offsetZ = EyPipesConfig.CLIENT.particleOffsetThirdViewZ.get().floatValue();

        Vec3 resultThird = basePos
                .add(lookVec.scale(0.5))  // Use full lookVec to follow vertical direction
                .add(rightVecThird.scale(offsetX + 0.2f))
                .add(upVecThird.scale(-offsetY + 0.2f));  // Lowered spawn point

        // First-person: full look direction
        Vec3 rightVecFirst = lookVec.cross(new Vec3(0, 1, 0)).normalize();
        Vec3 upVecFirst = rightVecFirst.cross(lookVec).normalize();

        Vec3 resultFirst = basePos
                .add(lookVec.scale(0.4))
                .add(rightVecFirst.scale(0.33))
                .add(upVecFirst.scale(0.0));

        return new Vec3[]{resultFirst, resultThird};
    }
}
