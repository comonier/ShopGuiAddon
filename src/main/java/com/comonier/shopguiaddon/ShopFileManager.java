package com.comonier.shopguiaddon;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ShopFileManager {

    public static boolean handle(Player player, String sub, String[] args) {
        switch (sub.toLowerCase()) {
            case "list":
                handleShopList(player);
                break;
            case "shopcreate":
                if (args.length < 2) {
                    player.sendMessage("§cUso: /sga shopcreate [nome]");
                    return true;
                }
                handleShopCreate(player, args[1].toLowerCase());
                break;
            case "shopremove":
                if (args.length < 2) {
                    player.sendMessage("§cUso: /sga shopremove [nome]");
                    return true;
                }
                handleShopRemove(player, args[1].toLowerCase());
                break;
        }
        return true;
    }

    private static void handleShopList(Player p) {
        File folder = new File(Bukkit.getPluginManager().getPlugin("ShopGUIPlus").getDataFolder(), "shops");
        FileConfiguration config = ShopGuiAddon.getInstance().getConfig();
        
        // Lógica Inversa: Validação de diretório
        if (!folder.exists() || !folder.isDirectory()) {
            p.sendMessage(ChatUtils.getMessage("error_shop_file", "%shop%", "folder"));
            return;
        }

        File[] files = folder.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files == null || files.length == 0) {
            p.sendMessage("§cNenhuma loja encontrada em /shops.");
            return;
        }

        // Puxa o estilo visual da config.yml
        String header = config.getString("list_settings.header", "&b&lLojas Disponíveis:");
        String prefix = config.getString("list_settings.prefix", " &f");

        p.sendMessage(ChatUtils.color(header));

        // CONCATENAÇÃO EM LINHA: Cria uma lista de nomes lado a lado
        StringBuilder listBuilder = new StringBuilder();
        for (int i = 0; i < files.length; i++) {
            String shopName = files[i].getName().replace(".yml", "");
            listBuilder.append(ChatUtils.color(prefix)).append(shopName);
            
            // Adiciona vírgula se não for o último item
            if (i < files.length - 1) {
                listBuilder.append("§7, ");
            }
        }

        p.sendMessage(listBuilder.toString());
    }

    private static void handleShopCreate(Player p, String id) {
        try {
            File f = ShopOperations.getShopFile(id);
            if (f.exists()) {
                p.sendMessage("§cERRO: A loja '" + id + "' já existe!");
                return;
            }
            
            FileConfiguration cfg = new YamlConfiguration();
            cfg.set(id + ".name", "&bLoja " + id);
            cfg.set(id + ".size", 54);
            cfg.set(id + ".items", new HashMap<>());
            
            cfg.save(f);
            
            SgaCommand.updateTabCache();
            ShopOperations.reloadSGP(p);
            
            p.sendMessage("§aSucesso! Loja '" + id + "' criada. Use /sga link para adicioná-la ao menu principal.");
        } catch (Exception e) {
            p.sendMessage("§cERRO ao criar arquivo da loja: " + e.getMessage());
        }
    }

    private static void handleShopRemove(Player p, String id) {
        File f = ShopOperations.getShopFile(id);
        if (f.exists()) {
            if (f.delete()) {
                SgaCommand.updateTabCache();
                ShopOperations.reloadSGP(p);
                p.sendMessage("§aArquivo da loja '" + id + "' deletado com sucesso.");
            } else {
                p.sendMessage("§cERRO: Não foi possível deletar o arquivo.");
            }
        } else {
            p.sendMessage(ChatUtils.getMessage("shop_not_found", "%shop%", id));
        }
    }
}
