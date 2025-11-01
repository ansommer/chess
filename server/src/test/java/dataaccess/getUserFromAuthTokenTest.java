package dataaccess;

import datamodel.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;
import service.RegisterService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class getUserFromAuthTokenTest {

    private DataAccess dataAccess;

    @BeforeEach
    void setUp() throws DataAccessException {
        dataAccess = new MySQLDataAccess();
        dataAccess.clear();
    }

    @Test
    public void getUserFromAuthTokenPositiveTest() throws Exception {
        var registerService = new RegisterService(dataAccess);
        registerService.register(new UserData("validUsername", "validPassword", "email@gmail.com"));
        String auth = getAuth("validUsername"); //not sure how I feel about this
        assertDoesNotThrow(() -> dataAccess.getUserFromAuthToken(auth));
    }

    @Test
    public void getUserFromAuthTokenNegativeTest() {
        assertNull(dataAccess.getUserFromAuthToken("fakeAuth"));
    }

    public String getAuth(String username) throws MySQLDataAccessException {
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
            throw new MySQLDataAccessException("Error: Unable to get auth", e);
        }
        return null;
    }
}
