package dataaccess;

import static org.junit.jupiter.api.Assertions.*;

import dataaccess.DataAccess;
import datamodel.UserData;
import org.junit.jupiter.api.*;
import org.mindrot.jbcrypt.BCrypt;
import service.RegisterService;

public class userExistsTest {

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
