package frenk.eypipes.item;

import frenk.eypipes.compat.Interactions;
import frenk.eypipes.client.SmokeClientEffects;
import frenk.eypipes.compat.Cooldowns;
import frenk.eypipes.compat.Nbt;
import frenk.eypipes.compat.ServerParticles;
import frenk.eypipes.config.EyPipesConfig;
import frenk.eypipes.particle.FirstPersonSmoke;
import frenk.eypipes.registries.ModParticles;
import frenk.eypipes.registries.ModSounds;
import frenk.eypipes.util.SmokeOrigin;
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
import net.minecraft.world.phys.Vec3;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
//? if <1.20.5
//import software.bernie.geckolib.core.object.PlayState;
//? if >=1.21.9 {
/*import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.animatable.processing.AnimationController;
*///?}
import software.bernie.geckolib.util.GeckoLibUtil;
//? if !fabric
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Cigar item - An animated smoking cigar with GeckoLib 4 and Curios integration.
 * Unlike the pipe, the cigar is destroyed when durability runs out (no refilling).
 * Ported from Fabric 1.19.2 (GeckoLib 3 + Trinkets) to NeoForge 1.21.1 (GeckoLib 4 + Curios)
 */
// Fabric wears it through Trinkets, which needs no interface on the item
public class CigarItem extends Item implements GeoItem/*? if !fabric {*/, ICurioItem/*?}*/ {
    private static final Logger LOGGER = LoggerFactory.getLogger("EyPipes");

    // Animation constants
    private static final RawAnimation SMOKE_ANIM = RawAnimation.begin().thenPlay("animation.cigar.smoke");

    // GeckoLib 4 cache
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    // Usage settings
    private static final int USAGE_TIME = 60;
    private static final String SMOKING_KEY = "smoking";

    // Scheduled executor for delayed particle spawning - daemon threads die with JVM, no server-stop leak
    private static final ScheduledExecutorService PARTICLE_EXECUTOR = Executors.newScheduledThreadPool(2, r -> {
        Thread t = new Thread(r, "eypipes-cigar-particle-scheduler");
        t.setDaemon(true);
        return t;
    });

    public CigarItem(Properties properties) {
        super(properties);
        // Register as singleton animatable for GeckoLib 4
        SingletonGeoAnimatable.registerSyncedAnimatable(this);
    }

    // NBT helpers for smoking state (using custom data in 1.21.1)
    private boolean isSmoking(ItemStack stack) {
        if (!Nbt.hasItemTag(stack)) {
            return false;
        }
        return Nbt.getBoolean(Nbt.itemTag(stack), SMOKING_KEY, false);
    }

    private void setSmoking(ItemStack stack, boolean smoking) {
        Nbt.updateItemTag(stack, tag -> tag.putBoolean(SMOKING_KEY, smoking));
    }

    //? if <1.21.9 {
    @Override
    public void onCraftedBy(ItemStack stack, Level level, Player player) {
        if (!level.isClientSide()) {
            // Start with cigar full (fresh cigar)
            stack.setDamageValue(0);
        }
    }
    //?} else {
    /*@Override
    public void onCraftedPostProcess(ItemStack stack, Level level) {
        if (!level.isClientSide()) {
            // Start with cigar full (fresh cigar)
            stack.setDamageValue(0);
        }
    }
    *///?}

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
    //? if <1.21.5 {
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
    //?} else
    /*public InteractionResult use(Level level, Player player, InteractionHand hand) {*/
        ItemStack itemStack = player.getItemInHand(hand);

        // Check if cigar is depleted (destroyed when empty, no refilling)
        if (itemStack.isDamageableItem() && itemStack.getDamageValue() >= itemStack.getMaxDamage()) {
            // Cigar is finished - destroy it
            if (!level.isClientSide()) {
                itemStack.shrink(1);
                level.playSound(null, player.getX(), player.getY(), player.getZ(),
                        ModSounds.PIPE_EXHALE.get(), SoundSource.PLAYERS, 0.5F, 0.8F);
            }
            return Interactions.useFail(itemStack);
        }

