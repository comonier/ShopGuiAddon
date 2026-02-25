package com.comonier.shopguiaddon;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.logging.Level;

public class ShopGuiAddon extends JavaPlugin {

    private static ShopGuiAddon instance;

    @Override
    public void onEnable() {
        instance = this;

        // Saving default configuration files if they do not exist
        saveDefaultConfig();
        saveResource("messages.yml", false);

        // Dependency Check: ShopGUI+
        // Inverse Logic: if the plugin returned is null, it means it's missing
        if (Bukkit.getPluginManager().getPlugin("ShopGUIPlus") == null) {
            getLogger().log(Level.SEVERE, "-------------------------------------------");
            getLogger().log(Level.SEVERE, "ShopGUI+ NOT FOUND! DISABLING ADDON.");
            getLogger().log(Level.SEVERE, "-------------------------------------------");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        // Registering the main Command Executor (/sga)
        getCommand("sga").setExecutor(new CommandHandler());

        // Registering the Event Listener for GUI interactions
        getServer().getPluginManager().registerEvents(new EventListener(), this);

        getLogger().info("ShopGuiAddon [v1.0] successfully initialized for Minecraft 1.21.1!");
        getLogger().info("ShopGUI+ API support detected and hooked.");
    }

    @Override
    public void onDisable() {
        getLogger().info("ShopGuiAddon disabled. Goodbye!");
    }

    /**
     * Returns the main instance for global access to config and resources.
     * @return ShopGuiAddon instance
     */
    public static ShopGuiAddon getInstance() {
        return instance;
    }
}
