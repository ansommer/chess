package dataaccess;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;

import static org.junit.jupiter.api.Assertions.*;


public class ClearTest {

    private DataAccess dataAccess;

    @BeforeEach
    void setUp() throws DataAccessException {
        dataAccess = new MySQLDataAccess();
        dataAccess.clear();
    }

    @Test
    public void clearTest() throws Exception {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "INSERT INTO authTokens (authToken, username) VALUES ('randomAuth123', 'validUsername')")) {
            ps.executeUpdate();
        }
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "INSERT INTO users (username, password, email) " +
                             "VALUES ('validUsername', 'validPassword', 'validEmail')")) {
            ps.executeUpdate();
        }
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     """
                             INSERT INTO games (gameID, whiteUsername, blackUsername, gameName, game)
                             VALUES (
                                 1,
                                 NULL,
                                 NULL,
                                 'coolGame',
                                 '{"teamTurn":"WHITE","bKingOrRook1Moved":false,"bKingOrRook2Moved":false,
                                 "wKingOrRook1Moved":false,"wKingOrRook2Moved":false}'
                             )
                             """)) {

            ps.executeUpdate();
        }
        assertDoesNotThrow(() -> dataAccess.clear());

        try (Connection conn = DatabaseManager.getConnection()) {
            assertEquals(0, countRows(conn, "authTokens"), "authTokens table should be empty");
            assertEquals(0, countRows(conn, "users"), "users table should be empty");
            assertEquals(0, countRows(conn, "games"), "games table should be empty");
        }
    }

    private int countRows(Connection conn, String tableName) throws Exception {
        try (PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM " + tableName)) {
            var rs = ps.executeQuery();
            rs.next();
            return rs.getInt(1);
        }
    }
}



