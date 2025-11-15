package client;

import chess.ChessGame;
import datamodel.*;
import org.junit.jupiter.api.*;
import server.Server;
import service.LogoutService;
import ui.FacadeException;
import ui.ServerFacade;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade(port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @BeforeEach
    void reset() {
        facade.clear();
    }

    @Test
    void registerTestPositive() throws Exception {
        var authData = facade.register(new UserData("username", "password", "example@email.com"));
        assertTrue(authData.authToken().length() > 10);
    }

    @Test
    void registerTestNegative() throws Exception {
        FacadeException e = assertThrows(FacadeException.class, () ->
                facade.register(new UserData("username", "password", null)));
        assertEquals("Error: Unable to save user", e.getMessage());
    }

    @Test
    void loginTestPositive() throws Exception {
        facade.register(new UserData("username", "password", "example@email.com"));
        var authData = facade.login(new LoginRequest("username", "password"));
        assertTrue(authData.authToken().length() > 10);
    }

    @Test
    void loginTestNegative() throws Exception {
        facade.register(new UserData("username", "password", "example@email.com"));
        FacadeException e = assertThrows(FacadeException.class, () ->
                facade.login(new LoginRequest("username", "pasword")));
        assertEquals("Error: unauthorized", e.getMessage());
    }

    @Test
    void logoutTestPositive() throws Exception {
        var authData = facade.register(new UserData("username", "password", "example@email.com"));
        assertDoesNotThrow(() -> facade.logout(authData));
    }

    @Test
    void logoutTestNegative() throws Exception {
        FacadeException e = assertThrows(FacadeException.class, () ->
                facade.logout(new AuthData("username", "fakeAuth")));
        assertEquals("Error: unauthorized", e.getMessage());
    }

    @Test
    void clearTestPositive() throws Exception {
        var authData = facade.register(new UserData("username", "password", "example@email.com"));
        facade.createGame("FunGame", authData.authToken());
        GameListResponse gameList = facade.listGames(authData);
        assertDoesNotThrow(() -> facade.clear());
    }

    @Test
    void createGameTestPositive() throws Exception {
        var authData = facade.register(new UserData("username", "password", "example@email.com"));
        assertDoesNotThrow(() -> facade.createGame("FunGame", authData.authToken()));
    }

    @Test
    void createGameTestNegative() throws Exception {
        FacadeException e = assertThrows(FacadeException.class, () ->
                facade.createGame("FunGame", "fakeAuth"));
        assertEquals("Error: unauthorized", e.getMessage());
    }

    @Test
    void listGamesTestPositive() throws Exception {
        var authData = facade.register(new UserData("username", "password", "example@email.com"));
        facade.createGame("FunGame", authData.authToken());
        GameListResponse gameList = facade.listGames(authData);
        assertEquals("FunGame", gameList.games().get(0).gameName());
    }

    @Test
    void listGamesTestNegative() throws Exception {
        FacadeException e = assertThrows(FacadeException.class, () ->
                facade.listGames(new AuthData("username", "fakeAuth")));
        assertEquals("Error: unauthorized", e.getMessage());
    }

    @Test
    void joinGameTestPositive() throws Exception {
        var authData = facade.register(new UserData("username", "password", "example@email.com"));
        var gameResponse = facade.createGame("FunGame", authData.authToken());
        GameListResponse gameList = facade.listGames(authData);
        int id = gameResponse.gameID();
        assertDoesNotThrow(() -> facade.joinGame(authData.authToken(), id, ChessGame.TeamColor.WHITE));
    }

    @Test
    void joinGameTestNegative() throws Exception {
        var authData = facade.register(new UserData("username", "password", "example@email.com"));
        facade.createGame("FunGame", authData.authToken());
        GameListResponse gameList = facade.listGames(authData);
        FacadeException e = assertThrows(FacadeException.class, () ->
                facade.joinGame(authData.authToken(), 5, ChessGame.TeamColor.WHITE));
        assertEquals("Error: bad request", e.getMessage());
    }
}
