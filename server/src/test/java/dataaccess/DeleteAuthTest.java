package dataaccess;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DeleteAuthTest {

    private DataAccess dataAccess;

    @BeforeEach
    void setUp() throws DataAccessException {
        dataAccess = new MySQLDataAccess();
        dataAccess.clear();
    }

    @Test
    public void deleteAuthPositiveTest() throws Exception {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "INSERT INTO authTokens (authToken, username) VALUES ('randomAuth123', 'validUsername')")) {
            ps.executeUpdate();
        }
        assertDoesNotThrow(() -> dataAccess.deleteAuth("randomAuth123"));
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT COUNT(*) FROM authTokens WHERE authToken = 'randomAuth123'")) {
            ResultSet rs = ps.executeQuery();
            rs.next();
            int count = rs.getInt(1);
            assertEquals(0, count);
        }
    }

    @Test
    public void deleteAuthNegativeTest() throws Exception {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "INSERT INTO authTokens (authToken, username) VALUES ('randomAuth123', 'validUsername')")) {
            ps.executeUpdate();
        }
        assertDoesNotThrow(() -> dataAccess.deleteAuth("fakeAuth"));
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT COUNT(*) FROM authTokens WHERE authToken = 'randomAuth123'")) {
            ResultSet rs = ps.executeQuery();
            rs.next();
            int count = rs.getInt(1);
            assertEquals(1, count);
        }
    }
}
