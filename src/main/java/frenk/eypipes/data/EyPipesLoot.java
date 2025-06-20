package frenk.eypipes.data;

import frenk.eypipes.block.EyPipesBlocks;
import frenk.eypipes.item.EyPipesItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.block.CropBlock;
import net.minecraft.loot.condition.BlockStatePropertyLootCondition;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.predicate.StatePredicate;

public class EyPipesLoot extends FabricBlockLootTableProvider {
    public EyPipesLoot(FabricDataGenerator dataGenerator) {
        super(dataGenerator);
    }

    @Override
    protected void generateBlockLootTables() {
        LootCondition.Builder erbapipa_builder = BlockStatePropertyLootCondition.builder(EyPipesBlocks.ERBAPIPA_CROP)
                .properties(StatePredicate.Builder.create().exactMatch(CropBlock.AGE, 7));
        addDrop(EyPipesBlocks.ERBAPIPA_CROP, block -> cropDrops(block, EyPipesItems.ERBAPIPA, EyPipesItems.ERBAPIPA_SEEDS, erbapipa_builder));
    }
}
