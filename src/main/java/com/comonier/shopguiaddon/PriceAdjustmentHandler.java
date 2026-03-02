package com.comonier.shopguiaddon;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

public class PriceAdjustmentHandler {

    // Travas individuais por UUID para evitar que cliques rápidos (Double Click) processem duas vezes
    private static final HashMap<UUID, Long> lastBuyTick = new HashMap<>();
    private static final HashMap<UUID, Long> lastSellTick = new HashMap<>();

    public static void handle(InventoryClickEvent event, Player p, String shopId, int targetSlot, int clicked) {
        File f = ShopOperations.getShopFile(shopId);
        if (!f.exists()) {
            p.sendMessage(ChatUtils.getMessage("error_shop_file", "%shop%", shopId));
            return;
        }
        
        FileConfiguration shopCfg = YamlConfiguration.loadConfiguration(f);
        FileConfiguration pluginCfg = ShopGuiAddon.getInstance().getConfig();
        
        // Identifica a seção de itens (ShopGUI+ usa 'items' ou 'ID_DA_LOJA.items')
        String root = shopCfg.contains(shopId + ".items") ? shopId + ".items" : "items";
        String key = findKey(shopCfg, root, targetSlot);
        
        // Se não houver item vinculado a este slot no arquivo da loja, o editor ignora o comando
        if (key == null) return;

        String path = root + "." + key;
        long currentTick = p.getWorld().getFullTime();
        boolean changed = false;

        // AJUSTE DE PREÇO DE COMPRA (Slots 18 a 25)
        if (clicked >= 18 && clicked <= 25) {
            if (lastBuyTick.getOrDefault(p.getUniqueId(), -1L) == currentTick) return;
            lastBuyTick.put(p.getUniqueId(), currentTick);

            double adj = pluginCfg.getDouble("gui.buy_adjustments." + (clicked - 18) + ".amount", 0.0);
            double currentPrice = shopCfg.getDouble(path + ".buyPrice", 0.0);
            
            // Calcula o novo valor usando BigDecimal (PriceCalculator)
            shopCfg.set(path + ".buyPrice", PriceCalculator.calculate(currentPrice, adj, isSubtractMode(event, 26)));
            changed = true;
        } 
        
        // AJUSTE DE PREÇO DE VENDA (Slots 27 a 34)
        else if (clicked >= 27 && clicked <= 34) {
            if (lastSellTick.getOrDefault(p.getUniqueId(), -1L) == currentTick) return;
            lastSellTick.put(p.getUniqueId(), currentTick);

            double adj = pluginCfg.getDouble("gui.sell_adjustments." + (clicked - 27) + ".amount", 0.0);
            double currentPrice = shopCfg.getDouble(path + ".sellPrice", 0.0);

            shopCfg.set(path + ".sellPrice", PriceCalculator.calculate(currentPrice, adj, isSubtractMode(event, 35)));
            changed = true;
        }

        if (changed) {
            try { 
                shopCfg.save(f); 
                // Atualiza o item visor central para exibir os novos valores salvos
                GuiManager.updateVisor(event.getInventory(), shopCfg, root, key, targetSlot); 
                p.updateInventory();
            } catch (IOException e) { 
                p.sendMessage(ChatUtils.color("&c&lSGA &8» &7Erro ao salvar o arquivo: " + shopId + ".yml"));
                e.printStackTrace(); 
            }
        }
    }

    /**
     * Localiza a chave única do item baseado no slot configurado no YAML.
     */
    private static String findKey(FileConfiguration cfg, String root, int slot) {
        if (cfg.getConfigurationSection(root) == null) return null;
        for (String k : cfg.getConfigurationSection(root).getKeys(false)) {
            if (cfg.getInt(root + "." + k + ".slot") == slot) return k;
        }
        return null;
    }

    /**
     * Verifica o estado visual do interruptor lateral (Preto/Vermelho = Subtrair).
     */
    private static boolean isSubtractMode(InventoryClickEvent e, int slot) {
        ItemStack item = e.getInventory().getItem(slot);
        if (item == null) return false;
        
        Material m = item.getType();
        return (m == Material.BLACK_STAINED_GLASS_PANE || m == Material.RED_STAINED_GLASS_PANE);
    }
}
