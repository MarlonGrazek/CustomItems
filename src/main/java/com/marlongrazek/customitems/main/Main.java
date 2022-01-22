package com.marlongrazek.customitems.main;

import com.marlongrazek.customcrafting.utils.GUI;
import com.marlongrazek.customitems.commands.CMDcustomItems;
import com.marlongrazek.customitems.events.EVNplayerJoin;
import com.marlongrazek.datafile.DataFile;
import com.marlongrazek.ui.History;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class Main extends JavaPlugin {

    private static Main plugin;

    private static final Map<Player, History> history = new HashMap<>();
    private static final Map<Player, Map<String, Integer>> page = new HashMap<>();

    private static Plugin customCrafting;

    @Override
    public void onEnable() {

        plugin = this;
        setup();

        getCommand("customitems").setExecutor(new CMDcustomItems());

        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new EVNplayerJoin(), this);

        Bukkit.getConsoleSender().sendMessage("§eCustomItems §fsuccessfully loaded");
    }

    public void setup() {

        List<Plugin> plugins = Arrays.asList(Bukkit.getPluginManager().getPlugins());
        Plugin customCrafting = Bukkit.getPluginManager().getPlugin("CustomCrafting");
        if(plugins.contains(customCrafting) && Bukkit.getPluginManager().isPluginEnabled(customCrafting))
            Main.customCrafting = customCrafting;

        DataFile items = new DataFile("items", plugin.getDataFolder().getAbsolutePath());

        Bukkit.getOnlinePlayers().forEach(this::setup);
    }

    public void setup(Player player) {

        if(customCrafting != null) history.put(player, com.marlongrazek.customcrafting.main.Main.getHistory(player));
        else history.put(player, new History(player));

        page.put(player, new HashMap<>());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static Main getPlugin() {
        return plugin;
    }

    public static DataFile getDataFile(String name) {

        DataFile dataFile = null;
        for(File file : plugin.getDataFolder().listFiles())
            if(file.getName().equalsIgnoreCase(name + ".yml")) dataFile = new DataFile(file);
        return dataFile;
    }

    public static History getHistory(Player player) {
        return history.get(player);
    }

    public static Map<String, Integer> getPage(Player player) {
        return page.get(player);
    }
}
