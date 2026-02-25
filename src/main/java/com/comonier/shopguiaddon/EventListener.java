package com.comonier.shopguiaddon;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import java.io.File;
import java.io.IOException;

public class EventListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        String title = event.getView().getTitle();
        if (false == title.contains("Editing:")) return;
        
        event.setCancelled(true);
        Player p = (Player) event.getWhoClicked();
        int clickedSlot = event.getRawSlot();

        // Extrair Shop e Slot do Titulo
        String[] pts = title.split(":|\\|");
        if (4 > pts.length) return;

        String shopId = pts[1].trim();
        int targetSlot = Integer.parseInt(pts[3].trim());

        // Botao de Reload do ShopGUI+ (Slot 53)
        if (clickedSlot == 53) {
            p.closeInventory();
            p.performCommand("shopgui reload");
            return;
        }

        File shopFile = new File(new File(Bukkit.getPluginManager().getPlugin("ShopGUIPlus").getDataFolder(), "shops"), shopId + ".yml");
        if (false == shopFile.exists()) return;

        FileConfiguration shopConfig = YamlConfiguration.loadConfiguration(shopFile);
        
        // LOGICA DE RAIZ PARA SALVAMENTO (Identifica armor.items ou items)
        String basePath = "items";
        if (false == shopConfig.contains("items") && shopConfig.contains(shopId + ".items")) {
            basePath = shopId + ".items";
        }

        String itemKey = null;
        if (null != shopConfig.getConfigurationSection(basePath)) {
            for (String key : shopConfig.getConfigurationSection(basePath).getKeys(false)) {
                if (shopConfig.getInt(basePath + "." + key + ".slot") == targetSlot) {
                    itemKey = key;
                    break;
                }
            }
        }

        // Se nao achou o item no arquivo, encerra para nao criar lixo
        if (null == itemKey) return;

        String fullPath = basePath + "." + itemKey;
        boolean changed = false;

        // Ajustes de Compra (Slots 18 a 25)
        if (clickedSlot >= 18 && 26 > clickedSlot) {
            double amt = ShopGuiAddon.getInstance().getConfig().getDouble("gui.buy_adjustments." + (clickedSlot - 18) + ".amount");
            double current = shopConfig.getDouble(fullPath + ".buyPrice");
            shopConfig.set(fullPath + ".buyPrice", (current + amt) > 0 ? (current + amt) : 0);
            changed = true;
        } 
        // Ajustes de Venda (Slots 27 a 34)
        else if (clickedSlot >= 27 && 35 > clickedSlot) {
            double amt = ShopGuiAddon.getInstance().getConfig().getDouble("gui.sell_adjustments." + (clickedSlot - 27) + ".amount");
            double current = shopConfig.getDouble(fullPath + ".sellPrice");
            shopConfig.set(fullPath + ".sellPrice", (current + amt) > 0 ? (current + amt) : 0);
            changed = true;
        }

        if (changed) {
            try {
                shopConfig.save(shopFile);
                // Reabre a GUI para atualizar o visor com o novo preco lido do arquivo
                GuiManager.openEditor(p, shopId, targetSlot);
            } catch (IOException e) { 
                e.printStackTrace(); 
            }
        }
    }
}
