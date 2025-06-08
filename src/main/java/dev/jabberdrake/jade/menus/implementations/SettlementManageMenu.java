package dev.jabberdrake.jade.menus.implementations;

import dev.jabberdrake.jade.menus.MenuItem;
import dev.jabberdrake.jade.menus.SimpleJadeMenu;
import dev.jabberdrake.jade.realms.SettlementRole;
import dev.jabberdrake.jade.realms.Settlement;
import dev.jabberdrake.jade.utils.TextUtils;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemLore;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import static net.kyori.adventure.text.Component.text;

public class SettlementManageMenu extends SimpleJadeMenu {

    private final Settlement settlement;

    public SettlementManageMenu(Settlement settlement) {
        super("Manage roles", Rows.ONE);
        this.settlement = settlement;
    }

    @Override
    public void composeMenu() {
        int titleCount = this.settlement.getRoles().size();
        if (titleCount == 0 || titleCount > 9) {
            error("Invalid number of titles " + titleCount + " in " + settlement.getName() + "!");
            return;
        }

        if (!settlement.getPopulation().containsKey(this.getPlayer().getUniqueId())) {
            error("You are not a member of this settlement!");
            return;
        }

        SettlementRole viewerRole = this.settlement.getRoleFromMember(this.getPlayer().getUniqueId());
        for (int authority = SettlementRole.MIN_AUTHORITY; authority <= SettlementRole.MAX_AUTHORITY; authority++) {
            SettlementRole role = settlement.getRoleForAuthority(authority);
            if (role == null) {
                setItem(getSlotForAuthority(authority), getFillerAsMenuItem());
            } else {
                setItem(getSlotForAuthority(authority), getRoleAsMenuItem(role, viewerRole));
            }
        }
    }

    public static MenuItem getFillerAsMenuItem() {
        ItemStack fillerItem = ItemStack.of(Material.GRAY_DYE);
        fillerItem.setData(DataComponentTypes.CUSTOM_NAME, text("Empty slot", TextUtils.ZORBA));

        return new MenuItem(fillerItem, (Consumer<Player>) null);
    }

    public static MenuItem getRoleAsMenuItem(SettlementRole role, SettlementRole viewerRole) {
        ItemStack roleItem = role.getIconAsItem();
        Map<ClickType, Consumer<Player>> menuActions = new HashMap<>();

        roleItem.setData(DataComponentTypes.CUSTOM_NAME, text()
                .append(role.getDisplayAsComponent())
                .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE)
                .build());

