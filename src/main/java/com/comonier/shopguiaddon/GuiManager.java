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
import java.util.Arrays;
import java.util.List;

public class GuiManager {

    public static void openEditor(Player player, String shopName, int slotId) {
        FileConfiguration config = ShopGuiAddon.getInstance().getConfig();
        String title = ChatUtils.color(config.getString("gui.title", "Editing: %shop% | Slot: %slot%")
                .replace("%shop%", shopName).replace("%slot%", String.valueOf(slotId)));
        
        Inventory gui = Bukkit.createInventory(null, 54, title);

        ItemStack filler = createItem(Material.GRAY_STAINED_GLASS_PANE, " ");
        for (int i = 0; 54 > i; i++) { gui.setItem(i, filler); }

        ItemStack highlight = createItem(Material.YELLOW_STAINED_GLASS_PANE, " ");
        gui.setItem(3, highlight);
        gui.setItem(5, highlight);
        gui.setItem(13, highlight);

        // INTERRUPTORES COM LORE EXPLICATIVA
        gui.setItem(26, createItemWithLore(Material.WHITE_STAINED_GLASS_PANE, "&a&lMODE: ADD (+)", 
                Arrays.asList("&7Click to switch to", "&c&lSUBTRACT (-) &7mode.")));
        gui.setItem(35, createItemWithLore(Material.WHITE_STAINED_GLASS_PANE, "&a&lMODE: ADD (+)", 
                Arrays.asList("&7Click to switch to", "&c&lSUBTRACT (-) &7mode.")));

        File shopFile = new File(new File(Bukkit.getPluginManager().getPlugin("ShopGUIPlus").getDataFolder(), "shops"), shopName + ".yml");
        FileConfiguration shopConfig = YamlConfiguration.loadConfiguration(shopFile);
        String root = shopConfig.contains(shopName + ".items") ? shopName + ".items" : "items";
        
        String itemKey = null;
        ConfigurationSection sec = shopConfig.getConfigurationSection(root);
        if (null != sec) {
            for (String k : sec.getKeys(false)) {
                if (shopConfig.getInt(root + "." + k + ".slot") == slotId) { itemKey = k; break; }
            }
        }

        updateVisor(gui, shopConfig, root, itemKey, slotId);

        for (int i = 0; 8 > i; i++) {
            gui.setItem(18 + i, createItemFromConfig(config, "gui.buy_adjustments." + i));
            gui.setItem(27 + i, createItemFromConfig(config, "gui.sell_adjustments." + i));
        }

        if (config.contains("gui.controls.prev_slot")) gui.setItem(45, createItemFromConfig(config, "gui.controls.prev_slot"));
        if (config.contains("gui.controls.next_slot")) gui.setItem(46, createItemFromConfig(config, "gui.controls.next_slot"));
        if (config.contains("gui.controls.reload_sga")) gui.setItem(52, createItemFromConfig(config, "gui.controls.reload_sga"));
        if (config.contains("gui.controls.reload_sgp")) gui.setItem(53, createItemFromConfig(config, "gui.controls.reload_sgp"));

        player.openInventory(gui);
    }

    public static void updateVisor(Inventory gui, FileConfiguration shopConfig, String root, String key, int slotId) {
        ItemStack visor;
        if (null != key) {
            double buy = shopConfig.getDouble(root + "." + key + ".buyPrice");
            double sell = shopConfig.getDouble(root + "." + key + ".sellPrice");
            String matName = shopConfig.getString(root + "." + key + ".item.material");
            Material m = Material.matchMaterial(null != matName ? matName : "BARRIER");
            visor = new ItemStack(null != m ? m : Material.BARRIER);
            ItemMeta meta = visor.getItemMeta();
            List lore = new ArrayList();
            lore.add(ChatUtils.color("&fBuy Price: &a$" + String.format("%.2f", buy)));
            lore.add(ChatUtils.color("&fSell Price: &c$" + String.format("%.2f", sell)));
            lore.add(" ");
            lore.add(ChatUtils.color("&7Editing slot: " + slotId));
            meta.setDisplayName(ChatUtils.color("&b&lCurrent Item"));
            meta.setLore(lore);
            visor.setItemMeta(meta);
        } else {
            visor = createItem(Material.BARRIER, "&cItem not found!");
        }
        gui.setItem(4, visor);
    }

    private static ItemStack createItemFromConfig(FileConfiguration config, String path) {
        String matName = config.getString(path + ".material", "STONE");
        Material mat = Material.matchMaterial(null != matName ? matName : "BARRIER");
        return createItem(mat, config.getString(path + ".name", "&cError"));
    }

    public static ItemStack createItem(Material mat, String name) {
        ItemStack item = new ItemStack(null != mat ? mat : Material.BARRIER);
        ItemMeta meta = item.getItemMeta();
        if (null != meta) {
            meta.setDisplayName(ChatUtils.color(name));
            item.setItemMeta(meta);
        }
        return item;
    }

    public static ItemStack createItemWithLore(Material mat, String name, List lore) {
        ItemStack item = new ItemStack(null != mat ? mat : Material.BARRIER);
        ItemMeta meta = item.getItemMeta();
        if (null != meta) {
            meta.setDisplayName(ChatUtils.color(name));
            List coloredLore = new ArrayList();
            // CORREÇÃO AQUI: Cast explícito para String para evitar erro de Object
            for (Object obj : lore) { 
                coloredLore.add(ChatUtils.color((String) obj)); 
            }
            meta.setLore(coloredLore);
            item.setItemMeta(meta);
        }
        return item;
    }
}
