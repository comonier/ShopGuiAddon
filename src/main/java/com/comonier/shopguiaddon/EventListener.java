package com.comonier.shopguiaddon;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class EventListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        String title = event.getView().getTitle();
        if (false == title.contains("Editing:")) return;
        
        event.setCancelled(true);
        Player p = (Player) event.getWhoClicked();
        int clickedSlot = event.getRawSlot();

        String[] pts = title.split(":|\\|");
        if (4 > pts.length) return;

        String shopId = pts[1].trim();
        int targetSlot = Integer.parseInt(pts[3].trim());

        if (clickedSlot == 53) {
            p.closeInventory();
            p.performCommand("shopgui reload");
            return;
        }

        if (clickedSlot == 26 || clickedSlot == 35) {
            ItemStack item = event.getCurrentItem();
            if (null != item) {
                Material current = item.getType();
                Material next = (current == Material.WHITE_STAINED_GLASS_PANE) ? Material.BLACK_STAINED_GLASS_PANE : Material.WHITE_STAINED_GLASS_PANE;
                event.getInventory().setItem(clickedSlot, GuiManager.createItem(next, (next == Material.WHITE_STAINED_GLASS_PANE) ? "&a&lMODE: ADD (+)" : "&c&lMODE: SUBTRACT (-)"));
            }
            return;
        }

        File shopFile = new File(new File(Bukkit.getPluginManager().getPlugin("ShopGUIPlus").getDataFolder(), "shops"), shopId + ".yml");
        if (false == shopFile.exists()) return;

        FileConfiguration shopConfig = YamlConfiguration.loadConfiguration(shopFile);
        String root = shopConfig.contains(shopId + ".items") ? shopId + ".items" : "items";

        String itemKey = null;
        if (null != shopConfig.getConfigurationSection(root)) {
            for (String k : shopConfig.getConfigurationSection(root).getKeys(false)) {
                if (shopConfig.getInt(root + "." + k + ".slot") == targetSlot) {
                    itemKey = k;
                    break;
                }
            }
        }

        if (null == itemKey) return;
        String fullPath = root + "." + itemKey;
        boolean changed = false;

        // LOGICA DE COMPRA (18-25)
        if (clickedSlot >= 18 && 26 > clickedSlot) {
            double amt = ShopGuiAddon.getInstance().getConfig().getDouble("gui.buy_adjustments." + (clickedSlot - 18) + ".amount");
            double current = shopConfig.getDouble(fullPath + ".buyPrice");
            
            ItemStack toggle = event.getInventory().getItem(26);
            boolean isSubtraction = (null != toggle && toggle.getType() == Material.BLACK_STAINED_GLASS_PANE);
            
            // CORREÇÃO DOS CENTAVOS USANDO BIGDECIMAL PARA PRECISÃO TOTAL
            BigDecimal currentBD = BigDecimal.valueOf(current);
            BigDecimal amtBD = BigDecimal.valueOf(amt);
            BigDecimal result;
            
            if (isSubtraction) {
                result = currentBD.subtract(amtBD);
            } else {
                result = currentBD.add(amtBD);
            }
            
            double newVal = result.setScale(2, RoundingMode.HALF_UP).doubleValue();
            if (0.0 > newVal) newVal = 0.0;
            
            shopConfig.set(fullPath + ".buyPrice", newVal);
            changed = true;
        }
        // LOGICA DE VENDA (27-34)
        else if (clickedSlot >= 27 && 35 > clickedSlot) {
            double amt = ShopGuiAddon.getInstance().getConfig().getDouble("gui.sell_adjustments." + (clickedSlot - 27) + ".amount");
            double current = shopConfig.getDouble(fullPath + ".sellPrice");
            
            ItemStack toggle = event.getInventory().getItem(35);
            boolean isSubtraction = (null != toggle && toggle.getType() == Material.BLACK_STAINED_GLASS_PANE);
            
            BigDecimal currentBD = BigDecimal.valueOf(current);
            BigDecimal amtBD = BigDecimal.valueOf(amt);
            BigDecimal result;
            
            if (isSubtraction) {
                result = currentBD.subtract(amtBD);
            } else {
                result = currentBD.add(amtBD);
            }
            
            double newVal = result.setScale(2, RoundingMode.HALF_UP).doubleValue();
            if (0.0 > newVal) newVal = 0.0;
            
            shopConfig.set(fullPath + ".sellPrice", newVal);
            changed = true;
        }

        if (changed) {
            try {
                shopConfig.save(shopFile);
                GuiManager.updateVisor(event.getInventory(), shopConfig, root, itemKey, targetSlot);
            } catch (IOException e) { e.printStackTrace(); }
        }
    }
}
