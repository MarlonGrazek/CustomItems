package com.marlongrazek.customitems.utils;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Objects;

public class PluginCommand implements CommandExecutor {
    private final CommandInfo commandInfo;

    public PluginCommand() {
        commandInfo = getClass().getDeclaredAnnotation(CommandInfo.class);
        Objects.requireNonNull(commandInfo, "Plugins must have CommandInfo annotations");
    }

    public CommandInfo getCommandInfo() {
        return commandInfo;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String lbl, String[] args) {

        if(!commandInfo.permission().isEmpty() && !sender.hasPermission(commandInfo.permission())) {
            sender.sendMessage("§cYou don't have the permission to do that!");
            return true;
        }

        if(commandInfo.requiresPlayer()) {
            if(!(sender instanceof Player)) {
                sender.sendMessage("§cYou need to be a player to execute this command");
                return true;
            }

            execute((Player) sender, args);
            return true;
        }

        execute(sender, args);
        return true;
    }

    public void execute(Player player, String[] args) {
    }

    public void execute(CommandSender sender, String[] args) {
    }
}
