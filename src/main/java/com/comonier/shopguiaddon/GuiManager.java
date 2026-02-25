package com.comonier.shopguiaddon;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class GuiManager {

    public static void openEditor(Player player, String shopName, int slotId) {
        FileConfiguration config = ShopGuiAddon.getInstance().getConfig();
        String title = ChatUtils.color(config.getString("gui.title", "Editing: %shop% | Slot: %slot%")
                .replace("%shop%", shopName).replace("%slot%", String.valueOf(slotId)));
        
        Inventory gui = Bukkit.createInventory(null, 54, title);
        ItemStack filler = createItem(Material.GRAY_STAINED_GLASS_PANE, " ");
        for (int i = 0; 54 > i; i++) gui.setItem(i, filler);

        File shopFile = new File(new File(Bukkit.getPluginManager().getPlugin("ShopGUIPlus").getDataFolder(), "shops"), shopName + ".yml");
        FileConfiguration shopConfig = YamlConfiguration.loadConfiguration(shopFile);
        
        String root = shopConfig.contains(shopName + ".items") ? shopName + ".items" : "items";
        ConfigurationSection items = shopConfig.getConfigurationSection(root);
        
        double buy = 0; double sell = 0; ItemStack icon = null;
        if (null != items) {
            for (String key : items.getKeys(false)) {
                if (shopConfig.getInt(root + "." + key + ".slot") == slotId) {
                    buy = shopConfig.getDouble(root + "." + key + ".buyPrice");
                    sell = shopConfig.getDouble(root + "." + key + ".sellPrice");
                    String mat = shopConfig.getString(root + "." + key + ".item.material");
                    if (null != mat) icon = new ItemStack(Material.matchMaterial(mat));
                    break;
                }
            }
        }

        if (null != icon) {
            ItemMeta meta = icon.getItemMeta();
            List<String> lore = new ArrayList<>();
            lore.add(ChatUtils.color("&fCompra: &a$" + String.format("%.2f", buy)));
            lore.add(ChatUtils.color("&fVenda: &c$" + String.format("%.2f", sell)));
            meta.setLore(lore);
            icon.setItemMeta(meta);
            gui.setItem(4, icon);
        }

        // Botoes de ajuste (Buy: 18-25 | Sell: 27-34)
        for (int i = 0; 8 > i; i++) {
            gui.setItem(18 + i, createItem(Material.LIME_STAINED_GLASS_PANE, "&a+" + config.getDouble("gui.buy_adjustments." + i + ".amount")));
            gui.setItem(27 + i, createItem(Material.RED_STAINED_GLASS_PANE, "&c+" + config.getDouble("gui.sell_adjustments." + i + ".amount")));
        }

        gui.setItem(53, createItem(Material.BLAZE_POWDER, "&6RELOAD SHOPGUI+"));
        player.openInventory(gui);
    }

    private static ItemStack createItem(Material mat, String name) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        if (null != meta) { meta.setDisplayName(ChatUtils.color(name)); item.setItemMeta(meta); }
        return item;
    }
}
