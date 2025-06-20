package frenk.eypipes.data;

import frenk.eypipes.block.*;
import frenk.eypipes.item.*;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.block.Block;
import net.minecraft.data.client.*;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.state.property.Property;
import net.minecraft.util.Identifier;

public class EyPipesModels extends FabricModelProvider {
    public EyPipesModels(FabricDataGenerator dataGenerator) { super(dataGenerator); }

    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        registerTallCrop(blockStateModelGenerator, EyPipesBlocks.ERBAPIPA_CROP, ErbapipaCropBlock.UPPER, ErbapipaCropBlock.AGE);
    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        for (Item item : EyPipesItems.ITEMS) {
            if (!(item instanceof BlockItem))
                itemModelGenerator.register(item, Models.GENERATED);
        }
        itemModelGenerator.register(EyPipesItems.ERBAPIPA_SEEDS, Models.GENERATED);
    }

    public final void registerTallCrop(BlockStateModelGenerator blockStateModelGenerator, Block crop, Property<Boolean> booleanProperty, Property<Integer> ageProperty) {
        BlockStateVariantMap blockStateVariantMap = BlockStateVariantMap.create(ageProperty, booleanProperty).register((integer, upper) -> {
            Identifier identifier = blockStateModelGenerator.createSubModel(crop, (upper ? "_top" : "") + "_stage" + integer, Models.CROP, TextureMap::crop);
            return BlockStateVariant.create().put(VariantSettings.MODEL, identifier);
        });
        blockStateModelGenerator.blockStateCollector.accept(VariantsBlockStateSupplier.create(crop).coordinate(blockStateVariantMap));
    }
}
