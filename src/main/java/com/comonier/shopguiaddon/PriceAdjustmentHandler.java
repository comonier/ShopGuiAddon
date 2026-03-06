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
    private static final HashMap<UUID, Long> lastBuyTick = new HashMap<>();
    private static final HashMap<UUID, Long> lastSellTick = new HashMap<>();
    public static void handle(InventoryClickEvent event, Player p, String shopId, int targetSlot, int page, int clicked) {
        File f = ShopOperations.getShopFile(shopId);
        if (f.exists()) {
            FileConfiguration shopCfg = YamlConfiguration.loadConfiguration(f);
            FileConfiguration pluginCfg = ShopGuiAddon.getInstance().getConfig();
            String root = shopCfg.contains(shopId + ".items") ? shopId + ".items" : "items";
            String key = findKey(shopCfg, root, targetSlot, page);
            if (key != null) {
                String path = root + "." + key;
                long currentTick = p.getWorld().getFullTime();
                boolean changed = false;
                if (clicked >= 18 && clicked <= 25) {
                    if (lastBuyTick.getOrDefault(p.getUniqueId(), -1L) == currentTick) return;
                    lastBuyTick.put(p.getUniqueId(), currentTick);
                    double adj = pluginCfg.getDouble("gui.buy_adjustments." + (clicked - 18) + ".amount", 0.0);
                    double currentPrice = shopCfg.getDouble(path + ".buyPrice", 0.0);
                    shopCfg.set(path + ".buyPrice", PriceCalculator.calculate(currentPrice, adj, isSubtractMode(event, 26)));
                    changed = true;
                } else if (clicked >= 27 && clicked <= 34) {
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
                        GuiManager.updateVisor(event.getInventory(), shopCfg, root, key, targetSlot);
                        p.updateInventory();
                    } catch (IOException e) { e.printStackTrace(); }
                }
            }
        }
    }
    private static String findKey(FileConfiguration cfg, String root, int slot, int page) {
        if (cfg.getConfigurationSection(root) == null) return null;
        for (String k : cfg.getConfigurationSection(root).getKeys(false)) {
            int itemSlot = cfg.getInt(root + "." + k + ".slot");
            int itemPage = cfg.getInt(root + "." + k + ".page", 1);
            if (itemSlot == slot && itemPage == page) return k;
        }
        return null;
    }
    private static boolean isSubtractMode(InventoryClickEvent e, int slot) {
        ItemStack item = e.getInventory().getItem(slot);
        if (item == null) return false;
        Material m = item.getType();
        return (m == Material.BLACK_STAINED_GLASS_PANE || m == Material.RED_STAINED_GLASS_PANE);
    }
}
