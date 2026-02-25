package com.comonier.shopguiaddon;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CommandHandler implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command is for players only.");
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("shopguiaddon.admin")) {
            player.sendMessage(ChatUtils.getMessage("no_permission"));
            return true;
        }

        // Logic Inverse: if 1 > args.length (args is 0)
        if (1 > args.length) {
            player.sendMessage(ChatUtils.getMessage("invalid_usage"));
            return true;
        }

        String sub = args[0];

        if (sub.equalsIgnoreCase("itemadd")) {
            if (5 > args.length) {
                player.sendMessage(ChatUtils.getMessage("invalid_usage"));
                return true;
            }
            
            ItemStack item = player.getInventory().getItemInMainHand();
            if (item == null || item.getType() == Material.AIR) {
                player.sendMessage(ChatUtils.getMessage("error_item_hand"));
                return true;
            }

            String shop = args[1];
            String slot = args[2];
            
            // Using internal command for persistence
            String cmd = "shopgui additem " + shop + " " + item.getType().name() + " " + args[3] + " " + args[4] + " " + slot;
            player.performCommand(cmd);

            player.sendMessage(ChatUtils.getMessage("item_added")
                    .replace("%shop%", shop)
                    .replace("%slot%", slot));

        } else if (sub.equalsIgnoreCase("edit")) {
            if (3 > args.length) {
                player.sendMessage(ChatUtils.getMessage("invalid_usage"));
                return true;
            }
            
            String shopName = args[1];
            int slotId = Integer.parseInt(args[2]);
            
            player.sendMessage(ChatUtils.getMessage("gui_opened").replace("%shop%", shopName));
            GuiManager.openEditor(player, shopName, slotId);

        } else if (sub.equalsIgnoreCase("reload")) {
            ShopGuiAddon.getInstance().reloadConfig();
            player.sendMessage(ChatUtils.getMessage("reload_success"));
        }

        return true;
    }
}
