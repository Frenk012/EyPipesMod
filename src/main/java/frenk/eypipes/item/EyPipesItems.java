package frenk.eypipes.item;

import net.minecraft.item.AliasedBlockItem;
import net.minecraft.item.Item;
import frenk.eypipes.EyPipes;
import frenk.eypipes.block.EyPipesBlocks;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.ArrayList;

public class EyPipesItems {
    public static void init(){
        EyPipes.LOGGER.info("Initializing Pipes Items!");
    }
    public static final ArrayList<Item> ITEMS = new ArrayList<>();

    public static final Item ERBAPIPA = registerItem("erbapipa",
            new Item(new FabricItemSettings().group(EyPipes.EY_PIPES_GROUP)));
    public static final Item ERBAPIPA_DRIED = registerItem("erbapipa_dried",
            new Item(new FabricItemSettings().group(EyPipes.EY_PIPES_GROUP)));
    public static final Item ERBAPIPA_CUTTED = registerItem("erbapipa_cutted",
            new Item(new FabricItemSettings().group(EyPipes.EY_PIPES_GROUP)));
    public static final Item ERBAPIPA_SEEDS = registerItem("erbapipa_seeds",
            new AliasedBlockItem(EyPipesBlocks.ERBAPIPA_CROP, new FabricItemSettings().group(EyPipes.EY_PIPES_GROUP)));
    public static final Item PIPE = Registry.register(Registry.ITEM, new Identifier(EyPipes.MOD_ID, "pipe"), new PipeItem(new FabricItemSettings().group(EyPipes.EY_PIPES_GROUP).maxCount(1),500));
    public static final Item CIGAR = Registry.register(Registry.ITEM, new Identifier(EyPipes.MOD_ID, "cigar"), new CigarItem(new FabricItemSettings().group(EyPipes.EY_PIPES_GROUP).maxCount(1),500));

    public static Item registerItem(String name, Item item) {
        ITEMS.add(item);
        return Registry.register(Registry.ITEM, new Identifier(EyPipes.MOD_ID, name), item);
    }

    public static void registerItems() {
    }
}

