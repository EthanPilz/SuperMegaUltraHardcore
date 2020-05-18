package com.ethanpilz.smuhc.io;

import com.ethanpilz.smuhc.SMUHC;
import com.ethanpilz.smuhc.components.SMUHCPlayer;
import com.ethanpilz.smuhc.components.arena.Arena;
import com.ethanpilz.smuhc.exceptions.SaveToDatabaseException;
import com.ethanpilz.smuhc.exceptions.arena.ArenaAlreadyExistsException;
import org.bukkit.*;
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


}


