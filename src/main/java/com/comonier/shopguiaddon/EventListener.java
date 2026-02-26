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

        // 1. EXTRAÇÃO DE DADOS DO TÍTULO
        String[] pts = title.split(":|\\|");
        if (4 > pts.length) return;
        String shopId = pts[1].trim();
        int targetSlot = Integer.parseInt(pts[3].trim());

        // 2. CONTROLES GERAIS (RELOAD SGP)
        if (clickedSlot == 53) {
            p.closeInventory();
            p.performCommand("shopgui reload");
            return;
        }

        // 3. INTERRUPTORES DE MODO (SOMA/SUBTRAÇÃO)
        if (clickedSlot == 26 || clickedSlot == 35 || clickedSlot == 44) {
            ItemStack item = event.getCurrentItem();
            if (null != item) {
                Material current = item.getType();
                Material next = (current == Material.WHITE_STAINED_GLASS_PANE) ? Material.BLACK_STAINED_GLASS_PANE : Material.WHITE_STAINED_GLASS_PANE;
                String modeName = (next == Material.WHITE_STAINED_GLASS_PANE) ? "&a&lMODE: ADD (+)" : "&c&lMODE: SUBTRACT (-)";
                event.getInventory().setItem(clickedSlot, GuiManager.createItem(next, modeName));
            }
            return;
        }

        // 4. CARREGAMENTO DO ARQUIVO DA LOJA
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

        // 5. LÓGICA INDEPENDENTE POR FILEIRA (EVITA BUG DE PREÇO)

        // FILEIRA DE COMPRA (Slots 18 a 25)
        if (clickedSlot >= 18 && 26 > clickedSlot) {
            double amt = ShopGuiAddon.getInstance().getConfig().getDouble("gui.buy_adjustments." + (clickedSlot - 18) + ".amount");
            double current = shopConfig.getDouble(fullPath + ".buyPrice");
            
            ItemStack toggle = event.getInventory().getItem(26);
            boolean isSub = (null != toggle && toggle.getType() == Material.BLACK_STAINED_GLASS_PANE);
            
            BigDecimal currentBD = BigDecimal.valueOf(current);
            BigDecimal amtBD = BigDecimal.valueOf(amt);
            BigDecimal result = isSub ? currentBD.subtract(amtBD) : currentBD.add(amtBD);
            
            double newVal = result.setScale(2, RoundingMode.HALF_UP).doubleValue();
            if (0.0 > newVal) newVal = 0.0;
            
            shopConfig.set(fullPath + ".buyPrice", newVal);
            changed = true;
        }

        // FILEIRA DE VENDA (Slots 27 a 34)
        else if (clickedSlot >= 27 && 35 > clickedSlot) {
            double amt = ShopGuiAddon.getInstance().getConfig().getDouble("gui.sell_adjustments." + (clickedSlot - 27) + ".amount");
            double current = shopConfig.getDouble(fullPath + ".sellPrice");
            
            ItemStack toggle = event.getInventory().getItem(35);
            boolean isSub = (null != toggle && toggle.getType() == Material.BLACK_STAINED_GLASS_PANE);
            
            BigDecimal currentBD = BigDecimal.valueOf(current);
            BigDecimal amtBD = BigDecimal.valueOf(amt);
            BigDecimal result = isSub ? currentBD.subtract(amtBD) : currentBD.add(amtBD);
            
            double newVal = result.setScale(2, RoundingMode.HALF_UP).doubleValue();
            if (0.0 > newVal) newVal = 0.0;
            
            shopConfig.set(fullPath + ".sellPrice", newVal);
            changed = true;
        }

        // FILEIRA DE QUANTIDADE (Slots 36 a 43)
        else if (clickedSlot >= 36 && 44 > clickedSlot) {
            int[] amounts = {1, 2, 4, 8, 16, 32, 64, 100};
            int amtAdjust = amounts[clickedSlot - 36];
            int current = shopConfig.getInt(fullPath + ".item.quantity", 1);
            
            ItemStack toggle = event.getInventory().getItem(44);
            boolean isSub = (null != toggle && toggle.getType() == Material.BLACK_STAINED_GLASS_PANE);
            
            int newVal = isSub ? (current - amtAdjust) : (current + amtAdjust);
            if (1 > newVal) newVal = 1;
            
            shopConfig.set(fullPath + ".item.quantity", newVal);
            changed = true;
        }

        // 6. SALVAMENTO E ATUALIZAÇÃO DO VISOR
        if (changed) {
            try {
                shopConfig.save(shopFile);
                GuiManager.updateVisor(event.getInventory(), shopConfig, root, itemKey, targetSlot);
            } catch (IOException e) { e.printStackTrace(); }
        }
    }
}
