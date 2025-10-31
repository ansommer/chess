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

    private int nextGameId = 1;
    
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

    private int updateTable(String statement, Object... values) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                for (int i = 0; i < values.length; i++) {
                    Object param = values[i];
                    if (param instanceof String p) {
                        ps.setString(i + 1, p);
                    } else if (param instanceof Integer p) {
                        ps.setInt(i + 1, p);
                    } else if (param instanceof ChessGame p) {
                        ps.setString(i + 1, p.toString());
                        //If it needs to be json try this
                        //String json = new Gson().toJson(p);
                        //ps.setString(i + 1, json);
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
            throw new DataAccessException("Unable to update table", e);
        }
    }

    @Override
    public void saveUser(UserData user) throws DataAccessException {
        var statement = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
        updateTable(statement, user.username(), user.password(), user.email());
    }

    @Override
    public void saveAuth(AuthData auth) throws DataAccessException {
        String statement = """
                INSERT INTO authTokens (username, authToken) 
                VALUES (?, ?) 
                ON DUPLICATE KEY UPDATE authToken = VALUES(authToken)
                """;
        updateTable(statement, auth.username(), auth.authToken());
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username, password, email FROM users WHERE username = ?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return new UserData(rs.getString("username"),
                                rs.getString("password"),
                                rs.getString("email")
                        );
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Unable to get user", e);
        }
        return null;
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
        return nextGameId++;
    }

    @Override
    public void createGame(GameData game) throws DataAccessException {
        var statement = "INSERT INTO games (gameID, whiteUsername, blackUsername, gameName, game) VALUES (?, ?, ?, ?, ?)";
        updateTable(statement, game.gameID(), game.whiteUsername(), game.blackUsername(), game.gameName(), game.game());
    }

    @Override
    public void deleteAuth(String auth) throws DataAccessException {
        String statement = "DELETE FROM authTokens WHERE authToken = ?";
        updateTable(statement, auth);
    }


    @Override
    public boolean authExists(String auth) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT authToken FROM authTokens WHERE authToken = ?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, auth);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Unable to get auth", e);
        }
        return false;
    }

    @Override
    public String getAuth(String username) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT authToken FROM authTokens WHERE username = ?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return (rs.getString("authToken")
                        );
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Unable to get auth", e);
        }
        return null;
    }

    @Override
    public String getPass(String username) throws DataAccessException {
        UserData user = getUser(username);
        return user.password();
    }

    @Override
    public boolean userExists(String username) throws DataAccessException {
        return getUser(username) != null;
    }

    @Override
    public void clear() throws DataAccessException {
        updateTable("TRUNCATE users");
        updateTable("TRUNCATE games");
        updateTable("TRUNCATE authTokens");
    }
}



