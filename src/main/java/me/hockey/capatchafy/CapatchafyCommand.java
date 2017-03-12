/*
    Copyright (C) 2017 James Depp

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package me.hockey.capatchafy;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CapatchafyCommand implements CommandExecutor
{
    //Friendly - One time verification.
    //Moderate - One time verification for each time the server starts.
    //Strict - Needs verification each time you join/leave.
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String cL, String[] args)
    {
        //TODO Implement command permissions with TFM and bukkit permissions.
        
        if (!sender.hasPermission("capatchafy.command"))
        {
            sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
            return true;
        }
        
        if (Capatchafy.configs.config.getBoolean("always-on"))
        {
            sender.sendMessage(ChatColor.RED + "The server owner has Capatchafy enabled at all times. You are not allowed to turn it off or change the security level.");
            return true;
        }

        //TODO Fix arguments problem, it will throw errors if the parameters aren't filled out properly. Also, make it show usage when the security level arg is spelled wrong.
        if (args.length < 1)
        {
            sender.sendMessage("Usage: /capatchafy <on:off> <friendly:moderate:strict>");
            return true;
        }

        if (args[0].equalsIgnoreCase("on"))
        {
            Capatchafy.enabled = true;
            Bukkit.broadcastMessage(ChatColor.DARK_RED + "Capatcha-based verification has been enabled.");

            //TODO Add time based disabling of capatchafy. E.g. when admin that turns capatchafy on leaves, turn capatchafy off.
            //If it does not match any of these, we just keep the security level as is.
            //Remember, the security level only changes when we tell it to with this command.

            Capatchafy.forced = true;
            Capatchafy.listeners.numberOfAttacks = 0;
            if (args.length < 2)
            {
                sender.sendMessage("Capatchafy will run in security level " + Capatchafy.securityLevel + ". It will not be auto-disabled.");
                return true;
            }

            if (args[1].equalsIgnoreCase("friendly") || args[1].equalsIgnoreCase("1"))
            {
                Capatchafy.securityLevel = 1;
            }
            else if (args[1].equalsIgnoreCase("moderate") || args[1].equalsIgnoreCase("2"))
            {
                Capatchafy.securityLevel = 2;
            }
            else if (args[1].equalsIgnoreCase("strict") || args[1].equalsIgnoreCase("3"))
            {
                Capatchafy.securityLevel = 3;
            }
            else
            {
                sender.sendMessage("Usage: /capatchafy <on:off> <friendly:moderate:strict>");
            }
            sender.sendMessage("Capatchafy will run in security level " + Capatchafy.securityLevel + ". It will not be auto-disabled.");
        }
        else if (args[0].equalsIgnoreCase("off"))
        {
            Capatchafy.enabled = false;
            Capatchafy.forced = false;
            Capatchafy.listeners.numberOfAttacks = 0;
            Bukkit.broadcastMessage(ChatColor.GREEN + "Capatcha-based verification has been disabled.");
            if (args.length < 2)
                return true;
            if (args[1].equalsIgnoreCase("-f"))
            {
                Capatchafy.forced = true;
                sender.sendMessage(ChatColor.YELLOW + "Capatchafy " + ChatColor.RED + "will not" + ChatColor.YELLOW + " automatically enable if the server detects an attack.");
            }
        }
        return false;
    }
}
