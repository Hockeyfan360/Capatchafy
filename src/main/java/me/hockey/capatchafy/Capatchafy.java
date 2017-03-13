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
import org.bukkit.plugin.java.JavaPlugin;
import org.glassfish.grizzly.http.server.HttpServer;

import java.io.IOException;

public class Capatchafy extends JavaPlugin
{
    public static Capatchafy plugin;
    public static Listeners listeners;

    public static Configuration configs;
    public static boolean enabled = false;
    public static boolean forced = false; //Disallows auto-enabling/disabling of capatchafy.
    public static int securityLevel;
    public HttpServer server;
    
    public static boolean error = false;
    
    @Override
    public void onEnable()
    {
        plugin = this;
        configs = new Configuration();
        listeners = new Listeners();
        Bukkit.getPluginManager().registerEvents(listeners, this);
        getCommand("capatchafy").setExecutor(new CapatchafyCommand());
        try
        {
            configs = new Configuration();
            configs.startup();
            if (configs.isIncomplete())
            {
                Bukkit.getLogger().severe("[Capatchafy] There is information missing in the config. Please make the appropriate changes. " +
                    "This is normal on the first run. Reload the server once you have made the correct edits.");
                Capatchafy.error = true;
                Bukkit.getPluginManager().disablePlugin(Capatchafy.plugin);
                return;
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        securityLevel = configs.config.getInt("security-level");
        if (securityLevel > 3 || securityLevel < 1)
        {
            securityLevel = 2;
            Bukkit.getLogger().severe("[Capatchafy] The 'security-level' config field was not between 1 and 3. Setting security level to 2.");
        }
        enabled = configs.config.getBoolean("always-on");
        server = HttpdServer.startServer();
        listeners.setURLMessage();
        Bukkit.getLogger().info("[Capatchafy] Running in security level " + securityLevel + ".");
    }
    
    @Override
    public void onDisable()
    {
        if (error)
        {
            return; //Prevents errors on the first startup.
        }
        
        try
        {
            configs.saveNames(true);
            configs.saveConfig();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        server.stop();
        configs.ipList.clear(); //TODO See if removing this line affects functionality.
    }
}
