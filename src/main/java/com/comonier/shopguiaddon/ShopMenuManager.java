package com.comonier.shopguiaddon;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import java.io.File;
public class ShopMenuManager {
    public static void handleMenuLink(Player p, String[] args, boolean force) {
        try {
            File f = new File(Bukkit.getPluginManager().getPlugin("ShopGUIPlus").getDataFolder(), "config.yml");
            FileConfiguration cfg = YamlConfiguration.loadConfiguration(f);
            int slot = Integer.parseInt(args[2]);
            String matName = args[3].toUpperCase();
            if (Material.getMaterial(matName) == null) {
                p.sendMessage(ChatUtils.getMessage("error_material", "%mat%", matName));
                return;
            }
            String pth = "shopMenuItems.link_" + slot;
            cfg.set(pth + ".shop", args[1]);
            cfg.set(pth + ".slot", slot);
            cfg.set(pth + ".item.material", matName);
            cfg.save(f);
            ShopOperations.reloadSGP(p);
            p.sendMessage(ChatUtils.getMessage("link_success", "%slot%", slot, "%mat%", matName));
        } catch (Exception e) { p.sendMessage(ChatUtils.getMessage("error_invalid_number")); }
    }
    public static void handleMenuUnlink(Player p, String[] args) {
        try {
            int slot = Integer.parseInt(args[1]);
            File f = new File(Bukkit.getPluginManager().getPlugin("ShopGUIPlus").getDataFolder(), "config.yml");
            FileConfiguration cfg = YamlConfiguration.loadConfiguration(f);
            ConfigurationSection sec = cfg.getConfigurationSection("shopMenuItems");
            if (sec != null) {
                for (String k : sec.getKeys(false)) {
                    if (cfg.getInt("shopMenuItems." + k + ".slot") == slot) {
                        cfg.set("shopMenuItems." + k, null);
                        cfg.save(f);
                        ShopOperations.reloadSGP(p);
                        p.sendMessage(ChatUtils.getMessage("slot_unlinked"));
                        return;
                    }
                }
            }
            p.sendMessage(ChatUtils.getMessage("menu_not_found", "%slot%", slot));
        } catch (Exception e) { p.sendMessage(ChatUtils.getMessage("error_invalid_number")); }
    }
}
