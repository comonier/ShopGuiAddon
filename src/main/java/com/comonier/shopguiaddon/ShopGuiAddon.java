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
            getLogger().severe("ERRO: ShopGUI+ nao encontrado! Desativando Addon...");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        getServer().getPluginManager().registerEvents(this, this);
        
        // REGISTRO CRITICO: Se isso falhar, nenhum comando funciona
        if (null != getCommand("sga")) {
            getCommand("sga").setExecutor(new CommandHandler());
            getLogger().info("Comando /sga registrado com sucesso!");
        } else {
            getLogger().severe("ERRO CRITICO: O comando 'sga' nao foi definido no plugin.yml!");
        }
    }

    @EventHandler
    public void onShopGUIPlusPostEnable(ShopGUIPlusPostEnableEvent event) {
        getServer().getPluginManager().registerEvents(new EventListener(), this);
        getLogger().info("ShopGUI+ detectado. Eventos de edicao ativados.");
    }

    public static ShopGuiAddon getInstance() {
        return instance;
    }
}
