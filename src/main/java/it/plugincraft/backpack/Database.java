package it.plugincraft.backpack;

import org.bukkit.configuration.file.FileConfiguration;

import java.sql.*;

public class Database {
    private static final String DB_USER_KEY = "Database.user";
    private static final String DB_PASSWORD_KEY = "Database.password";
    private static final String DB_IP_KEY = "Database.ip";

    private static final String DB_NAME = "provadx"; // Nome del database impostato direttamente nel codice

    private FileConfiguration config;

    public Database() {
        this.config = BackPack.getInstance().getConfig();
        try {
            createDatabase();
            createTable();
        } catch (SQLException e) {
            throw new RuntimeException("Errore nella creazione del database o della tabella: " + e.getMessage(), e);
        }
    }

    private void createDatabase() throws SQLException {
        String user = config.getString(DB_USER_KEY);
        String pass = config.getString(DB_PASSWORD_KEY);
        String ip = config.getString(DB_IP_KEY);
        String url = "jdbc:mysql://" + ip + "/";

        try (Connection con = DriverManager.getConnection(url, user, pass);
             Statement stmt = con.createStatement()) {
            String sql = "CREATE DATABASE IF NOT EXISTS " + DB_NAME;
            stmt.executeUpdate(sql);
            System.out.println("Database creato o già esistente");
        } catch (SQLException e) {
            System.err.println("Errore nella creazione del database: " + e.getMessage());
            throw e;
        }
    }

    private void createTable() throws SQLException {
        try (Connection con = createConnection()) {
            String sql = "CREATE TABLE IF NOT EXISTS PVInventories (" +
                    "player_uuid VARCHAR(36) NOT NULL, " +
                    "qta INT NOT NULL," +
                    "slot INT NOT NULL, " +
                    "item_data BLOB NOT NULL, " +
                    "PRIMARY KEY (player_uuid, slot))";

            try (PreparedStatement stm = con.prepareStatement(sql)) {
                stm.executeUpdate();
                System.out.println("Tabella creata o già esistente");
            }
        } catch (SQLException e) {
            System.err.println("Errore nella creazione della tabella: " + e.getMessage());
            throw e;
        }

    }

    private Connection createConnection() throws SQLException {
        String user = config.getString(DB_USER_KEY);
        String pass = config.getString(DB_PASSWORD_KEY);
        String ip = config.getString(DB_IP_KEY);
        String url = "jdbc:mysql://" + ip + "/" + DB_NAME;

        try {
            return DriverManager.getConnection(url, user, pass);
        } catch (SQLException e) {
            System.err.println("Errore nella connessione al database: " + e.getMessage());
            throw e;
        }
    }

    public Connection getConnection() throws SQLException {
        try {
            return createConnection();
        } catch (SQLException e) {
            throw new RuntimeException("Errore nella connessione al database: " + e.getMessage(), e);
        }
    }
}
