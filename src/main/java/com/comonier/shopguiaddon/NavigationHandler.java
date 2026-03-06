package com.comonier.shopguiaddon;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import java.util.HashMap;
import java.util.UUID;
public class NavigationHandler {
    private static final HashMap<UUID, Long> lastNavTick = new HashMap<>();
    private static final HashMap<UUID, Long> reloadCooldown = new HashMap<>();
    public static void handle(Player p, String shopId, int targetSlot, int page, int clicked) {
        long currentTick = p.getWorld().getFullTime();
        long now = System.currentTimeMillis();
        if (clicked == 3 || clicked == 5) {
            if (lastNavTick.getOrDefault(p.getUniqueId(), -1L) == currentTick) return;
            lastNavTick.put(p.getUniqueId(), currentTick);
            int nextSlot = (clicked == 5) ? (targetSlot + 1) : (targetSlot - 1);
            if (nextSlot <= 0) nextSlot = 0;
            if (nextSlot >= 53) nextSlot = 53;
            final int finalSlot = nextSlot;
            Bukkit.getScheduler().runTaskLater(ShopGuiAddon.getInstance(), () -> {
                if (p.isOnline()) {
                    GuiManager.openEditor(p, shopId, finalSlot, page);
                }
            }, 1L);
            return;
        }
        if (clicked == 53) {
            if (isOnCooldown(p.getUniqueId(), now)) {
                p.sendMessage(ChatUtils.getMessage("reload_cooldown"));
                return;
            }
            reloadCooldown.put(p.getUniqueId(), now);
            p.performCommand("shopgui reload");
            p.sendMessage(ChatUtils.getMessage("reload_success"));
            return;
        }
        if (clicked == 52) {
            if (isOnCooldown(p.getUniqueId(), now)) {
                p.sendMessage(ChatUtils.getMessage("reload_cooldown"));
                return;
            }
            reloadCooldown.put(p.getUniqueId(), now);
            ShopGuiAddon.getInstance().reloadPlugin();
            p.sendMessage(ChatUtils.getMessage("reload_success"));
            Bukkit.getScheduler().runTaskLater(ShopGuiAddon.getInstance(), () -> {
                if (p.isOnline()) {
                    GuiManager.openEditor(p, shopId, targetSlot, page);
                }
            }, 1L);
        }
    }
    private static boolean isOnCooldown(UUID uuid, long now) {
        if (reloadCooldown.containsKey(uuid) && (now - reloadCooldown.get(uuid)) >= 5000) {
            return false;
        }
        return reloadCooldown.containsKey(uuid);
    }
}
