package client;

import datamodel.UserData;
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
        var authData = facade.register(new UserData("player1", "password", "p1@email.com"));
        assertTrue(authData.authToken().length() > 10);
    }

    @Test
    void registerTestNegative() throws Exception {
        FacadeException e = assertThrows(FacadeException.class, () ->
                facade.register(new UserData("player1", "password", null)));
        assertEquals("Error: Unable to save user", e.getMessage());
    }
}
