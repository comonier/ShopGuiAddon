package com.comonier.shopguiaddon;

import net.brcdev.shopgui.event.ShopGUIPlusPostEnableEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class ShopGuiAddon extends JavaPlugin implements Listener {
    private static ShopGuiAddon instance;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        saveResource("messages.yml", false);

        if (null == Bukkit.getPluginManager().getPlugin("ShopGUIPlus")) {
            getLogger().severe("ShopGUI+ nao encontrado! Desativando...");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        getServer().getPluginManager().registerEvents(this, this);
        
        if (null != getCommand("sga")) {
            getCommand("sga").setExecutor(new CommandHandler());
            // REGISTRO DO AUTOCOMPLETAR
            getCommand("sga").setTabCompleter(new TabCompleter());
            getLogger().info("Comandos e TabCompleter registrados!");
        }
    }

    @EventHandler
    public void onShopGUIPlusPostEnable(ShopGUIPlusPostEnableEvent event) {
        getServer().getPluginManager().registerEvents(new EventListener(), this);
        getLogger().info("Addon 1.1 pronto para uso!");
    }

    public static ShopGuiAddon getInstance() {
        return instance;
    }
}
