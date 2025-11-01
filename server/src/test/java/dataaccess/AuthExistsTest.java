package dataaccess;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;

import static org.junit.jupiter.api.Assertions.*;


public class AuthExistsTest {

    private DataAccess dataAccess;

    @BeforeEach
    void setUp() throws DataAccessException {
        dataAccess = new MySQLDataAccess();
        dataAccess.clear();
    }

    @Test
    public void authExistsPositiveTest() throws Exception {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "INSERT INTO authTokens (authToken, username) VALUES ('randomAuth123', 'validUsername')")) {
            ps.executeUpdate();
        }

        assertDoesNotThrow(() -> dataAccess.authExists("randomAuth123"));
        assertTrue(dataAccess.authExists("randomAuth123"));

    }

    @Test
    public void xxxxxxxNegativeTest() throws Exception {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "INSERT INTO authTokens (authToken, username) VALUES ('randomAuth123', 'validUsername')")) {
            ps.executeUpdate();
        }

        assertDoesNotThrow(() -> dataAccess.authExists("fakeAuth"));
        assertFalse(dataAccess.authExists("fakeAuth"));
    }
}