        // Double-check cigar has durability before smoking
        if (itemStack.isDamageableItem() && itemStack.getDamageValue() >= itemStack.getMaxDamage()) {
            return Interactions.useFail(itemStack);
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

        // Stop animation when releasing
        if (!level.isClientSide() && entity instanceof ServerPlayer serverPlayer) {
            stopTriggeredAnim(serverPlayer, GeoItem.getOrAssignId(stack, (ServerLevel) level), "smokeController", "smoke");
        }

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

        if (remainingUseTicks % frequency == 0) {
            if (level.isClientSide()) {
                // Local player in first person: in front of the camera; anyone else: at the hand
                boolean firstPerson = SmokeClientEffects.isLocalFirstPerson(entity);
                Vec3 correctPosition = firstPerson
                        ? firstPersonPosition(entity)
                        : SmokeOrigin.handPosition(entity, 1.0F);
                Runnable spawnSmoke = () -> {
                    for (int i = 0; i < 8; i++) {
                        level.addParticle(ModParticles.SMOKE_STREAM.get(),
                                correctPosition.x + (level.random.nextGaussian() * 0.02),
                                correctPosition.y + (level.random.nextGaussian() * 0.02),
                                correctPosition.z + (level.random.nextGaussian() * 0.02),
                                0.001, 0.01, 0.001);
                    }
                };
                if (firstPerson) {
                    FirstPersonSmoke.run(spawnSmoke);
                } else {
                    spawnSmoke.run();
                }

                // Spawn ember particles if enabled
                if (EyPipesConfig.CLIENT.enableEmberParticles.get()) {
                    level.addParticle(ModParticles.EMBER.get(),
                            correctPosition.x, correctPosition.y, correctPosition.z,
                            0, 0.02, 0);
                }
            } else if (level instanceof ServerLevel serverLevel) {
                // Server-side particles for other players, at the smoker's hand
                Vec3 thirdPersonPos = SmokeOrigin.handPosition(entity, 1.0F);
                for (ServerPlayer player : serverLevel.players()) {
                    if (player != entity && player.distanceTo(entity) <= 32.0) {
                        ServerParticles.sendTo(serverLevel, player, ModParticles.SMOKE_STREAM.get(),
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
            Cooldowns.add(player, item, 20);
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

                PARTICLE_EXECUTOR.schedule(() ->
                    // Dispatch back to render thread - addParticle is not thread-safe
                    SmokeClientEffects.runOnRenderThread(() -> {
                        if (entity.isAlive()) {
                            SmokeClientEffects.spawnSmokeRing(entity, level, offsetMultiplier, velocityMultiplier);
                        }
                    }), particleIndex * 800L, TimeUnit.MILLISECONDS);
            }
        } else if (level instanceof ServerLevel serverLevel) {
            // Server-side smoke for other players
            for (int i = 0; i < 2; i++) {
                final int particleIndex = i;
                final double offsetMultiplier = 0.3 + (particleIndex * 0.15);

                PARTICLE_EXECUTOR.schedule(() ->
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
                                            vec.x * velocityMultiplier,
                                            vec.y * velocityMultiplier,
                                            vec.z * velocityMultiplier,
                                            1.0);
                                }
                            }
                        }
                    }), particleIndex * 800L, TimeUnit.MILLISECONDS);
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
    // cannot be repaired anyway, which is what this override was for. The hook is (Neo)Forge's;
    // on Fabric the item has no repair material, so only combining two in an anvil remains.
    //? if <1.21.9 && !fabric {
    @Override
    public boolean isRepairable(ItemStack stack) {
        return false; // Cigars cannot be repaired - they are consumed
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

        // Show remaining puffs
        if (remainingUses > 0) {
            lines.accept(Component.translatable("tooltip.eypipes.cigar_remaining", remainingUses)
                    .withStyle(ChatFormatting.GRAY));
        } else {
            lines.accept(Component.translatable("tooltip.eypipes.cigar_finished")
                    .withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC));
        }

        // Show mod name
        lines.accept(Component.translatable("itemGroup.eypipes.eypipes_tab")
                .withStyle(ChatFormatting.BLUE, ChatFormatting.ITALIC));
    }

    // GeckoLib 4 methods
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        //? if <1.21.9 {
        controllers.add(new AnimationController<>(this, "smokeController", 0, state -> {
        //?} else
        /*controllers.add(new AnimationController<>("smokeController", 0, state -> {*/
            // Only play animation when smoking
            return PlayState.STOP;
        }).triggerableAnim("smoke", SMOKE_ANIM));
    }

    // From GeckoLib 5 the item supplies its own renderer instead of it being registered
    // through NeoForge client extensions, which no longer have a BEWLR to hand back. Fabric has
    // no client extensions at all, so it always goes this way.
    //? if >=1.21.9 || fabric {
    /*@Override
    public void createGeoRenderer(java.util.function.Consumer<software.bernie.geckolib.animatable.client.GeoRenderProvider> consumer) {
        consumer.accept(new software.bernie.geckolib.animatable.client.GeoRenderProvider() {
            private software.bernie.geckolib.renderer.GeoItemRenderer<?> renderer;

            @Override
            public software.bernie.geckolib.renderer.GeoItemRenderer<?> getGeoItemRenderer() {
                if (this.renderer == null) {
                    this.renderer = new frenk.eypipes.client.renderer.CigarItemRenderer();
                }
                return this.renderer;
            }
        });
    }
    *///?}

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    /**
     * Smoke position for the local player in first person: below and to the right of the
     * camera, where the lowered cigar is drawn by CigarItemRenderer.
     */
    private Vec3 firstPersonPosition(LivingEntity entity) {
        Vec3 lookVec = entity.getViewVector(1.0F);
        Vec3 rightVec = lookVec.cross(new Vec3(0, 1, 0)).normalize();
        Vec3 upVec = rightVec.cross(lookVec).normalize();

        return entity.getEyePosition(1.0F)
                .add(lookVec.scale(0.35))
                .add(rightVec.scale(0.30))
                .add(upVec.scale(-0.15));
    }

    //? if forge {
    /*/^* Forge 1.20.1 asks each item for its client extension; NeoForge registers it from an event. ^/
    @Override
    public void initializeClient(java.util.function.Consumer<net.neoforged.neoforge.client.extensions.common.IClientItemExtensions> consumer) {
        consumer.accept(frenk.eypipes.EyPipesClient.createCigarExtension());
    }
    *///?}
}
