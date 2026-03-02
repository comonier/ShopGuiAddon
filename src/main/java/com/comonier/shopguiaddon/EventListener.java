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

    // --- NOVO: BLOQUEIA O ARRASTE DOS ITENS ---
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryDrag(InventoryDragEvent event) {
        String title = ChatColor.stripColor(event.getView().getTitle());
        if (title.contains("Editing:") || title.contains("Editando:")) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player p)) return;

        String rawTitle = event.getView().getTitle();
        String cleanTitle = ChatColor.stripColor(rawTitle);
        
        // Verificação expandida para aceitar tanto inglês quanto português
        if (!cleanTitle.contains("Editing:") && !cleanTitle.contains("Editando:")) return;
        
        // CANCELAMENTO GLOBAL: Isso impede de "pegar" ou "arrastar" qualquer item
        event.setCancelled(true);
        
        if (event.getClickedInventory() == null) return;
        
        // Bloqueia interações com o inventário do próprio jogador enquanto o editor está aberto
        if (event.getClickedInventory().equals(p.getInventory())) return;

        int slot = event.getRawSlot();
        if (slot < 0 || slot > 53) return;

        // Aceita apenas cliques que devem executar funções
        ClickType click = event.getClick();
        if (click != ClickType.LEFT && click != ClickType.RIGHT) return;

        try {
            // Extração flexível de dados
            String dataOnly = cleanTitle.replace("Editing:", "").replace("Editando:", "").replace("Slot:", "").trim();
            String[] pts = dataOnly.split("\\|");
            
            if (pts.length < 2) return;
            
            final String shopId = pts[0].trim();
            final int targetSlot = Integer.parseInt(pts[1].trim());

            // 1. INTERRUPTORES DE MODO (Vidros Brancos/Pretos)
            if (slot == 26 || slot == 35 || slot == 44) {
                handleToggle(event, slot, p);
                p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 0.6f, 1.2f);
                return;
            }

            // 2. NAVEGAÇÃO E RECARREGAMENTO
            if (slot == 3 || slot == 5 || slot == 52 || slot == 53) {
                NavigationHandler.handle(p, shopId, targetSlot, slot);
            } 
            // 3. AJUSTES DE PREÇO
            else if (slot >= 18 && slot <= 34) {
                PriceAdjustmentHandler.handle(event, p, shopId, targetSlot, slot);
            }
            // 4. AJUSTES DE QUANTIDADE
            else if (slot >= 36 && slot <= 43) {
                QuantityAdjustmentHandler.handle(event, p, shopId, targetSlot, slot);
            }

        } catch (Exception e) {
            // Silencia erros de parse para evitar spam no console
        }
    }

    private void handleToggle(InventoryClickEvent event, int slot, Player p) {
        ItemStack item = event.getCurrentItem();
        if (item == null || item.getType() == Material.AIR) return;
        
        Material nextMat;
        String nextName;
        
        if (item.getType() == Material.WHITE_STAINED_GLASS_PANE) {
            nextMat = Material.BLACK_STAINED_GLASS_PANE;
            nextName = "&c&lMODO: SUBTRAIR (-)";
        } else {
            nextMat = Material.WHITE_STAINED_GLASS_PANE;
            nextName = "&a&lMODO: ADICIONAR (+)";
        }
        
        ItemStack newItem = GuiManager.createItemWithLore(
            nextMat, 
            nextName, 
            Arrays.asList("&7Clique para alternar o modo.")
        );
        
        event.getInventory().setItem(slot, newItem);
        p.updateInventory(); // Força a atualização visual para o jogador
    }
}
