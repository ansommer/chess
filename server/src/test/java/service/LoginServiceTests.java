package service;

import dataaccess.MemoryDataAccess;
import datamodel.UserData;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class LoginServiceTests {
    private MemoryDataAccess dataAccess;

    @BeforeEach
    void setUp() {
        dataAccess = new MemoryDataAccess();
    }

    @Test
    public void loginTest() throws Exception {
        var loginService = new LoginService(dataAccess);
        var registerService = new RegisterService(dataAccess);
        var user = new UserData("validUsername", "validPassword", "email@gmail.com");
        registerService.register(user);
        var res = loginService.login(user);
        assertNotNull(res);
        assertEquals("validUsername", res.username());
        assertNotNull(res.authToken());
    }

    @Test
    public void registerTestAlreadyTaken() throws Exception {
        var loginService = new LoginService(dataAccess);
        var user = new UserData("validUsername", "validPassword", "email@gmail.com");
        UnauthorizedException e = assertThrows(UnauthorizedException.class, () -> loginService.login(user));
        assertEquals("Error: unauthorized", e.getMessage());
    }
}
