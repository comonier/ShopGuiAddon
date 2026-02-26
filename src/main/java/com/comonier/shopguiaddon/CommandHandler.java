package com.comonier.shopguiaddon;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandHandler implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (false == (sender instanceof Player)) return true;
        Player player = (Player) sender;

        if (false == player.hasPermission("shopguiaddon.admin")) {
            player.sendMessage(ChatUtils.getMessage("no_permission"));
            return true;
        }

        if (1 > args.length) {
            sendHelp(player);
            return true;
        }

        String sub = args[0].toLowerCase();

        if (sub.equals("help")) {
            sendHelp(player);
            return true;
        }

        if (sub.equals("reload")) {
            ShopGuiAddon.getInstance().reloadConfig();
            updateTabCache();
            player.sendMessage(ChatUtils.getMessage("reload_success"));
            return true;
        }

        else if (sub.equals("edit")) {
            if (3 > args.length) { player.sendMessage("§cUsage: /sga edit [shop] [slot]"); return true; }
            try { GuiManager.openEditor(player, args[1], Integer.parseInt(args[2])); } catch (Exception e) { player.sendMessage("§cError in slot!"); }
            return true;
        }

        else if (sub.equals("itemadd")) {
            if (6 > args.length) { player.sendMessage("§cUsage: /sga itemadd [shop] [slot] [buy] [sell] [amount]"); return true; }
            handleItemAdd(player, args);
            return true;
        }

        else if (sub.equals("itemremove")) {
            if (3 > args.length) { player.sendMessage("§cUsage: /sga itemremove [shop] [slot]"); return true; }
            handleItemRemove(player, args[1], args[2]);
            return true;
        }

        else if (sub.equals("shopcreate")) {
            if (2 > args.length) { player.sendMessage("§cUsage: /sga shopcreate [name]"); return true; }
            handleShopCreate(player, args[1].toLowerCase());
            return true;
        }

        else if (sub.equals("shopremove")) {
            if (2 > args.length) { player.sendMessage("§cUsage: /sga shopremove [name]"); return true; }
            handleShopRemove(player, args[1].toLowerCase());
            return true;
        }

        else if (sub.equals("link") || sub.equals("replace")) {
            if (4 > args.length) { player.sendMessage("§cUsage: /sga " + sub + " [shop] [slot] [mat] [skin]"); return true; }
            handleMenuLink(player, args, sub.equals("replace"));
            return true;
        }

        else if (sub.equals("unlink")) {
            if (2 > args.length) { player.sendMessage("§cUsage: /sga unlink [slot]"); return true; }
            handleMenuUnlink(player, args[1]);
            return true;
        }

        else if (sub.equals("menu")) {
            if (4 > args.length) { player.sendMessage("§cUsage: /sga menu [slot] [name|lore] [text]"); return true; }
            handleMenuEdit(player, args);
            return true;
        }

        else if (sub.equals("item")) {
            if (5 > args.length) { player.sendMessage("§cUsage: /sga item [shop] [slot] [name|lore] [text]"); return true; }
            handleItemEdit(player, args);
            return true;
        }

        sendHelp(player);
        return true;
    }

    private void handleItemAdd(Player p, String[] args) {
        try {
            String shopId = args[1];
            int slotId = Integer.parseInt(args[2]);
            double buy = Double.parseDouble(args[3]);
            double sell = Double.parseDouble(args[4]);
            int amount = Integer.parseInt(args[5]);

            ItemStack item = p.getInventory().getItemInMainHand();
            if (null == item || Material.AIR == item.getType()) { 
                p.sendMessage(ChatUtils.getMessage("error_item_hand")); 
                return; 
            }
            
            File f = new File(new File(Bukkit.getPluginManager().getPlugin("ShopGUIPlus").getDataFolder(), "shops"), shopId + ".yml");
            if (false == f.exists()) { 
                p.sendMessage(ChatUtils.getMessage("error_shop_file").replace("%shop%", shopId)); 
                return; 
            }
            
            FileConfiguration cfg = YamlConfiguration.loadConfiguration(f);
            String root = cfg.contains(shopId + ".items") ? shopId + ".items" : "items";
            ConfigurationSection sec = cfg.getConfigurationSection(root);
            
            if (null != sec) {
                for (String k : sec.getKeys(false)) {
                    if (cfg.getInt(root + "." + k + ".slot") == slotId) { cfg.set(root + "." + k, null); break; }
                }
            }
            
            String key = item.getType().name().toLowerCase() + "_" + System.currentTimeMillis();
            String pth = root + "." + key;
            
            cfg.set(pth + ".type", "item");
            cfg.set(pth + ".item.material", item.getType().name());
            cfg.set(pth + ".item.quantity", amount);
            cfg.set(pth + ".slot", slotId);
            cfg.set(pth + ".buyPrice", buy);
            cfg.set(pth + ".sellPrice", sell);
            
            cfg.save(f);
            p.performCommand("shopgui reload");
            
            String msg = ChatUtils.getMessage("item_added")
                    .replace("%shop%", shopId)
                    .replace("%slot%", String.valueOf(slotId))
                    .replace("%amount%", String.valueOf(amount));
            p.sendMessage(msg);

        } catch (NumberFormatException e) {
            p.sendMessage(ChatUtils.getMessage("error_invalid_number"));
        } catch (Exception e) { 
            p.sendMessage("§cAn unexpected error occurred during item addition."); 
        }
    }

    private void handleItemRemove(Player p, String shopId, String slotStr) {
        try {
            int targetSlot = Integer.parseInt(slotStr);
            File f = new File(new File(Bukkit.getPluginManager().getPlugin("ShopGUIPlus").getDataFolder(), "shops"), shopId + ".yml");
            if (false == f.exists()) { p.sendMessage("§cShop not found!"); return; }
            FileConfiguration cfg = YamlConfiguration.loadConfiguration(f);
            String root = cfg.contains(shopId + ".items") ? shopId + ".items" : "items";
            ConfigurationSection sec = cfg.getConfigurationSection(root);
            if (null != sec) {
                String keyToRemove = null;
                for (String k : sec.getKeys(false)) {
                    if (cfg.getInt(root + "." + k + ".slot") == targetSlot) { keyToRemove = k; break; }
                }
                if (null != keyToRemove) {
                    cfg.set(root + "." + keyToRemove, null);
                    cfg.save(f);
                    p.performCommand("shopgui reload");
                    p.sendMessage("§aItem removed from " + shopId + " slot " + targetSlot);
                } else { p.sendMessage("§cSlot is empty."); }
            }
        } catch (Exception e) { p.sendMessage("§cError removing item."); }
    }

    private void handleShopCreate(Player p, String id) {
        try {
            File f = new File(new File(Bukkit.getPluginManager().getPlugin("ShopGUIPlus").getDataFolder(), "shops"), id + ".yml");
            if (f.exists()) { p.sendMessage("§cAlready exists!"); return; }
            FileConfiguration cfg = new YamlConfiguration();
            cfg.set(id + ".name", "&bShop " + id);
            cfg.set(id + ".size", 54);
            cfg.set(id + ".items.1.type", "item");
            cfg.set(id + ".items.1.item.material", "STONE");
            cfg.set(id + ".items.1.item.quantity", 1);
            cfg.set(id + ".items.1.slot", 10);
            cfg.set(id + ".items.1.buyPrice", 10.0);
            cfg.set(id + ".items.1.sellPrice", 5.0);
            cfg.save(f);
            updateTabCache();
            p.performCommand("shopgui reload");
            p.sendMessage("§aShop created!");
        } catch (Exception e) { p.sendMessage("§cError creating shop."); }
    }

    private void handleShopRemove(Player p, String id) {
        File f = new File(new File(Bukkit.getPluginManager().getPlugin("ShopGUIPlus").getDataFolder(), "shops"), id + ".yml");
        if (f.exists()) {
            if (f.delete()) {
                updateTabCache();
                p.performCommand("shopgui reload");
                p.sendMessage("§aShop file " + id + ".yml deleted successfully!");
            } else { p.sendMessage("§cFailed to delete file."); }
        } else { p.sendMessage("§cShop file not found."); }
    }

    private void handleMenuLink(Player p, String[] args, boolean force) {
        try {
            String shopId = args[1].toLowerCase();
            int slot = Integer.parseInt(args[2]);
            String mat = args[3].toUpperCase();
            String skin = (5 > args.length) ? null : args[4];
            File f = new File(Bukkit.getPluginManager().getPlugin("ShopGUIPlus").getDataFolder(), "config.yml");
            FileConfiguration cfg = YamlConfiguration.loadConfiguration(f);
            String path = "shopMenuItems";
            ConfigurationSection sec = cfg.getConfigurationSection(path);
            String oldKey = null;
            if (null != sec) {
                for (String k : sec.getKeys(false)) {
                    if (cfg.getInt(path + "." + k + ".slot") == slot) { oldKey = k; break; }
                }
            }
            if (null != oldKey) {
                if (false == force) { p.sendMessage("§cSlot occupied! Use replace."); return; }
                cfg.set(path + "." + oldKey, null);
            }
            int nextId = 1;
            if (null != sec) {
                for (String k : sec.getKeys(false)) {
                    try { int cur = Integer.parseInt(k); if (cur >= nextId) nextId = cur + 1; } catch (Exception e) {}
                }
            }
            String finalKey = (null != oldKey) ? oldKey : String.valueOf(nextId);
            String pth = path + "." + finalKey;
            cfg.set(pth + ".item.material", mat);
            if (mat.equals("PLAYER_HEAD") && null != skin) cfg.set(pth + ".item.skin", skin);
            cfg.set(pth + ".item.name", "&b&lSHOP: " + shopId.toUpperCase());
            cfg.set(pth + ".item.lore", Arrays.asList("&7» Click to open", "&7» Access: /shop " + shopId));
            cfg.set(pth + ".shop", shopId);
            cfg.set(pth + ".slot", slot);
            cfg.save(f);
            p.performCommand("shopgui reload");
            p.sendMessage("§aMain Menu updated!");
        } catch (Exception e) { p.sendMessage("§cError in menu link!"); }
    }

    private void handleMenuUnlink(Player p, String slotStr) {
        try {
            int targetSlot = Integer.parseInt(slotStr);
            File f = new File(Bukkit.getPluginManager().getPlugin("ShopGUIPlus").getDataFolder(), "config.yml");
            FileConfiguration cfg = YamlConfiguration.loadConfiguration(f);
            String path = "shopMenuItems";
            ConfigurationSection sec = cfg.getConfigurationSection(path);
            if (null != sec) {
                String keyToRemove = null;
                for (String k : sec.getKeys(false)) {
                    if (cfg.getInt(path + "." + k + ".slot") == targetSlot) { keyToRemove = k; break; }
                }
                if (null != keyToRemove) {
                    cfg.set(path + "." + keyToRemove, null);
                    cfg.save(f);
                    p.performCommand("shopgui reload");
                    p.sendMessage("§aUnlinked slot " + targetSlot);
                } else { p.sendMessage("§cSlot not found in menu."); }
            }
        } catch (Exception e) { p.sendMessage("§cError in unlink."); }
    }

    private void handleMenuEdit(Player p, String[] args) {
        try {
            int slot = Integer.parseInt(args[1]);
            String type = args[2].toLowerCase();
            String text = String.join(" ", Arrays.copyOfRange(args, 3, args.length));
            File f = new File(Bukkit.getPluginManager().getPlugin("ShopGUIPlus").getDataFolder(), "config.yml");
            FileConfiguration cfg = YamlConfiguration.loadConfiguration(f);
            ConfigurationSection sec = cfg.getConfigurationSection("shopMenuItems");
            if (null != sec) {
                for (String k : sec.getKeys(false)) {
                    if (cfg.getInt("shopMenuItems." + k + ".slot") == slot) {
                        String pth = "shopMenuItems." + k + ".item." + type;
                        if (type.equals("lore")) cfg.set(pth, Arrays.asList(text.split(";")));
                        else cfg.set(pth, ChatUtils.color(text));
                        cfg.save(f);
                        p.performCommand("shopgui reload");
                        p.sendMessage("§aMenu display updated!");
                        return;
                    }
                }
            }
            p.sendMessage("§cNo item found at slot " + slot);
        } catch (Exception e) { p.sendMessage("§cError in menu edit!"); }
    }

    private void handleItemEdit(Player p, String[] args) {
        try {
            String shopId = args[1].toLowerCase();
            int slot = Integer.parseInt(args[2]);
            String type = args[3].toLowerCase();
            String text = String.join(" ", Arrays.copyOfRange(args, 4, args.length));
            File f = new File(new File(Bukkit.getPluginManager().getPlugin("ShopGUIPlus").getDataFolder(), "shops"), shopId + ".yml");
            if (false == f.exists()) { p.sendMessage("§cShop not found!"); return; }
            FileConfiguration cfg = YamlConfiguration.loadConfiguration(f);
            String root = cfg.contains(shopId + ".items") ? shopId + ".items" : "items";
            ConfigurationSection sec = cfg.getConfigurationSection(root);
            if (null != sec) {
                for (String k : sec.getKeys(false)) {
                    if (cfg.getInt(root + "." + k + ".slot") == slot) {
                        String pth = root + "." + k + ".item." + type;
                        if (type.equals("lore")) cfg.set(pth, Arrays.asList(text.split(";")));
                        else cfg.set(pth, ChatUtils.color(text));
                        cfg.save(f);
                        p.performCommand("shopgui reload");
                        p.sendMessage("§aItem display updated!");
                        return;
                    }
                }
            }
            p.sendMessage("§cSlot " + slot + " not found in " + shopId);
        } catch (Exception e) { p.sendMessage("§cError in item edit!"); }
    }

    private void updateTabCache() {
        if (null != ShopGuiAddon.getInstance().getCommand("sga").getTabCompleter()) {
            ((TabCompleter) ShopGuiAddon.getInstance().getCommand("sga").getTabCompleter()).updateCache();
        }
    }

    private void sendHelp(Player p) {
        p.sendMessage(ChatUtils.getMessage("help_header"));
        p.sendMessage(ChatUtils.getMessage("help_edit"));
        p.sendMessage(ChatUtils.getMessage("help_itemadd"));
        p.sendMessage(ChatUtils.getMessage("help_shopcreate"));
        p.sendMessage(ChatUtils.getMessage("help_shopremove"));
        p.sendMessage(ChatUtils.getMessage("help_link"));
        p.sendMessage(ChatUtils.getMessage("help_reload"));
    }
}
