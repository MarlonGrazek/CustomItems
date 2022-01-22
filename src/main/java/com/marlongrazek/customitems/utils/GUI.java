package com.marlongrazek.customitems.utils;

import com.marlongrazek.customitems.main.Main;
import com.marlongrazek.ui.History;
import net.wesjd.anvilgui.AnvilGUI;
import org.apache.commons.math3.util.Precision;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import com.marlongrazek.ui.UI;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class GUI {

    private final Player player;

    private final UI.Item backgroundItem = new UI.Item(" ", Material.GRAY_STAINED_GLASS_PANE);

    public GUI(Player player) {
        this.player = player;
    }

    public void open(UI.Page page) {
        page.open(player);
    }

    public void reload() {
        Main.getHistory(player).openPage(0);
    }


    public UI.Page materials(BiConsumer<Material, ClickType> clickAction) {

        UI.Page page = new UI.Page("Items", 54, Main.getPlugin());

        History history = Main.getHistory(player);
        history.addPage(page);

        page.onOpen(p -> {

            page.clear();

            List<Material> items = getItems();
            int currentPage = getCurrentPage(page);

            // footer
            UI.Section footer = new UI.Section(9, 1);
            footer.fill(backgroundItem);

            if (currentPage > 1) footer.setItem(getPreviousPageItem(items.size()), 3);

            footer.setItem(getNavigationItem(), 4);

            if (items.size() > currentPage * 45) footer.setItem(getNextPageItem(items.size()), 5);

            // content
            UI.Section content = new UI.Section(9, 5);

            for (int i = (currentPage - 1) * 45; i < currentPage * 45 && i < items.size(); i++) {

                Material material = items.get(i);
                String name = "§a" + material.name().toLowerCase().replaceAll("_", " ");
                UI.Item item = new UI.Item(name, material);
                item.addLoreLine("§7Click to choose");
                item.onClick(clickType -> clickAction.accept(material, clickType));

                content.addItem(item);
            }

            page.setSection(content, 0);
            page.setSection(footer, 45);
        });

        return page;
    }

    public UI.Page itemList() {

        UI.Page page = new UI.Page("Items", 54, Main.getPlugin());

        History history = Main.getHistory(player);
        history.addPage(page);

        page.onOpen(p -> {

            page.clear();
            int currentPage = getCurrentPage(page);
            List<CustomItem> items = CustomItem.getCustomItems(player);

            // footer
            UI.Section footer = new UI.Section(9, 1);
            footer.fill(backgroundItem);
            if (currentPage > 1) footer.setItem(getPreviousPageItem(items.size()), 3);
            footer.setItem(getNavigationItem(), 4);
            if (items.size() > currentPage * 45) footer.setItem(getNextPageItem(items.size()), 5);

            // content
            UI.Section content = new UI.Section(9, 5);

            if (!items.isEmpty()) {
                for (int i = (currentPage - 1) * 45; i < currentPage * 45 && i < items.size(); i++) {

                    CustomItem customItem = items.get(i);

                    Material material = customItem.getItemStack().getType();
                    String name = "§a" + material.name().toLowerCase().replaceAll("_", " ");

                    UI.Item item = new UI.Item(name, material);
                    item.addLoreLine("§7Click to view the settings");
                    item.onClick(clickType -> open(itemSettings(customItem)));

                    content.addItem(item);
                }
            } else {
                UI.Item noItems = new UI.Item("§cNo Custom Items", Material.RED_STAINED_GLASS_PANE);
                noItems.addLoreLines("§7Create custom items by", "§7clicking the \"Create\" button");
                content.setItem(noItems, 22);
            }

            page.setSection(content, 0);
            page.setSection(footer, 45);
        });

        return page;
    }

    public UI.Page itemSettings(CustomItem customItem) {

        UI.Page page = new UI.Page("LOL REPLACE", 54, Main.getPlugin());

        History history = Main.getHistory(player);
        history.addPage(page);

        page.onOpen(p -> {

            page.clear();

            // settings
            UI.Section settings = new UI.Section(7, 1);

            UI.Item name = new UI.Item("§eName", Material.NAME_TAG);
            settings.addItem(name);

            UI.Item lore = new UI.Item("§eLore", Material.BOOK);
            settings.addItem(lore);

            UI.Item material = new UI.Item("§eMaterial", customItem.getItemStack().getType());
            settings.addItem(material);

            UI.Item enchantments = new UI.Item("§eEnchantments", Material.ENCHANTED_BOOK);
            settings.addItem(enchantments);

            UI.Item recipeItem = new UI.Item(Material.CRAFTING_TABLE);
            if (Bukkit.getPluginManager().isPluginEnabled(Bukkit.getPluginManager().getPlugin("CustomCrafting"))) {
                recipeItem.setName("§eRecipe");
                if (customItem.getRecipe() != null) recipeItem.addLoreLine("§7Click to change the recipe");
                else {
                    recipeItem.addLoreLine("§7Click to set a recipe");
                    recipeItem.onClick(clickType -> {

                        com.marlongrazek.customcrafting.utils.GUI ccgui = new com.marlongrazek.customcrafting.utils.GUI(player);
                        ccgui.open(ccgui.recipeTypes((clickedRecipe, ct) -> {
                            AnvilGUI.Builder builder = new AnvilGUI.Builder();
                            builder.plugin(com.marlongrazek.customcrafting.main.Main.getPlugin());
                            builder.title("Enter a Key");
                            builder.text("Namespacedkey");
                            builder.itemLeft(new ItemStack(Material.NAME_TAG));
                            builder.onClose(p2 -> history.openPage(0));
                            builder.onLeftInputClick(p2 -> history.openPage(0));
                            builder.onComplete((p2, text) -> {
                                NamespacedKey key = new NamespacedKey(Main.getPlugin(), text);
                                Bukkit.getScheduler().runTaskLater(Main.getPlugin(), () -> {

                                    switch (clickedRecipe) {
                                        case "shapeless" -> {
                                            ShapelessRecipe recipe = new ShapelessRecipe(key, customItem.getItemStack());
                                            ccgui.open(ccgui.shapelessRecipe(recipe));
                                        }
                                        case "shaped" -> {
                                            ShapedRecipe recipe = new ShapedRecipe(key, customItem.getItemStack());
                                            ccgui.open(ccgui.shapedRecipe(recipe));
                                        }
                                        case "furnace" -> {
                                            FurnaceRecipe recipe = new FurnaceRecipe(key, customItem.getItemStack(), Material.STONE, 0, 1);
                                            ccgui.open(ccgui.furnaceRecipe(recipe, true));
                                        }
                                    }
                                }, 1);
                                return AnvilGUI.Response.close();
                            });
                            builder.open(p);
                        }));
                    });
                }
            } else {
                recipeItem.setName("§fRecipe");
                recipeItem.addLoreLine("§7This feature requires CustomCrafting");
            }
            settings.addItem(recipeItem);

            // footer
            UI.Section footer = new UI.Section(9, 1);
            footer.fill(backgroundItem);
            footer.setItem(getNavigationItem(), 4);

            page.setItem(UI.Item.fromItemStack(customItem.getItemStack()), 13);
            page.setSection(settings, 28);
            page.setSection(footer, 45);
        });

        return page;
    }

    public UI.Page menu() {

        UI.Page page = new UI.Page("Custom Items", 45, Main.getPlugin());

        History history = Main.getHistory(player);
        history.addPage(page);

        page.onOpen(p -> {

            page.clear();

            // header
            UI.Section header = new UI.Section(9, 1);
            header.fill(backgroundItem);

            // sidebar
            UI.Section sidebar = new UI.Section(1, 3);
            sidebar.fill(backgroundItem);

            // footer
            UI.Section footer = new UI.Section(9, 1);
            footer.fill(backgroundItem);

            footer.setItem(getNavigationItem(), 4);

            // content
            UI.Section content = new UI.Section(5, 1);

            // list
            UI.Item list = new UI.Item("§eItems", Material.BOOK);
            list.addLoreLine("§7Click to view the items");
            list.onClick(clickType -> open(itemList()));
            content.setItem(list, 1);

            // create
            UI.Item create = new UI.Item("§aCreate", Material.SLIME_BALL);
            create.addLoreLine("§7Click to create a new item");
            create.onClick(clickType -> open(materials((clickedItem, ct) -> {
                CustomItem item = new CustomItem(player, new ItemStack(clickedItem));
                item.create();
                history.removePage(1);
                itemList();
                open(itemSettings(item));
            })));
            content.setItem(create, 3);

            page.setSection(header, 0);
            page.setSection(sidebar, 9);
            page.setSection(sidebar, 17);
            page.setSection(content, 20);
            page.setSection(footer, 36);
        });

        return page;
    }

    public UI.Page history() {

        UI.Page page = new UI.Page("History", 54, Main.getPlugin());

        History history = Main.getHistory(player);
        history.addPage(page);

        page.onOpen(p -> {

            page.clear();
            int currentPage = getCurrentPage(page);
            List<UI.Page> historyPages = history.list();

            // footer
            UI.Section footer = new UI.Section(9, 1);
            footer.fill(backgroundItem);
            if (currentPage > 1) footer.setItem(getPreviousPageItem(historyPages.size()), 3);
            footer.setItem(getNavigationItem(), 4);
            if (historyPages.size() > currentPage * 45) footer.setItem(getNextPageItem(historyPages.size()), 5);

            // content
            UI.Section content = new UI.Section(9, 5);

            for (int i = (currentPage - 1) * 45; i < currentPage * 45 && i < historyPages.size() - 1; i++) {

                UI.Page historyPage = historyPages.get(i);

                if (historyPage != null) {
                    UI.Item pageItem = new UI.Item("§a" + historyPage.getTitle(), Material.FILLED_MAP);
                    pageItem.addLoreLine("§7Click to returnto that page");
                    int selectedPage = historyPages.size() - (i + 1);
                    pageItem.onClick(clickType -> history.openPage(selectedPage));
                    content.addItem(pageItem);
                }
            }

            page.setSection(content, 0);
            page.setSection(footer, 45);
        });

        return page;
    }

    public UI.Page pages(int pageSize) {

        UI.Page page = new UI.Page("Pages", 54, Main.getPlugin());

        History history = Main.getHistory(player);
        history.addPage(page);

        return page;
    }


    // CURRENT PAGE
    public Integer getCurrentPage(UI.Page page) {
        int currentPage = 1;
        if (Main.getPage(player).containsKey(page.getTitle())) currentPage = Main.getPage(player).get(page.getTitle());
        return currentPage;
    }

    public void setCurrentPage(UI.Page page, Integer index) {
        Main.getPage(player).put(page.getTitle(), index);
    }


    private List<Material> getItems() {

        List<Material> items = new ArrayList<>();
        for (Material material : Material.values())
            if (material.isItem()) if (material != Material.AIR) items.add(material);
        return items;
    }

    private UI.Item getNavigationItem() {
        History history = Main.getHistory(player);

        UI.Item navigation = new UI.Item();
        navigation.setMaterial(Material.BARRIER);
        if (history.getPage(1) != null) {
            navigation.setName("§eNavigation");
            navigation.clearLore();
            navigation.addLoreLine("§7Click to return to the previous page");
            navigation.addLoreLine("§7Right-click view the history");
            navigation.addLoreLine("§7Middle-click to close");
            navigation.onClick(clickType -> {
                switch (clickType) {
                    case RIGHT -> open(history());
                    case MIDDLE -> player.closeInventory();
                    default -> history.openPage(1);
                }
            });
        } else {
            navigation.setName("§eClose");
            navigation.clearLore();
            navigation.addLoreLine("§7Click to close");
            navigation.onClick(clickType -> player.closeInventory());
        }
        return navigation;
    }

    private UI.Item getPreviousPageItem(int amount) {
        UI.Page page = Main.getHistory(player).getPage(0);

        UI.Item previousPage = new UI.Item();
        previousPage.addLoreLine("§7Click to go to the previous page");
        previousPage.addLoreLine("§7Right-click to open the page list");
        previousPage.addLoreLine("§7Middle-click to jump to the beginning");
        previousPage.setName("§ePrevious page");
        previousPage.setMaterial(Material.ARROW);
        previousPage.onClick(clickType -> {
            switch (clickType) {
                case RIGHT -> open(pages((int) Precision.round((float) amount / 45, 0, 0)));
                case MIDDLE -> {
                    setCurrentPage(page, 1);
                    reload();
                }
                default -> {
                    setCurrentPage(page, getCurrentPage(page) - 1);
                    reload();
                }
            }
        });
        return previousPage;
    }

    private UI.Item getNextPageItem(int amount) {
        UI.Page page = Main.getHistory(player).getPage(0);

        UI.Item nextPage = new UI.Item();
        nextPage.addLoreLine("§7Click to go to the next page");
        nextPage.addLoreLine("§7Right-click to open the page list");
        nextPage.addLoreLine("§7Middle-click to jump to the end");
        nextPage.setName("§eNext page");
        nextPage.setMaterial(Material.ARROW);
        nextPage.onClick(clickType -> {
            switch (clickType) {
                case RIGHT -> open(pages((int) Precision.round((float) amount / 45, 0, 0)));
                case MIDDLE -> {
                    setCurrentPage(page, (int) Precision.round((float) amount / 45, 0, 0));
                    reload();
                }
                default -> {
                    setCurrentPage(page, getCurrentPage(page) + 1);
                    reload();
                }
            }
        });
        return nextPage;
    }
}
