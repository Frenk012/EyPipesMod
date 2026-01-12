package frenk.eypipes.block;

import com.mojang.serialization.MapCodec;
import frenk.eypipes.block.entity.CuttingBoardBlockEntity;
import frenk.eypipes.item.KnifeItem;
import frenk.eypipes.registries.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
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
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

/**
 * Herb Cutting Table block - A tall furniture piece for cutting dried herbs.
 * Place dried herbs on top, then right-click with a knife to cut them.
 * After cutting, the leaf stays stuck on the table until removed.
 * Preserves fermentation properties when cutting.
 */
public class CuttingBoardBlock extends BaseEntityBlock {

    public static final MapCodec<CuttingBoardBlock> CODEC = simpleCodec(CuttingBoardBlock::new);
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    // Tall furniture shape - almost a full block (14 pixels tall)
    private static final VoxelShape SHAPE = Block.box(1, 0, 1, 15, 14, 15);

    public CuttingBoardBlock(Properties properties) {
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

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CuttingBoardBlockEntity(pos, state);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                               Player player, InteractionHand hand, BlockHitResult hitResult) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof CuttingBoardBlockEntity cuttingBoard)) {
            return ItemInteractionResult.FAIL;
        }

        ItemStack heldItem = player.getItemInHand(hand);

        // If holding a knife and there's an item on the table, cut it
        if (heldItem.getItem() instanceof KnifeItem && !cuttingBoard.isEmpty()) {
            if (!level.isClientSide()) {
                ItemStack result = cuttingBoard.cutItem();
                if (result != null) {
                    // Damage the knife
                    heldItem.hurtAndBreak(1, player, player.getEquipmentSlotForItem(heldItem));

                    // Give the result to the player
                    if (!player.getInventory().add(result)) {
                        player.drop(result, false);
                    }

                    // Play cutting sound
                    level.playSound(null, pos, ModSounds.KNIFE_CUT.get(), SoundSource.BLOCKS, 1.0f, 1.0f);
                }
            } else {
                // Client-side: spawn leaf particles
                spawnCuttingParticles(level, pos);
            }
            return ItemInteractionResult.sidedSuccess(level.isClientSide());
        }

        // If holding a cuttable item (dried herb) and board is empty, place it
        if (CuttingBoardBlockEntity.canBeCut(heldItem) && cuttingBoard.isEmpty()) {
            if (!level.isClientSide()) {
                if (cuttingBoard.placeItem(heldItem)) {
                    heldItem.shrink(1);
                }
            }
            return ItemInteractionResult.sidedSuccess(level.isClientSide());
        }

        // If hand is empty and there's an item, remove it
        if (heldItem.isEmpty() && !cuttingBoard.isEmpty()) {
            if (!level.isClientSide()) {
                ItemStack removed = cuttingBoard.removeItem();
                if (!removed.isEmpty()) {
                    if (!player.getInventory().add(removed)) {
                        player.drop(removed, false);
                    }
                }
            }
            return ItemInteractionResult.sidedSuccess(level.isClientSide());
        }

        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    /**
     * Spawn leaf particles on the client side when cutting.
     */
    private void spawnCuttingParticles(Level level, BlockPos pos) {
        double x = pos.getX() + 0.5;
        double y = pos.getY() + 0.95; // Top of the tall furniture
        double z = pos.getZ() + 0.5;

        // Spawn leaf particles (cherry leaves block particles)
        BlockParticleOption leafParticle = new BlockParticleOption(ParticleTypes.BLOCK, Blocks.OAK_LEAVES.defaultBlockState());

        for (int i = 0; i < 12; i++) {
            double offsetX = (level.random.nextDouble() - 0.5) * 0.4;
            double offsetZ = (level.random.nextDouble() - 0.5) * 0.4;
            double velY = 0.02 + level.random.nextDouble() * 0.05;
            double velX = (level.random.nextDouble() - 0.5) * 0.08;
            double velZ = (level.random.nextDouble() - 0.5) * 0.08;

            level.addParticle(leafParticle,
                    x + offsetX, y, z + offsetZ,
                    velX, velY, velZ);
        }
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock())) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof CuttingBoardBlockEntity cuttingBoard) {
                ItemStack stored = cuttingBoard.getStoredItem();
                if (!stored.isEmpty()) {
                    Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), stored);
                }
            }
            super.onRemove(state, level, pos, newState, movedByPiston);
        }
    }
}
