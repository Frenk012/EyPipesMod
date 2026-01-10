package frenk.eypipes.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import frenk.eypipes.EyPipes;
import frenk.eypipes.registries.ModItems;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

/**
 * Command to reload EyPipes configuration.
 * Usage: /eypipes reload
 * Requires operator level 2.
 * Ported from Fabric 1.19.2 to NeoForge 1.21.1
 */
public class ReloadConfigCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("eypipes")
                .requires(source -> source.hasPermission(2)) // Requires OP level 2
                .then(Commands.literal("reload")
                        .executes(ReloadConfigCommand::execute)));
    }

    private static int execute(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();

        try {
            // In NeoForge, config is managed by the config system and auto-reloads on file change
            // We can force a re-registration of compostables with current values
            ModItems.registerCompostables();

            source.sendSuccess(
                    () -> Component.literal("\u00a7aEyPipes configuration reloaded successfully!"),
                    true
            );

            EyPipes.LOGGER.info("Configuration reloaded via command");
            return 1;
        } catch (Exception e) {
            source.sendFailure(
                    Component.literal("\u00a7cFailed to reload EyPipes configuration: " + e.getMessage())
            );

            EyPipes.LOGGER.error("Failed to reload configuration", e);
            return 0;
        }
    }
}
