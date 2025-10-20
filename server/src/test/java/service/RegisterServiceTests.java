package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import datamodel.UserData;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class RegisterServiceTests {

    private MemoryDataAccess dataAccess;

    @BeforeEach
    void setUp() {
        dataAccess = new MemoryDataAccess();
    }

    @Test
    public void registerTest() throws Exception {
        var registerService = new RegisterService(dataAccess);

        var res = registerService.register(new UserData("validUsername", "validPassword", "email@gmail.com"));
        assertNotNull(res);
        assertEquals("validUsername", res.username());
        assertNotNull(res.authToken());
        assertTrue(dataAccess.userExists("validUsername"));
        assertEquals("validPassword", dataAccess.getPass("validUsername"));
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
