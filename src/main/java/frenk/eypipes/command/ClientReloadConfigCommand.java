package frenk.eypipes.command;

import com.mojang.brigadier.CommandDispatcher;
import frenk.eypipes.config.EyPipesConfig;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;

public class ClientReloadConfigCommand {
    
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(
            ClientCommandManager.literal("eypipes")
                .then(ClientCommandManager.literal("reload")
                    .executes(context -> {
                        try {
                            EyPipesConfig.loadConfig();
                            context.getSource().sendFeedback(Text.literal("§aEyPipes client configuration reloaded successfully!"));
                            return 1;
                        } catch (Exception e) {
                            context.getSource().sendError(Text.literal("§cFailed to reload EyPipes configuration: " + e.getMessage()));
                            return 0;
                        }
                    })
                )
        );
    }
}