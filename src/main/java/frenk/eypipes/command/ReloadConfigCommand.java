package frenk.eypipes.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import frenk.eypipes.config.EyPipesConfig;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class ReloadConfigCommand {
    
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
            CommandManager.literal("eypipes")
                .then(CommandManager.literal("reload")
                    .requires(source -> source.hasPermissionLevel(2)) // Requires OP level 2
                    .executes(ReloadConfigCommand::execute)
                )
        );
    }
    
    private static int execute(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        
        try {
            EyPipesConfig.reloadConfig();
            source.sendFeedback(Text.literal("§aEyPipes configuration reloaded successfully!"), true);
            return 1;
        } catch (Exception e) {
            source.sendFeedback(Text.literal("§cFailed to reload EyPipes configuration: " + e.getMessage()), true);
            return 0;
        }
    }
}