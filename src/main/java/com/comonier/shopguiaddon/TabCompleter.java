package com.comonier.shopguiaddon;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.StringUtil;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TabCompleter implements org.bukkit.command.TabCompleter {

    private final List<String> shopCache = new ArrayList<>();

    public TabCompleter() {
        updateCache();
    }

    /**
     * Atualiza a lista de lojas disponíveis lendo a pasta do ShopGUIPlus.
     */
    public void updateCache() {
        shopCache.clear();
        Plugin sgp = Bukkit.getPluginManager().getPlugin("ShopGUIPlus");
        
        // Lógica Inversa: Só processa se o plugin dependente existir
        if (sgp == null) return;

        File shopFolder = new File(sgp.getDataFolder(), "shops");
        if (shopFolder.exists() && shopFolder.isDirectory()) {
            File[] files = shopFolder.listFiles((dir, name) -> name.endsWith(".yml"));
            if (files != null) {
                for (File f : files) {
                    shopCache.add(f.getName().replace(".yml", ""));
                }
            }
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        List<String> subs = Arrays.asList("help", "list", "edit", "itemadd", "itemremove", "reload", "shopcreate", "shopremove", "link", "replace", "unlink", "menu", "item");

        // 1. Sugestão de Subcomandos (Argumento 1)
        if (args.length == 1) {
            StringUtil.copyPartialMatches(args[0], subs, completions);
        } 
        
        // 2. Sugestões baseadas no subcomando (Argumento 2 em diante)
        else if (args.length >= 2) {
            String sub = args[0].toLowerCase();

            // Subcomandos que precisam do ID da loja no argumento 2
            List<String> needsShop = Arrays.asList("edit", "itemadd", "itemremove", "item", "shopremove", "link", "replace");
            if (needsShop.contains(sub) && args.length == 2) {
                StringUtil.copyPartialMatches(args[1], shopCache, completions);
            }

            // Lógica específica para ITEMADD
            if (sub.equals("itemadd")) {
                switch (args.length) {
                    case 3 -> completions.add("[slot]");
                    case 4 -> completions.add("[buyPrice]");
                    case 5 -> completions.add("[sellPrice]");
                    case 6 -> {
                        List<String> amounts = Arrays.asList("1", "16", "32", "64");
                        StringUtil.copyPartialMatches(args[5], amounts, completions);
                    }
                }
            }

            // Lógica específica para LINK / REPLACE
            if (sub.equals("link") || sub.equals("replace")) {
                if (args.length == 3) {
                    completions.add("[slot]");
                } else if (args.length == 4) {
                    List<String> mats = new ArrayList<>();
                    for (Material m : Material.values()) {
                        // Filtra apenas itens válidos para a versão 1.21
                        if (!m.isLegacy() && m.isItem() && m != Material.AIR) {
                            mats.add(m.name().toLowerCase());
                        }
                    }
                    StringUtil.copyPartialMatches(args[3], mats, completions);
                }
            }

            // Sugestão de slot para UNLINK ou MENU
            if ((sub.equals("unlink") || sub.equals("menu")) && args.length == 2) {
                completions.add("[slot]");
            }
        }

        Collections.sort(completions);
        return completions;
    }
}
