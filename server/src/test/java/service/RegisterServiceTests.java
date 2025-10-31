package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import dataaccess.MySQLDataAccess;
import datamodel.UserData;
import org.junit.jupiter.api.*;
import org.mindrot.jbcrypt.BCrypt;

import static org.junit.jupiter.api.Assertions.*;

public class RegisterServiceTests {

    private DataAccess dataAccess;

    @BeforeEach
    void setUp() throws DataAccessException {
        dataAccess = new MySQLDataAccess();
        dataAccess.clear();
    }

    @Test
    public void registerTest() throws Exception {
        var registerService = new RegisterService(dataAccess);

        var res = registerService.register(new UserData("validUsername", "validPassword", "email@gmail.com"));
        assertNotNull(res);
        assertEquals("validUsername", res.username());
        assertNotNull(res.authToken());
        assertTrue(dataAccess.userExists("validUsername"));
        assertTrue(BCrypt.checkpw("validPassword", dataAccess.getPass("validUsername")));
    }

    @Test
    public void registerTestAlreadyTaken() throws Exception {
        var registerService = new RegisterService(dataAccess);
        registerService.register(new UserData("validUsername", "validPassword", "email@gmail.com"));
        UserData duplicateUSer = new UserData("validUsername", "pass", "email2@gmail.com");
        DataAccessException e = assertThrows(DataAccessException.class, () -> registerService.register(duplicateUSer));
        assertEquals("Error: already taken", e.getMessage());
    }

}
