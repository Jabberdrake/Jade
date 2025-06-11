package dev.jabberdrake.jade.commands.nation;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.jabberdrake.jade.commands.CommonArgumentSuggestions;
import dev.jabberdrake.jade.commands.NationCommand;
import dev.jabberdrake.jade.commands.SettlementCommand;
import dev.jabberdrake.jade.commands.settlement.SettlementEditCommand;
import dev.jabberdrake.jade.players.PlayerManager;
import dev.jabberdrake.jade.realms.Nation;
import dev.jabberdrake.jade.realms.RealmManager;
import dev.jabberdrake.jade.realms.Settlement;
import dev.jabberdrake.jade.utils.TextUtils;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemType;

public class NationEditCommand {

    public static LiteralCommandNode<CommandSourceStack> buildCommand(final String label) {
        return Commands.literal(label)
                .then(Commands.literal("name")
                        .then(Commands.argument("name", StringArgumentType.word())
                                .requires(sender -> sender.getExecutor() instanceof Player)
                                .executes(NationEditCommand::runCommandForName)
                        )
                )
                .then(Commands.literal("display")
                        .then(Commands.argument("display_name", StringArgumentType.greedyString())
                                .requires(sender -> sender.getExecutor() instanceof Player)
                                .executes(NationEditCommand::runCommandForDisplay)
                        )
                )
                .then(Commands.literal("description")
                        .then(Commands.argument("description", StringArgumentType.greedyString())
                                .requires(sender -> sender.getExecutor() instanceof Player)
                                .executes(NationEditCommand::runCommandForDescription)
                        )
                )
                .then(Commands.literal("mapColor")
                        .then(Commands.argument("color_name_or_hex", StringArgumentType.greedyString())
                                .requires(sender -> sender.getExecutor() instanceof Player)
                                .executes(NationEditCommand::runCommandForMapColor)
                        )
                )
                .then(Commands.literal("icon")
                        .then(Commands.argument("icon", ArgumentTypes.namespacedKey())
                                .suggests(CommonArgumentSuggestions::suggestAllItemKeys)
                                .requires(sender -> sender.getExecutor() instanceof Player)
                                .executes(NationEditCommand::runCommandForIcon)
                        )
                )
                .build();
    }

    public static int runCommandForName(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();
        Settlement focus = PlayerManager.asJadePlayer(player.getUniqueId()).getFocusSettlement();

        if (!SettlementCommand.validateFocusSettlement(player, focus)) { return Command.SINGLE_SUCCESS; }
        if (!SettlementCommand.validateFocusLeadership(player, focus)) { return Command.SINGLE_SUCCESS; }
        if (!NationCommand.validateCapitalStatus(player, focus)) { return Command.SINGLE_SUCCESS; }

        String nameArgument = StringArgumentType.getString(context, "name");
        if (!RealmManager.isUniqueNationName(nameArgument)) {
            player.sendMessage(TextUtils.composeSimpleErrorMessage("That name is already in use!"));
            return Command.SINGLE_SUCCESS;
        }

        focus.getNation().setName(nameArgument);
        player.sendMessage(TextUtils.composeSimpleSuccessMessage("Changed reference name to ")
                .append(TextUtils.composeSuccessHighlight(focus.getNation().getName()))
                .append(TextUtils.composeSuccessText("!")));

        return Command.SINGLE_SUCCESS;
    }

    public static int runCommandForDisplay(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();
        Settlement focus = PlayerManager.asJadePlayer(player.getUniqueId()).getFocusSettlement();

        if (!SettlementCommand.validateFocusSettlement(player, focus)) { return Command.SINGLE_SUCCESS; }
        if (!SettlementCommand.validateFocusLeadership(player, focus)) { return Command.SINGLE_SUCCESS; }
        if (!NationCommand.validateCapitalStatus(player, focus)) { return Command.SINGLE_SUCCESS; }

        String displayArgument = StringArgumentType.getString(context, "display_name");
        focus.getNation().setDisplayName(displayArgument);
        player.sendMessage(TextUtils.composeSimpleSuccessMessage("Changed display name to ")
                .append(focus.getNation().getDisplayName())
                .append(TextUtils.composeSuccessText("!")));

        return Command.SINGLE_SUCCESS;
    }

