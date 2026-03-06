package com.comonier.shopguiaddon;
import net.brcdev.shopgui.event.ShopGUIPlusPostEnableEvent;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.File;
public class ShopGuiAddon extends JavaPlugin implements Listener {
    private static ShopGuiAddon instance;
    private FileConfiguration messagesConfig;
    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        saveResource("messages_en.yml", false);
        saveResource("messages_pt.yml", false);
        loadMessages();
        if (Bukkit.getPluginManager().getPlugin("ShopGUIPlus") == null) {
            getLogger().severe(ChatUtils.getMessage("error_dependency"));
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        if (getCommand("sga") != null) {
            getCommand("sga").setExecutor(new SgaCommand());
            getCommand("sga").setTabCompleter(new TabCompleter());
        }
        getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("SGA v" + getDescription().getVersion() + " loaded.");
    }
    @EventHandler
    public void onShopGUIPlusPostEnable(ShopGUIPlusPostEnableEvent event) {
        getServer().getPluginManager().registerEvents(new EventListener(), this);
    }
    public void loadMessages() {
        String lang = getConfig().getString("language", "en");
        File msgFile = new File(getDataFolder(), "messages_" + lang + ".yml");
        if (msgFile.exists() == false) {
            saveResource("messages_en.yml", false);
            msgFile = new File(getDataFolder(), "messages_en.yml");
        }
        this.messagesConfig = YamlConfiguration.loadConfiguration(msgFile);
    }
    public void reloadPlugin() {
        reloadConfig();
        loadMessages();
        SgaCommand.updateTabCache();
    }
    public FileConfiguration getMessagesConfig() {
        return messagesConfig;
    }
    public static ShopGuiAddon getInstance() {
        return instance;
    }
}
