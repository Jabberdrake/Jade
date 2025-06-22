package dev.jabberdrake.jade.commands.admin;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.jabberdrake.jade.commands.settlement.CommonSettlementSuggestions;
import dev.jabberdrake.jade.realms.ChunkAnchor;
import dev.jabberdrake.jade.realms.RealmManager;
import dev.jabberdrake.jade.realms.Settlement;
import dev.jabberdrake.jade.realms.SettlementRole;
import dev.jabberdrake.jade.utils.TextUtils;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemLore;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class AdminDumpCommand {

    private static final String INDENT = "     ";

    public static LiteralCommandNode<CommandSourceStack> buildCommand(final String label) {
        return Commands.literal(label)
                .then(Commands.literal("realms")
                        .then(Commands.literal("settlement_cache")
                                .requires(sender -> sender.getExecutor() instanceof Player)
                                .requires(sender -> sender.getSender().hasPermission("jade.admin") || sender.getSender().isOp())
                                .executes(AdminDumpCommand::runCommandForStmCache)
                        )
                        .then(Commands.literal("territory_map")
                                .requires(sender -> sender.getExecutor() instanceof Player)
                                .requires(sender -> sender.getSender().hasPermission("jade.admin") || sender.getSender().isOp())
                                .executes(AdminDumpCommand::runCommandForTerritoryMap)
                        )
                        .then(Commands.literal("settlement_roles")
                                .then(Commands.argument("settlement", StringArgumentType.string())
                                        .suggests(CommonSettlementSuggestions::suggestAllSettlements)
                                        .requires(sender -> sender.getExecutor() instanceof Player)
                                        .requires(sender -> sender.getSender().hasPermission("jade.admin") || sender.getSender().isOp())
                                        .executes(AdminDumpCommand::runCommandForRoles))
                        )
                )
                .build();
    }

    public static int runCommandForStmCache(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();

        player.sendMessage(TextUtils.composeSimpleOperatorMessage("Dumping ")
                .append(TextUtils.composeOperatorHighlight("RealmManager::settlementCache"))
                .append(TextUtils.composeOperatorText(":")));

        for (Settlement settlement : RealmManager.getAllSettlements()) {
            ItemStack hoverItem = ItemStack.of(Material.STONE);
            hoverItem.setData(DataComponentTypes.CUSTOM_NAME, settlement.asTextComponent().decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE));
            hoverItem.setData(DataComponentTypes.LORE, ItemLore.lore()
                    .addLine(settlement.getDescriptionAsComponent().decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE))
                    .addLine(Component.text(""))
                    .addLine(Component.text()
                            .content("Left click").color(NamedTextColor.GREEN).decorate(TextDecoration.BOLD)
                            .append(Component.text(" to read more").color(NamedTextColor.DARK_GREEN))
                            .build().decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE))
                    .build());

            player.sendMessage(
                    Component.text()
                            .append(TextUtils.composeOperatorText(INDENT + "[" + settlement.getId() + ", "))
                            .append(settlement.asTextComponent()
                                    .hoverEvent(hoverItem.asHoverEvent())
                                    .clickEvent(ClickEvent.runCommand("/settlement info " + settlement.getName()))
                            )
                            .append(TextUtils.composeOperatorText("]"))
                            .build()
            );
        }

        return Command.SINGLE_SUCCESS;
    }

    public static int runCommandForTerritoryMap(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();
        player.sendMessage(TextUtils.composeSimpleOperatorMessage("Dumping ")
                .append(TextUtils.composeOperatorHighlight("RealmManager::territoryMap"))
                .append(TextUtils.composeOperatorText(":")));

        for (ChunkAnchor claimedChunk : RealmManager.getTerritoryMap().keySet()) {
            Settlement settlement = RealmManager.getTerritoryMap().get(claimedChunk);
            ItemStack hoverItem = ItemStack.of(Material.STONE);
            hoverItem.setData(DataComponentTypes.CUSTOM_NAME, settlement.asTextComponent().decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE));
            hoverItem.setData(DataComponentTypes.LORE, ItemLore.lore()
                    .addLine(settlement.getDescriptionAsComponent().decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE))
                    .addLine(Component.text(""))
                    .addLine(Component.text()
                            .content("Left click").color(NamedTextColor.GREEN).decorate(TextDecoration.BOLD)
                            .append(Component.text(" to read more").color(NamedTextColor.DARK_GREEN))
                            .build().decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE))
                    .build());

            player.sendMessage(
                    Component.text()
                            .append(TextUtils.composeOperatorText(INDENT + "[(" + claimedChunk.toString() + "), "))
                            .append(settlement.asTextComponent()
                                    .hoverEvent(hoverItem.asHoverEvent())
                                    .clickEvent(ClickEvent.runCommand("/settlement info " + settlement.getName()))
                            )
                            .append(TextUtils.composeOperatorText("]"))
                            .build()
            );
        }
        return Command.SINGLE_SUCCESS;
    }
    public static int runCommandForRoles(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();
        Settlement settlement = RealmManager.getSettlement(StringArgumentType.getString(context, "settlement"));
        if (settlement == null) {
            player.sendMessage(TextUtils.composeSimpleErrorMessage("Settlement not found!"));
            return Command.SINGLE_SUCCESS;
        }

        player.sendMessage(TextUtils.composeSimpleOperatorMessage("Dumping ")
                .append(TextUtils.composeOperatorHighlight("role list"))
                .append(TextUtils.composeOperatorText(" for "))
                .append(settlement.getDisplayNameAsComponent())
                .append(TextUtils.composeOperatorText(":")));

        for (SettlementRole role : settlement.getRoles()) {
            player.sendMessage(
                    Component.text()
                            .append(TextUtils.composeOperatorText(INDENT + "[" + role.getDisplayAsString() + ", authority=" + role.getAuthority() + "]"))
                            .build()
            );
        }
        return Command.SINGLE_SUCCESS;
    }
}
