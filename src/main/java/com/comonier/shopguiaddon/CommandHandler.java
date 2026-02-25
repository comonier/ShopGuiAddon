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

        // --- 1. HELP ---
        if (sub.equals("help")) {
            sendHelp(player);
            return true;
        }

        // --- 2. RELOAD ---
        if (sub.equals("reload")) {
            ShopGuiAddon.getInstance().reloadConfig();
            player.sendMessage(ChatUtils.getMessage("reload_success"));
            return true;
        }

        // --- 3. EDIT (GUI VISUAL) ---
        else if (sub.equals("edit")) {
            if (3 > args.length) { player.sendMessage("§cUso: /sga edit <loja> <slot>"); return true; }
            try { GuiManager.openEditor(player, args[1], Integer.parseInt(args[2])); } catch (Exception e) { player.sendMessage("§cErro no slot!"); }
            return true;
        }

        // --- 4. ITEMADD (ADICIONAR ITEM DA MAO) ---
        else if (sub.equals("itemadd")) {
            if (5 > args.length) { player.sendMessage("§cUso: /sga itemadd <loja> <slot> <compra> <venda>"); return true; }
            ItemStack item = player.getInventory().getItemInMainHand();
            if (null == item || Material.AIR == item.getType()) { player.sendMessage("§cSegure um item!"); return true; }
            try {
                String shopId = args[1];
                int slotId = Integer.parseInt(args[2]);
                double buy = Double.parseDouble(args[3]);
                double sell = Double.parseDouble(args[4]);
                File f = new File(new File(Bukkit.getPluginManager().getPlugin("ShopGUIPlus").getDataFolder(), "shops"), shopId + ".yml");
                if (false == f.exists()) { player.sendMessage("§cLoja nao encontrada!"); return true; }
                FileConfiguration cfg = YamlConfiguration.loadConfiguration(f);
                String root = cfg.contains(shopId + ".items") ? shopId + ".items" : "items";
                ConfigurationSection sec = cfg.getConfigurationSection(root);
                if (null != sec) {
                    for (String k : sec.getKeys(false)) {
                        if (cfg.getInt(root + "." + k + ".slot") == slotId) { cfg.set(root + "." + k, null); break; }
                    }
                }
                String key = item.getType().name().toLowerCase() + "_" + System.currentTimeMillis();
                cfg.set(root + "." + key + ".type", "item");
                cfg.set(root + "." + key + ".item.material", item.getType().name());
                cfg.set(root + "." + key + ".slot", slotId);
                cfg.set(root + "." + key + ".buyPrice", buy);
                cfg.set(root + "." + key + ".sellPrice", sell);
                cfg.save(f);
                player.performCommand("shopgui reload");
                player.sendMessage("§aItem adicionado a " + shopId);
            } catch (Exception e) { player.sendMessage("§cErro no itemadd!"); }
            return true;
        }

        // --- 5. SHOPCREATE ---
        else if (sub.equals("shopcreate")) {
            if (2 > args.length) { player.sendMessage("§cUso: /sga shopcreate <nome>"); return true; }
            String id = args[1].toLowerCase();
            File f = new File(new File(Bukkit.getPluginManager().getPlugin("ShopGUIPlus").getDataFolder(), "shops"), id + ".yml");
            if (f.exists()) { player.sendMessage("§cJa existe!"); return true; }
            FileConfiguration cfg = new YamlConfiguration();
            cfg.set(id + ".name", "&bShop " + id);
            cfg.set(id + ".size", 54);
            cfg.set(id + ".items.1.type", "item");
            cfg.set(id + ".items.1.item.material", "STONE");
            cfg.set(id + ".items.1.slot", 10);
            cfg.set(id + ".items.1.buyPrice", 10.0);
            cfg.set(id + ".items.1.sellPrice", 5.0);
            try { cfg.save(f); player.performCommand("shopgui reload"); player.sendMessage("§aLoja criada!"); } catch (IOException e) { e.printStackTrace(); }
            return true;
        }

        // --- 6. LINK & REPLACE (MENU PRINCIPAL) ---
        else if (sub.equals("link") || sub.equals("replace")) {
            if (4 > args.length) { player.sendMessage("§cUso: /sga " + sub + " <loja> <slot> <material> [skin]"); return true; }
            handleMenuLink(player, args, sub.equals("replace"));
            return true;
        }

        // --- 7. MENU (EDITAR DISPLAY DO MENU PRINCIPAL) ---
        else if (sub.equals("menu")) {
            if (4 > args.length) { player.sendMessage("§cUso: /sga menu <slot> <name|lore> <texto;texto>"); return true; }
            handleMenuEdit(player, args);
            return true;
        }

        // --- 8. ITEM (EDITAR DISPLAY DE UM ITEM NA LOJA FISICA) ---
        else if (sub.equals("item")) {
            if (5 > args.length) { player.sendMessage("§cUso: /sga item <loja> <slot> <name|lore> <texto;texto>"); return true; }
            handleItemEdit(player, args);
            return true;
        }

        sendHelp(player);
        return true;
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
                if (false == force) { p.sendMessage("§cSlot ocupado! Use /sga replace."); return; }
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
            cfg.set(pth + ".item.name", "&b&lLOJA: " + shopId.toUpperCase());
            cfg.set(pth + ".item.lore", Arrays.asList("&7» Clique para abrir", "&7» Acesse: /shop " + shopId));
            cfg.set(pth + ".shop", shopId);
            cfg.set(pth + ".slot", slot);
            cfg.save(f);
            p.performCommand("shopgui reload");
            p.sendMessage("§aVínculo no Menu Principal atualizado!");
        } catch (Exception e) { p.sendMessage("§cErro no link/replace!"); }
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
                        else cfg.set(pth, text);
                        cfg.save(f);
                        p.performCommand("shopgui reload");
                        p.sendMessage("§aDisplay do Menu atualizado com sucesso!");
                        return;
                    }
                }
            }
            p.sendMessage("§cSlot nao encontrado no Menu Principal!");
        } catch (Exception e) { p.sendMessage("§cErro no comando menu!"); }
    }

    private void handleItemEdit(Player p, String[] args) {
        try {
            String shopId = args[1].toLowerCase();
            int slot = Integer.parseInt(args[2]);
            String type = args[3].toLowerCase();
            String text = String.join(" ", Arrays.copyOfRange(args, 4, args.length));
            File f = new File(new File(Bukkit.getPluginManager().getPlugin("ShopGUIPlus").getDataFolder(), "shops"), shopId + ".yml");
            FileConfiguration cfg = YamlConfiguration.loadConfiguration(f);
            String root = cfg.contains(shopId + ".items") ? shopId + ".items" : "items";
            ConfigurationSection sec = cfg.getConfigurationSection(root);
            if (null != sec) {
                for (String k : sec.getKeys(false)) {
                    if (cfg.getInt(root + "." + k + ".slot") == slot) {
                        String pth = root + "." + k + ".item." + type;
                        if (type.equals("lore")) cfg.set(pth, Arrays.asList(text.split(";")));
                        else cfg.set(pth, text);
                        cfg.save(f);
                        p.performCommand("shopgui reload");
                        p.sendMessage("§aDisplay do item na loja '" + shopId + "' atualizado!");
                        return;
                    }
                }
            }
            p.sendMessage("§cItem nao encontrado na loja fisica!");
        } catch (Exception e) { p.sendMessage("§cErro no comando item!"); }
    }

    private void sendHelp(Player p) {
        p.sendMessage("§b§lComandos ShopGuiAddon:");
        p.sendMessage("§f/sga edit <loja> <slot> §7- Edita precos visualmente");
        p.sendMessage("§f/sga itemadd <loja> <slot> <compra> <venda> §7- Adiciona item");
        p.sendMessage("§f/sga link/replace <loja> <slot> <material> [skin] §7- Atalho Menu");
        p.sendMessage("§f/sga menu <slot> <name|lore> <texto;texto> §7- Edita Vitrine");
        p.sendMessage("§f/sga item <loja> <slot> <name|lore> <texto;texto> §7- Edita Item Loja");
        p.sendMessage("§f/sga reload §7- Recarrega Addon");
    }
}
