# 🛒 ShopGuiAddon 1.0 to ShopGUIPlus-1.112.0 plugin

🛡️ Requirements
Java 21
Spigot/Paper 1.21+
ShopGUI+ plugin installed.


**ShopGuiAddon** is a powerful and intuitive expansion for the [ShopGUI+] plugin. It allows server administrators to manage prices, create shops, and edit visual menus directly in-game without the need for manual YAML editing.

---

## ✨ Key Features

- 🛠️ **Visual Price Editor (GUI):** Adjust buy and sell prices using increment buttons (ranging from +0.1 to +1,000,000).
- 📦 **Instant Item Addition:** Add the item in your hand directly to any slot in any shop with a single command.
- 🔗 **Menu Linking (Link/Replace):** Create or overwrite shortcuts in the main menu (`/shop`) with support for **Base64 Skins** and **Custom Lore**.
- 🎨 **In-Game Display Editor:** Change the name and Lore of main menu icons or shop items using simple commands.
- 📂 **Smart File Management:** Automatically detects your shop structure (whether it's `items` or `shopname.items`) to prevent duplicates.
- 🌐 **Multi-Language Support:** Fully translatable (EN/PT) via the `messages.yml` file.

---

## 💻 Commands and Permissions

All commands below require the permission: `shopguiaddon.admin`


| Command | Description |
| :--- | :--- |
| `/sga help` | Displays the interactive help menu. |
| `/sga edit <shop> <slot>` | Opens the visual GUI to edit item prices at the specified slot. |
| `/sga itemadd <shop> <slot> <buy> <sell>` | Adds the item in your hand to the shop with set prices. |
| `/sga shopcreate <name>` | Creates a new shop `.yml` file in the ShopGUI+ shops folder. |
| `/sga link <shop> <slot> <material> [skin]` | Adds a new shortcut to the main shop menu (`/shop`). |
| `/sga replace <shop> <slot> <material> [skin]` | Overwrites an existing shortcut in the main menu. |
| `/sga menu <slot> <name\|lore> <text>` | Edits the Name or Lore of an icon in the main menu. |
| `/sga item <shop> <slot> <name\|lore> <text>` | Edits the Name or Lore of a sale item inside a specific shop. |
| `/sga reload` | Reloads the Addon configurations and messages. |

---

Developed by Comonier