package frenk.eypipes;

import frenk.eypipes.data.*;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class EyPipesDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator generator) {
        generator.addProvider(EyPipesLoot::new);
        generator.addProvider(EyPipesModels::new);
        //generator.addProvider(EyPipesRecipes::new);
        generator.addProvider(EyPipesItemTags::new);
        //generator.addProvider(EyPipesBlockTags::new);
	}
}
