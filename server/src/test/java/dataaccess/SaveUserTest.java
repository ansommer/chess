package dataaccess;

import datamodel.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SaveUserTest {
    private DataAccess dataAccess;

    @BeforeEach
    void setUp() throws DataAccessException {
        dataAccess = new MySQLDataAccess();
        dataAccess.clear();
    }

    @Test
    public void saveUserPositiveTest() throws Exception {
        UserData user = new UserData("validUsername", "validPassword", "email@gmail.com");
        assertDoesNotThrow(() -> dataAccess.saveUser(user));
    }

    @Test
    public void saveUserNegativeTest() throws Exception {
        UserData user = new UserData(null, "validPassword", "email@gmail.com");
        assertThrows(MySQLDataAccessException.class, () -> dataAccess.saveUser(user));
    }

}
