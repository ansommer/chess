package dataaccess;

import datamodel.AuthData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class saveAuthTest {

    private DataAccess dataAccess;

    @BeforeEach
    void setUp() throws DataAccessException {
        dataAccess = new MySQLDataAccess();
        dataAccess.clear();
    }

    @Test
    public void saveAuthPositiveTest() {
        AuthData authData = new AuthData("validuser", "validpass");
        assertDoesNotThrow(() -> dataAccess.saveAuth(authData));
    }

    @Test
    public void saveAuthNegativeTest() {
        AuthData authData = new AuthData("validuser", null);
        assertThrows(MySQLDataAccessException.class, () -> dataAccess.saveAuth(authData));
    }
}
