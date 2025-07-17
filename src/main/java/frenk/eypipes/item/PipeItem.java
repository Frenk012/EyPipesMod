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
import frenk.eypipes.particles.EyPipesParticleTypes;
import frenk.eypipes.sound.EyPipesSound;

public class PipeItem extends TrinketItem {
    private final int USAGE_TIME = 60;
    private boolean smoking = false;
    
    @Override
    public void onCraft(ItemStack stack, World world, PlayerEntity player) {
        if (!world.isClient) {
            stack.setDamage(stack.getMaxDamage() - 1);
        }
    }

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
            // world.playSound(null, user.getX(), user.getY(), user.getZ(), EyPipesSound.PIPE_IGNITE, SoundCategory.PLAYERS, 1.0F, 1.0F); // Sound file missing
            user.setCurrentHand(hand);
            this.smoking = true;
            user.incrementStat(Stats.USED.getOrCreateStat(this));
            itemStack.setDamage(itemStack.getDamage() + 1);
        }
        return TypedActionResult.consume(itemStack);
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (smoking) {
            finishUsing(stack, world, user);
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
            // Also try playing to the specific player
            if (user instanceof PlayerEntity) {
                world.playSound((PlayerEntity) user, user.getBlockPos(), EyPipesSound.PIPE_EXHALE, SoundCategory.PLAYERS, 0.8F, 1.0F);
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
        return this.smoking ? UseAction.TOOT_HORN : UseAction.NONE;
    }

    public boolean isSmoking() {
        return this.smoking;
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
            .add(lookVec.multiply(0.6))
            .add(rightVecFirst.multiply(0.53))
            .add(upVecFirst.multiply(0.0));
            
        Vec3d[] results = new Vec3d[]{resultFirst, resultThird};
        return results;
    }
}