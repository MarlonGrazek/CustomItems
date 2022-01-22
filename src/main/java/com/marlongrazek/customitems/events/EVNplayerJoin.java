package com.marlongrazek.customitems.events;

import com.marlongrazek.customitems.main.Main;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class EVNplayerJoin implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Main.getPlugin().setup(e.getPlayer());
    }
}
