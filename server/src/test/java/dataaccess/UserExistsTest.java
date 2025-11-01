package dataaccess;

import static org.junit.jupiter.api.Assertions.*;

import datamodel.UserData;
import org.junit.jupiter.api.*;
import service.RegisterService;

public class UserExistsTest {

    private DataAccess dataAccess;

    @BeforeEach
    void setUp() throws DataAccessException {
        dataAccess = new MySQLDataAccess();
        dataAccess.clear();
    }

    @Test
    public void userExistsPositiveTest() throws Exception {
        var registerService = new RegisterService(dataAccess);
        registerService.register(new UserData("validUsername", "validPassword", "email@gmail.com"));
        assertTrue(dataAccess.userExists("validUsername"));
    }

    @Test
    public void userExistsNegativeTest() throws Exception {
        var registerService = new RegisterService(dataAccess);
        registerService.register(new UserData("validUsername", "validPassword", "email@gmail.com"));
        assertFalse(dataAccess.userExists("invalidUsername"));
    }
}
