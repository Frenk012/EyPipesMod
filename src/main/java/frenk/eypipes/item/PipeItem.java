package frenk.eypipes.item;

import net.minecraft.stat.Stats;

import net.minecraft.client.MinecraftClient;
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
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.server.world.ServerWorld;
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
            // world.playSound(null, user.getX(), user.getY(), user.getZ(), EyPipesSound.PIPE_IGNITE, SoundCategory.PLAYERS, 1.0F, 1.0F); // Sound file missing
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
                // Spawn particles on server side so all players can see them
                if (!world.isClient) {
                    // Spawn particles at configurable offset relative to player's face orientation
                    Vec3d lookVec = user.getRotationVec(1.0F);
                    
                    // Create local coordinate system relative to player's face
                    Vec3d rightVec = new Vec3d(-lookVec.z, 0, lookVec.x).normalize(); // Right vector (perpendicular to look direction)
                    Vec3d upVec = rightVec.crossProduct(lookVec).normalize(); // Up vector (perpendicular to both)
                    
                    // Base position at player's eye level
                    Vec3d basePos = new Vec3d(user.getX(), user.getY() + user.getEyeHeight(user.getPose()), user.getZ());
                    
                    
                    // Apply configurable offsets in local coordinate system:
                    // PARTICLE_OFFSET_X: forward/backward (positive = forward)
                    // PARTICLE_OFFSET_Y: right/left (positive = right)
                    // PARTICLE_OFFSET_Z: up/down (positive = up)
                    Vec3d offsetPos = basePos
                        .add(lookVec.multiply(EyPipesConfig.PARTICLE_OFFSET_X))  // Forward/backward offset
                        .add(rightVec.multiply(EyPipesConfig.PARTICLE_OFFSET_Y)) // Right/left offset
                        .add(upVec.multiply(EyPipesConfig.PARTICLE_OFFSET_Z));   // Up/down offset
                    
                    // Add random variation
                    double particleX = offsetPos.x + (world.random.nextGaussian() * 0.02);
                    double particleY = offsetPos.y + (world.random.nextGaussian() * 0.02);
                    double particleZ = offsetPos.z + (world.random.nextGaussian() * 0.02);
                    
                    // Use spawnParticles for server-side particle spawning that all players can see
                    ((ServerWorld) world).spawnParticles(ParticleTypes.SMOKE,
                            particleX, particleY, particleZ,
                            20, // particle count
                            0.001, 0.01, 0.001, // velocity
                            0.0 // speed
                            );
                    
                }
            }
        }
    }

    public ItemStack finishUsing(ItemStack item, World world, LivingEntity user){
        spawnSmoke(0, user, world);
        
        // Play exhale sound when finishing smoking (server-side only)
        if (!world.isClient) {
            System.out.println("[EyPipes] Playing exhale sound at: " + user.getX() + ", " + user.getY() + ", " + user.getZ());
            world.playSound(null, user.getX(), user.getY(), user.getZ(), EyPipesSound.PIPE_EXHALE, SoundCategory.MASTER, 1.0F, 1.0F);
            // Also try playing to the specific player
            if (user instanceof PlayerEntity) {
                world.playSound((PlayerEntity) user, user.getBlockPos(), EyPipesSound.PIPE_EXHALE, SoundCategory.MASTER, 1.0F, 1.0F);
            }
        }

        this.smoking = false;
        ((PlayerEntity)user).getItemCooldownManager().set(this, 20);
        return item;
    }

    public int getMaxUseTime(ItemStack stack, LivingEntity user) {
        return USAGE_TIME;
    }

    public void spawnSmoke(int remainingUseTicks, LivingEntity user, World world){
        float f = (float) (USAGE_TIME - remainingUseTicks) / 600;
        
        if (world.isClient) { // Client-side particles for the user
            // Method 2: Spawn particles with scheduled delays using separate threads
            for (int i = 0; i < 3; i++) {
                final int particleIndex = i;
                final double offsetMultiplier = 0.4 + (particleIndex * 0.1); // Slight position variation
                
                // Schedule each particle with increasing delay (0ms, 5ms, 10ms)
                new Thread(() -> {
                    try {
                        Thread.sleep(particleIndex * 1000);
                        Vec3d vec = user.getRotationVec(1.0F);
                        final double vecX = vec.x;
                        final double vecY = vec.y;
                        final double vecZ = vec.z;

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
        } else { // Server-side particles for all other players
            // Spawn ring of smoke particles with same timing as client-side but exclude the client player
            for (int i = 0; i < 3; i++) {
                final int particleIndex = i;
                final double offsetMultiplier = 0.4 + (particleIndex * 0.1); // Same position variation as client
                
                // Schedule each particle with same delay pattern (0ms, 1000ms, 2000ms)
                new Thread(() -> {
                    try {
                        Thread.sleep(particleIndex * 1000);
                        Vec3d vec = user.getRotationVec(1.0F);
                        final double vecX = vec.x;
                        final double vecY = vec.y;
                        final double vecZ = vec.z;

                        double particleX = user.getX() + vecX * offsetMultiplier;
                        double particleY = user.getY() + user.getEyeHeight(user.getPose()) + vecY * offsetMultiplier;
                        double particleZ = user.getZ() + vecZ * offsetMultiplier;
                        
                        // Spawn particles for all players except the client player
                        ServerWorld serverWorld = (ServerWorld) world;
                        for (net.minecraft.server.network.ServerPlayerEntity player : serverWorld.getPlayers()) {
                            if (player != user) { // Exclude the user who is smoking
                                serverWorld.spawnParticles(player, EyPipesParticleTypes.RING_OF_SMOKE,
                                        false, // don't force spawn
                                        particleX, particleY, particleZ,
                                        1, // particle count
                                        vecX * f, vecY * f, vecZ * f, // velocity
                                        0.1 // speed
                                        );
                            }
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }).start();
            }
        }
        
        // Sound is now played in finishUsing method instead of here to avoid duplication
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return this.smoking ? UseAction.TOOT_HORN : UseAction.NONE;
    }

    public boolean isSmoking() {
        return this.smoking;
    }
}