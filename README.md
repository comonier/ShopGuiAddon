# 🛒 ShopGuiAddon 1.3 - The "Universal Quantity" Update

**ShopGuiAddon** is a professional expansion for ShopGUI+. This version introduces full **Quantity (Amount)** management and **BigDecimal** financial precision, ensuring a 100% bug-free experience for both Java and Bedrock (Geyser) administrators.

---

## ✨ Key Features

- 🛠️ **Triple-Row Visual Editor:** Adjust buy prices, sell prices, and item quantities in a single GUI.
- 💰 **BigDecimal Precision:** Fixed Java's rounding bugs. Adjustments like **0.10** are now 100% exact.
- 📦 **Pack Management:** Add items from your hand with a specific quantity (e.g., a pack of 64).
- 📂 **Full Shop Management:** Create or permanently delete shop files via command.
- 🔗 **Main Menu Manager:** Link, replace, or unlink shortcuts in your `/shop` menu with Base64 support.
- 📱 **Bedrock Ready:** Uses a "Toggle Switch" system instead of right-clicks, making it compatible with touchscreens and controllers.

---

## ⚙️ The Triple Toggle System

To ensure full compatibility with mobile and console players, the editor uses three dedicated **Toggle Switches**:
- **Slot 26 (Buy Row):** White (+) to Add | Black (-) to Subtract.
- **Slot 35 (Sell Row):** White (+) to Add | Black (-) to Subtract.
- **Slot 44 (Quantity Row):** White (+) to Add | Black (-) to Subtract.

---

## 🧪 Logical Testing Flow (SGA 1.3)

# 1. Shop & Item Setup (Now with Amount argument)
/sga shopcreate test
/sga itemadd test 10 100 50 64
/sga edit test 10
/sga item test 10 name &aSpecial_Item
/sga item test 10 lore &7Line_1;&eLine_2

# 2. Main Menu Management
/sga link test 20 DIAMOND_BLOCK
/sga link test 20 GOLD_BLOCK
/sga replace test 20 GOLD_BLOCK
/sga menu 20 name &6&lVIP_Shop
/sga menu 20 lore &7Line_1;&eLine_2
/sga link test 21 PLAYER_HEAD [Base64]

# 3. Cleanup & Finalization
/sga unlink 20
/sga itemremove test 10
/sga shopremove test
/sga reload

# 4. GUI Internal Controls (Quantity Row Test)
- Open /sga edit test 10
- Click Slot 44 (Toggle White to Black) -> Subtract Mode
- Click Chests (Slots 36-43) -> Verify Quantity decrease in visor (Slot 4)
- Click Slot 44 (Toggle Black to White) -> Add Mode
- Click Chests (Slots 36-43) -> Verify Quantity increase in visor
- Click Slot 53 (Blaze Powder) -> Reload ShopGUI+

---

## 💻 Commands & Permissions
Permission: shopguiaddon.admin



| Command | Description |
| :--- | :--- |
| /sga help | Displays the help menu. |
| /sga edit [shop] [slot] | Opens the visual price/quantity editor. |
| /sga itemadd [shop] [slot] [buy] [sell] [amount] | Adds held item with a specific quantity. |
| /sga itemremove [shop] [slot] | Removes an item from a shop. |
| /sga shopcreate [name] | Creates a new shop .yml file. |
| /sga shopremove [name] | Deletes a shop .yml file permanently. |
| /sga link [shop] [slot] [mat] [skin] | Links a shop to the main menu. |
| /sga menu [slot] [name\|lore] [text] | Edits main menu icon display. |
| /sga item [shop] [slot] [name\|lore] [text] | Edits shop item display. |
| /sga reload | Reloads settings and shop cache. |

---
Developed by: Comonier
