package frenk.eypipes.registries;

import frenk.eypipes.EyPipes;
import java.util.function.Function;
import frenk.eypipes.block.CuttingBoardBlock;
import frenk.eypipes.block.DryingRackBlock;
import frenk.eypipes.block.ErbapipaCropBlock;
import frenk.eypipes.block.HerbBundleBlock;
import frenk.eypipes.block.HerbCropBlock;
import frenk.eypipes.block.PipeRackBlock;
import frenk.eypipes.block.TobaccoJarBlock;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import frenk.eypipes.platform.Registrar;
import frenk.eypipes.platform.RegistryEntry;

/**
 * Registry for all EyPipes blocks.
 *
 * <p>Blocks are declared through {@code block}, which builds the block only once its
 * {@link BlockBehaviour.Properties} carry the registry id that Minecraft requires from 1.21.2.
 */
public class ModBlocks {
    public static final Registrar<Block> BLOCKS = Registrar.create(Registries.BLOCK);

    private static <B extends Block> RegistryEntry<B> block(String name,
            Function<BlockBehaviour.Properties, B> factory, BlockBehaviour.Properties props) {
        return BLOCKS.register(name, () -> factory.apply(withId(name, props)));
    }

    /** From 1.21.2 a block's properties must carry its registry id before the block is built. */
    private static BlockBehaviour.Properties withId(String name, BlockBehaviour.Properties props) {
        //? if >=1.21.2 {
        /*return props.setId(net.minecraft.resources.ResourceKey.create(Registries.BLOCK, EyPipes.id(name)));
        *///?} else
        return props;
    }

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
    public static final RegistryEntry<ErbapipaCropBlock> ERBAPIPA_CROP = block("erbapipa_crop",
            ErbapipaCropBlock::new, cropProperties(MapColor.PLANT));

    // Valeriana Crop Block - single-block crop with 4 growth stages
    public static final RegistryEntry<HerbCropBlock> VALERIANA_CROP = block("valeriana_crop",
            props -> new HerbCropBlock(props, () -> ModItems.VALERIANA_SEEDS.get()),
            cropProperties(MapColor.COLOR_PURPLE));

    // Ginseng Crop Block - single-block crop with 4 growth stages
    public static final RegistryEntry<HerbCropBlock> GINSENG_CROP = block("ginseng_crop",
            props -> new HerbCropBlock(props, () -> ModItems.GINSENG_SEEDS.get()),
            cropProperties(MapColor.TERRACOTTA_ORANGE));

    // Salvia Crop Block - single-block crop with 4 growth stages
    public static final RegistryEntry<HerbCropBlock> SALVIA_CROP = block("salvia_crop",
            props -> new HerbCropBlock(props, () -> ModItems.SALVIA_SEEDS.get()),
            cropProperties(MapColor.COLOR_GRAY));

    // Drying Rack Block - Block entity for drying erbapipa
    public static final RegistryEntry<DryingRackBlock> DRYING_RACK = block("drying_rack_erb",
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
    public static final RegistryEntry<TobaccoJarBlock> TOBACCO_JAR = block("tobacco_jar",
            TobaccoJarBlock::new, BlockBehaviour.Properties.of()
                    .mapColor(MapColor.TERRACOTTA_BROWN)
                    .strength(1.5f)
                    .sound(SoundType.DECORATED_POT)
                    .noOcclusion());

    // Pipe Rack Block - Wall-mounted display for pipes
    public static final RegistryEntry<PipeRackBlock> PIPE_RACK = block("pipe_rack",
            PipeRackBlock::new, BlockBehaviour.Properties.of()
                    .mapColor(MapColor.WOOD)
                    .strength(1.0f)
                    .sound(SoundType.WOOD)
                    .noOcclusion());

    // Cutting Board Block - Used to cut dried herbs with a knife
    public static final RegistryEntry<CuttingBoardBlock> CUTTING_BOARD = block("cutting_board",
            CuttingBoardBlock::new, BlockBehaviour.Properties.of()
                    .mapColor(MapColor.WOOD)
                    .strength(1.0f)
                    .sound(SoundType.WOOD)
                    .noOcclusion());

    // Herb Bundle Blocks - Storage blocks for dried herbs (9 herbs = 1 bundle)
    public static final RegistryEntry<HerbBundleBlock> ERBAPIPA_BUNDLE = block("erbapipa_bundle",
            HerbBundleBlock::new, bundleProperties(MapColor.PLANT));

    public static final RegistryEntry<HerbBundleBlock> VALERIANA_BUNDLE = block("valeriana_bundle",
            HerbBundleBlock::new, bundleProperties(MapColor.COLOR_PURPLE));

    public static final RegistryEntry<HerbBundleBlock> GINSENG_BUNDLE = block("ginseng_bundle",
            HerbBundleBlock::new, bundleProperties(MapColor.TERRACOTTA_ORANGE));

    public static final RegistryEntry<HerbBundleBlock> SALVIA_BUNDLE = block("salvia_bundle",
            HerbBundleBlock::new, bundleProperties(MapColor.COLOR_GRAY));

    /** Loads this class, which declares (and on Fabric registers) its entries. */
    public static void init() {
        EyPipes.LOGGER.info("Registering EyPipes Blocks");
    }
}
