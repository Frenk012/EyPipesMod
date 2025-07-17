package frenk.eypipes;

import net.minecraft.util.registry.Registry;

import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.util.Identifier;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
public class PipesEntities {

    public static void init(){
        EyPipes.LOGGER.info("Initializing Pipes Entities!");
    }
    public static BlockEntityType<DryingRackErbBlockEntity> DRYING_RACK_ENTITY;

    public static final Identifier DRYING_RACK_ID = new Identifier(EyPipes.MOD_ID, "drying_rack_erb");
    public static final Block DRYING_RACK = new DryingRackErbBlock(FabricBlockSettings.of(Material.WOOD).strength(1.0f).nonOpaque().noCollision());
    
    public static void registerBlocks() {
        Registry.register(Registry.BLOCK, DRYING_RACK_ID, DRYING_RACK);
        Registry.register(Registry.ITEM, DRYING_RACK_ID,
            new BlockItem(DRYING_RACK, new FabricItemSettings().group(EyPipes.EY_PIPES_GROUP)));
    }

    public static void registerBlockEntities() {
        DRYING_RACK_ENTITY = Registry.register(
            Registry.BLOCK_ENTITY_TYPE,
            DRYING_RACK_ID,
            FabricBlockEntityTypeBuilder.create(DryingRackErbBlockEntity::new, DRYING_RACK).build(null)
        );
    }
}