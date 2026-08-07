package frenk.eypipes.registries;

import frenk.eypipes.EyPipes;
import frenk.eypipes.block.CuttingBoardBlock;
import frenk.eypipes.block.DryingRackBlock;
import frenk.eypipes.block.ErbapipaCropBlock;
import frenk.eypipes.block.HerbBundleBlock;
import frenk.eypipes.block.HerbCropBlock;
import frenk.eypipes.block.PipeRackBlock;
import frenk.eypipes.block.TobaccoJarBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Registry for all EyPipes blocks using NeoForge DeferredRegister.
 *
 * <p>Blocks are registered through {@code registerBlock} rather than the raw {@code register}
 * overload so that {@link BlockBehaviour.Properties} is supplied by the registry itself. From
 * Minecraft 1.21.2 onwards the properties must carry the block's registry id, which only this
 * overload can inject; on 1.21.1 the two forms behave identically.
 */
public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(EyPipes.MOD_ID);

    private static BlockBehaviour.Properties cropProperties(MapColor color) {
        return BlockBehaviour.Properties.of()
                .mapColor(color)
                //? if <1.21.9 {
                .noCollission()
                //?} else
                /*.noCollision()*/
                .randomTicks()
                .instabreak()
                .sound(SoundType.CROP)
                .pushReaction(PushReaction.DESTROY);
    }

    private static BlockBehaviour.Properties bundleProperties(MapColor color) {
        return BlockBehaviour.Properties.of()
                .mapColor(color)
                .strength(0.5f)
                .sound(SoundType.GRASS);
    }

    // Erbapipa Crop Block - 2-block tall crop with 8 growth stages
    public static final DeferredBlock<ErbapipaCropBlock> ERBAPIPA_CROP = BLOCKS.registerBlock("erbapipa_crop",
            ErbapipaCropBlock::new, cropProperties(MapColor.PLANT));

    // Valeriana Crop Block - single-block crop with 4 growth stages
    public static final DeferredBlock<HerbCropBlock> VALERIANA_CROP = BLOCKS.registerBlock("valeriana_crop",
            props -> new HerbCropBlock(props, () -> ModItems.VALERIANA_SEEDS.get()),
            cropProperties(MapColor.COLOR_PURPLE));

    // Ginseng Crop Block - single-block crop with 4 growth stages
    public static final DeferredBlock<HerbCropBlock> GINSENG_CROP = BLOCKS.registerBlock("ginseng_crop",
            props -> new HerbCropBlock(props, () -> ModItems.GINSENG_SEEDS.get()),
            cropProperties(MapColor.TERRACOTTA_ORANGE));

    // Salvia Crop Block - single-block crop with 4 growth stages
    public static final DeferredBlock<HerbCropBlock> SALVIA_CROP = BLOCKS.registerBlock("salvia_crop",
            props -> new HerbCropBlock(props, () -> ModItems.SALVIA_SEEDS.get()),
            cropProperties(MapColor.COLOR_GRAY));

    // Drying Rack Block - Block entity for drying erbapipa
    public static final DeferredBlock<DryingRackBlock> DRYING_RACK = BLOCKS.registerBlock("drying_rack_erb",
            DryingRackBlock::new, BlockBehaviour.Properties.of()
                    .mapColor(MapColor.WOOD)
                    .strength(1.0f)
                    .sound(SoundType.WOOD)
                    .noOcclusion()
                    //? if <1.21.9 {
                    .noCollission()
                    //?} else
                    /*.noCollision()*/);

    // Tobacco Jar Block - Container for fermenting herbs
    public static final DeferredBlock<TobaccoJarBlock> TOBACCO_JAR = BLOCKS.registerBlock("tobacco_jar",
            TobaccoJarBlock::new, BlockBehaviour.Properties.of()
                    .mapColor(MapColor.TERRACOTTA_BROWN)
                    .strength(1.5f)
                    .sound(SoundType.DECORATED_POT)
                    .noOcclusion());

    // Pipe Rack Block - Wall-mounted display for pipes
    public static final DeferredBlock<PipeRackBlock> PIPE_RACK = BLOCKS.registerBlock("pipe_rack",
            PipeRackBlock::new, BlockBehaviour.Properties.of()
                    .mapColor(MapColor.WOOD)
                    .strength(1.0f)
                    .sound(SoundType.WOOD)
                    .noOcclusion());

    // Cutting Board Block - Used to cut dried herbs with a knife
    public static final DeferredBlock<CuttingBoardBlock> CUTTING_BOARD = BLOCKS.registerBlock("cutting_board",
            CuttingBoardBlock::new, BlockBehaviour.Properties.of()
                    .mapColor(MapColor.WOOD)
                    .strength(1.0f)
                    .sound(SoundType.WOOD)
                    .noOcclusion());

    // Herb Bundle Blocks - Storage blocks for dried herbs (9 herbs = 1 bundle)
    public static final DeferredBlock<HerbBundleBlock> ERBAPIPA_BUNDLE = BLOCKS.registerBlock("erbapipa_bundle",
            HerbBundleBlock::new, bundleProperties(MapColor.PLANT));

    public static final DeferredBlock<HerbBundleBlock> VALERIANA_BUNDLE = BLOCKS.registerBlock("valeriana_bundle",
            HerbBundleBlock::new, bundleProperties(MapColor.COLOR_PURPLE));

    public static final DeferredBlock<HerbBundleBlock> GINSENG_BUNDLE = BLOCKS.registerBlock("ginseng_bundle",
            HerbBundleBlock::new, bundleProperties(MapColor.TERRACOTTA_ORANGE));

    public static final DeferredBlock<HerbBundleBlock> SALVIA_BUNDLE = BLOCKS.registerBlock("salvia_bundle",
            HerbBundleBlock::new, bundleProperties(MapColor.COLOR_GRAY));

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
        EyPipes.LOGGER.info("Registering EyPipes Blocks");
    }
}
