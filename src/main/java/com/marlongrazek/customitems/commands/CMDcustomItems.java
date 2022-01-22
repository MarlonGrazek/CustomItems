package com.marlongrazek.customitems.commands;

import com.marlongrazek.customitems.main.Main;
import com.marlongrazek.customitems.utils.CommandInfo;
import com.marlongrazek.customitems.utils.GUI;
import com.marlongrazek.customitems.utils.PluginCommand;
import com.marlongrazek.ui.History;
import org.bukkit.entity.Player;

@CommandInfo(name = "customitems", requiresPlayer = true)
public class CMDcustomItems extends PluginCommand {

    @Override
    public void execute(Player player, String[] args) {

        History history = Main.getHistory(player);
        history.addPage(null);

        GUI gui = new GUI(player);
        gui.open(gui.menu());
    }
}
