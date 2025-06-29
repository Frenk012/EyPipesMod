package frenk.eypipes.item;

import net.minecraft.stat.Stats;

import dev.emi.trinkets.api.TrinketItem;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import frenk.eypipes.config.EyPipesConfig;
import frenk.eypipes.particles.EyPipesParticleTypes;
import frenk.eypipes.sound.EyPipesSound;

public class PipeItem extends TrinketItem {
    private final int USAGE_TIME = 60;
    private boolean smoking = false;
    
    public PipeItem(FabricItemSettings settings, int amountOfUse) {
        super(settings.maxDamage(amountOfUse));
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
                // Increase durability by 10 (decrease damage by 10)
                int currentDamage = itemStack.getDamage();
                int newDamage = Math.max(0, currentDamage - 10);
                itemStack.setDamage(newDamage);
                
                // Consume one erbapipa_cutted
                if (!user.isCreative()) {
                    offHandStack.decrement(1);
                }
                
                // Play repair sound and set cooldown
                world.playSound(null, user.getX(), user.getY(), user.getZ(), EyPipesSound.PIPE_REFILL, SoundCategory.PLAYERS, 1.0F, 1.2F);
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
            world.playSound(null, user.getX(), user.getY(), user.getZ(), EyPipesSound.PIPE_REFILL, SoundCategory.PLAYERS, 1.0F, 1.0F);
        }
        else{
            world.playSound(null, user.getX(), user.getY(), user.getZ(), EyPipesSound.PIPE_IGNITE, SoundCategory.PLAYERS, 1.0F, 1.0F);
            user.setCurrentHand(hand);
            this.smoking = true;
            user.incrementStat(Stats.USED.getOrCreateStat(this));
            itemStack.setDamage(itemStack.getDamage() + 10);
        }
        return TypedActionResult.consume(itemStack);
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        // add a particle of smoke traveling away from the player "a final breath" -- froosty
        if(smoking && remainingUseTicks < USAGE_TIME / 2){
            spawnSmoke(remainingUseTicks, user, world);
        }
        this.smoking = false;
        ((PlayerEntity)user).getItemCooldownManager().set(this, 20);
    }

    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        if (smoking) {
            int totalTicks = USAGE_TIME - remainingUseTicks;
            int frequency = Math.max(4, (int) Math.pow((USAGE_TIME - totalTicks) / 10.0, 2));
            if (remainingUseTicks % frequency == 0) {
                // Spawn particles on client side to sync with visual animation
                if (world.isClient) {
                    // Calculate animated pipe position based on use time and animation progress
                    Vec3d pipePosition = calculateAnimatedPipePosition(user, remainingUseTicks);
                    
                    // Spawn particles directly on client
                    for (int i = 0; i < 10; i++) {
                        world.addParticle(
                                ParticleTypes.SMOKE,
                                pipePosition.x + (world.random.nextGaussian() * 0.02),
                                pipePosition.y + (world.random.nextGaussian() * 0.02),
                                pipePosition.z + (world.random.nextGaussian() * 0.02),
                                0, // Velocity X
                                0.01, // Velocity Y (slight upward drift)
                                0 // Velocity Z
                        );
                    }
                }
            }
        }
    }

    public ItemStack finishUsing(ItemStack item, World world, LivingEntity user){
        spawnSmoke(0, user, world);

        this.smoking = false;
        ((PlayerEntity)user).getItemCooldownManager().set(this, 20);
        return item;
    }

    public int getMaxUseTime(ItemStack stack, LivingEntity user) {
        return USAGE_TIME;
    }

    public void spawnSmoke(int remainingUseTicks, LivingEntity user, World world){
        if (world.isClient) { // Only spawn particles on client side
            float f = (float) (USAGE_TIME - remainingUseTicks) / 600;
            Vec3d vec = user.getRotationVec(1.0F);
            
            // Method 2: Spawn particles with scheduled delays using separate threads
            for (int i = 0; i < 3; i++) {
                final int particleIndex = i;
                final double offsetMultiplier = 0.4 + (particleIndex * 0.1); // Slight position variation
                final double vecX = vec.x;
                final double vecY = vec.y;
                final double vecZ = vec.z;
                
                // Schedule each particle with increasing delay (0ms, 5ms, 10ms)
                new Thread(() -> {
                    try {
                        Thread.sleep(particleIndex * 1000);
                        
                        world.addParticle(EyPipesParticleTypes.RING_OF_SMOKE,
                                user.getX() + vecX * offsetMultiplier,
                                user.getY() + user.getEyeHeight(user.getPose()) + vecY * offsetMultiplier,
                                user.getZ() + vecZ * offsetMultiplier,
                                vecX * f, vecY * f, vecZ * f);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }).start();
            }
        }
        
        // Sound should be played on server side
        if (!world.isClient) {
            //https://pixabay.com/service/license-summary/
            //https://pixabay.com//?utm_source=link-attribution&utm_medium=referral&utm_campaign=music&utm_content=106654"
            world.playSound(null, user.getX(), user.getY(), user.getZ(), EyPipesSound.PIPE_EXHALE, SoundCategory.PLAYERS, 1.0F, 1.0F);
        }
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return this.smoking ? UseAction.TOOT_HORN : UseAction.NONE;
    }

    public boolean isSmoking() {
        return this.smoking;
    }
    
    /**
     * Calculates the animated pipe tip position based on the current animation state
     * This mirrors the animation logic from PipeItemRenderer to ensure particles
     * spawn at the correct position relative to the pipe's movement
     */
    private Vec3d calculateAnimatedPipePosition(LivingEntity user, int remainingUseTicks) {
        // Calculate animation progress (same logic as in PipeItemRenderer)
        int useTime = USAGE_TIME - remainingUseTicks;
        float animationProgress = 0.0f;
        
        // Only start animation after a brief delay (5 ticks) to match renderer
        if (useTime > 5) {
            animationProgress = Math.min((useTime - 5) / 20.0f, 1.0f); // 20 ticks = 1 second for full animation
        }
    
        // Apply smooth animation curve (same as renderer)
        float smoothProgress = MathHelper.sin(animationProgress * (float) Math.PI * (float) EyPipesConfig.FIRST_PERSON_CURVE_MULTIPLIER);
        

        
        // Start from user's eye position
        Vec3d eyePos = new Vec3d(user.getX(), user.getY() + user.getEyeHeight(user.getPose()), user.getZ());
        
        // Get user's rotation vectors for coordinate system transformation
        Vec3d lookVec = user.getRotationVec(1.0F);
        Vec3d rightVec = new Vec3d(-lookVec.z, 0, lookVec.x).normalize(); // Right vector (perpendicular to look direction)
        Vec3d upVec = rightVec.crossProduct(lookVec).normalize(); // Up vector (perpendicular to both)
        
        // Calculate the pipe tip position with animation
        // The renderer applies both translation AND rotation, so we need to account for both
        
        // Base pipe position (without animation) - pipe extends forward from the hand
        double basePipeLength = EyPipesConfig.PIPE_LENGTH; // Length from hand to tip (configurable)
        Vec3d baseTipPos = eyePos.add(lookVec.multiply(basePipeLength));
        
        // Apply animation translation offset (convert model space translation to world space)
        // Model space: X=right, Y=up, Z=forward (towards face)
        // Adjust coordinate mapping to fix "too high and too left" issue
        Vec3d translationOffset = rightVec.multiply(-EyPipesConfig.FIRST_PERSON_X_TRANSLATION * smoothProgress) // Invert X to fix "too left"
                                 .add(upVec.multiply(-EyPipesConfig.FIRST_PERSON_Y_TRANSLATION * smoothProgress)) // Invert Y to fix "too high"
                                 .add(lookVec.multiply(EyPipesConfig.FIRST_PERSON_Z_TRANSLATION * smoothProgress));
        
        // Apply rotation effects if rotation is enabled
        Vec3d rotationOffset = Vec3d.ZERO;
        if (EyPipesConfig.FIRST_PERSON_ENABLE_ROTATION) {
            // Calculate how rotation affects the tip position
            // The pipe rotates around its base (hand position), so we need to calculate where the tip ends up
            
            // Convert rotation angles to radians
            double xRotRad = Math.toRadians(EyPipesConfig.FIRST_PERSON_X_ROTATION * smoothProgress);
            double yRotRad = Math.toRadians(EyPipesConfig.FIRST_PERSON_Y_ROTATION * smoothProgress);
            double zRotRad = Math.toRadians(EyPipesConfig.FIRST_PERSON_Z_ROTATION * smoothProgress);
            
            // Calculate the rotational displacement of the pipe tip
            // This is an approximation of how the tip moves due to rotation around the hand
            Vec3d rotationDisplacement = new Vec3d(
                basePipeLength * Math.sin(yRotRad), // Y rotation affects X displacement (inverted)
                basePipeLength * Math.sin(xRotRad), // X rotation affects Y displacement (inverted)
                basePipeLength * Math.sin(zRotRad) // Z rotation affects Z displacement
            );
            
            // Transform rotation displacement to world space
            rotationOffset = rightVec.multiply(rotationDisplacement.x)
                           .add(upVec.multiply(rotationDisplacement.y))
                           .add(lookVec.multiply(rotationDisplacement.z));
        }
        
        return baseTipPos.add(translationOffset).add(rotationOffset);
    }
}