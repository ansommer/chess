package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.MySQLDataAccess;
import datamodel.AuthData;
import datamodel.UserData;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class LogoutServiceTests {
    private DataAccess dataAccess;

    @BeforeEach
    void setUp() throws DataAccessException {
        dataAccess = new MySQLDataAccess();
        dataAccess.clear();
    }

    @Test
    public void logoutTest() throws Exception {
        var loginService = new LoginService(dataAccess);
        var registerService = new RegisterService(dataAccess);
        var logoutService = new LogoutService(dataAccess);
        var user = new UserData("validUsername", "validPassword", "email@gmail.com");

        registerService.register(user);
        AuthData loginResult = loginService.login(user);
        String authToken = loginResult.authToken();
        var res = logoutService.logout(authToken);

        assertNotNull(res); //technically?
        assertFalse(dataAccess.authExists(authToken));
    }

    @Test
    public void logoutTestNoUser() throws Exception {
        var logoutService = new LogoutService(dataAccess);
        var user = new UserData("validUsername", "validPassword", "email@gmail.com");

        String authToken = "1234";
        UnauthorizedException e = assertThrows(UnauthorizedException.class, () -> logoutService.logout(authToken));
        assertEquals("Error: unauthorized", e.getMessage());

    }


}
