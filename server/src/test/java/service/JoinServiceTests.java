package service;

import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import datamodel.AuthData;
import datamodel.GameData;
import datamodel.UserData;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class JoinServiceTests {
    private MemoryDataAccess dataAccess;

    @BeforeEach
    void setUp() {
        dataAccess = new MemoryDataAccess();
    }

    @Test
    public void joinTest() throws Exception {
        var createGameService = new CreateGameService(dataAccess);
        var registerService = new RegisterService(dataAccess);
        var joinService = new JoinService(dataAccess);
        var user = new UserData("validUsername", "validPassword", "email@gmail.com");

        AuthData registerResult = registerService.register(user);
        String authToken = registerResult.authToken();
        createGameService.createGame(authToken, "AwesomeGame");

        var res = joinService.join(authToken, 1, ChessGame.TeamColor.WHITE);
        assertNotNull(res);
        GameData game = dataAccess.getOneGame(1);
        assertEquals(game.whiteUsername(), "validUsername");
    }

    @Test
    public void joinBadColorTest() throws Exception {
        var createGameService = new CreateGameService(dataAccess);
        var registerService = new RegisterService(dataAccess);
        var joinService = new JoinService(dataAccess);
        var user = new UserData("validUsername", "validPassword", "email@gmail.com");

        AuthData registerResult = registerService.register(user);
        String authToken = registerResult.authToken();
        createGameService.createGame(authToken, "AwesomeGame");


        BadRequestException e = assertThrows(BadRequestException.class, () -> joinService.join(authToken, 1, null));
        assertEquals("Error: bad request", e.getMessage());
    }

}
