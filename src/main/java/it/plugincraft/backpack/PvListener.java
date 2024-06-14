package it.plugincraft.backpack;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PvListener implements Listener {
    BackPack main = BackPack.getInstance();

    @EventHandler
    public void OnInventoryClose(InventoryCloseEvent event) throws SQLException {
        Player p = (Player) event.getPlayer();

        if (p.hasMetadata("custom")) {

            saveInventoryInDB(p, event.getInventory());

            p.removeMetadata("custom", BackPack.getInstance());
        }
    }

    public void saveInventoryInDB(Player p, Inventory inv) throws SQLException {
        deleteAllItemDB(p);
        Connection con = main.getDatabase().getConnection();
        String SQL = "INSERT INTO PVInventories(player_uuid, qta,slot, item_data) VALUES(?,?,?,?)"
                +
                "ON DUPLICATE KEY UPDATE item_data = VALUES(item_data)";
        String playerid = p.getUniqueId().toString();
        try (PreparedStatement stm = con.prepareStatement(SQL)) {
            for (int slot = 0; slot < inv.getSize(); slot++) {
                ItemStack item = inv.getItem(slot);

                if (item != null) {
                    int qta = item.getAmount();
                    byte[] itemData = serializeItemStack(item);
                    stm.setString(1, playerid);
                    stm.setInt(2, qta);
                    stm.setInt(3, slot);
                    stm.setBytes(4, itemData);
                    stm.executeUpdate();
                }
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteAllItemDB(Player p) throws SQLException {
        Connection con = main.getDatabase().getConnection();
        String sql = "DELETE FROM PVInventories WHERE player_uuid = ?";

        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, p.getUniqueId().toString());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw e;
        }
    }


    private byte[] serializeItemStack(ItemStack item) throws IOException {
        try (ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
             BukkitObjectOutputStream objectStream = new BukkitObjectOutputStream(byteStream)) {
            objectStream.writeObject(item);
            return byteStream.toByteArray();
        }
    }

    private ItemStack deserializeItemStack(byte[] data) throws IOException, ClassNotFoundException {
        try (ByteArrayInputStream byteStream = new ByteArrayInputStream(data);
             BukkitObjectInputStream objectStream = new BukkitObjectInputStream(byteStream)) {
            return (ItemStack) objectStream.readObject();
        }
    }
}
