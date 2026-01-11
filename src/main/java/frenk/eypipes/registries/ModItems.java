package frenk.eypipes.registries;

import frenk.eypipes.EyPipes;
import frenk.eypipes.config.EyPipesConfig;
import frenk.eypipes.item.CigarItem;
import frenk.eypipes.item.PipeItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.level.block.ComposterBlock;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

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

    // Seeds item (plants the crop)
    public static final DeferredItem<Item> ERBAPIPA_SEEDS = ITEMS.register("erbapipa_seeds",
            () -> new ItemNameBlockItem(ModBlocks.ERBAPIPA_CROP.get(), new Item.Properties()));

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
