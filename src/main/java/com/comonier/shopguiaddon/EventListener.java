package com.comonier.shopguiaddon;

import net.brcdev.shopgui.ShopGuiPlusApi;
import net.brcdev.shopgui.shop.ShopItem;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.configuration.file.FileConfiguration;

public class EventListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        InventoryView view = event.getView();
        String title = view.getTitle();
        
        if (!title.contains("Editing:")) {
            return;
        }

        event.setCancelled(true);
        Player player = (Player) event.getWhoClicked();
        int slotClicked = event.getRawSlot();
        
        // Parsing "Editing: shopname | Slot: 10"
        String[] parts = title.split(":");
        String shopName = parts[1].split("\\|")[0].trim();
        int currentSlot = Integer.parseInt(parts[2].trim());

        ShopItem shopItem = ShopGuiPlusApi.getItemStackShopItem(player.getInventory().getItemInMainHand());
        FileConfiguration config = ShopGuiAddon.getInstance().getConfig();

        // Buy Price adjustments (Slots 0-7)
        if (8 > slotClicked) {
            double amount = config.getDouble("gui.buy_adjustments." + slotClicked + ".amount");
            updatePrice(shopItem, amount, true);
        }

        // Sell Price adjustments (Slots 35-42)
        if (slotClicked > 34 && 43 > slotClicked) {
            int index = slotClicked - 35;
            double amount = config.getDouble("gui.sell_adjustments." + index + ".amount");
            updatePrice(shopItem, amount, false);
        }

        // Navigation
        if (slotClicked == 45 && currentSlot > 0) {
            GuiManager.openEditor(player, shopName, currentSlot - 1);
        }

        if (slotClicked == 46 && 53 > currentSlot) {
            GuiManager.openEditor(player, shopName, currentSlot + 1);
        }

        // Control Buttons
        if (slotClicked == 52) {
            ShopGuiAddon.getInstance().reloadConfig();
            player.sendMessage(ChatUtils.getMessage("reload_success"));
        }

        if (slotClicked == 53) {
            player.performCommand("shopgui reload");
            player.sendMessage("§d[ShopGUI+] Saved and Reloaded!");
        }
    }

    private void updatePrice(ShopItem item, double amount, boolean isBuy) {
        if (item == null) return;
        if (isBuy) {
            double next = item.getBuyPrice() + amount;
            item.setBuyPrice(next > 0 ? next : 0);
        } else {
            double next = item.getSellPrice() + amount;
            item.setSellPrice(next > 0 ? next : 0);
        }
    }
}