        ItemLore.Builder loreBuilder = ItemLore.lore()
                .addLine(text()
                    .content("Role in ").color(TextUtils.ZORBA)
                    .append(role.getSettlement().getDisplayName())
                    .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE)
                    .build())
                .addLine(text()
                        .content("— Authority: ").color(TextUtils.ZORBA)
                        .append(text(role.getAuthority(), TextUtils.AMETHYST))
                        .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE)
                        .build());

        if (role.equals(viewerRole)) {
            roleItem.setData(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true);
            loreBuilder.addLine(text()
                    .content("(You have this role!)").color(TextUtils.DARK_ZORBA)
                    .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE)
                    .build());
        }
        loreBuilder.addLine(Component.empty());

        composePermissionLore(loreBuilder, role);

        if (role.isLeader()) {
            loreBuilder.addLine(Component.empty());
            loreBuilder.addLine(text("Leader Role").color(TextUtils.LEGENDARY_ORANGE)
                    .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE));
            loreBuilder.addLine(text()
                    .content("Leaders have all permissions and").color(TextUtils.DARK_ZORBA)
                    .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE)
                    .build());
            loreBuilder.addLine(text()
                    .content("their authority cannot be changed.").color(TextUtils.DARK_ZORBA)
                    .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE)
                    .build());
        }

        if (role.isDefault()) {
            loreBuilder.addLine(Component.empty());
            loreBuilder.addLine(text("Default Role").color(TextUtils.LIVINGMETAL)
                    .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE));
            loreBuilder.addLine(text()
                    .content("This role is automatically").color(TextUtils.DARK_ZORBA)
                    .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE)
                    .build());
            loreBuilder.addLine(text()
                    .content("assigned to newcomers!").color(TextUtils.DARK_ZORBA)
                    .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE)
                    .build());
        }

        if (viewerRole.isLeader() || viewerRole.canManage()) {
            loreBuilder.addLine(Component.empty());
            SettlementRole roleAbove = role.getSettlement().getRoleAbove(role);
            SettlementRole roleBelow = role.getSettlement().getRoleBelow(role);
            if ((viewerRole.canManage() && !role.isLeader() && roleAbove == null)
                    || (viewerRole.canManage() && !role.isLeader() && roleAbove != null && roleAbove.getAuthority() != role.getAuthority() + 1)) {
                loreBuilder.addLine(text()
                        .content("Left Click").color(NamedTextColor.GREEN).decorate(TextDecoration.BOLD)
                        .append(text(" to increase authority", NamedTextColor.DARK_GREEN))
                        .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE)
                        .build()
                );
                menuActions.put(ClickType.LEFT, p -> {
                    p.performCommand("settlement manage " + role.getName() + " authority increase");
                    new SettlementManageMenu(role.getSettlement()).open(p);
                });
            }
            if ((viewerRole.canManage() && !role.isLeader() && role.getAuthority() != SettlementRole.MIN_AUTHORITY && roleBelow == null)
                || (viewerRole.canManage() && !role.isLeader() && role.getAuthority() != SettlementRole.MIN_AUTHORITY && roleBelow != null && roleBelow.getAuthority() != role.getAuthority() - 1)) {
                loreBuilder.addLine(text()
                        .content("Right Click").color(NamedTextColor.GREEN).decorate(TextDecoration.BOLD)
                        .append(text(" to decrease authority", NamedTextColor.DARK_GREEN))
                        .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE)
                        .build()
                );
                menuActions.put(ClickType.RIGHT, p -> {
                    p.performCommand("settlement manage " + role.getName() + " authority decrease");
                    new SettlementManageMenu(role.getSettlement()).open(p);
                });
            }
            if (viewerRole.canManage()) {
                loreBuilder.addLine(text()
                        .content("Drop").color(NamedTextColor.GREEN).decorate(TextDecoration.BOLD)
                        .append(text(" to edit title", NamedTextColor.DARK_GREEN))
                        .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE)
                        .build()
                );
                menuActions.put(ClickType.DROP, p -> {
                    //TODO: IMPLEMENT ME
                    p.sendMessage(TextUtils.composeSimpleErrorMessage("Not implemented yet!"));
                    p.closeInventory();
                });
            }
        }

        roleItem.setData(DataComponentTypes.LORE, loreBuilder.build());

        return new MenuItem(roleItem, menuActions);
    }

    public static void composePermissionLore(ItemLore.Builder loreBuilder, SettlementRole role) {
        loreBuilder.addLine(text("Permissions:", TextUtils.LIGHT_ROSEMETAL).decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE));

        // — Can invite (?)
        if (role.canInvite()) {
            loreBuilder.addLine(text()
                    .content("— ").color(TextUtils.LIGHT_ROSEMETAL)
                    .append(TextUtils.composeAcceptedRequirement("Can Invite"))
                    .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE)
            );
        } else {
            loreBuilder.addLine(text()
                    .content("— ").color(TextUtils.LIGHT_ROSEMETAL)
                    .append(TextUtils.composeDeniedRequirement("Can Invite"))
                    .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE)
            );
        }

        // — Can kick (?)
        if (role.canKick()) {
            loreBuilder.addLine(text()
                    .content("— ").color(TextUtils.LIGHT_ROSEMETAL)
                    .append(TextUtils.composeAcceptedRequirement("Can Kick"))
                    .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE)
            );
        } else {
            loreBuilder.addLine(text()
                    .content("— ").color(TextUtils.LIGHT_ROSEMETAL)
                    .append(TextUtils.composeDeniedRequirement("Can Kick"))
                    .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE)
            );
        }

        // — Can claim (?)
        if (role.canClaim()) {
            loreBuilder.addLine(text()
                    .content("— ").color(TextUtils.LIGHT_ROSEMETAL)
                    .append(TextUtils.composeAcceptedRequirement("Can Claim"))
                    .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE)
            );
        } else {
            loreBuilder.addLine(text()
                    .content("— ").color(TextUtils.LIGHT_ROSEMETAL)
                    .append(TextUtils.composeDeniedRequirement("Can Claim"))
                    .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE)
            );
        }

        // — Can unclaim (?)
        if (role.canUnclaim()) {
            loreBuilder.addLine(text()
                    .content("— ").color(TextUtils.LIGHT_ROSEMETAL)
                    .append(TextUtils.composeAcceptedRequirement("Can Unclaim"))
                    .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE)
            );
        } else {
            loreBuilder.addLine(text()
                    .content("— ").color(TextUtils.LIGHT_ROSEMETAL)
                    .append(TextUtils.composeDeniedRequirement("Can Unclaim"))
                    .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE)
            );
        }

        // — Can promote (?)
        if (role.canInvite()) {
            loreBuilder.addLine(text()
                    .content("— ").color(TextUtils.LIGHT_ROSEMETAL)
                    .append(TextUtils.composeAcceptedRequirement("Can Promote"))
                    .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE)
            );
        } else {
            loreBuilder.addLine(text()
                    .content("— ").color(TextUtils.LIGHT_ROSEMETAL)
                    .append(TextUtils.composeDeniedRequirement("Can Promote"))
                    .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE)
            );
        }

        // — Can demote (?)
        if (role.canDemote()) {
            loreBuilder.addLine(text()
                    .content("— ").color(TextUtils.LIGHT_ROSEMETAL)
                    .append(TextUtils.composeAcceptedRequirement("Can Demote"))
                    .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE)
            );
        } else {
            loreBuilder.addLine(text()
                    .content("— ").color(TextUtils.LIGHT_ROSEMETAL)
                    .append(TextUtils.composeDeniedRequirement("Can Demote"))
                    .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE)
            );
        }

        // — Can edit (?)
        if (role.canEdit()) {
            loreBuilder.addLine(text()
                    .content("— ").color(TextUtils.LIGHT_ROSEMETAL)
                    .append(TextUtils.composeAcceptedRequirement("Can Edit"))
                    .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE)
            );
        } else {
            loreBuilder.addLine(text()
                    .content("— ").color(TextUtils.LIGHT_ROSEMETAL)
                    .append(TextUtils.composeDeniedRequirement("Can Edit"))
                    .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE)
            );
        }

        // — Can manage (?)
        if (role.canManage()) {
            loreBuilder.addLine(text()
                    .content("— ").color(TextUtils.LIGHT_ROSEMETAL)
                    .append(TextUtils.composeAcceptedRequirement("Can Manage"))
                    .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE)
            );
        } else {
            loreBuilder.addLine(text()
                    .content("— ").color(TextUtils.LIGHT_ROSEMETAL)
                    .append(TextUtils.composeDeniedRequirement("Can Manage"))
                    .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE)
            );
        }
    }

    public static int getSlotForAuthority(int authority) {
        return (SettlementRole.MAX_AUTHORITY-authority);
    }
}
