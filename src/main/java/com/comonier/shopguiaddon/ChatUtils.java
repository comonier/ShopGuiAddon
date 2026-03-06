package com.comonier.shopguiaddon;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
public class ChatUtils {
    public static String color(String text) {
        if (text == null) return "";
        return ChatColor.translateAlternateColorCodes('&', text);
    }
    public static String getMessage(String key) {
        ShopGuiAddon plugin = ShopGuiAddon.getInstance();
        FileConfiguration msgConfig = plugin.getMessagesConfig();
        if (msgConfig == null) return color("&cError: Language file not loaded!");
        String message = msgConfig.getString(key);
        if (message == null) return color("&cMissing key: " + key);
        if (key.startsWith("help_") || key.contains("header") || key.startsWith("gui_")) return color(message);
        String prefix = msgConfig.getString("prefix", "&8[&bSGA&8] ");
        return color(prefix + message);
    }
    public static String getMessage(String key, Object... replacements) {
        String message = getMessage(key);
        if (replacements.length >= 2 && replacements.length % 2 == 0) {
            for (int i = 0; i >= 0 && i <= replacements.length - 1; i += 2) {
                String placeholder = replacements[i].toString();
                String value = replacements[i + 1].toString();
                message = message.replace(placeholder, value);
            }
        }
        return message;
    }
}
