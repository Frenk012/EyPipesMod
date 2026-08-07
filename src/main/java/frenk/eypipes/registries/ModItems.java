package frenk.eypipes.registries;

import frenk.eypipes.EyPipes;
import frenk.eypipes.config.EyPipesConfig;
import frenk.eypipes.item.CigarItem;
import frenk.eypipes.item.HerbBundleBlockItem;
import frenk.eypipes.item.HerbItem;
import frenk.eypipes.item.KnifeItem;
import frenk.eypipes.item.PipeItem;
import net.minecraft.ChatFormatting;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
//? if <1.21.5
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.level.block.ComposterBlock;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Registry for all EyPipes items using NeoForge DeferredRegister.
 *
 * <p>Items are registered through {@code registerItem}/{@code registerSimpleItem} rather than the
 * raw {@code register} overload so that {@link Item.Properties} is supplied by the registry itself.
 * From Minecraft 1.21.2 onwards the properties must carry the item's registry id, which only these
 * overloads can inject; on 1.21.1 the two forms behave identically.
 */
public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(EyPipes.MOD_ID);

    /**
     * Block items took their name from the block automatically until 1.21.5, where
     * BlockItem stopped overriding getDescriptionId and the prefix became a property.
     * Without this they look up item.eypipes.&lt;name&gt; and show the raw key.
     */
    private static Item.Properties blockItemProperties(Item.Properties props) {
        //? if <1.21.5 {
        return props;
        //?} else
        /*return props.useBlockDescriptionPrefix();*/
    }

    private static Item.Properties pipeProperties() {
        return new Item.Properties().stacksTo(1).durability(50);
    }

    // Basic crop items
    public static final DeferredItem<Item> ERBAPIPA = ITEMS.registerSimpleItem("erbapipa");

    public static final DeferredItem<Item> ERBAPIPA_DRIED = ITEMS.registerSimpleItem("erbapipa_dried");

    public static final DeferredItem<Item> ERBAPIPA_CUTTED = ITEMS.registerSimpleItem("erbapipa_cutted");

    // Valeriana - calming effect (Slowness + Night Vision)
    public static final DeferredItem<Item> VALERIANA = ITEMS.registerItem("valeriana",
            props -> new HerbItem(props, "item.eypipes.valeriana.hint", ChatFormatting.LIGHT_PURPLE));

    public static final DeferredItem<Item> VALERIANA_DRIED = ITEMS.registerSimpleItem("valeriana_dried");

    public static final DeferredItem<Item> VALERIANA_CUTTED = ITEMS.registerSimpleItem("valeriana_cutted");

    // Ginseng - energizing effect (Speed + Haste)
    public static final DeferredItem<Item> GINSENG = ITEMS.registerItem("ginseng",
            props -> new HerbItem(props, "item.eypipes.ginseng.hint", ChatFormatting.GOLD));

    public static final DeferredItem<Item> GINSENG_DRIED = ITEMS.registerSimpleItem("ginseng_dried");

    public static final DeferredItem<Item> GINSENG_CUTTED = ITEMS.registerSimpleItem("ginseng_cutted");

    // Salvia - vision effect (Night Vision II + Glowing)
    public static final DeferredItem<Item> SALVIA = ITEMS.registerItem("salvia",
            props -> new HerbItem(props, "item.eypipes.salvia.hint", ChatFormatting.DARK_GREEN));

    public static final DeferredItem<Item> SALVIA_DRIED = ITEMS.registerSimpleItem("salvia_dried");

    public static final DeferredItem<Item> SALVIA_CUTTED = ITEMS.registerSimpleItem("salvia_cutted");

    // Seeds items (plants the crops)
    public static final DeferredItem<Item> ERBAPIPA_SEEDS = ITEMS.registerItem("erbapipa_seeds",
            //? if <1.21.5 {
            props -> new ItemNameBlockItem(ModBlocks.ERBAPIPA_CROP.get(), props));
            //?} else
            /*props -> new BlockItem(ModBlocks.ERBAPIPA_CROP.get(), props.useItemDescriptionPrefix()));*/

    public static final DeferredItem<Item> VALERIANA_SEEDS = ITEMS.registerItem("valeriana_seeds",
            //? if <1.21.5 {
            props -> new ItemNameBlockItem(ModBlocks.VALERIANA_CROP.get(), props));
            //?} else
            /*props -> new BlockItem(ModBlocks.VALERIANA_CROP.get(), props.useItemDescriptionPrefix()));*/

    public static final DeferredItem<Item> GINSENG_SEEDS = ITEMS.registerItem("ginseng_seeds",
            //? if <1.21.5 {
            props -> new ItemNameBlockItem(ModBlocks.GINSENG_CROP.get(), props));
            //?} else
            /*props -> new BlockItem(ModBlocks.GINSENG_CROP.get(), props.useItemDescriptionPrefix()));*/

    public static final DeferredItem<Item> SALVIA_SEEDS = ITEMS.registerItem("salvia_seeds",
            //? if <1.21.5 {
            props -> new ItemNameBlockItem(ModBlocks.SALVIA_CROP.get(), props));
            //?} else
            /*props -> new BlockItem(ModBlocks.SALVIA_CROP.get(), props.useItemDescriptionPrefix()));*/

    // Pipe item - GeckoLib animated trinket with 50 durability
    public static final DeferredItem<Item> PIPE = ITEMS.registerItem("pipe",
            PipeItem::new, pipeProperties());

    // Cigar item - GeckoLib animated trinket with 10 durability
    public static final DeferredItem<Item> CIGAR = ITEMS.registerItem("cigar",
            CigarItem::new, new Item.Properties().stacksTo(1).durability(10));

    // Pipe variants - same mechanics, different models/textures
    public static final DeferredItem<Item> WOODEN_PIPE = ITEMS.registerItem("wooden_pipe",
            PipeItem::new, pipeProperties());

    public static final DeferredItem<Item> CLAY_PIPE = ITEMS.registerItem("clay_pipe",
            PipeItem::new, pipeProperties());

    public static final DeferredItem<Item> CORN_COB_PIPE = ITEMS.registerItem("corn_cob_pipe",
            PipeItem::new, pipeProperties());

    public static final DeferredItem<Item> MEERSCHAUM_PIPE = ITEMS.registerItem("meerschaum_pipe",
            PipeItem::new, pipeProperties());

    public static final DeferredItem<Item> BRIAR_PIPE = ITEMS.registerItem("briar_pipe",
            PipeItem::new, pipeProperties());

    public static final DeferredItem<Item> CHERRY_PIPE = ITEMS.registerItem("cherry_pipe",
            PipeItem::new, pipeProperties());

    public static final DeferredItem<Item> CALABASH_PIPE = ITEMS.registerItem("calabash_pipe",
            PipeItem::new, pipeProperties());

    public static final DeferredItem<Item> CHURCHWARD_PIPE = ITEMS.registerItem("churchward_pipe",
            PipeItem::new, pipeProperties());

    public static final DeferredItem<Item> BENT_PIPE = ITEMS.registerItem("bent_pipe",
            PipeItem::new, pipeProperties());

    // Block items
    public static final DeferredItem<Item> DRYING_RACK_ITEM = ITEMS.registerItem("drying_rack_erb",
            props -> new BlockItem(ModBlocks.DRYING_RACK.get(), blockItemProperties(props)));

    public static final DeferredItem<Item> TOBACCO_JAR_ITEM = ITEMS.registerItem("tobacco_jar",
            props -> new BlockItem(ModBlocks.TOBACCO_JAR.get(), blockItemProperties(props)));

    public static final DeferredItem<Item> PIPE_RACK_ITEM = ITEMS.registerItem("pipe_rack",
            props -> new BlockItem(ModBlocks.PIPE_RACK.get(), blockItemProperties(props)));

    // Cutting Board block item
    public static final DeferredItem<Item> CUTTING_BOARD_ITEM = ITEMS.registerItem("cutting_board",
            props -> new BlockItem(ModBlocks.CUTTING_BOARD.get(), blockItemProperties(props)));

    // Knife item - used to cut dried herbs on the cutting board
    public static final DeferredItem<Item> KNIFE = ITEMS.registerItem("knife",
            KnifeItem::new, new Item.Properties().stacksTo(1).durability(64));

    // Herb Bundle block items - storage blocks for dried herbs
    public static final DeferredItem<Item> ERBAPIPA_BUNDLE_ITEM = ITEMS.registerItem("erbapipa_bundle",
            props -> new HerbBundleBlockItem(ModBlocks.ERBAPIPA_BUNDLE.get(), blockItemProperties(props), ChatFormatting.GREEN));

    public static final DeferredItem<Item> VALERIANA_BUNDLE_ITEM = ITEMS.registerItem("valeriana_bundle",
            props -> new HerbBundleBlockItem(ModBlocks.VALERIANA_BUNDLE.get(), blockItemProperties(props), ChatFormatting.LIGHT_PURPLE));

    public static final DeferredItem<Item> GINSENG_BUNDLE_ITEM = ITEMS.registerItem("ginseng_bundle",
            props -> new HerbBundleBlockItem(ModBlocks.GINSENG_BUNDLE.get(), blockItemProperties(props), ChatFormatting.GOLD));

    public static final DeferredItem<Item> SALVIA_BUNDLE_ITEM = ITEMS.registerItem("salvia_bundle",
            props -> new HerbBundleBlockItem(ModBlocks.SALVIA_BUNDLE.get(), blockItemProperties(props), ChatFormatting.DARK_GREEN));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
        EyPipes.LOGGER.info("Registering EyPipes Items");
    }

    /**
     * Register items as compostables with configured chances
     * Called during common setup after registration is complete
     */
    public static void registerCompostables() {
        // Add items to composter with config-based chances
        ComposterBlock.COMPOSTABLES.put(ERBAPIPA_SEEDS.get(), EyPipesConfig.COMMON.erbapipaSeedsCompostChance.get().floatValue());
        ComposterBlock.COMPOSTABLES.put(ERBAPIPA.get(), EyPipesConfig.COMMON.erbapipaCompostChance.get().floatValue());
        EyPipes.LOGGER.debug("Registered compostables for EyPipes");
    }
}
