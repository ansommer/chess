package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import dataaccess.MySQLDataAccess;
import datamodel.AuthData;
import datamodel.UserData;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class ListGamesServiceTests {
    private DataAccess dataAccess;

    @BeforeEach
    void setUp() throws DataAccessException {
        dataAccess = new MySQLDataAccess();
        dataAccess.clear();
    }

    @Test
    public void listGamesTest() throws Exception {
        var createGameService = new CreateGameService(dataAccess);
        var listGamesService = new ListGamesService(dataAccess);
        var registerService = new RegisterService(dataAccess);
        var user = new UserData("validUsername", "validPassword", "email@gmail.com");

        AuthData registerResult = registerService.register(user);
        String authToken = registerResult.authToken();
        createGameService.createGame(authToken, "AwesomeGame");

        var res = listGamesService.listGames(authToken);
        assertNotNull(res);
        assertNotNull(dataAccess.getGames());
    }

    @Test
    public void listGamesUnauthorizedTest() throws Exception {
        var listGamesService = new ListGamesService(dataAccess);

        String authToken = "badAuth";

        UnauthorizedException e = assertThrows(UnauthorizedException.class, () -> listGamesService.listGames(authToken));
        assertEquals("Error: unauthorized", e.getMessage());

    }

}
