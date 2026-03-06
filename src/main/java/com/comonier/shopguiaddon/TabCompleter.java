package com.comonier.shopguiaddon;
import org.bukkit.Bukkit;
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
    public TabCompleter() { updateCache(); }
    public void updateCache() {
        shopCache.clear();
        Plugin sgp = Bukkit.getPluginManager().getPlugin("ShopGUIPlus");
        if (sgp != null) {
            File folder = new File(sgp.getDataFolder(), "shops");
            if (folder.exists() && folder.isDirectory()) {
                File[] files = folder.listFiles((dir, name) -> name.endsWith(".yml"));
                if (files != null) {
                    for (File f : files) shopCache.add(f.getName().replace(".yml", ""));
                }
            }
        }
    }
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        List<String> subs = Arrays.asList("help", "list", "edit", "itemadd", "itemremove", "reload", "shopcreate", "shopremove", "link", "replace", "unlink", "menu", "item");
        if (args.length == 1) {
            StringUtil.copyPartialMatches(args[0], subs, completions);
        } else if (args.length >= 2) {
            String sub = args[0].toLowerCase();
            if (Arrays.asList("edit", "itemadd", "itemremove", "shopremove", "link", "replace", "item").contains(sub) && args.length == 2) {
                StringUtil.copyPartialMatches(args[1], shopCache, completions);
            }
            if (sub.equals("menu") && args.length == 2) completions.add("[slot]");
            if ((sub.equals("menu") && args.length == 3) || (sub.equals("item") && args.length == 4)) {
                List<String> types = Arrays.asList("name", "lore");
                StringUtil.copyPartialMatches(args[args.length - 1], types, completions);
            }
            if (sub.equals("itemadd")) {
                if (args.length == 3) completions.add("[slot]");
                if (args.length == 4) completions.add("[buyPrice]");
                if (args.length == 5) completions.add("[sellPrice]");
                if (args.length == 6) completions.add("1");
                if (args.length == 7) completions.add("[page]");
            }
            if ((sub.equals("edit") || sub.equals("itemremove")) && args.length == 3) completions.add("[slot]");
            if ((sub.equals("edit") || sub.equals("itemremove")) && args.length == 4) completions.add("[page]");
            if (sub.equals("item") && args.length == 3) completions.add("[slot]");
            if (sub.equals("item") && args.length == 6) completions.add("[page]");
        }
        Collections.sort(completions);
        return completions;
    }
}