    public static int runCommandForDescription(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();
        Settlement focus = PlayerManager.asJadePlayer(player.getUniqueId()).getFocusSettlement();

        if (!SettlementCommand.validateFocusSettlement(player, focus)) { return Command.SINGLE_SUCCESS; }
        if (!SettlementCommand.validateFocusLeadership(player, focus)) { return Command.SINGLE_SUCCESS; }
        if (!NationCommand.validateCapitalStatus(player, focus)) { return Command.SINGLE_SUCCESS; }

        String descArgument = StringArgumentType.getString(context, "description");
        focus.getNation().setDescription(descArgument);
        player.sendMessage(TextUtils.composeSimpleSuccessMessage("Changed description to \"")
                .append(focus.getDescription())
                .append(TextUtils.composeSuccessText("\"!")));

        return Command.SINGLE_SUCCESS;
    }

    public static int runCommandForMapColor(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();
        Settlement focus = PlayerManager.asJadePlayer(player.getUniqueId()).getFocusSettlement();

        if (!SettlementCommand.validateFocusSettlement(player, focus)) { return Command.SINGLE_SUCCESS; }
        if (!SettlementCommand.validateFocusLeadership(player, focus)) { return Command.SINGLE_SUCCESS; }
        if (!NationCommand.validateCapitalStatus(player, focus)) { return Command.SINGLE_SUCCESS; }

        String colorArgument = StringArgumentType.getString(context, "color_name_or_hex");
        TextColor color;
        if (colorArgument.startsWith("#")) {
            color = TextColor.fromHexString(colorArgument);
            if (color == null) {
                player.sendMessage(TextUtils.composeSimpleErrorMessage("Please provide a valid hexstring!"));
                player.sendMessage(TextUtils.composeSimpleInfoMessage("A hexstring is composed of a # and six hexadecimal characters (0-9 and a-f), e.g.: ")
                        .append(Component.text("#ffaa00", NamedTextColor.GOLD)));
                return Command.SINGLE_SUCCESS;
            }
        } else if (NamedTextColor.NAMES.keys().contains(colorArgument)) {
            color = NamedTextColor.NAMES.value(colorArgument);
        } else {
            player.sendMessage(TextUtils.composeSimpleErrorMessage("Please provide a valid color name!"));
            return Command.SINGLE_SUCCESS;
        }

        focus.getNation().setMapColor(color);
        player.sendMessage(TextUtils.composeSimpleSuccessMessage("Changed map color to \"")
                .append(Component.text(colorArgument).color(color))
                .append(TextUtils.composeSuccessText("\"!")));

        return Command.SINGLE_SUCCESS;
    }

    public static int runCommandForIcon(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();
        Settlement focus = PlayerManager.asJadePlayer(player.getUniqueId()).getFocusSettlement();

        if (!SettlementCommand.validateFocusSettlement(player, focus)) { return Command.SINGLE_SUCCESS; }
        if (!SettlementCommand.validateFocusLeadership(player, focus)) { return Command.SINGLE_SUCCESS; }
        if (!NationCommand.validateCapitalStatus(player, focus)) { return Command.SINGLE_SUCCESS; }

        NamespacedKey iconArgument = context.getArgument("icon", NamespacedKey.class);
        switch (iconArgument.getNamespace()) {
            case "minecraft":
                ItemType dummy = RegistryAccess.registryAccess().getRegistry(RegistryKey.ITEM).get(iconArgument);
                if (dummy == null) {
                    player.sendMessage(TextUtils.composeSimpleErrorMessage("Please provide a valid item key!"));
                    return Command.SINGLE_SUCCESS;
                }
                break;
            case "jade":
                // TODO: implement this
                player.sendMessage(TextUtils.composeSimpleErrorMessage("This feature has not been implemented yet!"));
                return Command.SINGLE_SUCCESS;
            default:
                player.sendMessage(TextUtils.composeSimpleErrorMessage("Please provide a valid namespace!"));
                return Command.SINGLE_SUCCESS;
        }

        focus.getNation().setIcon(iconArgument);
        player.sendMessage(TextUtils.composeSimpleSuccessMessage("Changed icon to ")
                .append(TextUtils.composeSuccessHighlight(focus.getNation().getIconAsString()))
                .append(TextUtils.composeSuccessText("!")));

        return Command.SINGLE_SUCCESS;
    }
}
