# 🛒 ShopGuiAddon 1.1

**ShopGuiAddon** is a powerful expansion for the [ShopGUI+](https://brcdev.net) plugin. It allows administrators to manage prices, create shops, and edit visual menus directly in-game. It is fully compatible with **Java** and **Bedrock (Geyser)** players thanks to its intuitive toggle-switch interface.

---

## ✨ Key Features

- 🛠️ **Visual Price Editor (GUI):** Adjust buy/sell prices in real-time with a smart toggle system.
- 📦 **Instant Item Addition:** Add items from your hand directly to any shop slot via command.
- 🔗 **Main Menu Manager:** Link, replace, or unlink shop shortcuts in your `/shop` menu.
- 🎨 **Display Customizer:** Edit Names and Lores for both shop items and menu icons (supports `;` for line breaks).
- 👤 **Base64 Skin Support:** Add custom player heads to your main menu shortcuts.
- ⌨️ **Smart Tab-Completion:** Cached suggestions for shops, materials, and subcommands.

---

## ⚙️ The Toggle System (Bedrock Compatible)

To ensure 100% compatibility with mobile and console players (Bedrock), the editor does not rely on right-clicks. Instead, it uses **Toggle Switches** (Slots 26 and 35):
- **White Glass Pane (+):** Addition Mode. Clicking adjustment buttons will **increase** the price.
- **Black Glass Pane (-):** Subtraction Mode. Clicking adjustment buttons will **decrease** the price.

---

## 🧪 Elite Testing Checklist
*Follow this roadmap to master the plugin and verify all integrations.*

### 1. Shop Management (`shops/` folder)
- [ ] **`/sga shopcreate test`**: Verify if `test.yml` was created in the `shops` folder.
- [ ] **`/sga itemadd test 10 100 50`**: Hold an item and check if it appears in slot 10 of `test.yml`.
- [ ] **`/sga itemremove test 10`**: Ensure the item is removed and ShopGUI+ reloads.

### 2. Main Menu Shortcuts (`config.yml`)
- [ ] **`/sga link test 20 DIAMOND_BLOCK`**: Check if the diamond appears in `/shop` slot 20.
- [ ] **`/sga link test 20 GOLD_BLOCK`**: The plugin should block this as slot 20 is occupied.
- [ ] **`/sga replace test 20 GOLD_BLOCK`**: The diamond should be replaced by gold in slot 20.
- [ ] **`/sga unlink 20`**: The slot should return to the default filler/empty state.

### 3. Visual Customization
- [ ] **`/sga menu 20 name &b&lVIP Shop`**: Verify the icon name in the main menu.
- [ ] **`/sga menu 20 lore &7Line 1;&eLine 2`**: Ensure the lore breaks correctly at the `;`.
- [ ] **`/sga item test 10 name &aSpecial Item`**: Check the item's name inside the shop.

### 4. Price Editor (The "Toggle" Test)
- [ ] **`/sga edit test 10`**:
    - **Step A**: Ensure the glass at **Slot 26** is **White**. Click a green pane; the price should **increase**.
    - **Step B**: Click **Slot 26** to turn it **Black**. Click a green pane; the price should **decrease**.
    - **Step C**: Click the **Blaze Powder (Slot 53)** to save and reload ShopGUI+.

### 5. Advanced Features
- [ ] **Base64 Skins**: `/sga link test 21 PLAYER_HEAD [Base64_String]`. Verify the custom head.
- [ ] **Tab-Complete**: Type `/sga edit ` and check if your shop names are suggested.

---

## 💻 Commands & Permissions

**Permission:** `shopguiaddon.admin`


| Command | Description |
| :--- | :--- |
| `/sga help` | Displays the help menu. |
| `/sga edit [shop] [slot]` | Opens the visual price editor. |
| `/sga itemadd [shop] [slot] [buy] [sell]` | Adds held item to a shop. |
| `/sga link [shop] [slot] [mat] [skin]` | Links a shop to the main menu. |
| `/sga menu [slot] [name\|lore] [text]` | Edits main menu icon display. |
| `/sga item [shop] [slot] [name\|lore] [text]` | Edits shop item display. |
| `/sga reload` | Reloads settings and shop cache. |

---

**Developed by:** Comonier
