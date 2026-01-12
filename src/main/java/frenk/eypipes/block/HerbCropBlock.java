package frenk.eypipes.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.function.Supplier;

/**
 * Generic herb crop block - A single-block crop with 4 growth stages.
 * Used for Valeriana, Ginseng, and Salvia plants.
 */
public class HerbCropBlock extends CropBlock {

    public static final IntegerProperty AGE = IntegerProperty.create("age", 0, 3);

    // Voxel shapes for each growth stage
    private static final VoxelShape[] SHAPE_BY_AGE = new VoxelShape[]{
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D),   // Stage 0
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 6.0D, 16.0D),   // Stage 1
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 10.0D, 16.0D),  // Stage 2
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 14.0D, 16.0D)   // Stage 3 (mature)
    };

    private final Supplier<? extends ItemLike> seedSupplier;

    public HerbCropBlock(Properties properties, Supplier<? extends ItemLike> seedSupplier) {
        super(properties);
        this.seedSupplier = seedSupplier;
        this.registerDefaultState(this.stateDefinition.any().setValue(AGE, 0));
    }

    @Override
    protected IntegerProperty getAgeProperty() {
        return AGE;
    }

    @Override
    public int getMaxAge() {
        return 3;
    }

    @Override
    protected ItemLike getBaseSeedId() {
        return seedSupplier.get();
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AGE);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE_BY_AGE[state.getValue(AGE)];
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!level.isAreaLoaded(pos, 1)) return;

        if (level.getRawBrightness(pos, 0) >= 9) {
            int age = getAge(state);
            if (age < getMaxAge()) {
                float growthSpeed = getGrowthSpeed(state, level, pos);
                if (random.nextFloat() < 0.15F * growthSpeed / 25.0F) {
                    level.setBlock(pos, state.setValue(AGE, age + 1), 2);
                }
            }
        }
    }
}
