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
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

public class GuiManager {

    private static final String SKIN_PREV = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2EyYzEyY2IyMjkxODM4NGUwYTgxYzgyYTFlZDk5YWViZGNlOTRiMmVjMjc1NDgwMDk3MjMxOWI1NzkwMGFmYiJ9fX0=";
    private static final String SKIN_NEXT = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjkxYWM0MzJhYTQwZDdlN2E2ODdhYTg1MDQxZGU2MzY3MTJkNGYwMjI2MzJkZDUzNTZjODgwNTIxYWYyNzIzYSJ9fX0=";

    public static void openEditor(Player player, String shopName, int slotId) {
        FileConfiguration config = ShopGuiAddon.getInstance().getConfig();
        String titleFormat = config.getString("gui.title", "&8Editing: &b%shop% &8| Slot: &b%slot%");
        // IMPORTANTE: O título deve bater exatamente com a verificação do EventListener
        String title = ChatUtils.color(titleFormat.replace("%shop%", shopName).replace("%slot%", String.valueOf(slotId)));
        
        Inventory gui = Bukkit.createInventory(null, 54, title);

        // Preenchimento total para evitar slots vazios (AIR)
        ItemStack fillerDark = createItem(Material.BLACK_STAINED_GLASS_PANE, " ");
        ItemStack fillerGray = createItem(Material.GRAY_STAINED_GLASS_PANE, " ");
        
        for (int i = 0; i < 54; i++) {
            if (i < 9 || i > 44 || i % 9 == 0 || i % 9 == 8) gui.setItem(i, fillerDark);
            else gui.setItem(i, fillerGray);
        }

        // Navegação e Visor
        gui.setItem(3, createSkull(parseSkin(SKIN_PREV), "&e« Slot Anterior"));
        gui.setItem(5, createSkull(parseSkin(SKIN_NEXT), "&ePróximo Slot »"));
        gui.setItem(13, createItem(Material.CYAN_STAINED_GLASS_PANE, "&f↑ Item em Edição ↑"));

        // Interruptores de Modo
        String modeMsg = "&a&lMODO: ADICIONAR (+)";
        List<String> modeLore = Collections.singletonList("&7Clique para alternar para &cSUBTRAIR (-)");
        gui.setItem(26, createItemWithLore(Material.WHITE_STAINED_GLASS_PANE, modeMsg, modeLore));
        gui.setItem(35, createItemWithLore(Material.WHITE_STAINED_GLASS_PANE, modeMsg, modeLore));
        gui.setItem(44, createItemWithLore(Material.WHITE_STAINED_GLASS_PANE, modeMsg, modeLore));

        // Carrega Item do ShopGUI+
        File f = ShopOperations.getShopFile(shopName);
        if (f.exists()) {
            FileConfiguration shopCfg = YamlConfiguration.loadConfiguration(f);
            String root = shopCfg.contains(shopName + ".items") ? shopName + ".items" : "items";
            String itemKey = findItemKey(shopCfg, root, slotId);
            updateVisor(gui, shopCfg, root, itemKey, slotId);
        }

        // Botões de Ajuste
        for (int i = 0; i < 8; i++) {
            gui.setItem(18 + i, createFromConfig(config, "gui.buy_adjustments." + i));
            gui.setItem(27 + i, createFromConfig(config, "gui.sell_adjustments." + i));
            
            int[] amts = {1, 2, 4, 8, 16, 32, 64, 100};
            gui.setItem(36 + i, createItemWithLore(Material.CHEST, "&eAjuste: &f±" + amts[i], 
                Collections.singletonList("&7Altera a quantidade do item.")));
        }

        gui.setItem(49, createItem(Material.BARRIER, "&cFechar Editor"));
        gui.setItem(52, createFromConfig(config, "gui.controls.reload_sga"));
        gui.setItem(53, createFromConfig(config, "gui.controls.reload_sgp"));

        player.openInventory(gui);
    }

