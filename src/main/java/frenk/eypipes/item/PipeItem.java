package frenk.eypipes.item;

import net.minecraft.stat.Stats;

import dev.emi.trinkets.api.TrinketItem;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.server.world.ServerWorld;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import frenk.eypipes.particles.EyPipesParticleTypes;
import frenk.eypipes.sound.EyPipesSound;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.network.GeckoLibNetwork;
import software.bernie.geckolib3.network.ISyncable;
import software.bernie.geckolib3.util.GeckoLibUtil;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import net.minecraft.client.render.model.json.ModelTransformation;
import java.util.concurrent.ThreadLocalRandom;

public class PipeItem extends TrinketItem implements IAnimatable, ISyncable {
    private static final Logger LOGGER = LoggerFactory.getLogger("EyPipesMod");
    private static final int SMOKE_ANIM_STATE = 0;
    private static final AnimationBuilder SMOKE_ANIM = new AnimationBuilder().addAnimation("animation.pipe.smoke", false);
    
    private final AnimationFactory factory = GeckoLibUtil.createFactory(this);
    private final int USAGE_TIME = 60;
    private static final String SMOKING_KEY = "smoking";
    
    private boolean isSmoking(ItemStack stack) {
        return stack.getOrCreateNbt().getBoolean(SMOKING_KEY);
    }
    
    private void setSmoking(ItemStack stack, boolean smoking) {
        stack.getOrCreateNbt().putBoolean(SMOKING_KEY, smoking);
    }
    
    @Override
    public void onCraft(ItemStack stack, World world, PlayerEntity player) {
        if (!world.isClient) {
            stack.setDamage(stack.getMaxDamage() - 1);
        }
    }

    public PipeItem(FabricItemSettings settings, int amountOfUse) {
        super(settings.maxDamage(amountOfUse));
        GeckoLibNetwork.registerSyncable(this);
    }

    @Override
    public int getItemBarColor(ItemStack stack) {
        return 0x01e81b0;
    }

    @Override
    public int getItemBarStep(ItemStack stack) {
        if (stack.isDamageable()) {
            return Math.round(13.0F - (float) stack.getDamage() * 13.0F / (float) stack.getMaxDamage());
        }
        return super.getItemBarStep(stack);
    }



    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        
        ItemStack itemStack = user.getStackInHand(hand);
        
        
        // Check for shift+right-click with erbapipa_cutted in off-hand for durability repair
        if (user.isSneaking() && hand == Hand.MAIN_HAND) {
            ItemStack offHandStack = user.getStackInHand(Hand.OFF_HAND);
            if (offHandStack.getItem() == EyPipesItems.ERBAPIPA_CUTTED && itemStack.isDamageable()) {
                // Check if pipe is already at maximum durability
                if (itemStack.getDamage() <= 1) {
                    return TypedActionResult.fail(itemStack);
                }
                
                // Increase durability by 1 (decrease damage by 1)
                int currentDamage = itemStack.getDamage();
                int newDamage = Math.max(0, currentDamage - 1);
                itemStack.setDamage(newDamage);
                
                // Consume one erbapipa_cutted
                if (!user.isCreative()) {
                    offHandStack.decrement(1);
                }
                
                // Play repair sound and set cooldown
                // world.playSound(null, user.getX(), user.getY(), user.getZ(), EyPipesSound.PIPE_REFILL, SoundCategory.PLAYERS, 1.0F, 1.2F); // Sound file missing
                user.getItemCooldownManager().set(this, 20);
                
                return TypedActionResult.success(itemStack);
            }
        }
        
