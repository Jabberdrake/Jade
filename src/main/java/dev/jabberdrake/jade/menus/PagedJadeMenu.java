package dev.jabberdrake.jade.menus;

import dev.jabberdrake.jade.utils.TextUtils;
import io.papermc.paper.datacomponent.DataComponentTypes;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.function.Consumer;

public abstract class PagedJadeMenu extends SimpleJadeMenu {
    private int currentPage = 0;
    private int maxPage = 0;

    public PagedJadeMenu(String title, Rows rows) {
        super(title, rows);
        setNavigation();
    }

    protected int getCurrentPage() {
        return currentPage;
    }

    protected int getMaxPage() {
        return maxPage;
    }

    protected List<ItemStack> getItemStacksInSafeArea() {
        return this.getItems().entrySet()
                .stream()
                .filter(pair -> isSafeSlot(getPagedSlot(pair.getKey())))
                .map(pair -> pair.getValue().getItemStack())
                .toList();
    }

    protected int getPagedSlot(int slot) {
        return slot < getInventory().getSize() - 9 ? (currentPage * getInventory().getSize()) + slot : slot;
    }

    protected boolean isSafeSlot(int slot) {
        return slot < getInventory().getSize() - 9;
    }

    protected void setNavigation() {
        MenuItem nextPageButton = new MenuItem(getNextPageButtonAsItemStack(), player -> {
            currentPage = Math.min(maxPage, currentPage + 1);
            update();
        });

        MenuItem currPageButton = new MenuItem(getCurrentPageButtonAsItemStack(), (Consumer<Player>) null);

        MenuItem prevPageButton = new MenuItem(getPreviousPageButtonAsItemStack(), player -> {
            currentPage = Math.max(0, currentPage - 1);
            update();
        });

        setItem(getInventory().getSize() - 4, nextPageButton);
        setItem(getInventory().getSize() - 5, currPageButton);
        setItem(getInventory().getSize() - 6, prevPageButton);
    }

    public void addAll(List<MenuItem> menuItems) {
        final int safeArea = getInventory().getSize() - 9;

        for (int i = 0; i < menuItems.size(); i++) {
            final int page = i / safeArea;
            final int slot = i % safeArea;

            setItem(page, slot, menuItems.get(i));
        }
    }

    public void setItem(int page, int slot, MenuItem menuItem) {
        final int index = page * getInventory().getSize() + slot;
        getItems().put(index, menuItem);

        if (page == 0)
            getInventory().setItem(index, menuItem.getItemStack());

        if (page > maxPage)
            maxPage = page;
    }

    @Override
    public void click(Player player, int slot, ClickType clickType, InventoryAction inventoryAction) {
        final int mapIndex = slot < getInventory().getSize() - 9 ? (currentPage * getInventory().getSize()) + slot : slot;

        final MenuItem clickedItem = this.getItems().get(mapIndex);
        if (clickedItem != null) {
            final Consumer<Player> action = clickedItem.getAction(clickType);
            if (action != null) {
                action.accept(player);
            }
        }
    }

    @Override
    public void update() {
        getInventory().clear();

        for (int i = 0; i < getInventory().getSize(); i++) {
            final int index = currentPage * getInventory().getSize() + i;
            final MenuItem item = this.getItems().get(index);

            if (item != null)
                getInventory().setItem(i, item.getItemStack());
        }

        setNavigation();
    }

    public ItemStack getPreviousPageButtonAsItemStack() {
        final ItemStack prevButton = ItemStack.of(Material.STONE_BUTTON);
        prevButton.setData(DataComponentTypes.CUSTOM_NAME, Component.text("Previous Page", TextUtils.ZORBA));

        return prevButton;
    }

    public ItemStack getCurrentPageButtonAsItemStack() {
        final ItemStack currentButton = ItemStack.of(Material.PAPER);
        currentButton.setData(DataComponentTypes.CUSTOM_NAME, Component.text("Page " + (currentPage + 1) + "/" + (maxPage + 1), TextUtils.ZORBA));

        return currentButton;
    }

    public ItemStack getNextPageButtonAsItemStack() {
        final ItemStack nextButton = ItemStack.of(Material.STONE_BUTTON);
        nextButton.setData(DataComponentTypes.CUSTOM_NAME, Component.text("Next Page", TextUtils.ZORBA));

        return nextButton;
    }
}
