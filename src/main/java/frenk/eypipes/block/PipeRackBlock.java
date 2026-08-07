package frenk.eypipes.block;

import frenk.eypipes.compat.Interactions;
import com.mojang.serialization.MapCodec;
import frenk.eypipes.block.entity.PipeRackBlockEntity;
import frenk.eypipes.registries.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
//? if <1.21.2 {
import net.minecraft.world.ItemInteractionResult;
//?} else
/*import net.minecraft.world.InteractionResult;*/
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

/**
 * Pipe Rack block - A wall-mounted display for pipes.
 * Can hold up to 4 pipes which are rendered on the rack.
 */
public class PipeRackBlock extends BaseEntityBlock {

    public static final MapCodec<PipeRackBlock> CODEC = simpleCodec(PipeRackBlock::new);
    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;

    // Shapes for different orientations - thin rack against wall
    private static final VoxelShape SHAPE_NORTH = Block.box(0, 2, 14, 16, 14, 16);
    private static final VoxelShape SHAPE_SOUTH = Block.box(0, 2, 0, 16, 14, 2);
    private static final VoxelShape SHAPE_EAST = Block.box(0, 2, 0, 2, 14, 16);
    private static final VoxelShape SHAPE_WEST = Block.box(14, 2, 0, 16, 14, 16);

    public PipeRackBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(FACING)) {
            case SOUTH -> SHAPE_SOUTH;
            case EAST -> SHAPE_EAST;
            case WEST -> SHAPE_WEST;
            default -> SHAPE_NORTH;
        };
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new PipeRackBlockEntity(pos, state);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    //? if <1.21.2 {
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
    //?} else
    /*protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {*/
        if (level.isClientSide()) {
            return Interactions.itemSuccess();
        }

        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof PipeRackBlockEntity pipeRack)) {
            return Interactions.itemFail();
        }

        ItemStack heldItem = player.getItemInHand(hand);

        // If player is holding a pipe, try to insert it
        if (!heldItem.isEmpty() && PipeRackBlockEntity.isPipeItem(heldItem)) {
            if (pipeRack.insertItem(heldItem)) {
                // Decrement the held item
                heldItem.shrink(1);

                // Update clients
                level.sendBlockUpdated(pos, state, state, Block.UPDATE_ALL);
                pipeRack.setChanged();

                return Interactions.itemConsume();
            }
        }
        // If player's hand is empty, try to remove an item
        else if (heldItem.isEmpty()) {
            ItemStack removed = pipeRack.removeFirstItem();
            if (!removed.isEmpty()) {
                if (!player.getInventory().add(removed)) {
                    player.drop(removed, false);
                }

                // Update clients
                level.sendBlockUpdated(pos, state, state, Block.UPDATE_ALL);
                pipeRack.setChanged();

                return Interactions.itemSuccess();
            }
        }

        return Interactions.itemPassToBlock();
    }

    //? if <1.21.5 {
    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock())) {
            if (level.getBlockEntity(pos) instanceof PipeRackBlockEntity pipeRack) {
                pipeRack.dropContents(level, pos);
            }
            super.onRemove(state, level, pos, newState, movedByPiston);
        }
    }
    //?}
}
