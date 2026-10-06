package frenk.eypipes.registries;

import frenk.eypipes.EyPipes;
import frenk.eypipes.block.entity.CuttingBoardBlockEntity;
import frenk.eypipes.block.entity.DryingRackBlockEntity;
import frenk.eypipes.block.entity.HerbBundleBlockEntity;
import frenk.eypipes.block.entity.PipeRackBlockEntity;
import frenk.eypipes.block.entity.TobaccoJarBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import frenk.eypipes.platform.Registrar;
import frenk.eypipes.platform.RegistryEntry;

/**
 * Registry for all EyPipes block entities
 */
public class ModBlockEntities {
    public static final Registrar<BlockEntityType<?>> BLOCK_ENTITIES = Registrar.create(Registries.BLOCK_ENTITY_TYPE);

    // Drying Rack Block Entity
    public static final RegistryEntry<BlockEntityType<DryingRackBlockEntity>> DRYING_RACK =
            BLOCK_ENTITIES.register("drying_rack_erb",
                    //? if <1.21.5 {
                    () -> BlockEntityType.Builder.of(DryingRackBlockEntity::new, ModBlocks.DRYING_RACK.get()).build(null));
                    //?} else
                    /*() -> new BlockEntityType<>(DryingRackBlockEntity::new, ModBlocks.DRYING_RACK.get()));*/

    // Tobacco Jar Block Entity
    public static final RegistryEntry<BlockEntityType<TobaccoJarBlockEntity>> TOBACCO_JAR =
            BLOCK_ENTITIES.register("tobacco_jar",
                    //? if <1.21.5 {
                    () -> BlockEntityType.Builder.of(TobaccoJarBlockEntity::new, ModBlocks.TOBACCO_JAR.get()).build(null));
                    //?} else
                    /*() -> new BlockEntityType<>(TobaccoJarBlockEntity::new, ModBlocks.TOBACCO_JAR.get()));*/

    // Pipe Rack Block Entity
    public static final RegistryEntry<BlockEntityType<PipeRackBlockEntity>> PIPE_RACK =
            BLOCK_ENTITIES.register("pipe_rack",
                    //? if <1.21.5 {
                    () -> BlockEntityType.Builder.of(PipeRackBlockEntity::new, ModBlocks.PIPE_RACK.get()).build(null));
                    //?} else
                    /*() -> new BlockEntityType<>(PipeRackBlockEntity::new, ModBlocks.PIPE_RACK.get()));*/

    // Cutting Board Block Entity
    public static final RegistryEntry<BlockEntityType<CuttingBoardBlockEntity>> CUTTING_BOARD =
            BLOCK_ENTITIES.register("cutting_board",
                    //? if <1.21.5 {
                    () -> BlockEntityType.Builder.of(CuttingBoardBlockEntity::new, ModBlocks.CUTTING_BOARD.get()).build(null));
                    //?} else
                    /*() -> new BlockEntityType<>(CuttingBoardBlockEntity::new, ModBlocks.CUTTING_BOARD.get()));*/

    // Herb Bundle Block Entity (for all 4 bundle types)
    public static final RegistryEntry<BlockEntityType<HerbBundleBlockEntity>> HERB_BUNDLE =
            BLOCK_ENTITIES.register("herb_bundle",
                    //? if <1.21.5 {
                    () -> BlockEntityType.Builder.of(HerbBundleBlockEntity::new, ModBlocks.ERBAPIPA_BUNDLE.get(), ModBlocks.VALERIANA_BUNDLE.get(), ModBlocks.GINSENG_BUNDLE.get(), ModBlocks.SALVIA_BUNDLE.get()).build(null));
                    //?} else
                    /*() -> new BlockEntityType<>(HerbBundleBlockEntity::new, ModBlocks.ERBAPIPA_BUNDLE.get(), ModBlocks.VALERIANA_BUNDLE.get(), ModBlocks.GINSENG_BUNDLE.get(), ModBlocks.SALVIA_BUNDLE.get()));*/

    /** Loads this class, which declares (and on Fabric registers) its entries. */
    public static void init() {
        EyPipes.LOGGER.info("Registering EyPipes Block Entities");
    }
}
