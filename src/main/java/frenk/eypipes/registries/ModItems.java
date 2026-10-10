package frenk.eypipes.registries;

import frenk.eypipes.EyPipes;
import java.util.List;
import java.util.function.Function;
import frenk.eypipes.config.EyPipesConfig;
import frenk.eypipes.item.CigarItem;
import frenk.eypipes.item.HerbBundleBlockItem;
import frenk.eypipes.item.HerbItem;
import frenk.eypipes.item.KnifeItem;
import frenk.eypipes.item.PipeItem;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
//? if <1.21.5
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.level.block.ComposterBlock;
import frenk.eypipes.platform.Registrar;
import frenk.eypipes.platform.RegistryEntry;

/**
 * Registry for all EyPipes items.
 *
 * <p>Items are declared through {@code item}/{@code simpleItem}, which build the item only once
 * its {@link Item.Properties} carry the registry id that Minecraft requires from 1.21.2.
 */
public class ModItems {
    public static final Registrar<Item> ITEMS = Registrar.create(Registries.ITEM);

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

    private static <I extends Item> RegistryEntry<I> item(String name, Function<Item.Properties, I> factory,
            Item.Properties props) {
        return ITEMS.register(name, () -> factory.apply(withId(name, props)));
    }

    private static <I extends Item> RegistryEntry<I> item(String name, Function<Item.Properties, I> factory) {
        return item(name, factory, new Item.Properties());
    }

    private static RegistryEntry<Item> simpleItem(String name) {
        return item(name, Item::new);
    }

    /** From 1.21.2 an item's properties must carry its registry id before the item is built. */
    private static Item.Properties withId(String name, Item.Properties props) {
        //? if >=1.21.2 {
        /*return props.setId(net.minecraft.resources.ResourceKey.create(Registries.ITEM, EyPipes.id(name)));
        *///?} else
        return props;
    }

    private static Item.Properties pipeProperties() {
        return new Item.Properties().stacksTo(1).durability(50);
    }

    // Basic crop items
    public static final RegistryEntry<Item> ERBAPIPA = simpleItem("erbapipa");

    public static final RegistryEntry<Item> ERBAPIPA_DRIED = simpleItem("erbapipa_dried");

    public static final RegistryEntry<Item> ERBAPIPA_CUTTED = simpleItem("erbapipa_cutted");

    // Valeriana - calming effect (Slowness + Night Vision)
    public static final RegistryEntry<Item> VALERIANA = item("valeriana",
            props -> new HerbItem(props, "item.eypipes.valeriana.hint", ChatFormatting.LIGHT_PURPLE));

    public static final RegistryEntry<Item> VALERIANA_DRIED = simpleItem("valeriana_dried");

    public static final RegistryEntry<Item> VALERIANA_CUTTED = simpleItem("valeriana_cutted");

    // Ginseng - energizing effect (Speed + Haste)
    public static final RegistryEntry<Item> GINSENG = item("ginseng",
            props -> new HerbItem(props, "item.eypipes.ginseng.hint", ChatFormatting.GOLD));

    public static final RegistryEntry<Item> GINSENG_DRIED = simpleItem("ginseng_dried");

    public static final RegistryEntry<Item> GINSENG_CUTTED = simpleItem("ginseng_cutted");

    // Salvia - vision effect (Night Vision II + Glowing)
    public static final RegistryEntry<Item> SALVIA = item("salvia",
            props -> new HerbItem(props, "item.eypipes.salvia.hint", ChatFormatting.DARK_GREEN));

    public static final RegistryEntry<Item> SALVIA_DRIED = simpleItem("salvia_dried");

    public static final RegistryEntry<Item> SALVIA_CUTTED = simpleItem("salvia_cutted");

    // Seeds items (plants the crops)
    public static final RegistryEntry<Item> ERBAPIPA_SEEDS = item("erbapipa_seeds",
            //? if <1.21.5 {
            props -> new ItemNameBlockItem(ModBlocks.ERBAPIPA_CROP.get(), props));
            //?} else
            /*props -> new BlockItem(ModBlocks.ERBAPIPA_CROP.get(), props.useItemDescriptionPrefix()));*/

    public static final RegistryEntry<Item> VALERIANA_SEEDS = item("valeriana_seeds",
            //? if <1.21.5 {
            props -> new ItemNameBlockItem(ModBlocks.VALERIANA_CROP.get(), props));
            //?} else
            /*props -> new BlockItem(ModBlocks.VALERIANA_CROP.get(), props.useItemDescriptionPrefix()));*/

    public static final RegistryEntry<Item> GINSENG_SEEDS = item("ginseng_seeds",
            //? if <1.21.5 {
            props -> new ItemNameBlockItem(ModBlocks.GINSENG_CROP.get(), props));
            //?} else
            /*props -> new BlockItem(ModBlocks.GINSENG_CROP.get(), props.useItemDescriptionPrefix()));*/

