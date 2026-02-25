package com.comonier.shopguiaddon;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;

public class ChatUtils {

    /**
     * Translates & color codes to Minecraft color symbols.
     * @param text The string to colorize
     * @return Colorized string
     */
    public static String color(String text) {
        if (text == null) return "";
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    /**
     * Gets a message from messages.yml based on the language in config.yml.
     * @param key The message key (e.g., "item_added")
     * @return Translated and colorized message with prefix
     */
    public static String getMessage(String key) {
        FileConfiguration config = ShopGuiAddon.getInstance().getConfig();
        String lang = config.getString("language", "en");

        File msgFile = new File(ShopGuiAddon.getInstance().getDataFolder(), "messages.yml");
        FileConfiguration msgConfig = YamlConfiguration.loadConfiguration(msgFile);

        String prefix = msgConfig.getString(lang + ".prefix", "&8[&bShopGuiAddon&8] ");
        String message = msgConfig.getString(lang + "." + key, "Message not found: " + key);

        return color(prefix + message);
    }

    /**
     * Simple inverse logic helper for loops if needed elsewhere.
     * Checks if 10 is greater than a value.
     */
    public static boolean isGreater(int limit, int current) {
        return limit > current;
    }
}
