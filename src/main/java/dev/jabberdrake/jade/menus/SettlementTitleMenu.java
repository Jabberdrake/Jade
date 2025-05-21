package dev.jabberdrake.jade.menus;

import dev.jabberdrake.jade.realms.SettlementRole;
import dev.jabberdrake.jade.realms.Settlement;
import dev.jabberdrake.jade.utils.TextUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class SettlementTitleMenu extends SimpleJadeMenu {

    private final Settlement settlement;

    public SettlementTitleMenu(Settlement settlement) {
        super("Manage roles", Rows.ONE);
        this.settlement = settlement;
    }

    @Override
    public void composeMenu(Player player) {
        int titleCount = this.settlement.getRoles().size();
        if (titleCount == 0 || titleCount > 9) {
            error(player, "Invalid number of titles " + titleCount + " in " + settlement.getName() + "!");
            return;
        }

        for (SettlementRole title : this.settlement.getRoles()) {
            ItemStack titleItem = new ItemStack(getTitleMaterial(title));
            ItemMeta titleMeta = titleItem.getItemMeta();

            titleMeta.customName(title.getDisplayAsComponent().decoration(TextDecoration.ITALIC, false));
            List<Component> lore = new ArrayList<>(List.of(
                    Component.text("Title in ", TextUtils.ZORBA)
                            .append(settlement.getDisplayName()),
                    Component.empty(),
                    Component.text("Permissions: ", TextUtils.LIGHT_ROSEMETAL),
                    composeComponentForTitlePermission(title, "canInvite"),
                    composeComponentForTitlePermission(title, "canKick"),
                    composeComponentForTitlePermission(title, "canClaim"),
                    composeComponentForTitlePermission(title, "canUnclaim"),
                    composeComponentForTitlePermission(title, "canPromote"),
                    composeComponentForTitlePermission(title, "canDemote"),
                    composeComponentForTitlePermission(title, "canEdit"),
                    Component.empty(),
                    composeComponentForTitleType(title)
            ));

            if (title.isDefault()) lore.add(Component.text("Default Title", TextUtils.LIVINGMETAL));

            SettlementRole viewerTitle = this.settlement.getRoleFromMember(player.getUniqueId());
            if (viewerTitle.isLeader() || viewerTitle.canPromote() || viewerTitle.canDemote()) {
                lore.add(Component.empty());
                if (viewerTitle.canPromote() && !title.isLeader()) {
                    lore.add(Component.text()
                            .append(Component.text("Left Click", NamedTextColor.GREEN, TextDecoration.BOLD))
                            .append(Component.text(" to increase authority", NamedTextColor.DARK_GREEN))
                            .build()
                    );
                }
                if (viewerTitle.canPromote() && !title.isLeader()) {
                    lore.add(Component.text()
                            .append(Component.text("Right Click", NamedTextColor.GREEN, TextDecoration.BOLD))
                            .append(Component.text(" to decrease authority", NamedTextColor.DARK_GREEN))
                            .build()
                    );
                }
                if (viewerTitle.isLeader()) {
                    lore.add(Component.text()
                            .append(Component.text("Drop", NamedTextColor.GREEN, TextDecoration.BOLD))
                            .append(Component.text(" to edit title", NamedTextColor.DARK_GREEN))
                            .build()
                    );
                }
            }

            titleMeta.lore(lore.stream()
                    .map(line -> line.decoration(TextDecoration.ITALIC, false))
                    .collect(Collectors.toList()));

            if (settlement.getPopulation().containsKey(player.getUniqueId()) &&
                settlement.getPopulation().get(player.getUniqueId()).equals(title)) {
                titleMeta.setEnchantmentGlintOverride(true);
            }

            titleItem.setItemMeta(titleMeta);

            this.setItem(getSlotForAuthority(title.getAuthority()), new MenuItem(titleItem, (Consumer<Player>) null));
        }
    }

    public static Component composeComponentForTitlePermission(SettlementRole title, String permissionKey) {
        Component base = Component.text("— ", TextUtils.LIGHT_ROSEMETAL);
        switch (permissionKey) {
            case "canInvite":
                if (title.canInvite()) {
                    return base.append(TextUtils.composeAcceptedRequirement("Can Invite"));
                } else {
                    return base.append(TextUtils.composeDeniedRequirement("Can Invite"));
                }

            case "canKick":
                if (title.canKick()) {
                    return base.append(TextUtils.composeAcceptedRequirement("Can Kick"));
                } else {
                    return base.append(TextUtils.composeDeniedRequirement("Can Kick"));
                }

            case "canClaim":
                if (title.canClaim()) {
                    return base.append(TextUtils.composeAcceptedRequirement("Can Claim"));
                } else {
                    return base.append(TextUtils.composeDeniedRequirement("Can Claim"));
                }

            case "canUnclaim":
                if (title.canUnclaim()) {
                    return base.append(TextUtils.composeAcceptedRequirement("Can Unclaim"));
                } else {
                    return base.append(TextUtils.composeDeniedRequirement("Can Unclaim"));
                }

            case "canPromote":
                if (title.canPromote()) {
                    return base.append(TextUtils.composeAcceptedRequirement("Can Promote"));
                } else {
                    return base.append(TextUtils.composeDeniedRequirement("Can Promote"));
                }

            case "canDemote":
                if (title.canDemote()) {
                    return base.append(TextUtils.composeAcceptedRequirement("Can Demote"));
                } else {
                    return base.append(TextUtils.composeDeniedRequirement("Can Demote"));
                }

            case "canEdit":
                if (title.canEdit()) {
                    return base.append(TextUtils.composeAcceptedRequirement("Can Edit"));
                } else {
                    return base.append(TextUtils.composeDeniedRequirement("Can Edit"));
                }

            default:
                return base.append(Component.text("Unknown permission key: " + permissionKey, NamedTextColor.RED));
        }
    }

    public static Component composeComponentForTitleType(SettlementRole title) {
        Component result;
        if (title.isLeader()) {
            return Component.text()
                    .append(Component.text("Leader Title", TextUtils.LEGENDARY_ORANGE).decorate(TextDecoration.BOLD))
                    .build();
        } else if (title.canInvite() && title.canKick()) {
            return Component.text()
                    .append(Component.text("Officer Title", TextUtils.RARE_PURPLE).decorate(TextDecoration.BOLD))
                    .build();
        } else {
            return Component.text()
                    .append(Component.text("Member Title", TextUtils.UNCOMMON_GREEN).decorate(TextDecoration.BOLD))
                    .build();
        }
    }

    public static Material getTitleMaterial(SettlementRole title) {
        if (title.isLeader()) {
            return Material.GLOWSTONE_DUST;
        } else if (title.canInvite() && title.canKick()) {
            return Material.REDSTONE;
        } else {
            return Material.GUNPOWDER;
        }
    }

    public static int getSlotForAuthority(int authority) {
        return (SettlementRole.MAX_AUTHORITY-authority);
    }
}
