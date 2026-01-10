package frenk.eypipes.block;

import frenk.eypipes.config.EyPipesConfig;
import frenk.eypipes.registries.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Erbapipa crop block - A 2-block tall crop with 8 growth stages.
 * Ported from Fabric 1.19.2 to NeoForge 1.21.1
 */
public class ErbapipaCropBlock extends CropBlock {

    public static final BooleanProperty UPPER = BooleanProperty.create("upper");
    public static final IntegerProperty AGE = IntegerProperty.create("age", 0, 7);

    // Voxel shapes for each growth stage (lower part)
    private static final VoxelShape[] SHAPE_BY_AGE = new VoxelShape[]{
            Block.box(0.0D, -1.0D, 0.0D, 16.0D, 3.0D, 16.0D),
            Block.box(0.0D, -1.0D, 0.0D, 16.0D, 7.0D, 16.0D),
            Block.box(0.0D, -1.0D, 0.0D, 16.0D, 11.0D, 16.0D),
            Block.box(0.0D, -1.0D, 0.0D, 16.0D, 15.0D, 16.0D),
            Block.box(0.0D, -1.0D, 0.0D, 16.0D, 15.0D, 16.0D),
            Block.box(0.0D, -1.0D, 0.0D, 16.0D, 15.0D, 16.0D),
            Block.box(0.0D, -1.0D, 0.0D, 16.0D, 15.0D, 16.0D),
            Block.box(0.0D, -1.0D, 0.0D, 16.0D, 15.0D, 16.0D)
    };

    // Voxel shapes for each growth stage (upper part)
    private static final VoxelShape[] UPPER_SHAPE_BY_AGE = new VoxelShape[]{
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 4.0D, 16.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 6.0D, 16.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 12.0D, 16.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 14.0D, 16.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 14.0D, 16.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 14.0D, 16.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 14.0D, 16.0D)
    };

    public ErbapipaCropBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(AGE, 0)
                .setValue(UPPER, false));
    }

    @Override
    protected IntegerProperty getAgeProperty() {
        return AGE;
    }

    @Override
    public int getMaxAge() {
        return 7;
    }

    @Override
    protected ItemLike getBaseSeedId() {
        return ModItems.ERBAPIPA_SEEDS.get();
    }

    /**
     * Check if the crop is at maximum age (mature).
     */
    public boolean isMature(BlockState state) {
        return getAge(state) >= getMaxAge();
    }

    public BooleanProperty getUpperProperty() {
        return UPPER;
    }

    public int getGrowUpperAge() {
        return EyPipesConfig.COMMON.growUpperAge.get();
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AGE, UPPER);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        int age = state.getValue(AGE);
        return state.getValue(UPPER) ? UPPER_SHAPE_BY_AGE[age] : SHAPE_BY_AGE[age];
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockPos belowPos = pos.below();
        BlockState belowState = level.getBlockState(belowPos);

        // Check if this is the upper part
        if (state.getValue(UPPER)) {
            if (belowState.is(this) && !belowState.getValue(UPPER)) {
                // Upper part can only exist above a lower part at appropriate age
                int belowAge = belowState.getValue(AGE);
                return belowAge >= getGrowUpperAge() &&
                       (level.getRawBrightness(pos, 0) >= 8 || level.canSeeSky(pos));
            }
            return false;
        }

        return super.canSurvive(state, level, pos);
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        // Only tick if not upper or not mature
        return !state.getValue(UPPER) || !isMature(state);
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!level.isAreaLoaded(pos, 1)) return;

        int age = getAge(state);
        float growthSpeed = getGrowthSpeed(state, level, pos);

        if (level.getRawBrightness(pos, 0) >= 9) {
            // Growth logic for the current block
            if (age < getMaxAge()) {
                float growthChance = EyPipesConfig.COMMON.growthChance.get().floatValue();
                float fertilizer = EyPipesConfig.COMMON.fertilizerEffectiveness.get().floatValue();

                if (random.nextFloat() < growthChance * fertilizer * growthSpeed / 25.0F) {
                    level.setBlock(pos, state.setValue(AGE, age + 1), 2);
                }
            }
        }

        // Don't grow upper part from upper block
        if (state.getValue(UPPER)) return;

        // Try to grow upper part
        if (age >= getGrowUpperAge()) {
            float growthChance = EyPipesConfig.COMMON.growthChance.get().floatValue();
            float fertilizer = EyPipesConfig.COMMON.fertilizerEffectiveness.get().floatValue();

            if (random.nextFloat() < growthChance * fertilizer * growthSpeed / 80.0F) {
                BlockPos abovePos = pos.above();
                BlockState defaultUpper = defaultBlockState().setValue(UPPER, true);

                if (defaultUpper.canSurvive(level, abovePos) && level.isEmptyBlock(abovePos)) {
                    level.setBlock(abovePos, defaultUpper, 3);
                }
            }
        }
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state) {
        BlockState aboveState = level.getBlockState(pos.above());

        // If there's an upper part, check if it's mature
        if (aboveState.is(this)) {
            return !isMature(aboveState);
        }

        // If this is the upper part, check if it's mature
        if (state.getValue(UPPER)) {
            return !isMature(state);
        }

        // Lower part without upper - always fertilizable
        return true;
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
        int newAge = Math.min(getAge(state) + getBonemealAgeIncrease(level), 15);

        if (newAge <= getMaxAge()) {
            level.setBlock(pos, state.setValue(AGE, newAge), 2);
        } else {
            level.setBlock(pos, state.setValue(AGE, getMaxAge()), 2);

            if (state.getValue(UPPER)) return;

            BlockPos abovePos = pos.above();
            BlockState aboveState = level.getBlockState(abovePos);

            if (aboveState.is(this)) {
                // Fertilize existing upper part
                BonemealableBlock growable = (BonemealableBlock) aboveState.getBlock();
                if (growable.isValidBonemealTarget(level, abovePos, aboveState)) {
                    growable.performBonemeal(level, random, abovePos, aboveState);
                }
            } else {
                // Create new upper part
                int remainingGrowth = newAge - getMaxAge() - 1;
                BlockState newUpper = defaultBlockState()
                        .setValue(UPPER, true)
                        .setValue(AGE, Math.max(0, Math.min(remainingGrowth, getMaxAge())));

                if (newUpper.canSurvive(level, abovePos) && level.isEmptyBlock(abovePos)) {
                    level.setBlock(abovePos, newUpper, 3);
                }
            }
        }
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock())) {
            boolean isUpper = state.getValue(UPPER);

            if (isUpper) {
                // Breaking upper part breaks lower part
                BlockPos belowPos = pos.below();
                BlockState belowState = level.getBlockState(belowPos);
                if (belowState.is(this) && !belowState.getValue(UPPER)) {
                    level.destroyBlock(belowPos, true);
                }
            } else {
                // Breaking lower part breaks upper part
                BlockPos abovePos = pos.above();
                BlockState aboveState = level.getBlockState(abovePos);
                if (aboveState.is(this) && aboveState.getValue(UPPER)) {
                    level.destroyBlock(abovePos, true);
                }
            }
        }

        super.onRemove(state, level, pos, newState, movedByPiston);
    }
}
