package it.plugincraft.backpack;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.logging.Logger;

public class PvCommand implements CommandExecutor {
    BackPack main = BackPack.getInstance();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        // Controlla se il comando è "/pv"
        if (!command.getName().equalsIgnoreCase("pv")) {
            // Se il comando non è "/pv", invia un messaggio di errore e non fare nulla
            if (sender instanceof Player) {
                sender.sendMessage("§cComando non riconosciuto. Usa /pv per accedere al tuo PV.");
            } else {
                Logger logger = Bukkit.getLogger();
                logger.info("Comando non riconosciuto.");
            }
            return false;
        }

        if (!(sender instanceof Player)) {
            Logger logger = Bukkit.getLogger();
            logger.info("Command is available for only players");
            return false;
        }

        Player player = (Player) sender;

        Inventory inv = createInv(player, "PV");
        try {
            if (isInDB(player)) {
                setInventory(player, inv);
                player.openInventory(inv);
            } else {
                player.openInventory(inv);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        player.setMetadata("custom", new FixedMetadataValue(BackPack.getInstance(), true));
        return true;
    }

    public void setInventory(Player p, Inventory pv) throws SQLException {
        String uuid = p.getUniqueId().toString();
        String sql = "SELECT * FROM PVInventories WHERE player_uuid = ?";
        Connection con = main.getDatabase().getConnection();

        try (PreparedStatement stm = con.prepareStatement(sql)) {
            stm.setString(1, uuid);
            ResultSet res = stm.executeQuery();
            while (res.next()) {
                int slot = res.getInt("slot");
                int quantity = res.getInt("qta");

                Blob itemFromDB = res.getBlob("item_data");
                int blobLenght = (int) itemFromDB.length();
                byte[] itemByte = itemFromDB.getBytes(1, blobLenght);
                ItemStack generateItem = deserializeItemStack(itemByte);
                generateItem.setAmount(quantity);
                pv.setItem(slot, generateItem);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public Boolean isInDB(Player p) throws SQLException {
        String uuid = p.getUniqueId().toString();
        String sql = "SELECT * FROM PVInventories WHERE player_uuid = ?";
        Connection con = main.getDatabase().getConnection();

        try (PreparedStatement stm = con.prepareStatement(sql)) {
            stm.setString(1, uuid);
            ResultSet res = stm.executeQuery();
            return res.next();
        }
    }

    public Inventory createInv(Player p, String name) {
        return Bukkit.createInventory(p, main.getConfig().getInt("Slot"), name);
    }

    private ItemStack deserializeItemStack(byte[] data) throws IOException, ClassNotFoundException {
        try (ByteArrayInputStream byteStream = new ByteArrayInputStream(data);
             BukkitObjectInputStream objectStream = new BukkitObjectInputStream(byteStream)) {
            return (ItemStack) objectStream.readObject();
        }
    }
}
