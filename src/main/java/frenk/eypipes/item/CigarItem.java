package frenk.eypipes.item;

import frenk.eypipes.config.EyPipesConfig;
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
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Cigar item - An animated smoking cigar with GeckoLib 4 and Curios integration.
 * Unlike the pipe, the cigar is destroyed when durability runs out (no refilling).
 * Ported from Fabric 1.19.2 (GeckoLib 3 + Trinkets) to NeoForge 1.21.1 (GeckoLib 4 + Curios)
 */
public class CigarItem extends Item implements GeoItem, ICurioItem {
    private static final Logger LOGGER = LoggerFactory.getLogger("EyPipes");

    // Animation constants
    private static final RawAnimation SMOKE_ANIM = RawAnimation.begin().thenPlay("animation.cigar.smoke");

    // GeckoLib 4 cache
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    // Usage settings
    private static final int USAGE_TIME = 60;
    private static final String SMOKING_KEY = "smoking";

    // Scheduled executor for delayed particle spawning
    private static final ScheduledExecutorService PARTICLE_EXECUTOR = Executors.newScheduledThreadPool(2);

    public CigarItem(Properties properties) {
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
            // Start with cigar full (fresh cigar)
            stack.setDamageValue(0);
        }
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return 0x8B4513; // Brown color for cigar durability bar
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

        // Check if cigar is depleted (destroyed when empty, no refilling)
        if (itemStack.isDamageableItem() && itemStack.getDamageValue() >= itemStack.getMaxDamage()) {
            // Cigar is finished - destroy it
            if (!level.isClientSide()) {
                itemStack.shrink(1);
                level.playSound(null, player.getX(), player.getY(), player.getZ(),
                        ModSounds.PIPE_EXHALE.get(), SoundSource.PLAYERS, 0.5F, 0.8F);
            }
            return InteractionResultHolder.fail(itemStack);
        }

        // Double-check cigar has durability before smoking
        if (itemStack.isDamageableItem() && itemStack.getDamageValue() >= itemStack.getMaxDamage()) {
            return InteractionResultHolder.fail(itemStack);
        }

        // Start smoking
        player.startUsingItem(hand);
        setSmoking(itemStack, true);
        LOGGER.debug("Cigar use started for player {}", player.getName().getString());

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
            // Spawn smoke particles
            Vec3[] positions = calculateParticlePosition(entity);