        if (itemStack.isDamageable() && itemStack.getDamage() >= itemStack.getMaxDamage()) {
            ItemStack driedErbapipaStack = user.getInventory().main.stream()
                    .filter(stack -> stack.getItem() == EyPipesItems.ERBAPIPA_DRIED)
                    .findFirst()
                    .orElse(ItemStack.EMPTY);

            if (driedErbapipaStack.isEmpty() && !user.isCreative()) {
                return TypedActionResult.fail(itemStack);
            }

            driedErbapipaStack.decrement(1);
            itemStack.setDamage(0);
            ((PlayerEntity)user).getItemCooldownManager().set(this, 20);
            // world.playSound(null, user.getX(), user.getY(), user.getZ(), EyPipesSound.PIPE_REFILL, SoundCategory.PLAYERS, 1.0F, 1.0F); // Sound file missing
        }
        else{
            user.setCurrentHand(hand);
            setSmoking(itemStack, true);
            LOGGER.info("Pipe use started for player {}", user.getName().getString());
            if (!world.isClient) {
                LOGGER.info("Triggering smoke animation on server for stack {}", itemStack);
                triggerSmokeAnimation(itemStack, (ServerWorld)world, user);
            }
            user.incrementStat(Stats.USED.getOrCreateStat(this));
        }
        return TypedActionResult.consume(itemStack);
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (isSmoking(stack)) {
            ItemStack result = finishUsing(stack, world, user);
        }
        setSmoking(stack, false);
        ((PlayerEntity)user).getItemCooldownManager().set(this, 20);
    }

    

    
    
    

    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        
        if (isSmoking(stack)) {
            int totalTicks = USAGE_TIME - remainingUseTicks;
            int frequency = Math.max(4, (int) Math.pow((USAGE_TIME - totalTicks) / 10.0, 2));
            if (remainingUseTicks % frequency == 0) {
                // Spawn particles
                if (world.isClient) {
                    net.minecraft.client.MinecraftClient client = net.minecraft.client.MinecraftClient.getInstance();
                    boolean isFirstPerson = client.options.getPerspective().isFirstPerson();
                    if (isFirstPerson == true) {
                        Vec3d pipePosition[]  = calculateParticlePosition(user);
                        Vec3d correctPosition = pipePosition[0];
                        for(int i=0;i<10;i++)
                            world.addParticle(ParticleTypes.SMOKE, correctPosition.x + (world.random.nextGaussian() * 0.02), correctPosition.y + (world.random.nextGaussian() * 0.02), correctPosition.z + (world.random.nextGaussian() * 0.02), 0.001, 0.01, 0.001); 
                    }
                    else{
                        Vec3d pipePosition[]  = calculateParticlePosition(user);
                        Vec3d correctPosition = pipePosition[1];
                        for(int i=0;i<10;i++)
                            world.addParticle(ParticleTypes.SMOKE, correctPosition.x + (world.random.nextGaussian() * 0.02), correctPosition.y + (world.random.nextGaussian() * 0.02), correctPosition.z + (world.random.nextGaussian() * 0.02), 0.001, 0.01, 0.001);  
                    }
                }
                if (world instanceof ServerWorld serverWorld) {
                    // Server-side particle spawning for all nearby players
                    for (net.minecraft.server.network.ServerPlayerEntity player : serverWorld.getPlayers()) {
                        if (player != user && player.distanceTo(user) <= 32.0) {
                            // Calculate server-safe smoke position
                            Vec3d pipePosition[] = calculateParticlePosition(user);
                            Vec3d correctPosition = pipePosition[1];
                            serverWorld.spawnParticles(player, ParticleTypes.SMOKE,
                                    false, correctPosition.x + (world.random.nextGaussian() * 0.02), correctPosition.y + (world.random.nextGaussian() * 0.02), correctPosition.z + (world.random.nextGaussian() * 0.02),
                                    10, 0.001, 0.01, 0.001, 0.0);
                        }
                    }
                }
            }
        }
    }

    public ItemStack finishUsing(ItemStack item, World world, LivingEntity user){
        spawnSmoke(0, user, world);
        if (!world.isClient) {
            world.playSound(null, user.getX(), user.getY(), user.getZ(), EyPipesSound.PIPE_EXHALE, SoundCategory.PLAYERS, 0.8F, 1.0F);
            if (user instanceof PlayerEntity) {
                world.playSound((PlayerEntity) user, user.getBlockPos(), EyPipesSound.PIPE_EXHALE, SoundCategory.PLAYERS, 0.8F, 1.0F);
            }
        }
        item.setDamage(item.getDamage() + 1);
        setSmoking(item, false);
        ((PlayerEntity)user).getItemCooldownManager().set(this, 20);
        return item;
    }

    public int getMaxUseTime(ItemStack stack) {
        return USAGE_TIME;
    }

    public void spawnSmoke(int remainingUseTicks, LivingEntity user, World world){
        float f = (float) (USAGE_TIME - remainingUseTicks) / 600;
        
        if (world.isClient) { // Client-side particles for the user
            for (int i = 0; i < 3; i++) {
                final int particleIndex = i;
                final double offsetMultiplier = 0.4 + (particleIndex * 0.1);
                new Thread(() -> {
                    try {
                        Thread.sleep(particleIndex * 1000);
                        Vec3d vec = user.getRotationVec(1.0F);
                        world.addParticle(EyPipesParticleTypes.RING_OF_SMOKE,
                                user.getX() + vec.x * offsetMultiplier,
                                user.getY() + user.getEyeHeight(user.getPose()) + vec.y * offsetMultiplier,
                                user.getZ() + vec.z * offsetMultiplier,
                                vec.x * f, vec.y * f, vec.z * f);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }).start();
            }
        } else { // Server-side particles for all other players
            if (world instanceof ServerWorld serverWorld) {
                for (int i = 0; i < 3; i++) {
                    final int particleIndex = i;
                    final double offsetMultiplier = 0.4 + (particleIndex * 0.1);
                    new Thread(() -> {
                        try {
                            Thread.sleep(particleIndex * 1000);
                            for (net.minecraft.server.network.ServerPlayerEntity player : serverWorld.getPlayers()) {
                                if (player != user) {
                                    Vec3d vec = user.getRotationVec(1.0F);
                                    double userX = user.getX();
                                    double userY = user.getY() + user.getEyeHeight(user.getPose());
                                    double userZ = user.getZ();
                                    
                                    // Exclude the user who is smoking
                                    serverWorld.spawnParticles(player,EyPipesParticleTypes.RING_OF_SMOKE,
                                            false,
                                            userX + vec.x * offsetMultiplier,
                                            userY + vec.y * offsetMultiplier,
                                            userZ + vec.z * offsetMultiplier,
                                            0, vec.x * f, vec.y * f, vec.z * f, 1.0f);
                                }
                            }
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }).start();
                }
            }
        }
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.NONE;
    }

    public boolean isSmoking() {
        return false;
    }
    
    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController<>(this, "smokeController", 0, this::predicate));
    }
    
    private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
        event.getController().setAnimation(SMOKE_ANIM);
        return PlayState.CONTINUE;
    }
    
    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }
    
    @Override
    public void onAnimationSync(int id, int state) {
        if (state == SMOKE_ANIM_STATE) {
            final AnimationController<?> controller = GeckoLibUtil.getControllerForID(this.factory, id, "smokeController");
            if (controller != null) {
                controller.setAnimation(SMOKE_ANIM);
            }
        }
    }
    
    public void triggerSmokeAnimation(ItemStack stack, ServerWorld world, PlayerEntity player) {
        final int id = GeckoLibUtil.guaranteeIDForStack(stack, world);
        LOGGER.info("Syncing animation with ID {} and state {}", id, SMOKE_ANIM_STATE);
        GeckoLibNetwork.syncAnimation(player, this, id, SMOKE_ANIM_STATE);
    }

    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
        return false; // Disable all repair functionality for pipes
    }
    
    /**
     * Server-safe method to calculate smoke position without client dependencies.
     * Dynamically calculates the absolute world position of the rendered pipe each tick.
     * For third-person: Height is fixed and does not follow the player's vertical look direction.
     * For first-person: Uses full look direction to match head rotation naturally.
     */
    private Vec3d[] calculateParticlePosition(LivingEntity entity) {
        // Get current rotation vectors (updates each tick with player movement/rotation)
        Vec3d lookVec = entity.getRotationVec(1.0F);
        
        // Get current absolute world position (updates each tick with player movement)
        Vec3d basePos = new Vec3d(
            entity.getX(),
            entity.getY() + entity.getEyeHeight(entity.getPose()),
            entity.getZ()
        );
        
        // For third-person view: use horizontal-only vectors for fixed height
        Vec3d horizontalLookVec = new Vec3d(lookVec.x, 0, lookVec.z).normalize();
        Vec3d rightVecThird = new Vec3d(horizontalLookVec.z, 0, horizontalLookVec.x).normalize();
        Vec3d upVecThird = new Vec3d(0, 1, 0); // Fixed upward direction
        
        Vec3d resultThird = basePos
            .add(horizontalLookVec.multiply(0.5))
            .add(rightVecThird.multiply(0.2))
            .add(upVecThird.multiply(-0.2));
        
        // For first-person view: use full look direction for natural head tracking
        Vec3d rightVecFirst = lookVec.crossProduct(new Vec3d(0, 1, 0)).normalize();
        Vec3d upVecFirst = rightVecFirst.crossProduct(lookVec).normalize();
        
        Vec3d resultFirst = basePos
            .add(lookVec.multiply(0.4))
            .add(rightVecFirst.multiply(0.33))
            .add(upVecFirst.multiply(0.0));
            
        Vec3d[] results = new Vec3d[]{resultFirst, resultThird};
        return results;
    }
}