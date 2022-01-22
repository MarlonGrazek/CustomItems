package com.marlongrazek.customitems.utils;

import com.marlongrazek.builder.StringBuilder;
import com.marlongrazek.customitems.main.Main;
import com.marlongrazek.datafile.DataFile;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class CustomItem {

    private final OfflinePlayer creator;
    private ItemStack itemStack;
    private long id;
    private Recipe recipe;

    public CustomItem(OfflinePlayer creator, ItemStack itemStack) {
        this.creator = creator;
        this.itemStack = itemStack;
    }

    public CustomItem(long id, OfflinePlayer creator, ItemStack itemStack, Recipe recipe) {
        this.id = id;
        this.creator = creator;
        this.itemStack = itemStack;
        this.recipe = recipe;
    }

    public void create() {

        DataFile items = Main.getDataFile("items");

        long id = generateID();

        // itemstack
        ItemStack itemStack = new ItemStack(Material.STICK);
        if(this.itemStack != null) itemStack = this.itemStack;

        Recipe recipe = null;
        if(this.recipe != null) {

            Plugin customcrafting = Bukkit.getPluginManager().getPlugin("CustomCrafting");
            if(Bukkit.getPluginManager().isPluginEnabled(customcrafting)) {

                // ModifiedRecipe modifiedRecipe = #todo idk lol
            }
        }

        items.set(id + ".creator", creator.getUniqueId().toString());
        items.set(id + ".itemstack", itemStack);
        items.set(id + ".recipe", recipe);
    }

    public void setItemStack(ItemStack itemStack) {
        Main.getDataFile("items").set(id + ".itemstack", itemStack);
        this.itemStack = itemStack;
    }

    public void setRecipe(Recipe recipe) {
        Main.getDataFile("items").set(id + ".recipe", recipe);
        this.recipe = recipe;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public long getId() {
        return id;
    }

    public Recipe getRecipe() {
        return recipe;
    }

    // id
    private long generateID() {
        StringBuilder sb = new StringBuilder();
        String randomNumbers = "0123456789";
        Random random = new Random();
        long id = 0;
        while (id == 0) {
            for (int i = 0; i < 16; i++)
                sb.add(String.valueOf(randomNumbers.charAt(random.nextInt(randomNumbers.length()))));
            long generatedID = Long.parseLong(sb.toString());
            if (!isExistingID(generatedID)) id = generatedID;
        }
        this.id = id;
        return id;
    }

    private boolean isExistingID(long id) {
        //for (String idString : com.marlongrazek.customcrafting.main.Main.getDataFile("recipes").getConfigurationSection("", false))
            //if (Long.parseLong(idString) == id) return true;
        return false;
    }

    public static List<CustomItem> getCustomItems(OfflinePlayer creator) {

        List<CustomItem> customItems = new ArrayList<>();

        DataFile items = Main.getDataFile("items");
        for(String idString : items.getConfigurationSection("", false)) {
            if(UUID.fromString(items.getString(idString + ".creator")).equals(creator.getUniqueId())) {

                long id = Long.parseLong(idString);

                ItemStack itemStack = (ItemStack) items.get(id + ".itemstack");

                Recipe recipe = null;

                customItems.add(new CustomItem(id, creator, itemStack, recipe));
            }
        }

        return customItems;
    }
}
