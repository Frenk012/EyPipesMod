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
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.level.block.ComposterBlock;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Registry for all EyPipes items using NeoForge DeferredRegister
 */
public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(EyPipes.MOD_ID);

    // Basic crop items
    public static final DeferredItem<Item> ERBAPIPA = ITEMS.register("erbapipa",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> ERBAPIPA_DRIED = ITEMS.register("erbapipa_dried",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> ERBAPIPA_CUTTED = ITEMS.register("erbapipa_cutted",
            () -> new Item(new Item.Properties()));

    // Valeriana - calming effect (Slowness + Night Vision)
    public static final DeferredItem<Item> VALERIANA = ITEMS.register("valeriana",
            () -> new HerbItem(new Item.Properties(), "item.eypipes.valeriana.hint", ChatFormatting.LIGHT_PURPLE));

    public static final DeferredItem<Item> VALERIANA_DRIED = ITEMS.register("valeriana_dried",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> VALERIANA_CUTTED = ITEMS.register("valeriana_cutted",
            () -> new Item(new Item.Properties()));

    // Ginseng - energizing effect (Speed + Haste)
    public static final DeferredItem<Item> GINSENG = ITEMS.register("ginseng",
            () -> new HerbItem(new Item.Properties(), "item.eypipes.ginseng.hint", ChatFormatting.GOLD));

    public static final DeferredItem<Item> GINSENG_DRIED = ITEMS.register("ginseng_dried",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> GINSENG_CUTTED = ITEMS.register("ginseng_cutted",
            () -> new Item(new Item.Properties()));

    // Salvia - vision effect (Night Vision II + Glowing)
    public static final DeferredItem<Item> SALVIA = ITEMS.register("salvia",
            () -> new HerbItem(new Item.Properties(), "item.eypipes.salvia.hint", ChatFormatting.DARK_GREEN));

    public static final DeferredItem<Item> SALVIA_DRIED = ITEMS.register("salvia_dried",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> SALVIA_CUTTED = ITEMS.register("salvia_cutted",
            () -> new Item(new Item.Properties()));

    // Seeds items (plants the crops)
    public static final DeferredItem<Item> ERBAPIPA_SEEDS = ITEMS.register("erbapipa_seeds",
            () -> new ItemNameBlockItem(ModBlocks.ERBAPIPA_CROP.get(), new Item.Properties()));

    public static final DeferredItem<Item> VALERIANA_SEEDS = ITEMS.register("valeriana_seeds",
            () -> new ItemNameBlockItem(ModBlocks.VALERIANA_CROP.get(), new Item.Properties()));

    public static final DeferredItem<Item> GINSENG_SEEDS = ITEMS.register("ginseng_seeds",
            () -> new ItemNameBlockItem(ModBlocks.GINSENG_CROP.get(), new Item.Properties()));

    public static final DeferredItem<Item> SALVIA_SEEDS = ITEMS.register("salvia_seeds",
            () -> new ItemNameBlockItem(ModBlocks.SALVIA_CROP.get(), new Item.Properties()));

    // Pipe item - GeckoLib animated trinket with 50 durability
    public static final DeferredItem<Item> PIPE = ITEMS.register("pipe",
            () -> new PipeItem(new Item.Properties().stacksTo(1).durability(50)));

    // Cigar item - GeckoLib animated trinket with 10 durability
    public static final DeferredItem<Item> CIGAR = ITEMS.register("cigar",
            () -> new CigarItem(new Item.Properties().stacksTo(1).durability(10)));

    // Pipe variants - same mechanics, different models/textures
    public static final DeferredItem<Item> WOODEN_PIPE = ITEMS.register("wooden_pipe",
            () -> new PipeItem(new Item.Properties().stacksTo(1).durability(50)));

    public static final DeferredItem<Item> CLAY_PIPE = ITEMS.register("clay_pipe",
            () -> new PipeItem(new Item.Properties().stacksTo(1).durability(50)));

    public static final DeferredItem<Item> CORN_COB_PIPE = ITEMS.register("corn_cob_pipe",
            () -> new PipeItem(new Item.Properties().stacksTo(1).durability(50)));

    public static final DeferredItem<Item> MEERSCHAUM_PIPE = ITEMS.register("meerschaum_pipe",
            () -> new PipeItem(new Item.Properties().stacksTo(1).durability(50)));

    public static final DeferredItem<Item> BRIAR_PIPE = ITEMS.register("briar_pipe",
            () -> new PipeItem(new Item.Properties().stacksTo(1).durability(50)));

    public static final DeferredItem<Item> CHERRY_PIPE = ITEMS.register("cherry_pipe",
            () -> new PipeItem(new Item.Properties().stacksTo(1).durability(50)));

    public static final DeferredItem<Item> CALABASH_PIPE = ITEMS.register("calabash_pipe",
            () -> new PipeItem(new Item.Properties().stacksTo(1).durability(50)));

    public static final DeferredItem<Item> CHURCHWARD_PIPE = ITEMS.register("churchward_pipe",
            () -> new PipeItem(new Item.Properties().stacksTo(1).durability(50)));

    public static final DeferredItem<Item> BENT_PIPE = ITEMS.register("bent_pipe",
            () -> new PipeItem(new Item.Properties().stacksTo(1).durability(50)));

    // Block items
    public static final DeferredItem<Item> DRYING_RACK_ITEM = ITEMS.register("drying_rack_erb",
            () -> new BlockItem(ModBlocks.DRYING_RACK.get(), new Item.Properties()));

    public static final DeferredItem<Item> TOBACCO_JAR_ITEM = ITEMS.register("tobacco_jar",
            () -> new BlockItem(ModBlocks.TOBACCO_JAR.get(), new Item.Properties()));

    public static final DeferredItem<Item> PIPE_RACK_ITEM = ITEMS.register("pipe_rack",
            () -> new BlockItem(ModBlocks.PIPE_RACK.get(), new Item.Properties()));

    // Cutting Board block item
    public static final DeferredItem<Item> CUTTING_BOARD_ITEM = ITEMS.register("cutting_board",
            () -> new BlockItem(ModBlocks.CUTTING_BOARD.get(), new Item.Properties()));

    // Knife item - used to cut dried herbs on the cutting board
    public static final DeferredItem<Item> KNIFE = ITEMS.register("knife",
            () -> new KnifeItem(new Item.Properties().stacksTo(1).durability(64)));

    // Herb Bundle block items - storage blocks for dried herbs
    public static final DeferredItem<Item> ERBAPIPA_BUNDLE_ITEM = ITEMS.register("erbapipa_bundle",
            () -> new HerbBundleBlockItem(ModBlocks.ERBAPIPA_BUNDLE.get(), new Item.Properties(), ChatFormatting.GREEN));

    public static final DeferredItem<Item> VALERIANA_BUNDLE_ITEM = ITEMS.register("valeriana_bundle",
            () -> new HerbBundleBlockItem(ModBlocks.VALERIANA_BUNDLE.get(), new Item.Properties(), ChatFormatting.LIGHT_PURPLE));

    public static final DeferredItem<Item> GINSENG_BUNDLE_ITEM = ITEMS.register("ginseng_bundle",
            () -> new HerbBundleBlockItem(ModBlocks.GINSENG_BUNDLE.get(), new Item.Properties(), ChatFormatting.GOLD));

    public static final DeferredItem<Item> SALVIA_BUNDLE_ITEM = ITEMS.register("salvia_bundle",
            () -> new HerbBundleBlockItem(ModBlocks.SALVIA_BUNDLE.get(), new Item.Properties(), ChatFormatting.DARK_GREEN));

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
