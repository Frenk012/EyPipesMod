package frenk.eypipes.fabric;

import frenk.eypipes.EyPipes;
import frenk.eypipes.command.ReloadConfigCommand;
import frenk.eypipes.config.EyPipesConfig;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
//? if >=1.20.5 {
import fuzs.forgeconfigapiport.fabric.api.neoforge.v4.NeoForgeConfigRegistry;
import net.neoforged.fml.config.ModConfig;
//?} else {
/*import fuzs.forgeconfigapiport.api.config.v2.ForgeConfigRegistry;
import net.neoforged.fml.config.ModConfig;
*///?}

/** Fabric entry point. The (Neo)Forge builds start from {@link EyPipes} itself. */
public class EyPipesFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        EyPipes.init();

        // Forge Config API Port gives Fabric the same config files and API as (Neo)Forge
        //? if >=1.20.5 {
        NeoForgeConfigRegistry.INSTANCE.register(EyPipes.MOD_ID, ModConfig.Type.COMMON, EyPipesConfig.COMMON_SPEC);
        NeoForgeConfigRegistry.INSTANCE.register(EyPipes.MOD_ID, ModConfig.Type.CLIENT, EyPipesConfig.CLIENT_SPEC);
        //?} else {
        /*ForgeConfigRegistry.INSTANCE.register(EyPipes.MOD_ID, ModConfig.Type.COMMON, EyPipesConfig.COMMON_SPEC);
        ForgeConfigRegistry.INSTANCE.register(EyPipes.MOD_ID, ModConfig.Type.CLIENT, EyPipesConfig.CLIENT_SPEC);
        *///?}

        CommandRegistrationCallback.EVENT.register(
                (dispatcher, context, selection) -> ReloadConfigCommand.register(dispatcher));

        // Fabric registers entries as they are declared, so everything is in place already
        EyPipes.commonSetup();
    }
}
