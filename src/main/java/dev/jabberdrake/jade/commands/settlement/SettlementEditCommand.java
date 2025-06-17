package dev.jabberdrake.jade.commands.settlement;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.jabberdrake.jade.commands.CommonArgumentSuggestions;
import dev.jabberdrake.jade.commands.SettlementCommand;
import dev.jabberdrake.jade.players.PlayerManager;
import dev.jabberdrake.jade.realms.RealmManager;
import dev.jabberdrake.jade.realms.Settlement;
import dev.jabberdrake.jade.utils.JadeTextColor;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemType;

import static dev.jabberdrake.jade.utils.TextUtils.error;
import static dev.jabberdrake.jade.utils.TextUtils.info;

public class SettlementEditCommand {

    public static LiteralCommandNode<CommandSourceStack> buildCommand(final String label) {
        return Commands.literal(label)
                .then(Commands.literal("name")
                        .then(Commands.argument("name", StringArgumentType.word())
                                .requires(sender -> sender.getExecutor() instanceof Player)
                                .executes(SettlementEditCommand::runCommandForName)
                        )
                )
                .then(Commands.literal("display")
                        .then(Commands.argument("display_name", StringArgumentType.greedyString())
                                .requires(sender -> sender.getExecutor() instanceof Player)
                                .executes(SettlementEditCommand::runCommandForDisplay)
                        )
                )
                .then(Commands.literal("description")
                        .then(Commands.argument("description", StringArgumentType.greedyString())
                                .requires(sender -> sender.getExecutor() instanceof Player)
                                .executes(SettlementEditCommand::runCommandForDescription)
                        )
                )
                .then(Commands.literal("mapColor")
                        .then(Commands.argument("color_name_or_hex", StringArgumentType.greedyString())
                                .suggests(CommonArgumentSuggestions::suggestVanillaTextColors)
                                .requires(sender -> sender.getExecutor() instanceof Player)
                                .executes(SettlementEditCommand::runCommandForMapColor)
                        )
                )
                .then(Commands.literal("icon")
                        .then(Commands.argument("icon", ArgumentTypes.namespacedKey())
                                .suggests(CommonArgumentSuggestions::suggestAllItemKeys)
                                .requires(sender -> sender.getExecutor() instanceof Player)
                                .executes(SettlementEditCommand::runCommandForIcon)
                        )
                )
                .build();
    }

    public static int runCommandForName(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();
        Settlement focus = PlayerManager.asJadePlayer(player.getUniqueId()).getFocusSettlement();

        if (!SettlementCommand.validateFocusSettlement(player, focus)) { return Command.SINGLE_SUCCESS; }
        if (!validateUserPermissions(player, focus)) { return Command.SINGLE_SUCCESS; }

        String nameArgument = StringArgumentType.getString(context, "name");
        if (!RealmManager.isUniqueSettlementName(nameArgument)) {
            player.sendMessage(error("That name (<highlight>" + nameArgument + "</highlight>) is already in use!"));
            return Command.SINGLE_SUCCESS;
        }

        focus.setName(nameArgument);
        focus.broadcast("Changed <highlight>reference name</highlight> to <highlight><i>" + focus.getName() + "</i></highlight>!");
        return Command.SINGLE_SUCCESS;
    }

    public static int runCommandForDisplay(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();
        Settlement focus = PlayerManager.asJadePlayer(player.getUniqueId()).getFocusSettlement();

        if (!SettlementCommand.validateFocusSettlement(player, focus)) { return Command.SINGLE_SUCCESS; }
        if (!validateUserPermissions(player, focus)) { return Command.SINGLE_SUCCESS; }

        String displayArgument = StringArgumentType.getString(context, "display_name");

        focus.setDisplayName(displayArgument);
        focus.broadcast("Changed <highlight>display name</highlight> to " + focus.getDisplayName() + "<normal>!");

        return Command.SINGLE_SUCCESS;
    }

