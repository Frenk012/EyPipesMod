package frenk.eypipes.registries;

import frenk.eypipes.EyPipes;
import frenk.eypipes.block.entity.CuttingBoardBlockEntity;
import frenk.eypipes.block.entity.DryingRackBlockEntity;
import frenk.eypipes.block.entity.PipeRackBlockEntity;
import frenk.eypipes.block.entity.TobaccoJarBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Registry for all EyPipes block entities using NeoForge DeferredRegister
 */
public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, EyPipes.MOD_ID);

    // Drying Rack Block Entity
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<DryingRackBlockEntity>> DRYING_RACK =
            BLOCK_ENTITIES.register("drying_rack_erb",
                    () -> BlockEntityType.Builder.of(DryingRackBlockEntity::new, ModBlocks.DRYING_RACK.get())
                            .build(null));

    // Tobacco Jar Block Entity
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TobaccoJarBlockEntity>> TOBACCO_JAR =
            BLOCK_ENTITIES.register("tobacco_jar",
                    () -> BlockEntityType.Builder.of(TobaccoJarBlockEntity::new, ModBlocks.TOBACCO_JAR.get())
                            .build(null));

    // Pipe Rack Block Entity
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<PipeRackBlockEntity>> PIPE_RACK =
            BLOCK_ENTITIES.register("pipe_rack",
                    () -> BlockEntityType.Builder.of(PipeRackBlockEntity::new, ModBlocks.PIPE_RACK.get())
                            .build(null));

    // Cutting Board Block Entity
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CuttingBoardBlockEntity>> CUTTING_BOARD =
            BLOCK_ENTITIES.register("cutting_board",
                    () -> BlockEntityType.Builder.of(CuttingBoardBlockEntity::new, ModBlocks.CUTTING_BOARD.get())
                            .build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
        EyPipes.LOGGER.info("Registering EyPipes Block Entities");
    }
}
