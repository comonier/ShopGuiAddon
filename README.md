# ShopGuiAddon

A professional **ShopGUI+** extension designed for Minecraft 1.21.1+, built with Java 21. This addon streamlines shop management through an advanced GUI editor and direct item injection commands.

## Features

*   **Real-time GUI Editor:** Dynamic 54-slot interface to adjust prices on the fly.
*   **Smart Price Adjustment:** Top row increments Buy prices; Bottom row decrements Sell prices.
*   **Direct Item Injection:** Use `/sga itemadd` to instantly push the item in your hand to any shop config.
*   **Localization System:** Built-in support for English (Default) and Portuguese via `messages.yml`.
*   **API Integrated:** Native hooks into the ShopGUI+ API for data consistency.

## Commands


| Command | Description | Permission |
|:---|:---|:---|
| `/sga edit [shop] [slot]` | Opens the visual price editor for a specific slot. | `shopguiaddon.admin` |
| `/sga itemadd [shop] [slot] [buy] [sell]` | Adds your hand item to a shop with set prices. | `shopguiaddon.admin` |
| `/sga reload` | Hot-reloads `config.yml` and `messages.yml`. | `shopguiaddon.admin` |

## GUI Interface Map

*   **Slots 0-7 (Top):** Buy Price Adjustments (**+0.1 to +1M** Coins).
*   **Slots 35-42 (Bottom):** Sell Price Adjustments (**-0.1 to -1M** Coins).
*   **Slot 45:** Previous Slot (Navigate backwards).
*   **Slot 46:** Next Slot (Navigate forwards).
*   **Slot 52:** Reload Addon Config.
*   **Slot 53:** Global Save & Reload (Triggers ShopGUI+ reload).

## Technical Requirements

*   **Java Version:** 21 or higher.
*   **Spigot/Paper:** 1.21.1.
*   **Dependency:** [ShopGUI+](https://docs.brcdev.net).

## Installation

1. Verify that **ShopGUI+** is active on your server.
2. Drop `ShopGuiAddon.jar` into the `/plugins` directory.
3. Restart the server to generate default files.
4. Customize `language: en` or `language: pt` in `config.yml`.
