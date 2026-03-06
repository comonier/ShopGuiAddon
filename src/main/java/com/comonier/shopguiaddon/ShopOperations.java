package com.comonier.shopguiaddon;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import java.io.File;
public class ShopOperations {
    public static boolean handle(Player player, String sub, String[] args) {
        try {
            String s = sub.toLowerCase();
            if (s.equals("itemadd")) {
                if (args.length >= 6) ShopItemEditor.handleItemAdd(player, args);
                else player.sendMessage(ChatUtils.getMessage("help_itemadd"));
            } else if (s.equals("itemremove")) {
                if (args.length >= 3) ShopItemEditor.handleItemRemove(player, args);
                else player.sendMessage(ChatUtils.getMessage("usage_itemremove"));
            } else if (s.equals("link") || s.equals("replace")) {
                if (args.length >= 4) ShopMenuManager.handleMenuLink(player, args, s.equals("replace"));
                else player.sendMessage(ChatUtils.getMessage("help_link"));
            } else if (s.equals("unlink")) {
                if (args.length >= 2) ShopMenuManager.handleMenuUnlink(player, args);
                else player.sendMessage(ChatUtils.getMessage("usage_unlink"));
            } else if (s.equals("menu")) {
                if (args.length >= 4) ShopMetadataHandler.handleMetadata(player, null, args, true);
                else player.sendMessage(ChatUtils.getMessage("usage_menu"));
            } else if (s.equals("item")) {
                if (args.length >= 5) ShopMetadataHandler.handleMetadata(player, args[1], args, false);
                else player.sendMessage(ChatUtils.getMessage("usage_item"));
            } else {
                return ShopFileManager.handle(player, s, args);
            }
        } catch (Exception e) {
            player.sendMessage(ChatUtils.getMessage("error_invalid_number"));
        }
        return true;
    }
    public static File getShopFile(String id) {
        return new File(new File(Bukkit.getPluginManager().getPlugin("ShopGUIPlus").getDataFolder(), "shops"), id + ".yml");
    }
    public static boolean clearSlot(FileConfiguration cfg, String root, int slot, int page) {
        ConfigurationSection sec = cfg.getConfigurationSection(root);
        if (sec != null) {
            for (String k : sec.getKeys(false)) {
                if (cfg.getInt(root + "." + k + ".slot") == slot && cfg.getInt(root + "." + k + ".page", 1) == page) {
                    cfg.set(root + "." + k, null);
                    return true;
                }
            }
        }
        return false;
    }
    public static void reloadSGP(Player p) { p.performCommand("shopgui reload"); }
}
