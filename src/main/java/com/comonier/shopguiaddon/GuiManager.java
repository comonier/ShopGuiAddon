package com.comonier.shopguiaddon;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.configuration.file.FileConfiguration;
import java.util.List;
import java.util.ArrayList;

public class GuiManager {

    public static void openEditor(Player player, String shopName, int slotId) {
        FileConfiguration config = ShopGuiAddon.getInstance().getConfig();
        String title = config.getString("gui.title")
                .replace("%shop%", shopName)
                .replace("%slot%", String.valueOf(slotId));
        
        Inventory gui = Bukkit.createInventory(null, 54, title);

        // Lógica Inversa: Preencher o fundo com vidro cinza (opcional para estética)
        ItemStack filler = createGuiItem(Material.BLACK_STAINED_GLASS_PANE, " ");
        for (int i = 0; 54 > i; i++) {
            gui.setItem(i, filler);
        }

        // Adicionando Botões de Compra (Slots 1-8 -> 0-7 no código)
        // Usamos a config para pegar os itens definidos anteriormente
        for (int i = 0; 8 > i; i++) {
            String path = "gui.buy_adjustments." + i;
            if (config.contains(path)) {
                Material mat = Material.valueOf(config.getString(path + ".material"));
                String name = config.getString(path + ".name");
                gui.setItem(i, createGuiItem(mat, name));
            }
        }

        // Adicionando Botões de Venda (Slots 36-43 -> 35-42 no código)
        for (int i = 0; 8 > i; i++) {
            int slotBase = 35 + i;
            String path = "gui.sell_adjustments." + i;
            if (config.contains(path)) {
                Material mat = Material.valueOf(config.getString(path + ".material"));
                String name = config.getString(path + ".name");
                gui.setItem(slotBase, createGuiItem(mat, name));
            }
        }

        // Botões de Navegação e Controle (Slots 45 ao 53)
        gui.setItem(45, createGuiItem(Material.ARROW, "§ePrevious Slot"));
        gui.setItem(46, createGuiItem(Material.ARROW, "§eNext Slot"));
        gui.setItem(52, createGuiItem(Material.PAPER, "§bReload ShopGuiAddon"));
        gui.setItem(53, createGuiItem(Material.NETHER_STAR, "§dSave & Reload ShopGUI+"));

        // O item que está sendo editado aparece no centro (ex: slot 22)
        // Isso será implementado quando integrarmos com a API do ShopGUI+
        
        player.openInventory(gui);
    }

    private static ItemStack createGuiItem(Material material, String name) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name.replace("&", "§"));
            item.setItemMeta(meta);
        }
        return item;
    }
}
