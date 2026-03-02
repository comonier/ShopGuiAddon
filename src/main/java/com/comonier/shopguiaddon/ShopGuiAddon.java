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
        
        // 1. Inicialização de Arquivos
        saveDefaultConfig();
        saveResource("messages.yml", false);
        loadMessages();

        // 2. Verificação de Dependência Crítica
        if (Bukkit.getPluginManager().getPlugin("ShopGUIPlus") == null) {
            getLogger().severe("ShopGUI+ não encontrado! O ShopGuiAddon requer este plugin para funcionar.");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        // 3. Registro de Comandos e TabCompleter
        if (getCommand("sga") != null) {
            getCommand("sga").setExecutor(new SgaCommand());
            getCommand("sga").setTabCompleter(new TabCompleter());
            getLogger().info("Comandos SGA e TabCompleter vinculados com sucesso.");
        }

        // Registrar a própria classe para ouvir o evento do ShopGUI+
        getServer().getPluginManager().registerEvents(this, this);
        
        getLogger().info("ShopGuiAddon v" + getDescription().getVersion() + " inicializado!");
    }

    @EventHandler
    public void onShopGUIPlusPostEnable(ShopGUIPlusPostEnableEvent event) {
        // Registro do Listener de Inventário apenas quando o ShopGUI+ estiver pronto
        getServer().getPluginManager().registerEvents(new EventListener(), this);
        getLogger().info("Sistema modular e Listeners de interface ativos!");
    }

    /**
     * Carrega ou recarrega o arquivo de mensagens na memória.
     */
    public void loadMessages() {
        File msgFile = new File(getDataFolder(), "messages.yml");
        if (!msgFile.exists()) {
            saveResource("messages.yml", false);
        }
        this.messagesConfig = YamlConfiguration.loadConfiguration(msgFile);
    }

    /**
     * Método centralizado para recarregar todo o plugin.
     */
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
