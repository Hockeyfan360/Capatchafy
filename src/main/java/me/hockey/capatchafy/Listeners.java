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

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import java.util.Date;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

public class Listeners implements Listener
{
    private final Capatchafy plugin;
    
    public Listeners(Capatchafy plugin)
    {
        this.plugin = plugin;
    }
    
    //Auto-enable Settings
    public int points;
    public Date lastLogin;
    public int numberOfAttacks; //This is the number of attacks since admin intervention, not necessarily the number of attacks since the server has been online.
    
    public long throttleTime; //The higher, the more often players will be counted as possible spammers. Required time between each login to not be considered an attack.
    public int throttleLogins; //The lower, the quicker the capatchas will auto-enable. Aka max points before capatchafy is enabled.
    public final long removeAllPointsTime = 7L; //Remove all points after x seconds.
    public final int maxAttacks = 3; //Set to 0 to disable this function.
    
    public final long startupThrottleTime = 1L; //The throttle time on startup. Default: 1
    public final int startupThrottleLogins = 20; //The throttle logins on startup. Default: 20
    
    public final long defaultThrottleTime = 3L; //After 30 seconds, the server defaults back to this. Default: 3
    public final int defaultThrottleLogins = 8; //Default: 9
    
    public static String url;
    
    //TODO If IP matches IP in TFM, dont force admins to verify.
    @EventHandler
    public void onPlayerLogin(AsyncPlayerPreLoginEvent event)
    {
        if(!Capatchafy.forced && !Capatchafy.configuration.mainConfig.getBoolean("always-on"))
        {
            throttleConnections();
        }
        
        if(!Capatchafy.enabled)
        {
            return;
        }
        
        String ip = event.getAddress().toString().replaceAll("/", "");
        if(!Capatchafy.configuration.isAuthorized(ip))
        {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, ChatColor.RED + "" + ChatColor.BOLD + "Yikes, we're under attack! Please solve the capatcha.\n" +
                                                                       ChatColor.WHITE + "Please go to " + ChatColor.GOLD + url + ChatColor.WHITE + " in your web browser and solve the capatcha.\n" +
                                                                       "Once solved successfully, you will be able to join.");
            return;
        }
        
        if(Capatchafy.securityLevel == 3)
        {
            Capatchafy.configuration.setAuthorized(ip, false);
            //Bukkit.broadcastMessage("Player unauthorized.");
            //Bukkit.broadcastMessage("Player is authorized: " + Capatchafy.configs.isAuthorized(ip));
        }
    }
    
    public void throttleConnections()
    {
        if(lastLogin == null)
        {
            lastLogin = new Date();
            return;
        }
        
        Date currentTime = new Date();
        long diffInSeconds = (currentTime.getTime() - lastLogin.getTime()) / 1000 % 60;
        if(diffInSeconds <= throttleTime && points != throttleLogins) //points != throttleLogins works because it adds a point when the points is not the ammount needed to throttle.
        {
            points++;
        }
        
        if(diffInSeconds >= removeAllPointsTime && !Capatchafy.forced)
        {
            points = 0;
            //Bukkit.broadcastMessage("[Capatchafy] Disabled");
            Capatchafy.enabled = false;
        }
        
        if(points == throttleLogins && !Capatchafy.enabled)
        {
            //Bukkit.broadcastMessage("[Capatchafy] Enabled");
            Capatchafy.enabled = true;
            numberOfAttacks++;
        }
        
        if(numberOfAttacks >= maxAttacks && maxAttacks != 0)
        {
            //Bukkit.broadcastMessage("[Capatchafy] Enabled for good.");
            Capatchafy.enabled = true;
            Capatchafy.forced = true;
            lastLogin = currentTime;
            return;
        }
        
        lastLogin = currentTime;
    }
    
    public void setURLMessage()
    {
        if(Capatchafy.configuration.getPort().equals("80"))
        {
            url = Capatchafy.configuration.getHostname();
            return;
        }
        
        url = Capatchafy.configuration.getHostname() + ":" + Capatchafy.configuration.getPort();
    }
    
    public void setThrottleSettings()
    {
        this.throttleTime = startupThrottleTime;
        this.throttleLogins = startupThrottleLogins;
        //Bukkit.getLogger().info("[Capatchafy] Delay started. " + throttleTime + " " + throttleLogins);
        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                throttleTime = defaultThrottleTime;
                throttleLogins = defaultThrottleLogins;
                //Bukkit.broadcastMessage("[Capatchafy] Delay ended: " + throttleTime + " " + throttleLogins);
            }
        }.runTaskLater(Capatchafy.plugin, 1200);
    }
}
