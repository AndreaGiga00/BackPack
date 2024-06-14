# BackPack

**Description**:
BackPack is an advanced Minecraft plugin that allows players to have an additional inventory accessible via the `/pv` command. This plugin is perfect for those who wish to expand their personal storage capacity without the need to constantly carry around chests or shulker boxes. With an intuitive and user-friendly interface, BackPack is ideal for survival servers, RPGs, and other gameplay styles that require efficient inventory management.

**Key Features**:
- **Additional Inventory**: Each player has access to an extra 18-slot inventory (2 rows of 9 slots) using the `/pv` command.
- **Data Persistence**: Items stored in the Personal Vault (PV) are saved in an SQL database, ensuring that players do not lose their items even after server restarts.
- **Ease of Use**: Players can open their extra inventory simply by executing the `/pv` command.
- **Security**: Uses PreparedStatement to prevent SQL injection and ensure data security.
- **Database Integration**: Fully integrated with SQL databases to store and retrieve player inventories efficiently.

**Commands**:
- `/pv`: Opens the player's additional inventory.

**Permissions**:
- `backpack.use`: Allows the player to use the `/pv` command.

**Requirements**:
- Minecraft server running Bukkit/Spigot/Paper.
- SQL database for inventory storage.

**Installation**:
1. Download the BackPack.jar plugin file.
2. Copy the .jar file into your server's `plugins` directory.
3. Restart the server to load the plugin.
4. Configure the SQL database in the plugin's configuration file if necessary.

**Configuration**:
Ensure your SQL database is correctly set up. Edit the `config.yml` file in the plugin's directory to configure database access credentials.

**Support**:
For support, bug reports, or suggestions, visit our [GitHub](https://github.com/AndreGiga00/BackPack).

**Changelog**:
- **v1.0**: Initial release with extra inventory feature and SQL database storage.

BackPack is the perfect plugin to enhance your players' gaming experience by offering a convenient and reliable storage solution. Download it today and enjoy an additional inventory without any hassle!
