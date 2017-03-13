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
package me.hockey.capatchafy.httpd;

import me.hockey.capatchafy.Capatchafy;
import org.bukkit.Bukkit;
import org.glassfish.grizzly.http.server.Request;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

@Path("/")
public class CapatchaPage 
{
    @GET
    @Produces(MediaType.TEXT_HTML)
    public String urlAccessed(@Context Request req)
    {
        if (Capatchafy.configs.isAuthorized(req.getRemoteAddr()) || !Capatchafy.enabled)
        {
            return "You are already authorized.";
        }
        return  "<head>" +
                "<script src='https://www.google.com/recaptcha/api.js'></script></head>" +
                "<script>function callback(){document.getElementById(\"form\").submit();}</script>"+
                "<form id=\"form\"method=\"post\">" +
                "<div class=\"g-recaptcha\" data-callback=\"callback\" data-sitekey=\"" + Capatchafy.configs.getCapatchaSiteKey() + "\"></div>" +
                "</form>";
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public void handlePost(@FormParam("g-recaptcha-response") String content, @Context Request req)
    {
        try
        {
            //Bukkit.getLogger().info("A capatcha has been recieved from " + req.getRemoteAddr());
            if (validateCaptcha(Capatchafy.configs.getCapatchaSecret(), content, req.getRemoteAddr()))
            {
                //Bukkit.getLogger().info("Capatcha from " + req.getRemoteAddr() + " is valid.");
                Capatchafy.configs.setAuthorized(req.getRemoteAddr(), true);
            }
        }
        catch (Exception e)
        {
            System.out.print(e.getLocalizedMessage());
        }
    }

    public boolean validateCaptcha(String secret, String response, String remoteip)
    {
        JsonObject jsonObject = null;
        URLConnection connection = null;
        InputStream is = null;
        String charset = java.nio.charset.StandardCharsets.UTF_8.name();

        String url = "https://www.google.com/recaptcha/api/siteverify";
        try 
        {
            String query = String.format("secret=%s&response=%s&remoteip=%s",
                    URLEncoder.encode(secret, charset),
                    URLEncoder.encode(response, charset),
                    URLEncoder.encode(remoteip, charset));

            connection = new URL(url + "?" + query).openConnection();
            is = connection.getInputStream();
            JsonReader rdr = Json.createReader(is);
            jsonObject = rdr.readObject();
        } 
        catch (IOException ex) 
        {
            Bukkit.broadcastMessage(ex.getStackTrace().toString());
        }
        finally 
        {
            if (is != null) 
            {
                try 
                {
                    is.close();
                } 
                catch (IOException e)
                {
                    System.out.print(e.getLocalizedMessage());
                }
            }
        }
        return jsonObject.getBoolean("success");
    }
}

