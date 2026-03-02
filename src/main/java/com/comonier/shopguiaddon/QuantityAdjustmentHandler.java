package com.comonier.shopguiaddon;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

public class QuantityAdjustmentHandler {

    // Trava de tick baseada no tempo do mundo para evitar que cliques fantasmas processem em dobro
    private static final HashMap<UUID, Long> lastQuantityTick = new HashMap<>();

    public static void handle(InventoryClickEvent event, Player p, String shopId, int targetSlot, int clicked) {
        // Lógica Inversa: bloqueia se já houve um clique processado neste mesmo tick
        long currentTick = p.getWorld().getFullTime();
        if (lastQuantityTick.getOrDefault(p.getUniqueId(), -1L) == currentTick) {
            return;
        }
        lastQuantityTick.put(p.getUniqueId(), currentTick);

        File f = ShopOperations.getShopFile(shopId);
        if (!f.exists()) return;
        
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(f);
        
        // Identifica o caminho raiz dos itens no YAML do ShopGUI+
        String root = cfg.contains(shopId + ".items") ? shopId + ".items" : "items";
        String key = findKey(cfg, root, targetSlot);
        
        // Ignora cliques se o slot selecionado na navegação não existir no arquivo da loja
        if (key == null) return;

        String path = root + "." + key;
        
        // FILEIRA DE QUANTIDADE (Slots 36 a 43 no menu de 54 slots)
        if (clicked >= 36 && clicked <= 43) {
            int[] amounts = {1, 2, 4, 8, 16, 32, 64, 100};
            int adjustment = amounts[clicked - 36];
            
            // Verifica o estado do interruptor de modo (Slot 44) para somar ou subtrair
            boolean isSub = isSubtractMode(event, 44);
            int currentQuant = cfg.getInt(path + ".item.quantity", 1);
            
            // Processa a nova quantidade respeitando os limites de 1 a 1000
            int newQuant = PriceCalculator.calculateQuantity(currentQuant, adjustment, isSub);
            cfg.set(path + ".item.quantity", newQuant);

            try { 
                cfg.save(f); 
                // Atualiza o item central do visor com o novo valor de quantidade salvo
                GuiManager.updateVisor(event.getInventory(), cfg, root, key, targetSlot);
                p.updateInventory();
            } catch (IOException e) { 
                p.sendMessage(ChatUtils.getMessage("error_shop_file", "%shop%", shopId));
                e.printStackTrace();
            }
        }
    }

    /**
     * Localiza a chave (ID) do item dentro do arquivo da loja baseado no slot.
     */
    private static String findKey(FileConfiguration cfg, String root, int slot) {
        if (cfg.getConfigurationSection(root) == null) return null;
        for (String k : cfg.getConfigurationSection(root).getKeys(false)) {
            if (cfg.getInt(root + "." + k + ".slot") == slot) return k;
        }
        return null;
    }

    /**
     * Identifica se a fileira de quantidade está configurada para subtrair.
     */
    private static boolean isSubtractMode(InventoryClickEvent e, int slot) {
        ItemStack item = e.getInventory().getItem(slot);
        if (item == null) return false;
        
        Material m = item.getType();
        return (m == Material.BLACK_STAINED_GLASS_PANE || m == Material.RED_STAINED_GLASS_PANE);
    }
}
