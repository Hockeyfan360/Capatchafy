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
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String cmdLabel, String[] args)
    {
        //TODO: Implement command permissions with TFM.
        if(!sender.hasPermission("capatchafy.command"))
        {
            sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
            return true;
        }
        if(Capatchafy.configuration.mainConfig.getBoolean("always-on"))
        {
            sender.sendMessage(ChatColor.RED + "The server owner has Capatchafy enabled at all times. You are not allowed to turn it off or change the security level.");
            return true;
        }
        switch(args.length)
        {
            case 0:
                return false;
            case 1:
                switch(args[0])
                {
                    case "on":
                        //TODO: Add time based disabling of capatchafy. E.g. when admin that turns capatchafy on leaves, turn capatchafy off.
                        //If it does not match any of these, we just keep the security level as is.
                        //Remember, the security level only changes when we tell it to with this command.
                        Capatchafy.enabled = true;
                        Capatchafy.forced = true;
                        Capatchafy.listeners.numberOfAttacks = 0;
                        Bukkit.broadcastMessage(ChatColor.DARK_RED + "Capatcha-based verification has been enabled.");
                        sender.sendMessage("Capatchafy will run on security level " + Capatchafy.securityLevel + ". It will not be auto-disabled.");
                        return true;
                    case "off":
                        Capatchafy.enabled = false;
                        Capatchafy.forced = false;
                        Capatchafy.listeners.numberOfAttacks = 0;
                        Bukkit.broadcastMessage(ChatColor.GREEN + "Capatcha-based verification has been disabled.");
                        return true;
                    default:
                        return false;
                }
            case 2:
                switch(args[0])
                {
                    case "on":
                        Capatchafy.enabled = true;
                        Capatchafy.forced = true;
                        Capatchafy.listeners.numberOfAttacks = 0;
                        Bukkit.broadcastMessage(ChatColor.DARK_RED + "Capatcha-based verification has been enabled.");
                        //Friendly - One time verification.
                        //Moderate - One time verification for each time the server starts.
                        //Strict - Needs verification each time you join/leave.
                        switch(args[1])
                        {
                            case "friendly":
                            case "1":
                                Capatchafy.securityLevel = 1;
                                break;
                            case "moderate":
                            case "2":
                                Capatchafy.securityLevel = 2;
                                break;
                            case "strict":
                            case "3":
                                Capatchafy.securityLevel = 3;
                                Capatchafy.configuration.ipList.clear();
                                break;
                            default:
                                return false;
                        }
                        return true;
                    case "off":
                        Capatchafy.enabled = false;
                        Capatchafy.forced = false;
                        Capatchafy.listeners.numberOfAttacks = 0;
                        Bukkit.broadcastMessage(ChatColor.GREEN + "Capatcha-based verification has been disabled.");
                        switch(args[1])
                        {
                            case "-f":
                                if(args[1].equalsIgnoreCase("-f"))
                                {
                                    Capatchafy.forced = true;
                                    sender.sendMessage(ChatColor.YELLOW + "Capatchafy " + ChatColor.RED + "will not" + ChatColor.YELLOW + " automatically enable if the server detects an attack.");
                                }
                                return true;
                            default:
                                break;
                        }
                        return true;
                    default:
                        return false;
                }
            default:
                return false;
        }
    }
}
