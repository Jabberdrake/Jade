package dev.jabberdrake.jade.commands.admin;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.jabberdrake.jade.JadeConfig;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import static dev.jabberdrake.jade.utils.TextUtils.error;
import static dev.jabberdrake.jade.utils.TextUtils.system;

public class AdminGameruleCommand {

    public static LiteralCommandNode<CommandSourceStack> buildCommand(final String label) {
        return Commands.literal(label)
                .then(Commands.literal("preventCoralFading")
                        .then(Commands.argument("value", BoolArgumentType.bool())
                                .requires(sender -> sender.getExecutor() instanceof Player)
                                .requires(sender -> sender.getSender().hasPermission("jade.admin") || sender.getSender().isOp())
                                .executes(AdminGameruleCommand::runCommandForCoralFading)
                        )
                )
                .then(Commands.literal("enablePlayerGraves")
                        .then(Commands.argument("value", BoolArgumentType.bool())
                                .requires(sender -> sender.getExecutor() instanceof Player)
                                .requires(sender -> sender.getSender().hasPermission("jade.admin") || sender.getSender().isOp())
                                .executes(AdminGameruleCommand::runCommandForPlayerGraves)
                        )
                )
                .then(Commands.literal("enableSpeedRoads")
                        .then(Commands.argument("value", BoolArgumentType.bool())
                                .requires(sender -> sender.getExecutor() instanceof Player)
                                .requires(sender -> sender.getSender().hasPermission("jade.admin") || sender.getSender().isOp())
                                .executes(AdminGameruleCommand::runCommandForSpeedRoads)
                        )
                )
                .then(Commands.literal("sayRandomAdvice")
                        .then(Commands.argument("value", BoolArgumentType.bool())
                                .requires(sender -> sender.getExecutor() instanceof Player)
                                .requires(sender -> sender.getSender().hasPermission("jade.admin") || sender.getSender().isOp())
                                .executes(AdminGameruleCommand::runCommandForRandomAdvice)
                        )
                )
                .then(Commands.literal("doMonsterGriefing")
                        .then(Commands.argument("value", BoolArgumentType.bool())
                                .requires(sender -> sender.getExecutor() instanceof Player)
                                .requires(sender -> sender.getSender().hasPermission("jade.admin") || sender.getSender().isOp())
                                .executes(AdminGameruleCommand::runCommandForMobExplosion)
                        )
                )
                .build();
    }

    public static int runCommandForCoralFading(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();

        boolean valueArg = BoolArgumentType.getBool(context, "value");

        boolean result = JadeConfig.setGamerule("preventCoralFading", valueArg);
        if (result == true) {
            if (valueArg) {
                Bukkit.broadcast(system("An operator has set gamerule <highlight>preventCoralFading</highlight> to <green>TRUE</green>!"));
            } else {
                Bukkit.broadcast(system("An operator has set gamerule <highlight>preventCoralFading</highlight> to <red>FALSE</red>!"));
            }
        } else {
            player.sendMessage(error("An error occurred while altering gamerules! Please report this to a developer!"));
        }

        return Command.SINGLE_SUCCESS;
    }

    public static int runCommandForPlayerGraves(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();

        boolean valueArg = BoolArgumentType.getBool(context, "value");

        boolean result = JadeConfig.setGamerule("enablePlayerGraves", valueArg);
        if (result == true) {
            if (valueArg) {
                Bukkit.broadcast(system("An operator has set gamerule <highlight>enablePlayerGraves</highlight> to <green>TRUE</green>!"));
            } else {
                Bukkit.broadcast(system("An operator has set gamerule <highlight>enablePlayerGraves</highlight> to <red>FALSE</red>!"));
            }
        } else {
            player.sendMessage(error("An error occurred while altering gamerules! Please report this to a developer!"));
        }

        return Command.SINGLE_SUCCESS;
    }

    public static int runCommandForSpeedRoads(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();

        boolean valueArg = BoolArgumentType.getBool(context, "value");

        boolean result = JadeConfig.setGamerule("enableSpeedRoads", valueArg);
        if (result == true) {
            if (valueArg) {
                Bukkit.broadcast(system("An operator has set gamerule <highlight>enableSpeedRoads</highlight> to <green>TRUE</green>!"));
            } else {
                Bukkit.broadcast(system("An operator has set gamerule <highlight>enableSpeedRoads</highlight> to <red>FALSE</red>!"));
            }
        } else {
            player.sendMessage(error("An error occurred while altering gamerules! Please report this to a developer!"));
        }

        return Command.SINGLE_SUCCESS;
    }

    public static int runCommandForRandomAdvice(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();

        boolean valueArg = BoolArgumentType.getBool(context, "value");

        boolean result = JadeConfig.setGamerule("sayRandomAdvice", valueArg);
        if (result == true) {
            if (valueArg) {
                Bukkit.broadcast(system("An operator has set gamerule <highlight>sayRandomAdvice</highlight> to <green>TRUE</green>!"));
            } else {
                Bukkit.broadcast(system("An operator has set gamerule <highlight>sayRandomAdvice</highlight> to <red>FALSE</red>!"));
            }
        } else {
            player.sendMessage(error("An error occurred while altering gamerules! Please report this to a developer!"));
        }

        return Command.SINGLE_SUCCESS;
    }

    public static int runCommandForMobExplosion(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();

        boolean valueArg = BoolArgumentType.getBool(context, "value");

        boolean result = JadeConfig.setGamerule("doMonsterGriefing", valueArg);
        if (result == true) {
            if (valueArg) {
                Bukkit.broadcast(system("An operator has set gamerule <highlight>doMonsterGriefing</highlight> to <green>TRUE</green>!"));
            } else {
                Bukkit.broadcast(system("An operator has set gamerule <highlight>doMonsterGriefing</highlight> to <red>FALSE</red>!"));
            }
        } else {
            player.sendMessage(error("An error occurred while altering gamerules! Please report this to a developer!"));
        }

        return Command.SINGLE_SUCCESS;
    }

}
