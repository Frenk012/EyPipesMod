package frenk.eypipes.block;

import frenk.eypipes.item.EyPipesItems;
import frenk.eypipes.config.EyPipesConfig;
import net.minecraft.block.*;
import net.minecraft.item.ItemConvertible;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public class ErbapipaCropBlock extends CropBlock {

    public static final BooleanProperty UPPER = BooleanProperty.of("upper");

    private static final VoxelShape[] SHAPE_TO_AGE = new VoxelShape[] {
            Block.createCuboidShape(0.0D, -1.0D, 0.0D, 16.0D, 3.0D, 16.0D),
            Block.createCuboidShape(0.0D, -1.0D, 0.0D, 16.0D, 7.0D, 16.0D),
            Block.createCuboidShape(0.0D, -1.0D, 0.0D, 16.0D, 11.0D, 16.0D),
            Block.createCuboidShape(0.0D, -1.0D, 0.0D, 16.0D, 15.0D, 16.0D),
            Block.createCuboidShape(0.0D, -1.0D, 0.0D, 16.0D, 15.0D, 16.0D),
            Block.createCuboidShape(0.0D, -1.0D, 0.0D, 16.0D, 15.0D, 16.0D),
            Block.createCuboidShape(0.0D, -1.0D, 0.0D, 16.0D, 15.0D, 16.0D),
            Block.createCuboidShape(0.0D, -1.0D, 0.0D, 16.0D, 15.0D, 16.0D)
    };

    private static final VoxelShape[] UPPER_SHAPE_TO_AGE = new VoxelShape[]{
            Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 4.0D, 16.0D),
            Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 6.0D, 16.0D),
            Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D),
            Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 12.0D, 16.0D),
            Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 14.0D, 16.0D),
            Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 14.0D, 16.0D),
            Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 14.0D, 16.0D),
            Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 14.0D, 16.0D)
    };

    public  ErbapipaCropBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getStateManager().getDefaultState().with(AGE, 0).with(UPPER, false));
    }

    @Override
    protected ItemConvertible getSeedsItem() {
        return EyPipesItems.ERBAPIPA_SEEDS;
    }

    public BooleanProperty getUpperProperty() {
        return UPPER;
    }

    public int getGrowUpperAge() {
        return 4;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(AGE, UPPER);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return state.get(UPPER) ? UPPER_SHAPE_TO_AGE[state.get(this.getAgeProperty())] : SHAPE_TO_AGE[state.get(this.getAgeProperty())];
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        BlockPos downpos = pos.down();
        if (world.getBlockState(downpos).isOf(this) && state.get(this.getUpperProperty()))
            return !world.getBlockState(downpos).get(this.getUpperProperty())
                    && (world.getBaseLightLevel(pos, 0) >= 8 || world.isSkyVisible(pos))
                    && this.getAge(world.getBlockState(downpos)) >= this.getGrowUpperAge();
        return super.canPlaceAt(state, world, pos);
    }

    @Override
    public boolean hasRandomTicks(BlockState state) {
        return !state.get(this.getUpperProperty()) || !this.isMature(state);
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        int age = this.getAge(state);
        float f = getAvailableMoisture(this, world, pos);
        if (world.getBaseLightLevel(pos, 0) >= 9) {
            if (age < this.getMaxAge()) {
                if (random.nextFloat() < EyPipesConfig.GROWTH_CHANCE * EyPipesConfig.FERTILIZER_EFFECTIVENESS * f / 25.0F) {
                    world.setBlockState(pos, this.withAge(age + 1).with(this.getUpperProperty(), state.get(this.getUpperProperty())), 2);
                }
            }
        }
        if (state.get(this.getUpperProperty()))
            return;
        if (age >= EyPipesConfig.GROW_UPPER_AGE) {
            if (random.nextFloat() < EyPipesConfig.GROWTH_CHANCE * EyPipesConfig.FERTILIZER_EFFECTIVENESS * f / 80.0F) {
                if (this.getDefaultState().with(this.getUpperProperty(), true).canPlaceAt(world, pos.up()) && world.isAir(pos.up())) {
                    world.setBlockState(pos.up(), this.getDefaultState().with(this.getUpperProperty(), true));
                }
            }
        }
    }

    @Override
    public boolean isFertilizable(BlockView world, BlockPos pos, BlockState state, boolean isClient) {
        BlockState upperState = world.getBlockState(pos.up());
        if (upperState.isOf(this)) {
            return !(this.isMature(upperState));
        }
        if (state.get(this.getUpperProperty())) {
            return !(this.isMature(state));
        }
        return true;
    }

    @Override
    public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
        int ageGrowth = Math.min(this.getAge(state) + this.getGrowthAmount(world), 15);
        if (ageGrowth <= this.getMaxAge()) {
            world.setBlockState(pos, state.with(AGE, ageGrowth));
        }
        else {
            world.setBlockState(pos, state.with(AGE, this.getMaxAge()));
            if (state.get(this.getUpperProperty())) {
                return;
            }
            BlockState top = world.getBlockState(pos.up());
            if (top.isOf(this)) {
                Fertilizable growable = (Fertilizable) top.getBlock();
                if (growable.isFertilizable(world, pos.up(), top, false)) {
                    growable.grow(world, world.random, pos.up(), top);
                }
            }
            else {
                int remainingGrowth = ageGrowth - this.getMaxAge() - 1;
                if (this.getDefaultState().canPlaceAt(world, pos.up()) && world.isAir(pos.up())) {
                    world.setBlockState(pos.up(), this.getDefaultState()
                            .with(this.getUpperProperty(), true)
                            .with(this.getAgeProperty(), remainingGrowth), 3);
                }
            }
        }
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.isOf(newState.getBlock())) {
            boolean isUpper = state.get(UPPER);
            
            if (isUpper) {
                // If upper part is broken, break the lower part
                BlockPos lowerPos = pos.down();
                BlockState lowerState = world.getBlockState(lowerPos);
                if (lowerState.isOf(this) && !lowerState.get(UPPER)) {
                    world.breakBlock(lowerPos, true);
                }
            } else {
                // If lower part is broken, break the upper part
                BlockPos upperPos = pos.up();
                BlockState upperState = world.getBlockState(upperPos);
                if (upperState.isOf(this) && upperState.get(UPPER)) {
                    world.breakBlock(upperPos, true);
                }
            }
        }
        
        super.onStateReplaced(state, world, pos, newState, moved);
    }
}
