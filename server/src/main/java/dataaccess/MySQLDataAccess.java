package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import datamodel.AuthData;
import datamodel.GameData;
import datamodel.UserData;

import java.sql.ResultSet;
import java.util.List;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class MySQLDataAccess implements DataAccess {

    public MySQLDataAccess() throws DataAccessException {
        configureDatabase();
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS users (
              `username` varchar(50) NOT NULL,
              `password` varchar(50) NOT NULL,
              `email` varchar(200) NOT NULL,
              PRIMARY KEY (`username`)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """,

            """
            CREATE TABLE IF NOT EXISTS games (
              `gameID` int NOT NULL,
              `whiteUsername` varchar(50) DEFAULT NULL,
              `blackUsername` varchar(50) DEFAULT NULL,
              `gameName` varchar(200) NOT NULL,
              `game` text NOT NULL,
               PRIMARY KEY (`gameID`)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """,

            """
            CREATE TABLE IF NOT EXISTS authTokens (
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

    private int updateTable(String statement, List<Object> values) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                for (int i = 0; i < values.size(); i++) {
                    Object param = values.get(i);
                    if (param instanceof String p) {
                        ps.setString(i + 1, p);
                    } else if (param instanceof Integer p) {
                        ps.setInt(i + 1, p);
                    } else if (param instanceof ChessGame p) { // not sure if this is right
                        ps.setString(i + 1, p.toString());
                    } else if (param == null) {
                        ps.setNull(i + 1, NULL);
                    }
                }
                ps.executeUpdate();

                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }

                return 0;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Unable to configure database", e);
        }
    }

    @Override
    public void saveUser(UserData user) throws DataAccessException {
        var statement = "INSTERT INTO User (username, password, email) VALUES (?, ?, ?)";
        List<Object> userList = List.of(user.username(), user.password(), user.email());
        updateTable(statement, userList);
    }

    @Override
    public void getUser(String username) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username FROM users WHERE username = ?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        rs.getString("username");
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Unable to get user", e);
        }
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
        //authTokens.put(auth.authToken(), auth.username());
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
        //return users.containsKey(username);
        return false;
    }

    @Override
    public void clear() {

    }
}