    public static final RegistryEntry<Item> SALVIA_SEEDS = item("salvia_seeds",
            //? if <1.21.5 {
            props -> new ItemNameBlockItem(ModBlocks.SALVIA_CROP.get(), props));
            //?} else
            /*props -> new BlockItem(ModBlocks.SALVIA_CROP.get(), props.useItemDescriptionPrefix()));*/

    // Pipe item - GeckoLib animated trinket with 50 durability
    public static final RegistryEntry<Item> PIPE = item("pipe",
            PipeItem::new, pipeProperties());

    // Cigar item - GeckoLib animated trinket with 10 durability
    public static final RegistryEntry<Item> CIGAR = item("cigar",
            CigarItem::new, new Item.Properties().stacksTo(1).durability(10));

    // Pipe variants - same mechanics, different models/textures
    public static final RegistryEntry<Item> WOODEN_PIPE = item("wooden_pipe",
            PipeItem::new, pipeProperties());

    public static final RegistryEntry<Item> CLAY_PIPE = item("clay_pipe",
            PipeItem::new, pipeProperties());

    public static final RegistryEntry<Item> CORN_COB_PIPE = item("corn_cob_pipe",
            PipeItem::new, pipeProperties());

    public static final RegistryEntry<Item> MEERSCHAUM_PIPE = item("meerschaum_pipe",
            PipeItem::new, pipeProperties());

    public static final RegistryEntry<Item> BRIAR_PIPE = item("briar_pipe",
            PipeItem::new, pipeProperties());

    public static final RegistryEntry<Item> CHERRY_PIPE = item("cherry_pipe",
            PipeItem::new, pipeProperties());

    public static final RegistryEntry<Item> CALABASH_PIPE = item("calabash_pipe",
            PipeItem::new, pipeProperties());

    public static final RegistryEntry<Item> CHURCHWARD_PIPE = item("churchward_pipe",
            PipeItem::new, pipeProperties());

    public static final RegistryEntry<Item> BENT_PIPE = item("bent_pipe",
            PipeItem::new, pipeProperties());

    /**
     * Every pipe variant. Each entry's registry name doubles as its GeckoLib asset base name, so
     * adding a variant only means adding it here plus its assets.
     */
    public static final List<RegistryEntry<Item>> PIPES = List.of(
            PIPE, WOODEN_PIPE, CLAY_PIPE, CORN_COB_PIPE, MEERSCHAUM_PIPE,
            BRIAR_PIPE, CHERRY_PIPE, CALABASH_PIPE, CHURCHWARD_PIPE, BENT_PIPE);

    // Block items
    public static final RegistryEntry<Item> DRYING_RACK_ITEM = item("drying_rack_erb",
            props -> new BlockItem(ModBlocks.DRYING_RACK.get(), blockItemProperties(props)));

    public static final RegistryEntry<Item> TOBACCO_JAR_ITEM = item("tobacco_jar",
            props -> new BlockItem(ModBlocks.TOBACCO_JAR.get(), blockItemProperties(props)));

    public static final RegistryEntry<Item> PIPE_RACK_ITEM = item("pipe_rack",
            props -> new BlockItem(ModBlocks.PIPE_RACK.get(), blockItemProperties(props)));

    // Cutting Board block item
    public static final RegistryEntry<Item> CUTTING_BOARD_ITEM = item("cutting_board",
            props -> new BlockItem(ModBlocks.CUTTING_BOARD.get(), blockItemProperties(props)));

    // Knife item - used to cut dried herbs on the cutting board
    public static final RegistryEntry<Item> KNIFE = item("knife",
            KnifeItem::new, new Item.Properties().stacksTo(1).durability(64));

    // Herb Bundle block items - storage blocks for dried herbs
    public static final RegistryEntry<Item> ERBAPIPA_BUNDLE_ITEM = item("erbapipa_bundle",
            props -> new HerbBundleBlockItem(ModBlocks.ERBAPIPA_BUNDLE.get(), blockItemProperties(props), ChatFormatting.GREEN));

    public static final RegistryEntry<Item> VALERIANA_BUNDLE_ITEM = item("valeriana_bundle",
            props -> new HerbBundleBlockItem(ModBlocks.VALERIANA_BUNDLE.get(), blockItemProperties(props), ChatFormatting.LIGHT_PURPLE));

    public static final RegistryEntry<Item> GINSENG_BUNDLE_ITEM = item("ginseng_bundle",
            props -> new HerbBundleBlockItem(ModBlocks.GINSENG_BUNDLE.get(), blockItemProperties(props), ChatFormatting.GOLD));

    public static final RegistryEntry<Item> SALVIA_BUNDLE_ITEM = item("salvia_bundle",
            props -> new HerbBundleBlockItem(ModBlocks.SALVIA_BUNDLE.get(), blockItemProperties(props), ChatFormatting.DARK_GREEN));

    public static boolean isPipe(ItemStack stack) {
        for (RegistryEntry<Item> pipe : PIPES) {
            if (stack.is(pipe.get())) return true;
        }
        return false;
    }

    public static boolean isCigar(ItemStack stack) {
        return stack.is(CIGAR.get());
    }

    /** Loads this class, which declares (and on Fabric registers) its entries. */
    public static void init() {
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
