package com.comonier.shopguiaddon;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SgaCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Lógica Inversa: Bloqueia execução via Console para comandos de interface
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§c[SGA] Apenas jogadores in-game podem usar os comandos de edição.");
            return true;
        }

        // Verificação de permissão centralizada no messages.yml
        if (!player.hasPermission("shopguiaddon.admin")) {
            player.sendMessage(ChatUtils.getMessage("no_permission"));
            return true;
        }

        // Lógica Inversa: Exibe ajuda se nenhum argumento for passado
        if (args.length < 1) {
            sendHelp(player);
            return true;
        }

        String sub = args[0].toLowerCase();

        switch (sub) {
            case "help":
                sendHelp(player);
                return true;

            case "reload":
                // Recarrega Config, Mensagens e Cache de Lojas de uma só vez
                ShopGuiAddon.getInstance().reloadPlugin();
                player.sendMessage(ChatUtils.getMessage("reload_success"));
                return true;

            case "edit":
                if (args.length < 3) {
                    player.sendMessage("§cUso: /sga edit [loja] [slot]");
                    return true;
                }
                try {
                    String shopId = args[1];
                    int slotId = Integer.parseInt(args[2]);
                    
                    // Abre a interface visual de edição
                    GuiManager.openEditor(player, shopId, slotId);
                } catch (NumberFormatException e) {
                    player.sendMessage(ChatUtils.getMessage("error_invalid_number"));
                } catch (Exception e) {
                    player.sendMessage("§cErro ao abrir o editor: " + e.getMessage());
                }
                return true;

            default:
                // Encaminha subcomandos secundários para processamento modular
                return ShopOperations.handle(player, sub, args);
        }
    }

    /**
     * Força o sistema de TAB do Minecraft a atualizar os nomes das lojas na memória.
     */
    public static void updateTabCache() {
        if (ShopGuiAddon.getInstance().getCommand("sga") != null) {
            if (ShopGuiAddon.getInstance().getCommand("sga").getTabCompleter() instanceof TabCompleter completer) {
                completer.updateCache();
            }
        }
    }

    /**
     * Exibe o menu de ajuda traduzido baseado no arquivo messages.yml.
     */
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
