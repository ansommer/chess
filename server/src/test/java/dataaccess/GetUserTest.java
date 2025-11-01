package dataaccess;

import datamodel.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;
import service.RegisterService;

import static org.junit.jupiter.api.Assertions.*;

public class GetUserTest {

    private DataAccess dataAccess;

    @BeforeEach
    void setUp() throws DataAccessException {
        dataAccess = new MySQLDataAccess();
        dataAccess.clear();
    }

    @Test
    public void getUserPositiveTest() throws Exception {
        var registerService = new RegisterService(dataAccess);
        registerService.register(new UserData("validUsername", "validPassword", "email@gmail.com"));
        assertDoesNotThrow(() -> dataAccess.getUser("validUsername"));
        UserData res = dataAccess.getUser("validUsername");
        assertEquals("validUsername", res.username());
        assertTrue(BCrypt.checkpw("validPassword", res.password()));
        assertEquals("email@gmail.com", res.email());
    }

    @Test
    public void getUserNegativeTest() throws Exception {
        var registerService = new RegisterService(dataAccess);
        registerService.register(new UserData("validUsername", "validPassword", "email@gmail.com"));
        assertNull(dataAccess.getUser("invalidUsername"));
    }
}
