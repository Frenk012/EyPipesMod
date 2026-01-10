package frenk.eypipes.datagen;

import frenk.eypipes.EyPipes;
import frenk.eypipes.block.DryingRackBlock;
import frenk.eypipes.block.ErbapipaCropBlock;
import frenk.eypipes.registries.ModBlocks;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

/**
 * Block state and model provider for EyPipes blocks.
 * Generates block states for crop and drying rack.
 */
public class ModBlockStateProvider extends BlockStateProvider {

    public ModBlockStateProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, EyPipes.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        // Register erbapipa crop block states
        registerErbapipaCrop();

        // Register drying rack block states
        registerDryingRack();
    }

    private void registerErbapipaCrop() {
        getVariantBuilder(ModBlocks.ERBAPIPA_CROP.get()).forAllStates(state -> {
            int age = state.getValue(ErbapipaCropBlock.AGE);
            boolean upper = state.getValue(ErbapipaCropBlock.UPPER);

            String suffix = (upper ? "_top" : "") + "_stage" + age;
            ResourceLocation texture = modLoc("block/erbapipa_crop" + suffix);

            // Use cross model (like flowers) instead of flat crop model
            ModelFile model = models().cross("erbapipa_crop" + suffix, texture).renderType("cutout");

            return ConfiguredModel.builder()
                    .modelFile(model)
                    .build();
        });
    }

    private void registerDryingRack() {
        ResourceLocation modelLoc = modLoc("block/drying_rack");

        getVariantBuilder(ModBlocks.DRYING_RACK.get()).forAllStates(state -> {
            Direction facing = state.getValue(DryingRackBlock.FACING);

            int yRot = switch (facing) {
                case NORTH -> 0;
                case EAST -> 90;
                case SOUTH -> 180;
                case WEST -> 270;
                default -> 0;
            };

            return ConfiguredModel.builder()
                    .modelFile(models().getExistingFile(modelLoc))
                    .rotationY(yRot)
                    .build();
        });
    }
}
