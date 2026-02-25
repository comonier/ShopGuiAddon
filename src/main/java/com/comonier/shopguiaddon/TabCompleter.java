package com.comonier.shopguiaddon;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.util.StringUtil;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TabCompleter implements org.bukkit.command.TabCompleter {

    private List shopCache = new ArrayList();

    public TabCompleter() {
        updateCache();
    }

    public void updateCache() {
        shopCache.clear();
        if (null != Bukkit.getPluginManager().getPlugin("ShopGUIPlus")) {
            File folder = new File(Bukkit.getPluginManager().getPlugin("ShopGUIPlus").getDataFolder(), "shops");
            if (folder.exists() && folder.isDirectory()) {
                File[] files = folder.listFiles();
                if (null != files) {
                    for (File f : files) {
                        if (f.getName().endsWith(".yml")) {
                            shopCache.add(f.getName().replace(".yml", ""));
                        }
                    }
                }
            }
        }
    }

    @Override
    public List onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List completions = new ArrayList();
        // Lista atualizada com shopremove
        List subs = Arrays.asList("help", "edit", "itemadd", "itemremove", "reload", "shopcreate", "shopremove", "link", "replace", "unlink", "menu", "item");

        if (1 == args.length) {
            StringUtil.copyPartialMatches(args[0], subs, completions);
        } 
        else if (2 == args.length) {
            String sub = args[0].toLowerCase();
            // Comandos que sugerem nomes de lojas do cache
            List needsShop = Arrays.asList("edit", "itemadd", "itemremove", "item", "shopremove");
            
            if (needsShop.contains(sub)) {
                StringUtil.copyPartialMatches(args[1], shopCache, completions);
            } else if (sub.equals("unlink") || sub.equals("menu")) {
                completions.add("[slot]");
            }
        }
        else if (4 == args.length) {
            String sub = args[0].toLowerCase();
            if (sub.equals("link") || sub.equals("replace")) {
                List mats = new ArrayList();
                for (Material m : Material.values()) {
                    mats.add(m.name());
                }
                StringUtil.copyPartialMatches(args[3], mats, completions);
            }
        }

        Collections.sort(completions);
        return completions;
    }
}
