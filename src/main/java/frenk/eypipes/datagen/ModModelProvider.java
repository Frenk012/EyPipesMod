package frenk.eypipes.datagen;

import frenk.eypipes.EyPipes;
import frenk.eypipes.block.DryingRackBlock;
import frenk.eypipes.block.ErbapipaCropBlock;
import frenk.eypipes.registries.ModBlocks;
import frenk.eypipes.registries.ModItems;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import frenk.eypipes.platform.RegistryEntry;
import software.bernie.geckolib.renderer.base.GeckolibItemSpecialRenderer;

import java.util.List;
import java.util.stream.Stream;

/**
 * Model, blockstate and client-item generation for EyPipes.
 *
 * <p>From 1.21.2 NeoForge's {@code BlockStateProvider} and {@code ItemModelProvider} are gone;
 * Minecraft's own {@link ModelProvider} does both jobs, and it also writes the
 * {@code assets/<namespace>/items/} client-item definitions that 1.21.4 made mandatory. Those
 * definitions are what actually bind an item to a model, so every item needs one even when its
 * model is hand-authored and no JSON is generated for it.
 *
 * <p>{@code ModelProvider} validates that every registered block and item of this mod is covered
 * and fails datagen otherwise, so the blockstate side deliberately narrows
 * {@link #getKnownBlocks()} to the two blocks generated here; the rest ship hand-written
 * blockstates under {@code src/main/resources}.
 */
public class ModModelProvider extends ModelProvider {

    /** Items whose model is generated from a single texture layer. */
    private static final List<RegistryEntry<Item>> FLAT_ITEMS = List.of(
            ModItems.ERBAPIPA, ModItems.ERBAPIPA_DRIED, ModItems.ERBAPIPA_CUTTED, ModItems.ERBAPIPA_SEEDS,
            ModItems.VALERIANA, ModItems.VALERIANA_DRIED, ModItems.VALERIANA_CUTTED, ModItems.VALERIANA_SEEDS,
            ModItems.GINSENG, ModItems.GINSENG_DRIED, ModItems.GINSENG_CUTTED, ModItems.GINSENG_SEEDS,
            ModItems.SALVIA, ModItems.SALVIA_DRIED, ModItems.SALVIA_CUTTED, ModItems.SALVIA_SEEDS);

    /** Items whose model is hand-authored; they only need the client-item definition. */
    private static final List<RegistryEntry<Item>> HAND_AUTHORED_ITEMS = List.of(
            ModItems.TOBACCO_JAR_ITEM, ModItems.PIPE_RACK_ITEM,
            ModItems.CUTTING_BOARD_ITEM, ModItems.KNIFE,
            ModItems.ERBAPIPA_BUNDLE_ITEM, ModItems.VALERIANA_BUNDLE_ITEM,
            ModItems.GINSENG_BUNDLE_ITEM, ModItems.SALVIA_BUNDLE_ITEM);

    /** Items drawn by GeckoLib rather than by a baked model. */
    private static final List<RegistryEntry<Item>> ANIMATED_ITEMS = List.of(
            ModItems.PIPE, ModItems.CIGAR,
            ModItems.WOODEN_PIPE, ModItems.CLAY_PIPE, ModItems.CORN_COB_PIPE, ModItems.MEERSCHAUM_PIPE,
            ModItems.BRIAR_PIPE, ModItems.CHERRY_PIPE, ModItems.CALABASH_PIPE, ModItems.CHURCHWARD_PIPE,
            ModItems.BENT_PIPE);

    public ModModelProvider(PackOutput output) {
        super(output, EyPipes.MOD_ID);
    }

    @Override
    protected Stream<? extends Holder<Block>> getKnownBlocks() {
        // Only these two have generated blockstates; the others are hand-written.
        return Stream.of(ModBlocks.ERBAPIPA_CROP, ModBlocks.DRYING_RACK)
                .map(holder -> holder.get().builtInRegistryHolder());
    }

    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        registerErbapipaCrop(blockModels);
        registerDryingRack(blockModels);

        for (RegistryEntry<Item> item : FLAT_ITEMS) {
            itemModels.generateFlatItem(item.get(), ModelTemplates.FLAT_ITEM);
        }

        // The model already exists on disk, so only the client-item definition is emitted.
        for (RegistryEntry<Item> item : HAND_AUTHORED_ITEMS) {
            itemModels.declareCustomModelItem(item.get());
        }

        // The drying rack's item model is named after the block rather than the item, so the
        // default "item/<registry name>" lookup would miss it.
        itemModels.itemModelOutput.accept(ModItems.DRYING_RACK_ITEM.get(),
                ItemModelUtils.plainModel(
                        ResourceLocation.fromNamespaceAndPath(EyPipes.MOD_ID, "item/drying_rack")));

        // GeckoLib draws these through its own special renderer, which it registers itself;
        // the client item just has to point at that renderer rather than at a baked model.
        for (RegistryEntry<Item> item : ANIMATED_ITEMS) {
            ResourceLocation base = ResourceLocation.fromNamespaceAndPath(
                    EyPipes.MOD_ID, "item/" + item.getId().getPath());
            itemModels.itemModelOutput.accept(item.get(),
                    ItemModelUtils.specialModel(base, new GeckolibItemSpecialRenderer.Unbaked()));
        }
    }

    /** The two-block-tall crop: a cross model per age, with a separate set for the upper half. */
    private void registerErbapipaCrop(BlockModelGenerators blockModels) {
        Block crop = ModBlocks.ERBAPIPA_CROP.get();

        blockModels.blockStateOutput.accept(MultiVariantGenerator.dispatch(crop)
                .with(PropertyDispatch.initial(ErbapipaCropBlock.AGE, ErbapipaCropBlock.UPPER)
                        .generate((age, upper) -> {
                            String suffix = (upper ? "_top" : "") + "_stage" + age;
                            ResourceLocation model = blockModels.createSuffixedVariant(
                                    crop, suffix, ModelTemplates.CROSS, TextureMapping::cross);
                            return BlockModelGenerators.plainVariant(model);
                        })));
    }

    /** Wall-facing rack using its hand-authored model, rotated to match the facing property. */
    private void registerDryingRack(BlockModelGenerators blockModels) {
        Block dryingRack = ModBlocks.DRYING_RACK.get();
        ResourceLocation model = ResourceLocation.fromNamespaceAndPath(EyPipes.MOD_ID, "block/drying_rack");

        blockModels.blockStateOutput.accept(
                BlockModelGenerators.createSimpleBlock(dryingRack, BlockModelGenerators.plainVariant(model))
                        // The model is authored facing west, so every variant is a quarter turn
                        // further round than the compass direction suggests.
                        .with(PropertyDispatch.modify(DryingRackBlock.FACING)
                                .select(Direction.WEST, BlockModelGenerators.NOP)
                                .select(Direction.NORTH, BlockModelGenerators.Y_ROT_90)
                                .select(Direction.EAST, BlockModelGenerators.Y_ROT_180)
                                .select(Direction.SOUTH, BlockModelGenerators.Y_ROT_270)));
    }
}
