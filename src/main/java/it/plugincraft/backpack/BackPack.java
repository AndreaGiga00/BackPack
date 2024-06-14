package it.plugincraft.backpack;

import org.bukkit.plugin.java.JavaPlugin;

public final class BackPack extends JavaPlugin {

    private static BackPack instance;
    private Database db;

    @Override
    public void onEnable() {
        instance = this;

        db = new Database();

        getServer().getPluginManager().registerEvents(new PvListener(), this);
        this.getCommand("pv").setExecutor(new PvCommand());
        System.out.println("ONLINE");
        saveDefaultConfig();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static BackPack getInstance() {
        return instance;
    }

    public Database getDatabase() {
        return db;
    }
}
