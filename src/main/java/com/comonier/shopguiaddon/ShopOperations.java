package com.comonier.shopguiaddon;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import java.io.File;
import java.util.List;

public class ShopOperations {

    public static boolean handle(Player player, String sub, String[] args) {
        switch (sub.toLowerCase()) {
            case "itemadd":
                if (args.length < 6) {
                    player.sendMessage(ChatUtils.getMessage("help_itemadd"));
                    return true;
                }
                handleItemAdd(player, args);
                break;

            case "itemremove":
                if (args.length < 3) {
                    player.sendMessage("§cUso: /sga itemremove [loja] [slot]");
                    return true;
                }
                handleItemRemove(player, args[1], args[2]);
                break;

            case "link":
            case "replace":
                if (args.length < 4) {
                    player.sendMessage(ChatUtils.getMessage("help_link"));
                    return true;
                }
                handleMenuLink(player, args, sub.equals("replace"));
                break;

            case "unlink":
                if (args.length < 2) {
                    player.sendMessage("§cUso: /sga unlink [slot]");
                    return true;
                }
                handleMenuUnlink(player, args[1]);
                break;

            default:
                return ShopFileManager.handle(player, sub, args);
        }
        return true;
    }

    private static void handleItemAdd(Player p, String[] args) {
        try {
            String shopId = args[1];
            int slotId = Integer.parseInt(args[2]);
            double buy = Double.parseDouble(args[3]);
            double sell = Double.parseDouble(args[4]);
            int amount = Integer.parseInt(args[5]);

            ItemStack item = p.getInventory().getItemInMainHand();
            if (item == null || item.getType() == Material.AIR) {
                p.sendMessage(ChatUtils.getMessage("error_item_hand"));
                return;
            }

            File f = getShopFile(shopId);
            if (!f.exists()) {
                p.sendMessage(ChatUtils.getMessage("error_shop_file", "%shop%", shopId));
                return;
            }

            FileConfiguration cfg = YamlConfiguration.loadConfiguration(f);
            String root = cfg.contains(shopId + ".items") ? shopId + ".items" : "items";
            clearSlot(cfg, root, slotId);

            // Gera uma chave única baseada no material e tempo
            String key = item.getType().name().toLowerCase() + "_" + System.currentTimeMillis();
            String pth = root + "." + key;
            ItemMeta meta = item.getItemMeta();

            // Configuração base do ShopGUI+
            cfg.set(pth + ".type", "item");
            cfg.set(pth + ".item.material", item.getType().name());
            cfg.set(pth + ".item.quantity", amount);
            cfg.set(pth + ".slot", slotId);
            cfg.set(pth + ".buyPrice", buy);
            cfg.set(pth + ".sellPrice", sell);

            // SALVAMENTO DE SKINS E NBT
            if (meta != null) {
                // Nome e Lore customizados
                if (meta.hasDisplayName()) cfg.set(pth + ".item.name", meta.getDisplayName());
                if (meta.hasLore()) cfg.set(pth + ".item.lore", meta.getLore());
                
                // Lógica de Skin para PLAYER_HEAD
                if (item.getType() == Material.PLAYER_HEAD && meta instanceof SkullMeta skullMeta) {
                    if (skullMeta.getOwnerProfile() != null && skullMeta.getOwnerProfile().getTextures().getSkin() != null) {
                        String skinUrl = skullMeta.getOwnerProfile().getTextures().getSkin().toString();
                        // Gera o Base64 no formato que o SGP entende
                        String b64Skin = GuiManager.generateBase64Skin(skinUrl);
                        cfg.set(pth + ".item.skin", b64Skin);
                    }
                }
            }

            cfg.save(f);
            reloadSGP(p);
            p.sendMessage(ChatUtils.getMessage("item_added", "%shop%", shopId, "%slot%", slotId, "%amount%", amount));
        } catch (Exception e) {
            p.sendMessage("§cErro ao adicionar item: " + e.getMessage());
        }
    }

    private static void handleMenuLink(Player p, String[] args, boolean force) {
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
            reloadSGP(p);
            p.sendMessage(ChatUtils.getMessage("link_success", "%slot%", slot, "%mat%", matName));
        } catch (Exception e) {
            p.sendMessage("§cErro no link: " + e.getMessage());
        }
    }

    private static void handleItemRemove(Player p, String shopId, String slotStr) {
        try {
            int targetSlot = Integer.parseInt(slotStr);
            File f = getShopFile(shopId);
            if (!f.exists()) return;
            
            FileConfiguration cfg = YamlConfiguration.loadConfiguration(f);
            String root = cfg.contains(shopId + ".items") ? shopId + ".items" : "items";
            
            if (clearSlot(cfg, root, targetSlot)) {
                cfg.save(f);
                reloadSGP(p);
                p.sendMessage("§aItem removido com sucesso.");
            }
        } catch (Exception e) {
            p.sendMessage("§cErro.");
        }
    }

    private static void handleMenuUnlink(Player p, String slotStr) {
        try {
            int slot = Integer.parseInt(slotStr);
            File f = new File(Bukkit.getPluginManager().getPlugin("ShopGUIPlus").getDataFolder(), "config.yml");
            FileConfiguration cfg = YamlConfiguration.loadConfiguration(f);
            ConfigurationSection sec = cfg.getConfigurationSection("shopMenuItems");
            
            if (sec != null) {
                for (String k : sec.getKeys(false)) {
                    if (cfg.getInt("shopMenuItems." + k + ".slot") == slot) {
                        cfg.set("shopMenuItems." + k, null);
                        cfg.save(f);
                        reloadSGP(p);
                        p.sendMessage("§aSlot desvinculado.");
                        return;
                    }
                }
            }
        } catch (Exception ignored) {}
    }

    public static File getShopFile(String id) {
        return new File(new File(Bukkit.getPluginManager().getPlugin("ShopGUIPlus").getDataFolder(), "shops"), id + ".yml");
    }

    public static boolean clearSlot(FileConfiguration cfg, String root, int slot) {
        ConfigurationSection sec = cfg.getConfigurationSection(root);
        if (sec == null) return false;
        for (String k : sec.getKeys(false)) {
            if (cfg.getInt(root + "." + k + ".slot") == slot) {
                cfg.set(root + "." + k, null);
                return true;
            }
        }
        return false;
    }

    public static void reloadSGP(Player p) {
        p.performCommand("shopgui reload");
    }
}
