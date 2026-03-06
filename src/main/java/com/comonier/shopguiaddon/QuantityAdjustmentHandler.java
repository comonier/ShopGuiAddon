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
public class QuantityAdjustmentHandler {
    private static final HashMap<UUID, Long> lastQuantityTick = new HashMap<>();
    public static void handle(InventoryClickEvent event, Player p, String shopId, int targetSlot, int page, int clicked) {
        long currentTick = p.getWorld().getFullTime();
        if (lastQuantityTick.getOrDefault(p.getUniqueId(), -1L) == currentTick) return;
        lastQuantityTick.put(p.getUniqueId(), currentTick);
        File f = ShopOperations.getShopFile(shopId);
        if (f.exists()) {
            FileConfiguration cfg = YamlConfiguration.loadConfiguration(f);
            String root = cfg.contains(shopId + ".items") ? shopId + ".items" : "items";
            String key = findKey(cfg, root, targetSlot, page);
            if (key != null) {
                String path = root + "." + key;
                if (clicked >= 36 && clicked <= 43) {
                    int[] amounts = {1, 2, 4, 8, 16, 32, 64, 100};
                    int adjustment = amounts[clicked - 36];
                    boolean isSub = isSubtractMode(event, 44);
                    int currentQuant = cfg.getInt(path + ".item.quantity", 1);
                    int newQuant = PriceCalculator.calculateQuantity(currentQuant, adjustment, isSub);
                    cfg.set(path + ".item.quantity", newQuant);
                    try { 
                        cfg.save(f);
                        GuiManager.updateVisor(event.getInventory(), cfg, root, key, targetSlot);
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
