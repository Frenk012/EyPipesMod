package frenk.eypipes.datagen;

import frenk.eypipes.block.ErbapipaCropBlock;
import frenk.eypipes.block.HerbCropBlock;
import frenk.eypipes.registries.ModBlocks;
import frenk.eypipes.registries.ModItems;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * Loot table provider for EyPipes blocks.
 * Generates loot tables for crop drops and drying rack.
 */
public class ModLootTableProvider extends LootTableProvider {

    public ModLootTableProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, Set.of(), List.of(
                new SubProviderEntry(ModBlockLoot::new, LootContextParamSets.BLOCK)
        ), lookupProvider);
    }

    public static class ModBlockLoot extends BlockLootSubProvider {

        protected ModBlockLoot(HolderLookup.Provider lookupProvider) {
            super(Set.of(), FeatureFlags.REGISTRY.allFlags(), lookupProvider);
        }

        @Override
        protected void generate() {
            // Erbapipa crop - drops seeds always, plus erbapipa when mature (age 7)
            LootItemCondition.Builder erbapipaMatureCondition = LootItemBlockStatePropertyCondition
                    .hasBlockStateProperties(ModBlocks.ERBAPIPA_CROP.get())
                    .setProperties(StatePropertiesPredicate.Builder.properties()
                            .hasProperty(ErbapipaCropBlock.AGE, 7));

            add(ModBlocks.ERBAPIPA_CROP.get(),
                    createCropDrops(ModBlocks.ERBAPIPA_CROP.get(),
                            ModItems.ERBAPIPA.get(),
                            ModItems.ERBAPIPA_SEEDS.get(),
                            erbapipaMatureCondition));

            // Valeriana crop - drops seeds always, plus valeriana when mature (age 3)
            LootItemCondition.Builder valerianaMatureCondition = LootItemBlockStatePropertyCondition
                    .hasBlockStateProperties(ModBlocks.VALERIANA_CROP.get())
                    .setProperties(StatePropertiesPredicate.Builder.properties()
                            .hasProperty(HerbCropBlock.AGE, 3));

            add(ModBlocks.VALERIANA_CROP.get(),
                    createCropDrops(ModBlocks.VALERIANA_CROP.get(),
                            ModItems.VALERIANA.get(),
                            ModItems.VALERIANA_SEEDS.get(),
                            valerianaMatureCondition));

            // Ginseng crop - drops seeds always, plus ginseng when mature (age 3)
            LootItemCondition.Builder ginsengMatureCondition = LootItemBlockStatePropertyCondition
                    .hasBlockStateProperties(ModBlocks.GINSENG_CROP.get())
                    .setProperties(StatePropertiesPredicate.Builder.properties()
                            .hasProperty(HerbCropBlock.AGE, 3));

            add(ModBlocks.GINSENG_CROP.get(),
                    createCropDrops(ModBlocks.GINSENG_CROP.get(),
                            ModItems.GINSENG.get(),
                            ModItems.GINSENG_SEEDS.get(),
                            ginsengMatureCondition));

            // Salvia crop - drops seeds always, plus salvia when mature (age 3)
            LootItemCondition.Builder salviaMatureCondition = LootItemBlockStatePropertyCondition
                    .hasBlockStateProperties(ModBlocks.SALVIA_CROP.get())
                    .setProperties(StatePropertiesPredicate.Builder.properties()
                            .hasProperty(HerbCropBlock.AGE, 3));

            add(ModBlocks.SALVIA_CROP.get(),
                    createCropDrops(ModBlocks.SALVIA_CROP.get(),
                            ModItems.SALVIA.get(),
                            ModItems.SALVIA_SEEDS.get(),
                            salviaMatureCondition));

            // Drying rack drops itself
            dropSelf(ModBlocks.DRYING_RACK.get());

            // Tobacco jar drops itself
            dropSelf(ModBlocks.TOBACCO_JAR.get());

            // Pipe rack drops itself
            dropSelf(ModBlocks.PIPE_RACK.get());

            // Cutting board drops itself
            dropSelf(ModBlocks.CUTTING_BOARD.get());
        }

        @Override
        protected Iterable<Block> getKnownBlocks() {
            // All blocks with loot tables must be listed here for the loot system to recognize them
            // Even blocks with manual loot tables in resources need to be included
            return List.of(
                    ModBlocks.ERBAPIPA_CROP.get(),
                    ModBlocks.VALERIANA_CROP.get(),
                    ModBlocks.GINSENG_CROP.get(),
                    ModBlocks.SALVIA_CROP.get(),
                    ModBlocks.DRYING_RACK.get(),
                    ModBlocks.TOBACCO_JAR.get(),
                    ModBlocks.PIPE_RACK.get(),
                    ModBlocks.CUTTING_BOARD.get()
            );
        }
    }
}
