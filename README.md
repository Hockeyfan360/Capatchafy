# Capatchafy - The only spambot solution you'll ever need.

### What is Capatchafy?
Capatchafy is a Spigot plugin/extension for Minecraft servers. Its sole purpose is to protect against spambot attacks. When a server is under attack, Capatchafy directs players to solve a capatcha before joining. This prevents spambots from flooding and crashing a server. Attacks are automatically detected by Capatchafy, so you can kick back and let Capatchafy do the work.

### How does Capatchafy work?
From a user's standpoint, during an attack, they will be kicked and ask to solve a capatcha at example.com:port/capatcha/. Once the capatcha is solved, their ip is added to a list of authorized IPs and they are allowed to join.

Capatchafy runs on a lightweight, embedded Grizzly container for Jersey. This allows us to make full use of the jax-rs API. When a player accesses the capatcha URL in their browser, they send an HTTP/GET request to the server, which is processed by Capatchafy. Capatchafy serves the user the capatcha. The player solves the capatcha. Their capatcha data is POSTed back to the server, and is then sent off to Google to be verified. Once Google gives the okay, the player's IP is added to the list of authorized IPs.

### Features
- Capatcha-based Spam-bot Prevention.
- IP Whitelisting
- Automatic Attack Detection
- Multiple Security Modes
- Check the Issues tab for more.

### How do I use Capatchafy?
Capatchafy comes with one simple command: /capatchafy.

Usage: /capatchafy <on:off> <friendly:moderate:strict> or /capatchafy <on:off> <1:2:3>
The on/off parameter will turn on/turn off Capatcha based verification. The second parameter is the security level.

Security levels:
1/Friendly - Players will only have to solve a capatcha once. Their IPs will be saved in a config for future logins.
2/Moderate - Players will have to solve one capatcha every time the server reloads. (Recommended)
3/Strict - Players will have to solve a capatcha every time they join.

Capatchafy will auto-enable/disable when the server is attacked. You can force Capatchafy to stay off by using the command /capatchafy off -f. Capatchafy will not auto-disable if you enable it using the command.

You will need to assign the permission 'capatchafy.command' to anyone who needs to use /capatchafy. It is assigned to OPs by default.

You can also whitelist IPs in the config. These IPs will never have to solve a capatcha.

### How do I install Capatchafy?
Installation is fairly straight-forward. Drag Capatchafy to your plugins folder like any other add-on, and start your server. This will allow Capatchafy to generate the necessary config files. Then, edit the config as you please. You will need to obtain a reCapatcha key from Google. You can do that here: https://www.google.com/recaptcha/. Place the keys in the config file, and you're ready to go!

---

### Compiling
Capatchafy makes use of Maven, so it should compile automatically. If you are using Intellij or Eclipse, you might need to run 'mvn package' in the command line to build the project. Netbeans does this for you. You shouldn't need to add any dependencies or make edits to the POM. However, you MUST use the jar labeled Capatchafy-jar-with-dependencies. If you don't, you will get NoClassDefFound errors. If you have any problems, open an issue.

### License
Capatchafy is licensed under the GNU General Public License v3.0. You can find a copy of it in the License.txt file. 
