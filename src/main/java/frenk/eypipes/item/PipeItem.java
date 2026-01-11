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
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
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

        // Check for shift+right-click with erbapipa_cutted in off-hand for durability repair
        if (player.isShiftKeyDown() && hand == InteractionHand.MAIN_HAND) {
            ItemStack offHandStack = player.getItemInHand(InteractionHand.OFF_HAND);
            if (offHandStack.is(ModItems.ERBAPIPA_CUTTED.get()) && itemStack.isDamageableItem()) {
                // Check if pipe is already at maximum durability
                if (itemStack.getDamageValue() <= 1) {
                    return InteractionResultHolder.fail(itemStack);
                }

                // Increase durability by 1 (decrease damage by 1)
                int currentDamage = itemStack.getDamageValue();
                int newDamage = Math.max(0, currentDamage - 1);
                itemStack.setDamageValue(newDamage);

                // Consume one erbapipa_cutted
                if (!player.isCreative()) {
                    offHandStack.shrink(1);
                }

                // Play repair sound and set cooldown
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
                        level.addParticle(ParticleTypes.SMOKE,
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
                        serverLevel.sendParticles(player, ParticleTypes.SMOKE,
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
        }

        item.setDamageValue(item.getDamageValue() + 1);
        setSmoking(item, false);

        if (entity instanceof Player player) {
            player.getCooldowns().addCooldown(this, 20);
        }

        // Ash effect removed - user didn't want gray particles flying up

        return item;
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
                .add(upVecThird.scale(-offsetY + 0.3f));  // Add 0.3f higher

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
