package dev.jabberdrake.jade.commands.settlement;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.jabberdrake.jade.commands.SettlementCommand;
import dev.jabberdrake.jade.players.PlayerManager;
import dev.jabberdrake.jade.realms.RealmManager;
import dev.jabberdrake.jade.realms.Settlement;
import dev.jabberdrake.jade.utils.TextUtils;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.datacomponent.DataComponentTypes;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

import static dev.jabberdrake.jade.utils.TextUtils.error;

public class SettlementFoodCommand {

    private static final String INDENT = "     ";

    public static LiteralCommandNode<CommandSourceStack> buildCommand(final String label) {
        return Commands.literal(label)
                .requires(sender -> sender.getExecutor() instanceof Player)
                .executes(SettlementFoodCommand::runCommandWithoutArgs)
                .then(Commands.literal("info")
                        .requires(sender -> sender.getExecutor() instanceof Player)
                        .executes(SettlementFoodCommand::runCommandWithoutArgs)
                )
                .then(Commands.literal("deposit")
                        .then(Commands.literal("main_hand")
                                .requires(sender -> sender.getExecutor() instanceof Player)
                                .executes(SettlementFoodCommand::runCommandForMainhand)
                        )
                        .then(Commands.literal("off_hand")
                                .requires(sender -> sender.getExecutor() instanceof Player)
                                .executes(SettlementFoodCommand::runCommandForOffhand)
                        )
                        .then(Commands.literal("hotbar")
                                .requires(sender -> sender.getExecutor() instanceof Player)
                                .executes(SettlementFoodCommand::runCommandForHotbar)
                        )
                        .then(Commands.literal("inventory")
                                .requires(sender -> sender.getExecutor() instanceof Player)
                                .executes(SettlementFoodCommand::runCommandForInventory)
                        )
                )
                .build();
    }

    public static int runCommandWithoutArgs(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();
        Settlement focus = PlayerManager.asJadePlayer(player.getUniqueId()).getFocusSettlement();

        if (!SettlementCommand.validateFocusSettlement(player, focus)) {
            return Command.SINGLE_SUCCESS;
        }

        player.sendMessage(TextUtils.composeInfoPrefix().append(TextUtils.composeInfoHighlight("Food").decorate(TextDecoration.BOLD)));
        player.sendMessage(TextUtils.composeInfoText(INDENT + "Certain settlement actions, such as claiming chunks,"));
        player.sendMessage(TextUtils.composeInfoText(INDENT + "cost food. You can gain more food by depositing food"));
        player.sendMessage(TextUtils.composeInfoText(INDENT + "items. The food value of any item is equal to half of"));
        player.sendMessage(TextUtils.composeInfoText(INDENT + "how much hunger it restores."));
        player.sendMessage(Component.text());
        player.sendMessage(Component.text(INDENT + "Food: ", TextUtils.LIGHT_BRASS)
                .append(Component.text(focus.getFood() + "/" + focus.getFoodCapacity(), TextUtils.LIVINGMETAL)));
        player.sendMessage(Component.text());

        return Command.SINGLE_SUCCESS;
    }

    public static int runCommandForMainhand(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();
        Settlement focus = PlayerManager.asJadePlayer(player.getUniqueId()).getFocusSettlement();

        if (!SettlementCommand.validateFocusSettlement(player, focus)) {
            return Command.SINGLE_SUCCESS;
        }

        ItemStack mhItem = player.getInventory().getItemInMainHand();
        if (mhItem == null || !mhItem.hasData(DataComponentTypes.FOOD)) {
            player.sendMessage(error("You do not have a food item in your main hand!"));
            return Command.SINGLE_SUCCESS;
        }

        int foodValue = (mhItem.getData(DataComponentTypes.FOOD).nutrition() / 2) * mhItem.getAmount();

        if (!focus.canHandleFoodDeposit(foodValue)) {
            player.sendMessage(error("Too much food! (<highlight>" + foodValue + "</highlight> > " + (focus.getFoodCapacity() - focus.getFood()) + ")"));
            return Command.SINGLE_SUCCESS;
        }

        player.getInventory().setItemInMainHand(null);
        focus.addFood(foodValue);
        focus.broadcast("<highlight>" + player.getName() + "</highlight> has deposited <livingmetal>" + foodValue + "</livingmetal> food!");

        return Command.SINGLE_SUCCESS;
    }

    public static int runCommandForOffhand(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();
        Settlement focus = PlayerManager.asJadePlayer(player.getUniqueId()).getFocusSettlement();

        if (!SettlementCommand.validateFocusSettlement(player, focus)) {
            return Command.SINGLE_SUCCESS;
        }

        ItemStack ohItem = player.getInventory().getItemInOffHand();
        if (ohItem == null || !ohItem.hasData(DataComponentTypes.FOOD)) {
            player.sendMessage(error("You do not have a food item in your off hand!"));
            return Command.SINGLE_SUCCESS;
        }

        int foodValue = (ohItem.getData(DataComponentTypes.FOOD).nutrition() / 2) * ohItem.getAmount();

        if (!focus.canHandleFoodDeposit(foodValue)) {
            player.sendMessage(error("Too much food! (<highlight>" + foodValue + "</highlight> > " + (focus.getFoodCapacity() - focus.getFood()) + ")"));
            return Command.SINGLE_SUCCESS;
        }

        player.getInventory().setItemInOffHand(null);
        focus.addFood(foodValue);
        focus.broadcast("<highlight>" + player.getName() + "</highlight> has deposited <livingmetal>" + foodValue + "</livingmetal> food!");

        return Command.SINGLE_SUCCESS;
    }

    public static int runCommandForHotbar(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();
        Settlement focus = PlayerManager.asJadePlayer(player.getUniqueId()).getFocusSettlement();

        if (!SettlementCommand.validateFocusSettlement(player, focus)) {
            return Command.SINGLE_SUCCESS;
        }

        int totalFoodValue = 0;
        for (int slot = 0; slot <= 8; slot++) {
            ItemStack item = player.getInventory().getItem(slot);
            if (item == null) continue;
            if (!item.hasData(DataComponentTypes.FOOD)) continue;

            int itemFoodValue = (item.getData(DataComponentTypes.FOOD).nutrition() / 2) * item.getAmount();
            if (!focus.canHandleFoodDeposit(itemFoodValue)) break;

            player.getInventory().setItem(slot, null);
            totalFoodValue += itemFoodValue;
        }

        focus.broadcast("<highlight>" + player.getName() + "</highlight> has deposited <livingmetal>" + totalFoodValue + "</livingmetal> food!");

        return Command.SINGLE_SUCCESS;
    }

    public static int runCommandForInventory(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();
        Settlement focus = PlayerManager.asJadePlayer(player.getUniqueId()).getFocusSettlement();

        if (!SettlementCommand.validateFocusSettlement(player, focus)) {
            return Command.SINGLE_SUCCESS;
        }

        int totalFoodValue = 0;
        for (int slot = 0; slot <= 35; slot++) {
            ItemStack item = player.getInventory().getItem(slot);
            if (item == null) continue;
            if (!item.hasData(DataComponentTypes.FOOD)) continue;

            int itemFoodValue = (item.getData(DataComponentTypes.FOOD).nutrition() / 2) * item.getAmount();
            if (!focus.canHandleFoodDeposit(itemFoodValue)) break;

            player.getInventory().setItem(slot, null);
            totalFoodValue += itemFoodValue;
        }

        focus.broadcast("<highlight>" + player.getName() + "</highlight> has deposited <livingmetal>" + totalFoodValue + "</livingmetal> food!");

        return Command.SINGLE_SUCCESS;
    }
}
