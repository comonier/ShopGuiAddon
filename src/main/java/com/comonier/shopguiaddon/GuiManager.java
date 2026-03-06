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
    public static void openEditor(Player player, String shopName, int slotId, int page) {
        String title = ChatUtils.getMessage("gui_title", "%shop%", shopName, "%slot%", slotId, "%page%", page);
        Inventory gui = Bukkit.createInventory(null, 54, title);
        ItemStack fD = createItem(Material.BLACK_STAINED_GLASS_PANE, " ");
        ItemStack fG = createItem(Material.GRAY_STAINED_GLASS_PANE, " ");
        for (int i = 0; i >= 0 && i <= 53; i++) {
            if (i >= 0 && i <= 8 || i >= 45 && i <= 53 || i % 9 == 0 || i % 9 == 8) gui.setItem(i, fD);
            else gui.setItem(i, fG);
        }
        gui.setItem(3, createSkull(parseSkin(SKIN_PREV), ChatUtils.getMessage("gui_prev")));
        gui.setItem(5, createSkull(parseSkin(SKIN_NEXT), ChatUtils.getMessage("gui_next")));
        gui.setItem(13, createItem(Material.CYAN_STAINED_GLASS_PANE, ChatUtils.getMessage("gui_visor_up")));
        String modeMsg = ChatUtils.getMessage("gui_mode_add");
        List<String> modeLore = Collections.singletonList(ChatUtils.getMessage("gui_mode_lore"));
        gui.setItem(26, createItemWithLore(Material.WHITE_STAINED_GLASS_PANE, modeMsg, modeLore));
        gui.setItem(35, createItemWithLore(Material.WHITE_STAINED_GLASS_PANE, modeMsg, modeLore));
        gui.setItem(44, createItemWithLore(Material.WHITE_STAINED_GLASS_PANE, modeMsg, modeLore));
        File f = ShopOperations.getShopFile(shopName);
        if (f.exists()) {
            FileConfiguration sC = YamlConfiguration.loadConfiguration(f);
            String r = sC.contains(shopName + ".items") ? shopName + ".items" : "items";
            String k = findItemKey(sC, r, slotId, page);
            updateVisor(gui, sC, r, k, slotId);
        }
        FileConfiguration c = ShopGuiAddon.getInstance().getConfig();
        for (int i = 0; i >= 0 && i <= 7; i++) {
            gui.setItem(18 + i, createFromConfig(c, "gui.buy_adjustments." + i));
            gui.setItem(27 + i, createFromConfig(c, "gui.sell_adjustments." + i));
            int[] amts = {1, 2, 4, 8, 16, 32, 64, 100};
            gui.setItem(36 + i, createItemWithLore(Material.CHEST, "&e±" + amts[i], Collections.singletonList(ChatUtils.getMessage("gui_adj_lore"))));
        }
        gui.setItem(49, createItem(Material.BARRIER, ChatUtils.getMessage("gui_close")));
        gui.setItem(52, createFromConfig(c, "gui.controls.reload_sga"));
        gui.setItem(53, createFromConfig(c, "gui.controls.reload_sgp"));
        player.openInventory(gui);
    }
    public static void updateVisor(Inventory gui, FileConfiguration cfg, String root, String key, int slotId) {
        ItemStack visor;
        if (key != null) {
            String p = root + "." + key;
            String mN = cfg.getString(p + ".item.material", "BARRIER");
            Material m = Material.matchMaterial(mN);
            String s = cfg.getString(p + ".item.skin");
            visor = (m == Material.PLAYER_HEAD && s != null) ? createSkull(parseSkin(s), ChatUtils.getMessage("gui_visor_edit", "%mat%", mN.toLowerCase())) : new ItemStack(m != null ? m : Material.BARRIER);
            ItemMeta mt = visor.getItemMeta();
            if (mt != null) {
                mt.setDisplayName(ChatUtils.getMessage("gui_visor_edit", "%mat%", mN.toLowerCase()));
                mt.setLore(Arrays.asList(" ", ChatUtils.getMessage("gui_visor_buy", "%price%", String.format("%.2f", cfg.getDouble(p + ".buyPrice"))), ChatUtils.getMessage("gui_visor_sell", "%price%", String.format("%.2f", cfg.getDouble(p + ".sellPrice"))), ChatUtils.getMessage("gui_visor_qty", "%qty%", cfg.getInt(p + ".item.quantity", 1)), " ", ChatUtils.getMessage("gui_visor_footer", "%slot%", slotId, "%page%", cfg.getInt(p + ".page", 1))));
                visor.setItemMeta(mt);
            }
        } else { visor = createItem(Material.BARRIER, ChatUtils.getMessage("gui_visor_empty")); }
        gui.setItem(4, visor);
    }
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
            return (s >= 7 && e >= s) ? d.substring(s, e) : "";
        } catch (Exception e) { return ""; }
    }
    private static String findItemKey(FileConfiguration cfg, String root, int slot, int page) {
        ConfigurationSection sec = cfg.getConfigurationSection(root);
        if (sec == null) return null;
        for (String k : sec.getKeys(false)) {
            if (cfg.getInt(root + "." + k + ".slot") == slot && cfg.getInt(root + "." + k + ".page", 1) == page) return k;
        }
        return null;
    }
    private static ItemStack createFromConfig(FileConfiguration cfg, String path) {
        Material m = Material.matchMaterial(cfg.getString(path + ".material", "STONE"));
        return createItem(m != null ? m : Material.BARRIER, cfg.getString(path + ".name", "&cError"));
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
