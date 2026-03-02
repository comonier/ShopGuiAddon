# 🛒 ShopGuiAddon 1.4 - The "Universal Quantity" Update

**ShopGuiAddon** is a professional expansion for [ShopGUI+](https://www.spigotmc.org). This version introduces full **Quantity (Amount)** management and **BigDecimal** financial precision, ensuring a 100% bug-free experience for both Java and Bedrock (Geyser) administrators.

---

## ✨ Key Features

- 🛠️ **Triple-Row Visual Editor:** Adjust buy prices, sell prices, and item quantities in a single unified GUI.
- 💰 **BigDecimal Precision:** Fixed Java's floating-point rounding bugs. Adjustments like **0.10** are now 100% exact.
- 📦 **Pack Management:** Add items from your hand with a specific quantity (e.g., a full pack of 64).
- 📂 **Full Shop Management:** Create or permanently delete shop files (.yml) via in-game commands.
- 🔗 **Main Menu Manager:** Link, replace, or unlink shortcuts in your `/shop` menu with Base64 skin support.
- 📱 **Bedrock Ready:** Uses a "Toggle Switch" system instead of right-clicks, making it 100% compatible with touchscreens and controllers.
- 🔒 **Anti-Drag Interface:** Advanced `InventoryDragEvent` blocking system prevents "floating items" or accidental icon movement.
- 🌍 **Localization Support:** Full `messages.yml` support for English (default) and Portuguese (pt-BR).

---

## ⚙️ The Triple Toggle System

To ensure full compatibility with mobile and console players, the editor uses three dedicated **Toggle Switches**:
- **Slot 26 (Buy Row):** White (+) to Add | Black (-) to Subtract.
- **Slot 35 (Sell Row):** White (+) to Add | Black (-) to Subtract.
- **Slot 44 (Quantity Row):** White (+) to Add | Black (-) to Subtract.

---

## 💻 Complete Command List
**Required Permission:** `shopguiaddon.admin`


| Command | Description |
| :--- | :--- |
| `/sga help` | Displays the localized help menu. |
| `/sga list` | Lists all available shop files in the `/shops` folder. |
| `/sga reload` | Reloads configurations, messages, and shop cache. |
| `/sga edit [shop] [slot]` | Opens the visual price/quantity editor for a specific slot. |
| `/sga itemadd [shop] [slot] [buy] [sell] [amount]` | Adds the held item to a shop with specified prices/quantity. |
| `/sga itemremove [shop] [slot]` | Permanently removes an item from a shop slot. |
| `/sga shopcreate [name]` | Creates a new shop `.yml` file with default settings. |
| `/sga shopremove [name]` | Deletes a shop `.yml` file from the plugin folder. |
| `/sga link [shop] [slot] [mat] [skin]` | Adds a new shop link to the main menu icon. |
| `/sga replace [shop] [slot] [mat]` | Overwrites an existing main menu slot with a new shop link. |
| `/sga unlink [slot]` | Removes a shop link/shortcut from the main menu. |
| `/sga menu [slot] [name\|lore] [text]` | Edits the display name or lore of a main menu icon. |
| `/sga item [shop] [slot] [name\|lore] [text]` | Edits the display name or lore of an item inside a shop. |

---

## 🧪 Logical Testing Flow (SGA 1.4)

1. **Shop & Item Setup:**
   - `/sga shopcreate test`
   - `/sga itemadd test 10 100 50 64`
   - `/sga edit test 10`

2. **Main Menu Management:**
   - `/sga link test 20 DIAMOND_BLOCK`
   - `/sga menu 20 name &6&lVIP_Shop`
   - `/sga menu 20 lore &7Line_1;&eLine_2`
   - `/sga replace test 20 GOLD_BLOCK`

3. **GUI Internal Controls (Quantity Test):**
   - Open `/sga edit test 10`
   - Click **Slot 44** (Toggle White to Black) -> Subtract Mode.
   - Click Chests (**Slots 36-43**) -> Verify Quantity decrease in visor (Slot 4).
   - Click **Slot 53** (Blaze Powder) -> Reload ShopGUI+ via command.

---
**Developed by:** Comonier | **Version:** 1.4
