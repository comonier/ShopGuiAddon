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

        // 1. FUNDO CINZA (LIMPEZA DO INV)
        ItemStack filler = createItem(Material.GRAY_STAINED_GLASS_PANE, " ");
        for (int i = 0; 54 > i; i++) {
            gui.setItem(i, filler);
        }

        // 2. A "CRUZ" DE DESTAQUE AMARELA (EM VOLTA DO SLOT 4)
        ItemStack highlight = createItem(Material.YELLOW_STAINED_GLASS_PANE, " ");
        gui.setItem(3, highlight);
        gui.setItem(5, highlight);
        gui.setItem(13, highlight);

        // 3. LEITURA DO ITEM REAL (DIRETO DO ARQUIVO DA LOJA)
        File shopFile = new File(new File(Bukkit.getPluginManager().getPlugin("ShopGUIPlus").getDataFolder(), "shops"), shopName + ".yml");
        FileConfiguration shopConfig = YamlConfiguration.loadConfiguration(shopFile);
        
        String root = shopConfig.contains(shopName + ".items") ? shopName + ".items" : "items";
        ConfigurationSection items = shopConfig.getConfigurationSection(root);
        
        double buy = 0; double sell = 0; ItemStack visor = null;
        
        if (null != items) {
            for (String key : items.getKeys(false)) {
                String path = root + "." + key;
                if (shopConfig.getInt(path + ".slot") == slotId) {
                    buy = shopConfig.getDouble(path + ".buyPrice");
                    sell = shopConfig.getDouble(path + ".sellPrice");
                    String mat = shopConfig.getString(path + ".item.material");
                    if (null != mat) {
                        Material m = Material.matchMaterial(mat);
                        if (null != m) visor = new ItemStack(m);
                    }
                    break;
                }
            }
        }

        // 4. COLOCAR ITEM NO VISOR (SLOT 4)
        if (null != visor) {
            ItemMeta meta = visor.getItemMeta();
            List<String> lore = new ArrayList<>();
            lore.add(ChatUtils.color("&fCompra: &a$" + String.format("%.2f", buy)));
            lore.add(ChatUtils.color("&fVenda: &c$" + String.format("%.2f", sell)));
            lore.add(" ");
            lore.add(ChatUtils.color("&7Editando slot: " + slotId));
            meta.setDisplayName(ChatUtils.color("&b&lItem Atual"));
            meta.setLore(lore);
            visor.setItemMeta(meta);
            gui.setItem(4, visor);
        } else {
            gui.setItem(4, createItem(Material.BARRIER, "&cItem não encontrado!"));
        }

        // 5. BOTOES DE AJUSTE (BUY: 18-25 | SELL: 27-34)
        for (int i = 0; 8 > i; i++) {
            // Botoes Verdes (Compra)
            String bPath = "gui.buy_adjustments." + i;
            if (config.contains(bPath)) {
                gui.setItem(18 + i, createItemFromConfig(config, bPath));
            }
            // Botoes Vermelhos (Venda)
            String sPath = "gui.sell_adjustments." + i;
            if (config.contains(sPath)) {
                gui.setItem(27 + i, createItemFromConfig(config, sPath));
            }
        }

        // 6. CONTROLES DE NAVEGACAO E RELOAD
        if (config.contains("gui.controls.prev_slot")) gui.setItem(45, createItemFromConfig(config, "gui.controls.prev_slot"));
        if (config.contains("gui.controls.next_slot")) gui.setItem(46, createItemFromConfig(config, "gui.controls.next_slot"));
        if (config.contains("gui.controls.reload_sga")) gui.setItem(52, createItemFromConfig(config, "gui.controls.reload_sga"));
        if (config.contains("gui.controls.reload_sgp")) gui.setItem(53, createItemFromConfig(config, "gui.controls.reload_sgp"));

        player.openInventory(gui);
    }

    private static ItemStack createItemFromConfig(FileConfiguration config, String path) {
        String matName = config.getString(path + ".material", "STONE");
        Material mat = Material.matchMaterial(matName);
        if (null == mat) mat = Material.BARRIER;
        return createItem(mat, config.getString(path + ".name", "&cErro"));
    }

    private static ItemStack createItem(Material mat, String name) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        if (null != meta) {
            meta.setDisplayName(ChatUtils.color(name));
            item.setItemMeta(meta);
        }
        return item;
    }
}
