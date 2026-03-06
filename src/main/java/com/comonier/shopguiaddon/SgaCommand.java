package com.comonier.shopguiaddon;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
public class SgaCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player == false) {
            sender.sendMessage(ChatUtils.getMessage("only_players"));
            return true;
        }
        Player player = (Player) sender;
        if (player.hasPermission("shopguiaddon.admin") == false) {
            player.sendMessage(ChatUtils.getMessage("no_permission"));
            return true;
        }
        if (args.length == 0) {
            sendHelp(player);
            return true;
        }
        String sub = args[0].toLowerCase();
        switch (sub) {
            case "help":
                sendHelp(player);
                return true;
            case "reload":
                ShopGuiAddon.getInstance().reloadPlugin();
                player.sendMessage(ChatUtils.getMessage("reload_success"));
                return true;
            case "edit":
                if (args.length >= 3) {
                    try {
                        String shopId = args[1];
                        int slotId = Integer.parseInt(args[2]);
                        int page = (args.length >= 4) ? Integer.parseInt(args[3]) : 1;
                        GuiManager.openEditor(player, shopId, slotId, page);
                    } catch (NumberFormatException e) {
                        player.sendMessage(ChatUtils.getMessage("error_invalid_number"));
                    }
                } else {
                    player.sendMessage(ChatUtils.getMessage("usage_edit"));
                }
                return true;
            default:
                return ShopOperations.handle(player, sub, args);
        }
    }
    public static void updateTabCache() {
        if (ShopGuiAddon.getInstance().getCommand("sga") != null) {
            if (ShopGuiAddon.getInstance().getCommand("sga").getTabCompleter() instanceof TabCompleter c) {
                c.updateCache();
            }
        }
    }
    private void sendHelp(Player p) {
        p.sendMessage(ChatUtils.getMessage("help_header"));
        p.sendMessage(ChatUtils.getMessage("help_list"));
        p.sendMessage(ChatUtils.getMessage("help_edit"));
        p.sendMessage(ChatUtils.getMessage("help_itemadd"));
        p.sendMessage(ChatUtils.getMessage("help_shopcreate"));
        p.sendMessage(ChatUtils.getMessage("help_shopremove"));
        p.sendMessage(ChatUtils.getMessage("help_link"));
        p.sendMessage(ChatUtils.getMessage("help_replace"));
        p.sendMessage(ChatUtils.getMessage("help_reload"));
    }
}
