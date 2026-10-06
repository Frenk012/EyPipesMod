package frenk.eypipes.block;

import frenk.eypipes.compat.Interactions;
//? if >=1.20.5
import com.mojang.serialization.MapCodec;
import frenk.eypipes.block.entity.TobaccoJarBlockEntity;
import frenk.eypipes.registries.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
//? if >=1.20.5 && <1.21.2 {
import net.minecraft.world.ItemInteractionResult;
//?} else
//import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

/**
 * Tobacco Jar block - A container for storing and fermenting herbs.
 * Dried herbs placed inside will gradually ferment, increasing their quality.
 * Fermentation takes time: 1 MC day for Aged, 3 MC days for Fermented.
 */
public class TobaccoJarBlock extends BaseEntityBlock {

    //? if >=1.20.5
    public static final MapCodec<TobaccoJarBlock> CODEC = simpleCodec(TobaccoJarBlock::new);

    // Fill level from 0 (empty) to 6 (full)
    public static final IntegerProperty FILL_LEVEL = IntegerProperty.create("fill_level", 0, 6);

    // Jar shape - slightly smaller than a full block
    private static final VoxelShape SHAPE = Block.box(3, 0, 3, 13, 12, 13);

    public TobaccoJarBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FILL_LEVEL, 0));
    }

    //? if >=1.20.5 {
    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }
    //?}

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FILL_LEVEL);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TobaccoJarBlockEntity(pos, state);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    //? if <1.20.5 {
    /*public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        ItemStack stack = player.getItemInHand(hand);
    *///?} elif <1.21.2 {
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
    //?} else
    //protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (level.isClientSide()) {
            return Interactions.itemSuccess();
        }

        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof TobaccoJarBlockEntity tobaccoJar)) {
            return Interactions.itemFail();
        }

        ItemStack heldItem = player.getItemInHand(hand);

        // If player is holding a fermentable item, try to insert it
        if (!heldItem.isEmpty() && TobaccoJarBlockEntity.canFerment(heldItem)) {
            if (tobaccoJar.insertItem(heldItem)) {
                // Decrement the held item
                heldItem.shrink(1);

                // Update block state for fill level
                updateFillLevel(level, pos, tobaccoJar);

                return Interactions.itemConsume();
            }
        }
        // If player's hand is empty or holding non-fermentable item, try to remove an item
        else if (heldItem.isEmpty()) {
            ItemStack removed = tobaccoJar.removeFirstItem();
            if (!removed.isEmpty()) {
                if (!player.getInventory().add(removed)) {
                    player.drop(removed, false);
                }

                // Update block state for fill level
                updateFillLevel(level, pos, tobaccoJar);

                return Interactions.itemSuccess();
            }
        }

        return Interactions.itemPassToBlock();
    }

    private void updateFillLevel(Level level, BlockPos pos, TobaccoJarBlockEntity tobaccoJar) {
        int fillLevel = tobaccoJar.getItemCount();
        BlockState currentState = level.getBlockState(pos);
        if (currentState.getValue(FILL_LEVEL) != fillLevel) {
            level.setBlock(pos, currentState.setValue(FILL_LEVEL, fillLevel), Block.UPDATE_ALL);
        }
        tobaccoJar.setChanged();
        level.sendBlockUpdated(pos, currentState, level.getBlockState(pos), Block.UPDATE_ALL);
    }

    //? if <1.21.5 {
    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock())) {
            if (level.getBlockEntity(pos) instanceof TobaccoJarBlockEntity tobaccoJar) {
                tobaccoJar.dropContents(level, pos);
            }
            super.onRemove(state, level, pos, newState, movedByPiston);
        }
    }
    //?}

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        // Tick on both sides: server for fermentation, client for ambient particles
        return createTickerHelper(type, ModBlockEntities.TOBACCO_JAR.get(), TobaccoJarBlockEntity::tick);
    }
}
