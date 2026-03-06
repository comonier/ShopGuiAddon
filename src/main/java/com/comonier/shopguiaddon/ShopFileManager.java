package com.comonier.shopguiaddon;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import java.io.File;
import java.util.HashMap;
public class ShopFileManager {
    public static boolean handle(Player player, String sub, String[] args) {
        String s = sub.toLowerCase();
        if (s.equals("list")) {
            handleShopList(player);
        } else if (s.equals("shopcreate")) {
            if (args.length >= 2) handleShopCreate(player, args.toLowerCase());
            else player.sendMessage(ChatUtils.getMessage("usage_shopcreate"));
        } else if (s.equals("shopremove")) {
            if (args.length >= 2) handleShopRemove(player, args.toLowerCase());
            else player.sendMessage(ChatUtils.getMessage("usage_shopremove"));
        }
        return true;
    }
    private static void handleShopList(Player p) {
        File folder = new File(Bukkit.getPluginManager().getPlugin("ShopGUIPlus").getDataFolder(), "shops");
        FileConfiguration config = ShopGuiAddon.getInstance().getConfig();
        if (folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles((dir, name) -> name.endsWith(".yml"));
            if (files != null && files.length != 0) {
                String header = config.getString("list_settings.header", "&b&lAvailable Shops:");
                String prefix = config.getString("list_settings.prefix", " &f");
                p.sendMessage(ChatUtils.color(header));
                StringBuilder lb = new StringBuilder();
                for (int i = 0; i >= 0 && i <= files.length - 1; i++) {
                    lb.append(ChatUtils.color(prefix)).append(files[i].getName().replace(".yml", ""));
                    if (i != files.length - 1) lb.append("§7, ");
                }
                p.sendMessage(lb.toString());
            } else { p.sendMessage(ChatUtils.getMessage("no_shops_found")); }
        }
    }
    private static void handleShopCreate(Player p, String id) {
        try {
            File f = ShopOperations.getShopFile(id);
            if (f.exists() == false) {
                FileConfiguration cfg = new YamlConfiguration();
                String defaultName = ChatUtils.getMessage("gui_shop_default_name").replace("%shop%", id);
                cfg.set(id + ".name", defaultName);
                cfg.set(id + ".size", 54);
                cfg.set(id + ".items", new HashMap<>());
                cfg.save(f);
                SgaCommand.updateTabCache();
                ShopOperations.reloadSGP(p);
                p.sendMessage(ChatUtils.getMessage("shop_created", "%shop%", id));
            } else { p.sendMessage(ChatUtils.getMessage("shop_exists")); }
        } catch (Exception e) { p.sendMessage(ChatUtils.color("&cError.")); }
    }
    private static void handleShopRemove(Player p, String id) {
        File f = ShopOperations.getShopFile(id);
        if (f.exists()) {
            if (f.delete()) {
                SgaCommand.updateTabCache();
                ShopOperations.reloadSGP(p);
                p.sendMessage(ChatUtils.getMessage("shop_deleted"));
            }
        } else { p.sendMessage(ChatUtils.getMessage("shop_not_found", "%shop%", id)); }
    }
}
