package frenk.eypipes.block;

import frenk.eypipes.EyPipes;
import net.minecraft.block.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import net.minecraft.block.Block;

public class EyPipesBlocks {
    
    public static final Block ERBAPIPA_CROP = registerBlock("erbapipa_crop",
            new ErbapipaCropBlock(AbstractBlock.Settings.copy(Blocks.WHEAT)));
        
    private static Block registerBlock(String name, Block block) {
        return Registry.register(Registry.BLOCK, new Identifier(EyPipes.MOD_ID, name), block);
    }

    public static void registerBlocks() {
    }
}
