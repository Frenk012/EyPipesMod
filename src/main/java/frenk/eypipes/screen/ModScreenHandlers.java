package frenk.eypipes.screen;

import frenk.eypipes.EyPipes;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

public class ModScreenHandlers {
    public static ScreenHandlerType<ProcessorScreenHandler> PROCESSOR_SCREEN_HANDLER;

    public static void registerAllScreenHandlers() {
        PROCESSOR_SCREEN_HANDLER = ScreenHandlerRegistry.registerSimple(
            new Identifier(EyPipes.MOD_ID, "processor"),
            ProcessorScreenHandler::new
        );
    }
}