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

import java.io.File;
import java.io.IOException;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import java.util.ArrayList;
import java.util.List;

public class Configuration
{
    private final Capatchafy plugin;
    
    public Configuration(Capatchafy plugin)
    {
        this.plugin = plugin;
    }
    
    //TODO Add time based authorization expiration.
    FileConfiguration ipConfig;
    final File ipConfigFile = new File(Capatchafy.plugin.getDataFolder(), "ips.yml");
    FileConfiguration mainConfig;
    final File mainConfigFile = new File(Capatchafy.plugin.getDataFolder(), "config.yml");
    public final List<String> ipList = new ArrayList<>();
    public List<String> alwaysAuthorizedList = new ArrayList<>();
    
    private void generateMainConfig()
    {
        new File(plugin.getDataFolder().getAbsolutePath()).mkdirs();
        File configFile = new File(plugin.getDataFolder() + File.separator + "config.yml");
        if(!configFile.exists())
        {
            Bukkit.getLogger().info("[Capatchafy] Generated main config.");
            plugin.saveDefaultConfig();
        }
        else if(isIncomplete())
        {
            Bukkit.getLogger().severe("[Capatchafy] There is information missing in the config. Please make the appropriate changes. " +
                                      "This is normal on the first run. Reload the server once you have made the correct edits.");
            Capatchafy.error = true;
            plugin.getServer().getPluginManager().disablePlugin(plugin);
        }
    }
    
    private void generateIpConfig()
    {
        new File(plugin.getDataFolder().getAbsolutePath()).mkdirs();
        File configFile = new File(plugin.getDataFolder() + File.separator + "ips.yml");
        if(!configFile.exists())
        {
            Bukkit.getLogger().info("[Capatchafy] Generated ip config.");
            plugin.saveConfig();
        }
    }
    
    public void generateConfigs()
    {
        generateMainConfig();
        generateIpConfig();
    }
    
    public void loadConfigs()
    {
        mainConfig = YamlConfiguration.loadConfiguration(mainConfigFile);
        ipConfig = YamlConfiguration.loadConfiguration(ipConfigFile);
        alwaysAuthorizedList = ipConfig.getStringList("authorized-ips");
        Bukkit.getLogger().info("[Capatchafy] Configs loaded.");
    }
    
    public boolean isIncomplete()
    {
        return getHostname() == null
               || getPort() == null
               || getCapatchaSiteKey() == null
               || getCapatchaSecret() == null
               || mainConfig.get("always-on") == null
               || mainConfig.get("security-level") == null;
    }
    
    public void saveIps()
    {
        try
        {
            ipConfig.set("authorized-ips", alwaysAuthorizedList);
            ipConfig.save(ipConfigFile);
        }
        catch(IOException e)
        {
            Bukkit.getLogger().severe("[Capatchafy] Fatal error encountered when saving configuration! Stack trace: " + e.getMessage());
        }
    }
    
    public void setAuthorized(String ip, boolean authorize)
    {
        if(!authorize)
        {
            ipList.remove(ip);
            return;
            //We dont remove always authorized ips, because they were added while in the friendly mode, so they should still be added to the ips.yml.
        }
        switch(Capatchafy.securityLevel)
        {
            case 1:
            {
                if(!ipList.contains(ip))
                {
                    ipList.add(ip);
                }
                alwaysAuthorizedList.add(ip);
            }
            case 2:
            case 3:
            {
                if(!ipList.contains(ip))
                {
                    ipList.add(ip);
                }
            }
        }
    }
    
    public boolean isAuthorized(String ip)
    {
        if(mainConfig.getStringList("whitelisted-ips").contains(ip))
        {
            return true;
        }
        switch(Capatchafy.securityLevel)
        {
            case 1:
            {
                return alwaysAuthorizedList.contains(ip) || ipConfig.getStringList("authorized-ips").contains(ip);
            }
            case 2:
            {
                return ipList.contains(ip);
            }
            case 3:
            {
                return ipList.contains(ip);
            }
        }
        return false;
    }
    
    public String getHostname()
    {
        return mainConfig.getString("hostname");
    }
    
    public String getPort()
    {
        return mainConfig.getString("port");
    }
    
    public String getCapatchaSiteKey()
    {
        return mainConfig.getString("recapatcha-key");
    }
    
    public String getCapatchaSecret()
    {
        return mainConfig.getString("recapatcha-secret");
    }
}