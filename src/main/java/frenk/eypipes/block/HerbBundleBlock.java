package frenk.eypipes.block;

import frenk.eypipes.block.entity.HerbBundleBlockEntity;
import frenk.eypipes.registries.ModDataComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

import org.jetbrains.annotations.Nullable;
import java.util.List;

/**
 * Herb bundle block that stores fermentation level.
 * Simple storage block with no special functionality beyond preserving herb quality.
 */
public class HerbBundleBlock extends Block implements EntityBlock {

    public HerbBundleBlock(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new HerbBundleBlockEntity(pos, state);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);

        // Transfer fermentation level from item to block entity
        if (!level.isClientSide()) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof HerbBundleBlockEntity bundleBE) {
                if (ModDataComponents.FERMENTATION_LEVEL.has(stack)) {
                    bundleBE.setFermentationLevel(ModDataComponents.FERMENTATION_LEVEL.get(stack));
                }
            }
        }
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        // Get the block entity to preserve fermentation level
        BlockEntity be = builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY);

        List<ItemStack> drops = super.getDrops(state, builder);

        // Add fermentation level to dropped items
        if (be instanceof HerbBundleBlockEntity bundleBE) {
            int fermentationLevel = bundleBE.getFermentationLevel();
            for (ItemStack drop : drops) {
                if (drop.getItem() == this.asItem()) {
                    ModDataComponents.FERMENTATION_LEVEL.set(drop, fermentationLevel);
                }
            }
        }

        return drops;
    }
}
