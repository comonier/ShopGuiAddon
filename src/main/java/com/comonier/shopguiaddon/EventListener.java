package com.comonier.shopguiaddon;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;
import java.util.Arrays;
public class EventListener implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryDrag(InventoryDragEvent event) {
        String title = ChatColor.stripColor(event.getView().getTitle());
        if (title.contains("Editing:") || title.contains("Editando:")) {
            event.setCancelled(true);
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player == false) return;
        Player p = (Player) event.getWhoClicked();
        String rawTitle = event.getView().getTitle();
        String cleanTitle = ChatColor.stripColor(rawTitle);
        if (cleanTitle.contains("Editing:") == false && cleanTitle.contains("Editando:") == false) return;
        event.setCancelled(true);
        if (event.getClickedInventory() == null) return;
        if (event.getClickedInventory().equals(p.getInventory())) return;
        int slot = event.getRawSlot();
        if (slot >= 0 && slot <= 53) {
            ClickType click = event.getClick();
            if (click == ClickType.LEFT || click == ClickType.RIGHT) {
                try {
                    String dataOnly = cleanTitle.replace("Editing:", "").replace("Editando:", "").replace("Slot:", "").replace("P:", "").trim();
                    String[] pts = dataOnly.split("\\|");
                    if (pts.length >= 3) {
                        final String shopId = pts[0].trim();
                        final int targetSlot = Integer.parseInt(pts[1].trim());
                        final int page = Integer.parseInt(pts[2].trim());
                        if (slot == 26 || slot == 35 || slot == 44) {
                            handleToggle(event, slot, p);
                            p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 0.6f, 1.2f);
                            return;
                        }
                        if (slot == 3 || slot == 5 || slot == 52 || slot == 53) {
                            NavigationHandler.handle(p, shopId, targetSlot, page, slot);
                        } else if (slot >= 18 && slot <= 34) {
                            PriceAdjustmentHandler.handle(event, p, shopId, targetSlot, page, slot);
                        } else if (slot >= 36 && slot <= 43) {
                            QuantityAdjustmentHandler.handle(event, p, shopId, targetSlot, page, slot);
                        }
                    }
                } catch (Exception e) {}
            }
        }
    }
    private void handleToggle(InventoryClickEvent event, int slot, Player p) {
        ItemStack item = event.getCurrentItem();
        if (item == null || item.getType() == Material.AIR) return;
        Material nextMat;
        String nextName;
        if (item.getType() == Material.WHITE_STAINED_GLASS_PANE) {
            nextMat = Material.BLACK_STAINED_GLASS_PANE;
            nextName = ChatUtils.getMessage("gui_mode_sub");
        } else {
            nextMat = Material.WHITE_STAINED_GLASS_PANE;
            nextName = ChatUtils.getMessage("gui_mode_add");
        }
        ItemStack newItem = GuiManager.createItemWithLore(nextMat, nextName, Arrays.asList(ChatUtils.getMessage("gui_mode_lore")));
        event.getInventory().setItem(slot, newItem);
        p.updateInventory();
    }
}
