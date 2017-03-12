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

import me.hockey.capatchafy.httpd.HttpdServer;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPreLoginEvent;

import java.util.Date;
import org.bukkit.ChatColor;

public class Listeners implements Listener
{
    //Auto-enable Settings
    public int points;
    public Date lastLogin;
    public int numberOfAttacks; //This is the number of attacks since admin intervention, not necessarily the number of attacks since the server has been online.

    public long throttleTime = 1L; //The higher, the more often players will be counted as possible spammers. Change to .1 .
    public int throttleLogins = 3; //The lower, the quicker the capatchas will auto-enable. Change to 5.
    public long removeAllPointsTime = 10L; //Remove all points after x seconds.
    public int maxAttacks = 3; //Set to 0 to disable this function.
    
    //TODO If IP matches IP in TFM, dont force admins to verify.
    @EventHandler
    public void onPlayerLogin(PlayerPreLoginEvent event)
    {
        if (!Capatchafy.forced && !Capatchafy.configs.config.getBoolean("always-on"))
        {
            throttleConnections();
        }
        if (!Capatchafy.enabled) return;
        String ip = event.getAddress().toString().replaceAll("/", "");
        if (!Capatchafy.configs.isAuthorized(ip))
        {
            event.disallow(PlayerPreLoginEvent.Result.KICK_OTHER, ChatColor.RED + "" + ChatColor.BOLD + "Yikes, we're under attack! Please solve the capatcha.\n" +
                    ChatColor.WHITE + "Please go to " + ChatColor.GOLD + HttpdServer.BASE_URI.replaceAll(":80", "") + "capatcha/" + ChatColor.WHITE + " and solve the capatcha.\n" +
                    "Once solved successfully, you will be able to join.");
            return;
        }
        if (Capatchafy.securityLevel == 3)
        {
            Capatchafy.configs.setAuthorized(ip, false);
            //Bukkit.broadcastMessage("Player unauthorized.");
            //Bukkit.broadcastMessage("Player is authorized: " + Capatchafy.configs.isAuthorized(ip));
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

        if (points == throttleLogins && !Capatchafy.enabled)
        {
            Capatchafy.enabled = true;
            numberOfAttacks++;
        }
        if (numberOfAttacks >= maxAttacks && maxAttacks != 0)
        {
            Capatchafy.enabled = true;
            Capatchafy.forced = true;
            lastLogin = currentTime;
            return;
        }
        if (diffInSeconds >= removeAllPointsTime)
        {
            points = 0;
            Capatchafy.enabled = false;
        }
        lastLogin = currentTime;
    }
}
