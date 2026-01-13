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

    // Valeriana Crop Block - single-block crop with 4 growth stages
    public static final DeferredBlock<HerbCropBlock> VALERIANA_CROP = BLOCKS.register("valeriana_crop",
            () -> new HerbCropBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_PURPLE)
                    .noCollission()
                    .randomTicks()
                    .instabreak()
                    .sound(SoundType.CROP)
                    .pushReaction(PushReaction.DESTROY),
                    () -> ModItems.VALERIANA_SEEDS.get()));

    // Ginseng Crop Block - single-block crop with 4 growth stages
    public static final DeferredBlock<HerbCropBlock> GINSENG_CROP = BLOCKS.register("ginseng_crop",
            () -> new HerbCropBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.TERRACOTTA_ORANGE)
                    .noCollission()
                    .randomTicks()
                    .instabreak()
                    .sound(SoundType.CROP)
                    .pushReaction(PushReaction.DESTROY),
                    () -> ModItems.GINSENG_SEEDS.get()));

    // Salvia Crop Block - single-block crop with 4 growth stages
    public static final DeferredBlock<HerbCropBlock> SALVIA_CROP = BLOCKS.register("salvia_crop",
            () -> new HerbCropBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_GRAY)
                    .noCollission()
                    .randomTicks()
                    .instabreak()
                    .sound(SoundType.CROP)
                    .pushReaction(PushReaction.DESTROY),
                    () -> ModItems.SALVIA_SEEDS.get()));

    // Drying Rack Block - Block entity for drying erbapipa
    public static final DeferredBlock<DryingRackBlock> DRYING_RACK = BLOCKS.register("drying_rack_erb",
            () -> new DryingRackBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.WOOD)
                    .strength(1.0f)
                    .sound(SoundType.WOOD)
                    .noOcclusion()
                    .noCollission()));

    // Tobacco Jar Block - Container for fermenting herbs
    public static final DeferredBlock<TobaccoJarBlock> TOBACCO_JAR = BLOCKS.register("tobacco_jar",
            () -> new TobaccoJarBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.TERRACOTTA_BROWN)
                    .strength(1.5f)
                    .sound(SoundType.DECORATED_POT)
                    .noOcclusion()));

    // Pipe Rack Block - Wall-mounted display for pipes
    public static final DeferredBlock<PipeRackBlock> PIPE_RACK = BLOCKS.register("pipe_rack",
            () -> new PipeRackBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.WOOD)
                    .strength(1.0f)
                    .sound(SoundType.WOOD)
                    .noOcclusion()));

    // Cutting Board Block - Used to cut dried herbs with a knife
    public static final DeferredBlock<CuttingBoardBlock> CUTTING_BOARD = BLOCKS.register("cutting_board",
            () -> new CuttingBoardBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.WOOD)
                    .strength(1.0f)
                    .sound(SoundType.WOOD)
                    .noOcclusion()));

    // Herb Bundle Blocks - Storage blocks for dried herbs (9 herbs = 1 bundle)
    public static final DeferredBlock<HerbBundleBlock> ERBAPIPA_BUNDLE = BLOCKS.register("erbapipa_bundle",
            () -> new HerbBundleBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.PLANT)
                    .strength(0.5f)
                    .sound(SoundType.GRASS)));

    public static final DeferredBlock<HerbBundleBlock> VALERIANA_BUNDLE = BLOCKS.register("valeriana_bundle",
            () -> new HerbBundleBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_PURPLE)
                    .strength(0.5f)
                    .sound(SoundType.GRASS)));

    public static final DeferredBlock<HerbBundleBlock> GINSENG_BUNDLE = BLOCKS.register("ginseng_bundle",
            () -> new HerbBundleBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.TERRACOTTA_ORANGE)
                    .strength(0.5f)
                    .sound(SoundType.GRASS)));

    public static final DeferredBlock<HerbBundleBlock> SALVIA_BUNDLE = BLOCKS.register("salvia_bundle",
            () -> new HerbBundleBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_GRAY)
                    .strength(0.5f)
                    .sound(SoundType.GRASS)));

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
        EyPipes.LOGGER.info("Registering EyPipes Blocks");
    }
}
