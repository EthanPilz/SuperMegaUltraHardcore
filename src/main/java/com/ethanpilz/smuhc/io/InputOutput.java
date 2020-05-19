package com.ethanpilz.smuhc.io;

import com.ethanpilz.smuhc.SMUHC;
import com.ethanpilz.smuhc.components.SMUHCPlayer;
import com.ethanpilz.smuhc.components.arena.Arena;
import com.ethanpilz.smuhc.exceptions.SaveToDatabaseException;
import com.ethanpilz.smuhc.exceptions.arena.ArenaAlreadyExistsException;
import com.ethanpilz.smuhc.exceptions.arena.ArenaDoesNotExistException;
import org.bukkit.*;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.sql.*;
import java.util.logging.Level;

public class InputOutput {

    public static YamlConfiguration global;
    private static Connection connection;
    public static String worldName;

    public InputOutput() {
        if (!SMUHC.instance.getDataFolder().exists()) {
            try {
                (SMUHC.instance.getDataFolder()).mkdir();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        global = new YamlConfiguration();
    }

    private static Connection createConnection() {

        try {
            Class.forName("org.sqlite.JDBC");
            Connection ret = DriverManager.getConnection("jdbc:sqlite:" +  new File(SMUHC.instance.getDataFolder().getPath(), "db.sqlite").getPath());
            ret.setAutoCommit(false);
            return ret;
        }
        catch (ClassNotFoundException e) {
            SMUHC.log.log(Level.SEVERE, SMUHC.consolePrefix + "ClassNotFound while attempting to create database connection");
            e.printStackTrace();
            return null;
        }
        catch (SQLException e) {
            SMUHC.log.log(Level.SEVERE, SMUHC.consolePrefix + "Encountered SQL exception while attempting to create database connection");
            e.printStackTrace();
            return null;
        }
    }

    public static synchronized Connection getConnection() {
        if (connection == null) connection = createConnection();

        try {
            if(connection.isClosed()) connection = createConnection();
        }

        catch (SQLException ex) {
            ex.printStackTrace();
        }

        return connection;
    }

    public static synchronized void freeConnection() {
        Connection conn = getConnection();
        if(conn != null) {
            try {
                conn.close();
                conn = null;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void prepareDB() {
        Connection conn = getConnection();
        Statement st;
        try {
            st = conn.createStatement();
            st.executeUpdate("CREATE TABLE IF NOT EXISTS \"smuhc_arenas\" (\"Name\" VARCHAR PRIMARY KEY NOT NULL, \"WorldName\" VARCHAR, \"WaitX\" DOUBLE, \"WaitY\" DOUBLE, \"WaitZ\" DOUBLE, \"WaitWorld\" VARCHAR, \"ReturnX\" DOUBLE, \"ReturnY\" DOUBLE, \"ReturnZ\" DOUBLE, \"ReturnWorld\" VARCHAR, \"SecondsWaitingRoom\" DOUBLE DEFAULT 60)");
            st.executeUpdate("CREATE TABLE IF NOT EXISTS \"smuhc_players\" (\"UUID\" VARCHAR PRIMARY KEY NOT NULL, \"XP\" INTEGER DEFAULT 0)");
            st.executeUpdate("CREATE TABLE IF NOT EXISTS \"smuhc_signs\" (\"X\" DOUBLE, \"Y\" DOUBLE, \"Z\" DOUBLE, \"World\" VARCHAR, \"Arena\" VARCHAR, \"Type\" VARCHAR)");
            conn.commit();
            st.close();

        }
        catch (SQLException e) {
            SMUHC.log.log(Level.SEVERE, SMUHC.consolePrefix + "Encountered SQL error while attempting to prepare database: " + e.getMessage());
            e.printStackTrace();
        }
        catch (Exception e) {
            SMUHC.log.log(Level.SEVERE, SMUHC.consolePrefix + "Unknown error encountered while attempting to prepare database.");
        }
    }

    /**
     * Updates DB to latest version
     */
    public void updateDB() {
        performUpdate("SELECT SecondsWaitingRoom FROM smuhc_arenas", "ALTER TABLE smuhc_arenas ADD SecondsWaitingRoom DOUBLE DEFAULT 60");
        performUpdate("SELECT XP FROM smuhc_players", "ALTER TABLE smuhc_players ADD XP INTEGER DEFAULT 0");
    }

    /**
     * Performs update to database if check query fails
     *
     * @param check
     * @param sqlite
     */

    private void performUpdate(String check, String sqlite) {
        try {
            Statement statement = getConnection().createStatement();
            statement.executeQuery(check);
            statement.close();
        } catch (SQLException ex) {

            try {
                String[] query;

                query = sqlite.split(";");
                Connection conn = getConnection();
                Statement st = conn.createStatement();
                for (String q : query)
                    st.executeUpdate(q);
                conn.commit();
                st.close();
                SMUHC.log.log(Level.INFO, SMUHC.consolePrefix + "Database updated to new version!");

            } catch (SQLException e) {

                SMUHC.log.log(Level.SEVERE, SMUHC.consolePrefix + "Error while attempting to update database to new version!");
                e.printStackTrace();
            }
        }
    }


    public void storeArena(Arena arena) throws SaveToDatabaseException {
        try {
            String sql;
            Connection conn = InputOutput.getConnection();

            sql = "INSERT INTO smuhc_arenas (`Name`, `WorldName`, `WaitX`, `WaitY`, `WaitZ`, `WaitWorld`, `ReturnX`, `ReturnY`, `ReturnZ`, `ReturnWorld`, `SecondsWaitingRoom`) VALUES (?,?,?,?,?,?,?,?,?,?,?)";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);

            preparedStatement.setString(1, arena.getName() + "");
            preparedStatement.setString(2, arena.getWorldName() + "");
            preparedStatement.setDouble(3, arena.getWaitingLocation().getX());
            preparedStatement.setDouble(4, arena.getWaitingLocation().getY());
            preparedStatement.setDouble(5, arena.getWaitingLocation().getZ());
            preparedStatement.setString(6, arena.getWaitingLocation().getWorld().getName()+"");
            preparedStatement.setDouble(7, arena.getReturnLocation().getX());
            preparedStatement.setDouble(8, arena.getReturnLocation().getY());
            preparedStatement.setDouble(9, arena.getReturnLocation().getZ());
            preparedStatement.setString(10, arena.getReturnLocation().getWorld().getName()+"");
            preparedStatement.setString(11, "60");

            preparedStatement.executeUpdate();
            conn.commit();
        } catch (SQLException e) {
            SMUHC.log.log(Level.WARNING, SMUHC.consolePrefix + "Encountered an error while attempting to save new arena into database: " + e.getMessage());
            throw new SaveToDatabaseException();
        }
    }
    public void loadPlayer(String UUID) {
        try {
            Connection conn;
            PreparedStatement ps = null;
            ResultSet result = null;
            conn = getConnection();
            ps = conn.prepareStatement("SELECT `XP` FROM `smuhc_players` WHERE `UUID` = ?");
            ps.setString(1, UUID);
            result = ps.executeQuery();

            while (result.next()) {
                SMUHCPlayer player = new SMUHCPlayer(UUID, result.getInt("XP"));
                SMUHC.playerController.addPlayer(player);
            }

            conn.commit();
            ps.close();
        } catch (SQLException e) {
            SMUHC.log.log(Level.WARNING, SMUHC.consolePrefix + "Encountered a SQL exception while attempting to load SMUHC player from database: " + e.getMessage());
        }
    }

    public void storePlayer(SMUHCPlayer player) throws SaveToDatabaseException {
        try {
            String sql;
            Connection conn = InputOutput.getConnection();

            sql = "INSERT INTO smuhc_players (`UUID`) VALUES (?)";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, player.getPlayerUUID());
            preparedStatement.executeUpdate();
            conn.commit();
        } catch (SQLException e) {
            SMUHC.log.log(Level.WARNING, e.getMessage());
            throw new SaveToDatabaseException();
        }
    }

    /**
     * Updates player in the database
     *
     * @param player
     */

    public void updatePlayer(SMUHCPlayer player) {
        try {
            String sql;
            Connection conn = InputOutput.getConnection();

            sql = "UPDATE `smuhc_players` SET `XP` = ? WHERE `UUID` = ?";

            //updateInDatabase
            PreparedStatement preparedStatement = conn.prepareStatement(sql);

            preparedStatement.setInt(2, player.getXP());
            preparedStatement.setString(6, player.getPlayerUUID());
            preparedStatement.executeUpdate();
            connection.commit();

            conn.commit();


        } catch (SQLException e) {
            SMUHC.log.log(Level.WARNING, SMUHC.consolePrefix + "Encountered a SQL exception while attempting to update player in DB: " + e.getMessage());
        }
    }

    public void loadArenas() {
        try {
            Connection conn;
            PreparedStatement ps = null;
            ResultSet result = null;
            conn = getConnection();
            ps = conn.prepareStatement("SELECT `Name`, `WorldName`, `WaitX`, `WaitY`, `WaitZ`, `WaitWorld`, `ReturnX`, `ReturnY`, `ReturnZ`, `ReturnWorld`, `SecondsWaitingRoom` FROM `smuhc_arenas`");
            result = ps.executeQuery();

            int count = 0;
            while (result.next()) {

                Location waitLoc = new Location(Bukkit.getWorld(result.getString("WaitWorld")), result.getDouble("WaitX"),result.getDouble("WaitY"),result.getDouble("WaitZ"));
                Location returnLoc = new Location(Bukkit.getWorld(result.getString("ReturnWorld")), result.getDouble("ReturnX"),result.getDouble("ReturnY"),result.getDouble("ReturnZ"));
                worldName = result.getString("WorldName");
                Arena arena = new Arena(result.getString("Name"), result.getString("WorldName"), waitLoc, returnLoc, (int) result.getDouble("SecondsWaitingRoom"));

                try {
                    SMUHC.arenaController.addArena(arena);
                }
                catch (ArenaAlreadyExistsException exception) {
                    SMUHC.log.log(Level.SEVERE, SMUHC.consolePrefix + "Attempted to load arena (" + arena.getName() + ") from database that was already in controller memory");
                }

                SMUHC.log.log(Level.INFO, SMUHC.consolePrefix + "Arena " + arena.getName() + " loaded successfully.");
                count++;
            }

            if (count > 0) {
                SMUHC.log.log(Level.INFO, SMUHC.consolePrefix + "Loaded " + count + " arena(s).");
            }

            conn.commit();
            ps.close();
        } catch (SQLException e) {
            SMUHC.log.log(Level.WARNING, SMUHC.consolePrefix + "Encountered a SQL exception while attempting to load arenas..." + e.getMessage());
        }
    }

    /**
     * Updates arena in the database
     *
     * @param arena
     */

    public void updateArena(Arena arena) {
        try {
            String sql;
            Connection conn = InputOutput.getConnection();

            sql = "UPDATE `smuhc_arenas` SET `SecondsWaitingRoom` = ? WHERE `Name` = ?";

            //updateInDatabase
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setDouble(2, arena.getSecondsWaitingRoom());
            preparedStatement.setString(4, arena.getName());
            preparedStatement.executeUpdate();
            connection.commit();
            conn.commit();

        } catch (SQLException e) {
            SMUHC.log.log(Level.WARNING, SMUHC.consolePrefix + "Encountered a SQL exception while attempting to update arena in DB: " + e.getMessage());
        }
    }

    /**
     * Removes an arena from the database
     * @param arenaName
     */

    public void deleteArena(String arenaName) {
        try {
            Connection conn = InputOutput.getConnection();
            PreparedStatement ps = conn.prepareStatement("DELETE FROM smuhc_arenas WHERE Name = ?");
            ps.setString(1, arenaName);
            ps.executeUpdate();
            conn.commit();
            ps.close();

        } catch (SQLException e) {
            SMUHC.log.log(Level.WARNING, SMUHC.consolePrefix + "Encountered an error while attempting to remove an arena from the database: " + e.getMessage());
        }
    }

    /**
     * Stores sign to the database
     * @param sign
     * @throws SaveToDatabaseException
     */
    public void storeSign(Sign sign, Arena arena) throws SaveToDatabaseException
    {
        try
        {
            String sql;
            Connection conn = InputOutput.getConnection();

            sql = "INSERT INTO smuhc_signs (`X`, `Y`, `Z`, `World`, `Arena`, `Type`) VALUES (?,?,?,?,?,?)";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);


            preparedStatement.setDouble(1, sign.getLocation().getX());
            preparedStatement.setDouble(2, sign.getLocation().getY());
            preparedStatement.setDouble(3, sign.getLocation().getZ());
            preparedStatement.setString(4, sign.getLocation().getWorld().getName());
            preparedStatement.setString(5, arena.getName());
            preparedStatement.setString(6, "Join");

            preparedStatement.executeUpdate();
            conn.commit();
        }
        catch (SQLException e)
        {
            SMUHC.log.log(Level.WARNING, SMUHC.consolePrefix + "Encountered an error while attempting to store a sign to the database: " + e.getMessage());
            throw new SaveToDatabaseException();
        }
    }

    /**
     * Removes a sign from the database
     * @param X
     * @param Y
     * @param Z
     * @param world
     */
    public void deleteSign(double X, double Y, double Z, String world)
    {
        try
        {
            Connection conn = InputOutput.getConnection();
            PreparedStatement ps = conn.prepareStatement("DELETE FROM smuhc_signs WHERE World = ? AND X = ? AND Y = ? AND Z = ?");
            ps.setString(1, world);
            ps.setDouble(2, X);
            ps.setDouble(3, Y);
            ps.setDouble(4, Z);
            ps.executeUpdate();
            conn.commit();
            ps.close();

        }
        catch (SQLException e)
        {
            SMUHC.log.log(Level.WARNING, SMUHC.consolePrefix + "Encountered an error while attempting to remove a sign from the database: " + e.getMessage());
        }
    }


    /**
     * Loads chests from database into arena memory
     */
    public void loadSigns()
    {
        try
        {
            Connection conn;
            PreparedStatement ps = null;
            ResultSet result = null;
            conn = getConnection();
            ps = conn.prepareStatement("SELECT `World`, `X`, `Y`, `Z`, `Arena`, `Type` FROM `smuhc_signs`");
            result = ps.executeQuery();

            int count = 0;
            int removed = 0;
            while (result.next())
            {
                if (Bukkit.getWorld(result.getString("World")) != null) {
                    Location signLocation = new Location(Bukkit.getWorld(result.getString("World")), result.getDouble("X"), result.getDouble("Y"), result.getDouble("Z"));

                    if (signLocation.getBlock().getType().equals(Material.LEGACY_WALL_SIGN) || signLocation.getBlock().getType().equals(Material.LEGACY_SIGN) || signLocation.getBlock().getType().equals(Material.LEGACY_SIGN_POST)) {
                        try {
                            Arena arena = SMUHC.arenaController.getArena(result.getString("Arena"));
                            arena.getSignManager().addJoinSign((Sign) signLocation.getBlock().getState());
                            count++;
                        } catch (ArenaDoesNotExistException exception) {
                            SMUHC.log.log(Level.SEVERE, SMUHC.consolePrefix + "Attempted to load sign in arena (" + result.getString("Arena") + "), arena does not exist in memory.");
                            deleteSign(result.getDouble("X"), result.getDouble("Y"), result.getDouble("Z"), result.getString("World"));
                            removed++;
                        }
                    } else {
                        //This location is no longer a chest, so remove it
                        deleteSign(result.getDouble("X"), result.getDouble("Y"), result.getDouble("Z"), result.getString("World"));
                        removed++;
                    }
                } else {
                    //The world was deleted
                    deleteSign(result.getDouble("X"), result.getDouble("Y"), result.getDouble("Z"), result.getString("World"));
                    removed++;
                }
            }

            if (count > 0)
            {
                SMUHC.log.log(Level.INFO, SMUHC.consolePrefix + "Loaded " + count + " signs(s).");
            }

            if (removed > 0)
            {
                SMUHC.log.log(Level.INFO, SMUHC.consolePrefix + "Removed " + removed + " signs(s).");
            }

            conn.commit();
            ps.close();
        }
        catch (SQLException e)
        {
            SMUHC.log.log(Level.WARNING, SMUHC.consolePrefix + "Encountered a SQL exception while attempting to load signs from database: " + e.getMessage());
        }
    }

}


