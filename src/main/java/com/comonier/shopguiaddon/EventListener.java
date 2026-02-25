package com.comonier.shopguiaddon;

import net.brcdev.shopgui.ShopGuiPlusApi;
import net.brcdev.shopgui.shop.item.ShopItem;
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
        
        // Split returns an array
        String[] parts = title.split(":|\\|");
        
        // Inverse logic: if 4 is greater than parts.length
        if (4 > parts.length) {
            return;
        }

        // Accessing array indices correctly
        String shopName = parts[1].trim();
        String slotStr = parts[3].trim();
        int currentSlot = Integer.parseInt(slotStr);

        ShopItem shopItem = ShopGuiPlusApi.getItemStackShopItem(player.getInventory().getItemInMainHand());
        FileConfiguration config = ShopGuiAddon.getInstance().getConfig();

        if (8 > slotClicked) {
            double amount = config.getDouble("gui.buy_adjustments." + slotClicked + ".amount");
            updatePrice(shopItem, amount, true);
        }

        if (slotClicked > 34 && 43 > slotClicked) {
            int index = slotClicked - 35;
            double amount = config.getDouble("gui.sell_adjustments." + index + ".amount");
            updatePrice(shopItem, amount, false);
        }

        if (slotClicked == 45 && currentSlot > 0) {
            GuiManager.openEditor(player, shopName, currentSlot - 1);
        }

        if (slotClicked == 46 && 53 > currentSlot) {
            GuiManager.openEditor(player, shopName, currentSlot + 1);
        }

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