            if (level.isClientSide()) {
                // Client-side particles
                Vec3 correctPosition = positions[0]; // First person position
                for (int i = 0; i < 8; i++) {
                    level.addParticle(ParticleTypes.SMOKE,
                            correctPosition.x + (level.random.nextGaussian() * 0.02),
                            correctPosition.y + (level.random.nextGaussian() * 0.02),
                            correctPosition.z + (level.random.nextGaussian() * 0.02),
                            0.001, 0.01, 0.001);
                }

                // Spawn ember particles if enabled
                if (EyPipesConfig.CLIENT.enableEmberParticles.get()) {
                    level.addParticle(ModParticles.EMBER.get(),
                            correctPosition.x, correctPosition.y, correctPosition.z,
                            0, 0.02, 0);
                }
            } else if (level instanceof ServerLevel serverLevel) {
                // Server-side particles for other players
                Vec3 thirdPersonPos = positions[1];
                for (ServerPlayer player : serverLevel.players()) {
                    if (player != entity && player.distanceTo(entity) <= 32.0) {
                        serverLevel.sendParticles(player, ParticleTypes.SMOKE,
                                false,
                                thirdPersonPos.x + (level.random.nextGaussian() * 0.02),
                                thirdPersonPos.y + (level.random.nextGaussian() * 0.02),
                                thirdPersonPos.z + (level.random.nextGaussian() * 0.02),
                                8, 0.001, 0.01, 0.001, 0.0);
                    }
                }
            }
        }
    }

    public ItemStack finishUsing(ItemStack item, Level level, LivingEntity entity) {
        spawnSmoke(entity, level);

        if (!level.isClientSide()) {
            level.playSound(null, entity.getX(), entity.getY(), entity.getZ(),
                    ModSounds.PIPE_EXHALE.get(), SoundSource.PLAYERS, 0.8F, 0.9F);
        }

        // Increase damage (use up cigar)
        int newDamage = item.getDamageValue() + 1;

        // Check if cigar should be destroyed
        if (item.isDamageableItem() && newDamage >= item.getMaxDamage()) {
            // Cigar is finished - destroy it
            if (!level.isClientSide()) {
                item.shrink(1);
                // Play a finishing sound
                level.playSound(null, entity.getX(), entity.getY(), entity.getZ(),
                        ModSounds.PIPE_EXHALE.get(), SoundSource.PLAYERS, 0.3F, 0.7F);
            }
        } else {
            item.setDamageValue(newDamage);
        }

        setSmoking(item, false);

        if (entity instanceof Player player) {
            player.getCooldowns().addCooldown(this, 20);
        }

        // Spawn ash particles if enabled
        if (level.isClientSide() && EyPipesConfig.CLIENT.enableAshParticles.get()) {
            Vec3[] positions = calculateParticlePosition(entity);
            for (int i = 0; i < 3; i++) {
                level.addParticle(ModParticles.ASH.get(),
                        positions[0].x, positions[0].y, positions[0].z,
                        (level.random.nextDouble() - 0.5) * 0.1,
                        -0.05,
                        (level.random.nextDouble() - 0.5) * 0.1);
            }
        }

        return item;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return USAGE_TIME;
    }

    public void spawnSmoke(LivingEntity entity, Level level) {
        float velocityMultiplier = USAGE_TIME / 600.0f;

        if (level.isClientSide()) {
            // Client-side smoke with delayed spawning (fewer rings than pipe)
            for (int i = 0; i < 2; i++) {
                final int particleIndex = i;
                final double offsetMultiplier = 0.3 + (particleIndex * 0.15);

                PARTICLE_EXECUTOR.schedule(() -> {
                    if (entity.isAlive()) {
                        Vec3 vec = entity.getViewVector(1.0F);
                        level.addParticle(ModParticles.RING_OF_SMOKE.get(),
                                entity.getX() + vec.x * offsetMultiplier,
                                entity.getY() + entity.getEyeHeight() + vec.y * offsetMultiplier,
                                entity.getZ() + vec.z * offsetMultiplier,
                                vec.x * velocityMultiplier,
                                vec.y * velocityMultiplier,
                                vec.z * velocityMultiplier);

                        // Spawn smoke wisps if enabled
                        if (EyPipesConfig.CLIENT.enableSmokeWisps.get()) {
                            level.addParticle(ModParticles.SMOKE_WISP.get(),
                                    entity.getX() + vec.x * offsetMultiplier,
                                    entity.getY() + entity.getEyeHeight() + vec.y * offsetMultiplier,
                                    entity.getZ() + vec.z * offsetMultiplier,
                                    vec.x * velocityMultiplier * 0.5,
                                    vec.y * velocityMultiplier * 0.5 + 0.02,
                                    vec.z * velocityMultiplier * 0.5);
                        }
                    }
                }, particleIndex * 800L, TimeUnit.MILLISECONDS);
            }
        } else if (level instanceof ServerLevel serverLevel) {
            // Server-side smoke for other players
            for (int i = 0; i < 2; i++) {
                final int particleIndex = i;
                final double offsetMultiplier = 0.3 + (particleIndex * 0.15);

                PARTICLE_EXECUTOR.schedule(() -> {
                    if (entity.isAlive()) {
                        Vec3 vec = entity.getViewVector(1.0F);
                        for (ServerPlayer player : serverLevel.players()) {
                            if (player != entity) {
                                serverLevel.sendParticles(player, ModParticles.RING_OF_SMOKE.get(),
                                        false,
                                        entity.getX() + vec.x * offsetMultiplier,
                                        entity.getY() + entity.getEyeHeight() + vec.y * offsetMultiplier,
                                        entity.getZ() + vec.z * offsetMultiplier,
                                        1,
                                        vec.x * velocityMultiplier,
                                        vec.y * velocityMultiplier,
                                        vec.z * velocityMultiplier,
                                        1.0);
                            }
                        }
                    }
                }, particleIndex * 800L, TimeUnit.MILLISECONDS);
            }
        }
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.NONE;
    }

    @Override
    public boolean isRepairable(ItemStack stack) {
        return false; // Cigars cannot be repaired - they are consumed
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
     * Cigar position is slightly different from pipe (held differently).
     * Returns array: [0] = first person position, [1] = third person position
     */
    private Vec3[] calculateParticlePosition(LivingEntity entity) {
        Vec3 lookVec = entity.getViewVector(1.0F);

        Vec3 basePos = new Vec3(
                entity.getX(),
                entity.getY() + entity.getEyeHeight(),
                entity.getZ()
        );

        // Third-person: horizontal vectors for fixed height
        Vec3 horizontalLookVec = new Vec3(lookVec.x, 0, lookVec.z).normalize();
        Vec3 rightVecThird = new Vec3(horizontalLookVec.z, 0, -horizontalLookVec.x).normalize();
        Vec3 upVecThird = new Vec3(0, 1, 0);

        // Cigar offsets (slightly different from pipe)
        float offsetX = EyPipesConfig.CLIENT.particleOffsetThirdViewX.get().floatValue();
        float offsetY = EyPipesConfig.CLIENT.particleOffsetThirdViewY.get().floatValue() + 0.05f;
        float offsetZ = EyPipesConfig.CLIENT.particleOffsetThirdViewZ.get().floatValue();

        Vec3 resultThird = basePos
                .add(horizontalLookVec.scale(0.45))
                .add(rightVecThird.scale(offsetX))
                .add(upVecThird.scale(-offsetY));

        // First-person: full look direction
        Vec3 rightVecFirst = lookVec.cross(new Vec3(0, 1, 0)).normalize();
        Vec3 upVecFirst = rightVecFirst.cross(lookVec).normalize();

        Vec3 resultFirst = basePos
                .add(lookVec.scale(0.35))
                .add(rightVecFirst.scale(0.30))
                .add(upVecFirst.scale(0.02));

        return new Vec3[]{resultFirst, resultThird};
    }
}
