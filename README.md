# 🌶️ SpicyQoL

A modern, optimized Quality of Life plugin for Minecraft servers.

## ✨ Features
- GUI-based homes system
- Interactive TPA system (chat buttons + GUI)
- /back system (death & teleport safe)
- Random teleport (/rtp)
- Player menu and sidebar scoreboard
- Fixed menu compass in hotbar slot 9 (right-click to open)
- Team prefix and colors in player chat and nametags, plus a styled tab header/footer
- 20-minute random teleport cooldown
- Day/night requests decided by a majority vote when multiple players are online
- Spawn management
- Ping command
- Optimized autosave system
- Clean and customizable messages

## 📌 Commands
| Command | Description |
|------|-------------|
| /qol | Show plugin help |
| /spawn | Teleport to spawn |
| /setspawn | Set spawn location |
| /home [name] | Teleport to a home |
| /sethome [name] | Set a home |
| /delhome <name> | Delete a home |
| /homes | Open homes GUI |
| /tpa <player> | Send teleport request |
| /tpaccept | Accept teleport request |
| /tpdeny | Deny teleport request |
| /tpamenu | Open TPA GUI |
| /back | Return to last location |
| /rtp | Open random teleport menu |
| /ping | Show ping |
| /sit | Sit / stand toggle |
| /qolmenu | Open the player menu |
| /day | Propose a vote to set daytime |
| /night | Propose a vote to set nighttime |
| /timevote si\|no | Vote on the active time request |

## 🔐 Permissions
- spicyqol.setspawn
- spicyqol.spawn
- spicyqol.home
- spicyqol.sethome
- spicyqol.delhome
- spicyqol.homes
- spicyqol.tpa
- spicyqol.back
- spicyqol.rtp
- spicyqol.rtp.bypass (bypass the RTP cooldown; operators by default)
- spicyqol.timevote
- spicyqol.ping
- spicyqol.sit

## ⚙️ Compatibility
- Minecraft Java Edition 1.20.1 and later (including 1.21 and 26.3)
- Spigot / Paper
- Java 17 bytecode; use the Java version required by your Minecraft server

The plugin is compiled against the Spigot 1.20.1 API and uses `api-version: "1.20"`, so it can load on 1.20.1 and later servers. The 26.3 update does not introduce API usage required by this plugin; it continues to use stable server APIs.

## 🚀 Installation
1. Download the latest release
2. Put `SpicyQoL.jar` into `/plugins`
3. Restart the server

## 🛠️ Build
```bash
mvn clean package
```

The resulting plugin is `target/SpicyQoL.jar`.

## 🧑‍💻 Autore
**giospezia.it**  
