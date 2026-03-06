package com.comonier.shopguiaddon;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import java.io.File;
public class ShopItemEditor {
    public static void handleItemAdd(Player p, String[] args) {
        try {
            String shopId = args[1];
            int slotId = Integer.parseInt(args[2]);
            double buy = Double.parseDouble(args[3]);
            double sell = Double.parseDouble(args[4]);
            int amount = Integer.parseInt(args[5]);
            int page = (args.length >= 7) ? Integer.parseInt(args[6]) : 1;
            ItemStack item = p.getInventory().getItemInMainHand();
            if (item == null || item.getType() == Material.AIR) {
                p.sendMessage(ChatUtils.getMessage("error_item_hand"));
                return;
            }
            File f = ShopOperations.getShopFile(shopId);
            if (f.exists()) {
                FileConfiguration cfg = YamlConfiguration.loadConfiguration(f);
                String root = cfg.contains(shopId + ".items") ? shopId + ".items" : "items";
                ShopOperations.clearSlot(cfg, root, slotId, page);
                String key = item.getType().name().toLowerCase() + "_" + System.currentTimeMillis();
                String pth = root + "." + key;
                ItemMeta meta = item.getItemMeta();
                cfg.set(pth + ".type", "item");
                cfg.set(pth + ".item.material", item.getType().name());
                cfg.set(pth + ".item.quantity", amount);
                cfg.set(pth + ".slot", slotId);
                cfg.set(pth + ".page", page);
                cfg.set(pth + ".buyPrice", buy);
                cfg.set(pth + ".sellPrice", sell);
                if (meta != null) {
                    if (meta.hasDisplayName()) cfg.set(pth + ".item.name", meta.getDisplayName());
                    if (meta.hasLore()) cfg.set(pth + ".item.lore", meta.getLore());
                    if (item.getType() == Material.PLAYER_HEAD && meta instanceof SkullMeta sm && sm.getOwnerProfile() != null && sm.getOwnerProfile().getTextures().getSkin() != null) {
                        cfg.set(pth + ".item.skin", GuiManager.generateBase64Skin(sm.getOwnerProfile().getTextures().getSkin().toString()));
                    }
                }
                cfg.save(f);
                ShopOperations.reloadSGP(p);
                p.sendMessage(ChatUtils.getMessage("item_added", "%shop%", shopId, "%slot%", slotId, "%amount%", amount));
            } else { p.sendMessage(ChatUtils.getMessage("error_shop_file", "%shop%", shopId)); }
        } catch (Exception e) { p.sendMessage(ChatUtils.getMessage("error_invalid_number")); }
    }
    public static void handleItemRemove(Player p, String[] args) {
        try {
            String shopId = args[1];
            int targetSlot = Integer.parseInt(args[2]);
            int page = (args.length >= 4) ? Integer.parseInt(args[3]) : 1;
            File f = ShopOperations.getShopFile(shopId);
            if (f.exists()) {
                FileConfiguration cfg = YamlConfiguration.loadConfiguration(f);
                String root = cfg.contains(shopId + ".items") ? shopId + ".items" : "items";
                if (ShopOperations.clearSlot(cfg, root, targetSlot, page)) {
                    cfg.save(f);
                    ShopOperations.reloadSGP(p);
                    p.sendMessage(ChatUtils.getMessage("item_removed"));
                }
            } else { p.sendMessage(ChatUtils.getMessage("error_shop_file", "%shop%", shopId)); }
        } catch (Exception e) { p.sendMessage(ChatUtils.getMessage("error_invalid_number")); }
    }
}
