package frenk.eypipes.block.entity;

import frenk.eypipes.compat.Nbt;
//? if >=1.21.6 {
/*import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
*///?}
import frenk.eypipes.registries.ModBlockEntities;
import frenk.eypipes.registries.ModDataComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Simple block entity for herb bundles that stores fermentation level.
 * No special functionality - just preserves the quality data.
 */
public class HerbBundleBlockEntity extends BlockEntity {

    private static final String FERMENTATION_KEY = "fermentation_level";
    private int fermentationLevel = ModDataComponents.QUALITY_DRIED;

    public HerbBundleBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.HERB_BUNDLE.get(), pos, state);
    }

    public int getFermentationLevel() {
        return fermentationLevel;
    }

    public void setFermentationLevel(int level) {
        this.fermentationLevel = level;
        setChanged();
    }

    //? if <1.21.6 {
    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        Nbt.putInt(tag, FERMENTATION_KEY, fermentationLevel);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        fermentationLevel = Nbt.getInt(tag, FERMENTATION_KEY, fermentationLevel);
    }
    //?} else {
    /*@Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        Nbt.putInt(output, FERMENTATION_KEY, fermentationLevel);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        fermentationLevel = Nbt.getInt(input, FERMENTATION_KEY, fermentationLevel);
    }
    *///?}
}
