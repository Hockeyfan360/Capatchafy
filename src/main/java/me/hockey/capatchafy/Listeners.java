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
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.Date;
import org.bukkit.ChatColor;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class Listeners implements Listener
{
    //Auto-enable Settings
    public int points;
    public Date lastLogin;
    public int numberOfAttacks; //This is the number of attacks since admin intervention, not necessarily the number of attacks since the server has been online.

    public long throttleTime; //The higher, the more often players will be counted as possible spammers. Required time between each login to not be considered an attack.
    public int throttleLogins; //The lower, the quicker the capatchas will auto-enable. Aka max points before capatchafy is enabled.
    public long removeAllPointsTime = 7L; //Remove all points after x seconds. Default: 7
    public int maxAttacks = 3; //Set to 0 to disable this function. Default: 3
    
    public long startupThrottleTime = 1L; //The throttle time on startup. Default: 1
    public int startupThrottleLogins = 20; //The throttle logins on startup. Default: 20
    
    public long defaultThrottleTime = 3L; //After 30 seconds, the server defaults back to this. Default: 3
    public int defaultThrottleLogins = 8; //Default: 8
    
    public static String url;
    
    //TODO If IP matches IP in TFM, dont force admins to verify.
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerLogin(PlayerLoginEvent event)
    {
        if (!Capatchafy.forced && !Capatchafy.configs.config.getBoolean("always-on") && !Capatchafy.enabled)
        {
            throttleConnections();
        }
        if (!Capatchafy.enabled) return;
        String ip = event.getAddress().toString().replaceAll("/", "");
        if (!Capatchafy.configs.isAuthorized(ip))
        {
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, ChatColor.RED + "" + ChatColor.BOLD + "Yikes, we're under attack! Please solve the capatcha.\n" +
                    ChatColor.WHITE + "Please go to " + ChatColor.GOLD + url + ChatColor.WHITE + " in your web browser and solve the capatcha.\n" +
                    "Once solved successfully, you will be able to join.");
            return;
        }
        if (Capatchafy.securityLevel == 3)
        {
            Capatchafy.configs.setAuthorized(ip, false);
        }
    }

    public void throttleConnections()
    {
        if (lastLogin == null)
        {
            lastLogin = new Date();
            return;
        }
        Date currentTime = new Date();
        long diffInSeconds = (currentTime.getTime() - lastLogin.getTime()) / 1000 % 60;
        if (diffInSeconds <= throttleTime && points != throttleLogins) //points != throttleLogins works because it adds a point when the points is not the ammount needed to throttle.
        {
            points++;
        }

        if (diffInSeconds >= removeAllPointsTime)
        {
            points = 0;
        }

        if (points == throttleLogins && !Capatchafy.enabled)
        {
            Bukkit.broadcastMessage(ChatColor.DARK_RED + "Capatcha-based verification has been enabled.");
            Capatchafy.enabled = true;
            numberOfAttacks++;
            points = 0;

            new BukkitRunnable()
            {
                @Override
                public void run()
                {
                    if (!Capatchafy.forced && Capatchafy.enabled)
                    {
                        Bukkit.broadcastMessage(ChatColor.GREEN + "Capatcha-based verification has been disabled.");
                        Capatchafy.enabled = false;
                    }
                }
            }.runTaskLater(Capatchafy.plugin, 5 * 60 * 20); //Default 5 * 60 * 20
        }
        if (numberOfAttacks >= maxAttacks && maxAttacks != 0)
        {
            Capatchafy.enabled = true;
            Capatchafy.forced = true;
            lastLogin = currentTime;
            Bukkit.broadcastMessage(ChatColor.DARK_RED + "Capatchafy will not auto-disable.");
            return;
        }
        lastLogin = currentTime;
    }
    
    public void setURLMessage()
    {
        if (Capatchafy.configs.getPort().equals("80"))
        {
            url = Capatchafy.configs.getHostname();
            return;
        }
        url = Capatchafy.configs.getHostname() + ":" + Capatchafy.configs.getPort();
    }
    
    public void setThrottleSettings()
    {
        this.throttleTime = startupThrottleTime;
        this.throttleLogins = startupThrottleLogins;
        new BukkitRunnable() 
        {
            @Override
            public void run() 
            {
                throttleTime = defaultThrottleTime;
                throttleLogins = defaultThrottleLogins;
            }           
        }.runTaskLater(Capatchafy.plugin, 1200);
    }
}
