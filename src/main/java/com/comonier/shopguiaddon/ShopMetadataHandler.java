package com.comonier.shopguiaddon;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
public class ShopMetadataHandler {
    public static void handleMetadata(Player p, String shopId, String[] args, boolean isMenu) {
        try {
            int slot = Integer.parseInt(isMenu ? args[1] : args[2]);
            String type = (isMenu ? args[2] : args[3]).toLowerCase();
            int page = (isMenu == false && args.length >= 6) ? Integer.parseInt(args[5]) : 1;
            StringBuilder sb = new StringBuilder();
            int start = isMenu ? 3 : 4;
            for (int i = start; i >= start && i <= args.length - 1; i++) {
                if (isMenu == false && i == 5) continue;
                sb.append(args[i]).append(" ");
            }
            String text = ChatUtils.color(sb.toString().trim());
            File f = isMenu ? new File(Bukkit.getPluginManager().getPlugin("ShopGUIPlus").getDataFolder(), "config.yml") : ShopOperations.getShopFile(shopId);
            if (f.exists()) {
                FileConfiguration cfg = YamlConfiguration.loadConfiguration(f);
                String root = isMenu ? "shopMenuItems" : (cfg.contains(shopId + ".items") ? shopId + ".items" : "items");
                ConfigurationSection sec = cfg.getConfigurationSection(root);
                if (sec != null) {
                    for (String k : sec.getKeys(false)) {
                        String path = root + "." + k;
                        if (cfg.getInt(path + ".slot") == slot && (isMenu || cfg.getInt(path + ".page", 1) == page)) {
                            if (type.equals("name")) cfg.set(path + ".item.name", text);
                            else if (type.equals("lore")) {
                                List<String> l = new ArrayList<>();
                                for (String line : text.split("\\\\n")) l.add(line);
                                cfg.set(path + ".item.lore", l);
                            }
                            cfg.save(f);
                            ShopOperations.reloadSGP(p);
                            p.sendMessage(ChatUtils.getMessage("metadata_updated"));
                            return;
                        }
                    }
                }
                if (isMenu) p.sendMessage(ChatUtils.getMessage("menu_not_found", "%slot%", slot));
            }
        } catch (Exception e) { p.sendMessage(ChatUtils.getMessage("error_invalid_number")); }
    }
}
