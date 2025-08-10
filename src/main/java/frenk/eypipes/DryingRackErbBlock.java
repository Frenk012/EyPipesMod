package frenk.eypipes;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.block.BlockRenderType;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.Direction;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.util.ItemScatterer;

import frenk.eypipes.item.EyPipesItems;

public class DryingRackErbBlock extends BlockWithEntity {

    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;

    public DryingRackErbBlock(Settings settings) {
        super(settings);
        setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> stateManager) {
        stateManager.add(FACING);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getPlayerFacing().getOpposite());
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new DryingRackErbBlockEntity(pos, state);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient) return ActionResult.SUCCESS;

        DryingRackErbBlockEntity entity = (DryingRackErbBlockEntity) world.getBlockEntity(pos);
        ItemStack heldItem = player.getStackInHand(hand);

        if (entity == null) return ActionResult.FAIL;

        // If player is holding erbapipa, try to insert it
        if (heldItem.getItem() == EyPipesItems.ERBAPIPA) {
            if (entity.insertItem(heldItem)) {
                
                // Decrement the held item
                heldItem.decrement(1);
                
                // Update clients
                world.updateListeners(pos, state, state, Block.NOTIFY_ALL);
                
                return ActionResult.CONSUME;
            }
        }
        // If player's hand is empty, try to remove an item
        else if (heldItem.isEmpty()) {
            ItemStack removed = entity.removeFirstItem();
            if (!removed.isEmpty()) {
                if (!player.getInventory().insertStack(removed)) {
                    player.dropItem(removed, false);
                }
                
                // Update clients
                world.updateListeners(pos, state, state, Block.NOTIFY_ALL);
                
                return ActionResult.SUCCESS;
            }
        }
        
        return ActionResult.PASS;
    }
    
    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL; // così viene usato il modello JSON
    }
    
    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.isOf(newState.getBlock())) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof DryingRackErbBlockEntity dryingRack) {
                // Drop all items stored in the drying rack
                for (int i = 0; i < dryingRack.getAllItems().length; i++) {
                    ItemStack stack = dryingRack.getAllItems()[i];
                    if (!stack.isEmpty()) {
                        ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(), stack);
                    }
                }
            }
            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }
    
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, PipesEntities.DRYING_RACK_ENTITY, DryingRackErbBlockEntity::tick);
    }
}
