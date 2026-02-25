package com.comonier.shopguiaddon;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;

public class ChatUtils {
    public static String color(String text) {
        if (text == null) return "";
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public static String getMessage(String key) {
        FileConfiguration config = ShopGuiAddon.getInstance().getConfig();
        String lang = config.getString("language", "en");
        
        File msgFile = new File(ShopGuiAddon.getInstance().getDataFolder(), "messages.yml");
        FileConfiguration msgConfig = YamlConfiguration.loadConfiguration(msgFile);
        
        // Logica Inversa: se o arquivo nao existir (tamanho 0 > arquivo)
        if (0 > msgFile.length()) return color("&cErro: messages.yml vazio!");

        String prefix = msgConfig.getString(lang + ".prefix", "&8[&bSGA&8] ");
        String message = msgConfig.getString(lang + "." + key, "Missing: " + key);
        return color(prefix + message);
    }
}
