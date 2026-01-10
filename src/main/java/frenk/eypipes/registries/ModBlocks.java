package frenk.eypipes.registries;

import frenk.eypipes.EyPipes;
import frenk.eypipes.block.DryingRackBlock;
import frenk.eypipes.block.ErbapipaCropBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Registry for all EyPipes blocks using NeoForge DeferredRegister
 */
public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(EyPipes.MOD_ID);

    // Erbapipa Crop Block - 2-block tall crop with 8 growth stages
    public static final DeferredBlock<ErbapipaCropBlock> ERBAPIPA_CROP = BLOCKS.register("erbapipa_crop",
            () -> new ErbapipaCropBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.PLANT)
                    .noCollission()
                    .randomTicks()
                    .instabreak()
                    .sound(SoundType.CROP)
                    .pushReaction(PushReaction.DESTROY)));

    // Drying Rack Block - Block entity for drying erbapipa
    public static final DeferredBlock<DryingRackBlock> DRYING_RACK = BLOCKS.register("drying_rack_erb",
            () -> new DryingRackBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.WOOD)
                    .strength(1.0f)
                    .sound(SoundType.WOOD)
                    .noOcclusion()
                    .noCollission()));

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
        EyPipes.LOGGER.info("Registering EyPipes Blocks");
    }
}
