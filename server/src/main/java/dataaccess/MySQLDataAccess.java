package dataaccess;

import datamodel.AuthData;
import datamodel.GameData;
import datamodel.UserData;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;

public class MySQLDataAccess implements DataAccess {

    public MySQLDataAccess() throws DataAccessException {
        configureDatabase();
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS User (
              `username` varchar(50) NOT NULL,
              `password` varchar(50) NOT NULL,
              `email` varchar(200) NOT NULL,
              PRIMARY KEY (`username`)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """,

            """
            CREATE TABLE IF NOT EXISTS Game (
              `gameID` int NOT NULL,
              `whiteUsername` varchar(50) DEFAULT NULL,
              `blackUsername` varchar(50) DEFAULT NULL,
              `gameName` varchar(200) NOT NULL,
              `game` text NOT NULL,
               PRIMARY KEY (`gameID`)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """,

            """
            CREATE TABLE IF NOT EXISTS Auth (
              `authToken` text NOT NULL,
              `username` varchar(50) NOT NULL,
              PRIMARY KEY (`username`)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };

    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (Connection conn = DatabaseManager.getConnection()) {
            for (String statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Unable to configure database", e);
        }
    }

    @Override
    public void saveUser(UserData user) {

    }

    @Override
    public void getUser(String username) {

    }

    @Override
    public String getUserFromAuthToken(String authToken) {
        return "";
    }

    @Override
    public HashMap<String, GameData> getGames() {
        return null;
    }

    @Override
    public GameData getOneGame(int gameID) {
        return null;
    }

    @Override
    public int getNextGameId() {
        return 0;
    }

    @Override
    public void createGame(GameData game) {

    }

    @Override
    public void deleteAuth(String auth) {

    }

    @Override
    public void saveAuth(AuthData auth) {

    }

    @Override
    public boolean authExists(String auth) {
        return false;
    }

    @Override
    public String getPass(String username) {
        return "";
    }

    @Override
    public boolean userExists(String username) {
        return false;
    }

    @Override
    public void clear() {

    }
}



