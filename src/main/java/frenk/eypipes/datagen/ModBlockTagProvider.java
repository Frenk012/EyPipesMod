package frenk.eypipes.datagen;

import frenk.eypipes.EyPipes;
import frenk.eypipes.registries.ModBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

/**
 * Block tag provider for EyPipes blocks.
 * Generates tags for mineable blocks and crop blocks.
 */
public class ModBlockTagProvider extends BlockTagsProvider {

    public ModBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
            ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, EyPipes.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        // Drying rack, pipe rack, and cutting board are mineable with axe (wooden)
        tag(BlockTags.MINEABLE_WITH_AXE)
                .add(ModBlocks.DRYING_RACK.get())
                .add(ModBlocks.PIPE_RACK.get())
                .add(ModBlocks.CUTTING_BOARD.get());

        // Tobacco jar is mineable with pickaxe (ceramic/brick material)
        tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(ModBlocks.TOBACCO_JAR.get());

        // Crop block tags
        tag(BlockTags.CROPS)
                .add(ModBlocks.ERBAPIPA_CROP.get())
                .add(ModBlocks.VALERIANA_CROP.get())
                .add(ModBlocks.GINSENG_CROP.get())
                .add(ModBlocks.SALVIA_CROP.get());

        // Bee-pollinated crops
        tag(BlockTags.BEE_GROWABLES)
                .add(ModBlocks.ERBAPIPA_CROP.get())
                .add(ModBlocks.VALERIANA_CROP.get())
                .add(ModBlocks.GINSENG_CROP.get())
                .add(ModBlocks.SALVIA_CROP.get());
    }
}
