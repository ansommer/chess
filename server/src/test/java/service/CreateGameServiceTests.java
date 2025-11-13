package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.MySQLDataAccess;
import datamodel.AuthData;
import datamodel.GameData;
import datamodel.UserData;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class CreateGameServiceTests {
    private DataAccess dataAccess;

    @BeforeEach
    void setUp() throws DataAccessException {
        dataAccess = new MySQLDataAccess();
        dataAccess.clear();
    }

    @Test
    public void createGameTest() throws Exception {
        var createGameService = new CreateGameService(dataAccess);
        var registerService = new RegisterService(dataAccess);
        var user = new UserData("validUsername", "validPassword", "email@gmail.com");

        AuthData registerResult = registerService.register(user);
        String authToken = registerResult.authToken();


        var res = createGameService.createGame(authToken, "AwesomeGame");
        assertNotNull(res);
        GameData game = dataAccess.getOneGame(1);
        assertEquals(game.gameName(), "AwesomeGame");
    }

    @Test
    public void createGameNoNameTest() throws Exception {
        var createGameService = new CreateGameService(dataAccess);
        var registerService = new RegisterService(dataAccess);
        var user = new UserData("validUsername", "validPassword", "email@gmail.com");

        AuthData registerResult = registerService.register(user);
        String authToken = registerResult.authToken();

        BadRequestException e = assertThrows(BadRequestException.class, () -> createGameService.createGame(authToken, null));
        assertEquals("Error: bad request", e.getMessage());

    }
}
