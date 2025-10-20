package service;

import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import datamodel.AuthData;
import datamodel.GameData;
import datamodel.UserData;
import org.junit.jupiter.api.*;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

public class ClearServiceTests {
    private MemoryDataAccess dataAccess;

    @BeforeEach
    void setUp() {
        dataAccess = new MemoryDataAccess();
    }

    @Test
    public void createGameTest() throws Exception {
        var createGameService = new CreateGameService(dataAccess);
        var registerService = new RegisterService(dataAccess);
        var loginService = new LoginService(dataAccess);
        var joinService = new JoinService(dataAccess);
        var clearService = new ClearService(dataAccess);
        var user = new UserData("validUsername", "validPassword", "email@gmail.com");

        AuthData registerResult = registerService.register(user);
        String authToken = registerResult.authToken();
        loginService.login(user);
        createGameService.createGame(authToken, "AwesomeGame");
        joinService.join(authToken, 1, ChessGame.TeamColor.WHITE);

        var res = clearService.clear();

        assertNotNull(res);
        assertTrue(dataAccess.getGames().isEmpty());
        assertFalse(dataAccess.authExists(authToken));

    }
}
