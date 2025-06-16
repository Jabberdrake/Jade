package dev.jabberdrake.jade.commands.settlement;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.jabberdrake.jade.realms.ChunkAnchor;
import dev.jabberdrake.jade.realms.RealmManager;
import dev.jabberdrake.jade.realms.Settlement;
import dev.jabberdrake.jade.utils.TextUtils;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemLore;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import static dev.jabberdrake.jade.utils.TextUtils.info;

public class SettlementMapCommand {

    private static final String INDENT = "       ";

    public static LiteralCommandNode<CommandSourceStack> buildCommand(final String label) {
        return Commands.literal(label)
                .requires(sender -> sender.getExecutor() instanceof Player)
                .executes(SettlementMapCommand::runCommand)
                .build();
    }

    public static int runCommand(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();
        World currentWorld = player.getWorld();
        int currentX = player.getChunk().getX();
        int currentZ = player.getChunk().getZ();

        final int mapRange = 5;
        Component mapAsText = Component.text("");
        for (int cZ = -mapRange; cZ <= mapRange; cZ++) {
            mapAsText = mapAsText.append(Component.text(INDENT));
            for (int cX = -mapRange; cX <= mapRange; cX++) {
                Settlement owner = RealmManager.getChunkOwner(new ChunkAnchor(currentWorld, currentX + cX, currentZ + cZ));
                if (owner == null) {
                    mapAsText = mapAsText.append(Component.text("■", TextUtils.LAUREL)
                            .hoverEvent(HoverEvent.showText(Component.text("Wilderness", TextUtils.LAUREL))));
                } else {
                    ItemStack hoverItem = ItemStack.of(Material.STONE);
                    hoverItem.setData(DataComponentTypes.CUSTOM_NAME, owner.asTextComponent().decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE));
                    hoverItem.setData(DataComponentTypes.LORE, ItemLore.lore()
                                    .addLine(owner.getDescriptionAsComponent().decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE))
                                    .addLine(Component.text(""))
                                    .addLine(Component.text()
                                            .content("Left click").color(NamedTextColor.GREEN).decorate(TextDecoration.BOLD)
                                            .append(Component.text(" to read more").color(NamedTextColor.DARK_GREEN))
                                            .build().decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE))
                            .build());

                    mapAsText = mapAsText.append(Component.text("■", owner.getMapColor())
                            .hoverEvent(hoverItem.asHoverEvent())
                            .clickEvent(ClickEvent.runCommand("/settlement info " + owner.getName())));
                }
            }
            if (cZ != mapRange) {
                mapAsText = mapAsText.appendNewline();
            }
        }
        player.sendMessage(info("Settlement map (5 chunk radius):"));
        player.sendMessage(Component.text(INDENT + "===========", TextUtils.DARK_ZORBA));
        player.sendMessage(mapAsText);
        player.sendMessage(Component.text(INDENT + "===========", TextUtils.DARK_ZORBA)
                .appendNewline());

        return Command.SINGLE_SUCCESS;
    }
}
