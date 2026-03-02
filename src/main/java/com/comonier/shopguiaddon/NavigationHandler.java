package com.comonier.shopguiaddon;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import java.util.HashMap;
import java.util.UUID;

public class NavigationHandler {

    private static final HashMap<UUID, Long> lastNavTick = new HashMap<>();
    private static final HashMap<UUID, Long> reloadCooldown = new HashMap<>();

    public static void handle(Player p, String shopId, int targetSlot, int clicked) {
        long currentTick = p.getWorld().getFullTime();
        long now = System.currentTimeMillis();

        // 1. NAVEGAÇÃO ENTRE SLOTS (Anterior: 3 | Próximo: 5)
        if (clicked == 3 || clicked == 5) {
            // Trava de tick para evitar que o clique registre múltiplas vezes
            if (lastNavTick.getOrDefault(p.getUniqueId(), -1L) == currentTick) return;
            lastNavTick.put(p.getUniqueId(), currentTick);

            int nextSlot = (clicked == 5) ? (targetSlot + 1) : (targetSlot - 1);
            
            // Limites de segurança do inventário (0 a 53)
            if (nextSlot < 0) nextSlot = 0;
            if (nextSlot > 53) nextSlot = 53;
            
            final int finalSlot = nextSlot;
            
            // Agendamento de 1 tick para processar a troca de inventário de forma segura
            Bukkit.getScheduler().runTaskLater(ShopGuiAddon.getInstance(), () -> {
                if (p.isOnline()) {
                    GuiManager.openEditor(p, shopId, finalSlot);
                }
            }, 1L);
            return;
        }

        // 2. RELOAD DO SHOPGUI+ (Slot 53)
        if (clicked == 53) {
            if (isOnCooldown(p.getUniqueId(), now)) {
                p.sendMessage(ChatUtils.color("&c&lSGA &8» &7Aguarde para recarregar o ShopGUI+ novamente!"));
                return;
            }
            
            reloadCooldown.put(p.getUniqueId(), now);
            p.performCommand("shopgui reload");
            p.sendMessage(ChatUtils.getMessage("reload_success"));
            return;
        }

        // 3. RELOAD DO SHOPGUIADDON (Slot 52)
        if (clicked == 52) {
            if (isOnCooldown(p.getUniqueId(), now)) {
                p.sendMessage(ChatUtils.color("&c&lSGA &8» &7Aguarde para recarregar as configurações!"));
                return;
            }
            
            reloadCooldown.put(p.getUniqueId(), now);
            ShopGuiAddon.getInstance().reloadPlugin();
            
            p.sendMessage(ChatUtils.getMessage("reload_success"));
            
            // Reabre o editor para aplicar mudanças visuais da config.yml (como cores e nomes)
            Bukkit.getScheduler().runTaskLater(ShopGuiAddon.getInstance(), () -> {
                if (p.isOnline()) {
                    GuiManager.openEditor(p, shopId, targetSlot);
                }
            }, 1L);
        }
    }

    /**
     * Verifica se o jogador ainda está no tempo de espera de 5 segundos.
     */
    private static boolean isOnCooldown(UUID uuid, long now) {
        return reloadCooldown.containsKey(uuid) && (now - reloadCooldown.get(uuid)) < 5000;
    }
}
