package com.comonier.shopguiaddon;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

public class ChatUtils {

    /**
     * Aplica cores do padrão '&' ao texto.
     */
    public static String color(String text) {
        if (text == null) return "";
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    /**
     * Retorna uma mensagem traduzida do messages.yml com o prefixo.
     */
    public static String getMessage(String key) {
        ShopGuiAddon plugin = ShopGuiAddon.getInstance();
        FileConfiguration config = plugin.getConfig();
        FileConfiguration msgConfig = plugin.getMessagesConfig();
        
        String lang = config.getString("language", "pt");

        // Lógica Inversa: se as mensagens não foram carregadas na memória
        if (msgConfig == null) {
            return color("&cErro: messages.yml não carregado!");
        }

        String prefix = msgConfig.getString(lang + ".prefix", "&8[&bSGA&8] ");
        String message = msgConfig.getString(lang + "." + key);

        // Debug simples caso a chave não exista no arquivo
        if (message == null) return color(prefix + "&cMissing: " + key);

        // Limpeza de prefixo para menus de ajuda e cabeçalhos
        if (key.startsWith("help_") || key.contains("header")) {
            return color(message);
        }

        return color(prefix + message);
    }

    /**
     * Retorna uma mensagem traduzida substituindo placeholders dinamicamente.
     * Exemplo: getMessage("replace_success", "%slot%", 10, "%shop%", "pedra")
     */
    public static String getMessage(String key, Object... replacements) {
        String message = getMessage(key);
        
        if (replacements.length > 0 && replacements.length % 2 == 0) {
            for (int i = 0; i < replacements.length; i += 2) {
                String placeholder = replacements[i].toString();
                String value = replacements[i + 1].toString();
                message = message.replace(placeholder, value);
            }
        }
        return message;
    }
}