    public static void updateVisor(Inventory gui, FileConfiguration cfg, String root, String key, int slotId) {
        ItemStack visor;
        if (key != null) {
            String path = root + "." + key;
            String matName = cfg.getString(path + ".item.material", "BARRIER");
            Material m = Material.matchMaterial(matName);
            String skin = cfg.getString(path + ".item.skin");
            
            visor = (m == Material.PLAYER_HEAD && skin != null) ? 
                    createSkull(parseSkin(skin), "&b&lItem: &f" + matName.toLowerCase()) : 
                    new ItemStack(m != null ? m : Material.BARRIER);
            
            ItemMeta meta = visor.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(ChatUtils.color("&b&lEditando: &f" + matName.toLowerCase()));
                meta.setLore(Arrays.asList(
                    " ",
                    ChatUtils.color("&fPreço Compra: &a$" + String.format("%.2f", cfg.getDouble(path + ".buyPrice"))),
                    ChatUtils.color("&fPreço Venda: &c$" + String.format("%.2f", cfg.getDouble(path + ".sellPrice"))),
                    ChatUtils.color("&fQtd p/ Venda: &e" + cfg.getInt(path + ".item.quantity", 1)),
                    " ",
                    ChatUtils.color("&7Slot original: &7" + slotId)
                ));
                visor.setItemMeta(meta);
            }
        } else {
            visor = createItem(Material.BARRIER, "&cSlot Vazio no ShopGUI+");
        }
        gui.setItem(4, visor);
    }

    // MÉTODO QUE FALTAVA PARA COMPILAR O SHOPOPERATIONS
    public static String generateBase64Skin(String url) {
        if (url == null || url.isEmpty()) return "";
        String json = "{\"textures\":{\"SKIN\":{\"url\":\"" + url + "\"}}}";
        return Base64.getEncoder().encodeToString(json.getBytes());
    }

    public static ItemStack createSkull(String url, String name) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        if (url == null || url.isEmpty()) return head;
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        if (meta == null) return head;
        PlayerProfile profile = Bukkit.createPlayerProfile(UUID.nameUUIDFromBytes(url.getBytes()), "SGA_Head");
        try { profile.getTextures().setSkin(new URL(url)); meta.setOwnerProfile(profile); } catch (MalformedURLException ignored) {}
        meta.setDisplayName(ChatUtils.color(name));
        head.setItemMeta(meta);
        return head;
    }

    public static String parseSkin(String b64) {
        if (b64 == null || b64.isEmpty()) return "";
        try {
            String d = new String(Base64.getDecoder().decode(b64));
            int s = d.indexOf("\"url\":\"") + 7;
            int e = d.indexOf("\"", s);
            return (s > 6 && e > s) ? d.substring(s, e) : "";
        } catch (Exception e) { return ""; }
    }

    private static String findItemKey(FileConfiguration cfg, String root, int slot) {
        ConfigurationSection sec = cfg.getConfigurationSection(root);
        if (sec == null) return null;
        for (String k : sec.getKeys(false)) if (cfg.getInt(root + "." + k + ".slot") == slot) return k;
        return null;
    }

    private static ItemStack createFromConfig(FileConfiguration cfg, String path) {
        Material m = Material.matchMaterial(cfg.getString(path + ".material", "STONE"));
        return createItem(m != null ? m : Material.BARRIER, cfg.getString(path + ".name", "&cErro Config"));
    }

    public static ItemStack createItem(Material m, String n) {
        ItemStack i = new ItemStack(m != null ? m : Material.BARRIER);
        ItemMeta mt = i.getItemMeta();
        if (mt != null) { mt.setDisplayName(ChatUtils.color(n)); i.setItemMeta(mt); }
        return i;
    }

    public static ItemStack createItemWithLore(Material m, String n, List<String> l) {
        ItemStack i = createItem(m, n);
        ItemMeta mt = i.getItemMeta();
        if (mt != null) {
            List<String> cl = new ArrayList<>();
            for (String s : l) cl.add(ChatUtils.color(s));
            mt.setLore(cl);
            i.setItemMeta(mt);
        }
        return i;
    }
}