    public static int runCommandForDescription(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();
        Settlement focus = PlayerManager.asJadePlayer(player.getUniqueId()).getFocusSettlement();

        if (!SettlementCommand.validateFocusSettlement(player, focus)) { return Command.SINGLE_SUCCESS; }
        if (!validateUserPermissions(player, focus)) { return Command.SINGLE_SUCCESS; }

        String descArgument = StringArgumentType.getString(context, "description");
        if (!descArgument.startsWith("<")) {
            descArgument = "<white>" + descArgument;
        }
        focus.setDescription(descArgument);
        focus.broadcast("Changed <highlight>description</highlight> to: " + focus.getDescription());
        return Command.SINGLE_SUCCESS;
    }

    public static int runCommandForMapColor(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();
        Settlement focus = PlayerManager.asJadePlayer(player.getUniqueId()).getFocusSettlement();

        if (!SettlementCommand.validateFocusSettlement(player, focus)) { return Command.SINGLE_SUCCESS; }
        if (!validateUserPermissions(player, focus)) { return Command.SINGLE_SUCCESS; }

        String colorArgument = StringArgumentType.getString(context, "color_name_or_hex");
        TextColor color = JadeTextColor.fromNameOrHexstring(colorArgument, true);
        if (color == null) {
            player.sendMessage(error("Please provide a valid color name or hexstring!"));
            player.sendMessage(info("A hexstring is composed of a <highlight>#</highlight> and six <highlight>hexadecimal</highlight> characters (0-9 and a-f), e.g.: <gold>#ffaa00"));
            player.sendMessage(info("This input recognizes all default Minecraft colors. For a full list of valid names, see this " +
                    "<hover:show_text:'<zorba>Clicking this text will open </zorba><aqua>https://minecraft.wiki/w/Formatting_codes#Color_codes</aqua>'>" +
                    "<click:open_url:https://minecraft.wiki/w/Formatting_codes#Color_codes>webpage</click>" +
                    "</hover>."));
            return Command.SINGLE_SUCCESS;
        }

        focus.setMapColor(color);
        focus.broadcast("Changed <highlight>map color</highlight> to <color:" + color.asHexString() + ">" + colorArgument + "</color>!");
        return Command.SINGLE_SUCCESS;
    }

    public static int runCommandForIcon(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();
        Settlement focus = PlayerManager.asJadePlayer(player.getUniqueId()).getFocusSettlement();

        if (!SettlementCommand.validateFocusSettlement(player, focus)) { return Command.SINGLE_SUCCESS; }
        if (!validateUserPermissions(player, focus)) { return Command.SINGLE_SUCCESS; }

        NamespacedKey iconArgument = context.getArgument("icon", NamespacedKey.class);
        switch (iconArgument.getNamespace()) {
            case "minecraft":
                ItemType dummy = RegistryAccess.registryAccess().getRegistry(RegistryKey.ITEM).get(iconArgument);
                if (dummy == null) {
                    player.sendMessage(error("Please provide a valid item key!"));
                    return Command.SINGLE_SUCCESS;
                }
                break;
            case "jade":
                // TODO: implement this
                player.sendMessage(error("This feature has not been implemented yet!"));
                return Command.SINGLE_SUCCESS;
            default:
                player.sendMessage(error("Please provide a valid namespace!"));
                return Command.SINGLE_SUCCESS;
        }

        focus.setIcon(iconArgument);
        focus.broadcast("Changed <highlight>icon</highlight> to <highlight>" + focus.getIconAsString() + "</highlight>!");
        return Command.SINGLE_SUCCESS;
    }

    public static boolean validateUserPermissions(Player player, Settlement settlement) {
        if (!settlement.getRoleFromMember(player.getUniqueId()).canEdit()) {
            player.sendMessage(error("You are not allowed to edit attributes in <highlight>" + settlement.getDisplayName() + "<normal>!"));
            return false;
        }
        return true;
    }
}
