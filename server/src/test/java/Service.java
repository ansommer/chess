import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import datamodel.UserData;
import org.eclipse.jetty.server.Authentication;
import org.junit.jupiter.api.*;
import passoff.model.*;
import server.Server;
import service.*;

import java.net.HttpURLConnection;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class Service {

    private MemoryDataAccess dataAccess;
    private RegisterService registerService;

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
        //String expectedResult = "{ \"username\": \"validUsername\", \"password\": \"validPassword\", \"email\": \"email@gmail.com\" }";
        //assertEquals(expectedResult, register());
        assertTrue(dataAccess.userExists("validUsername"));
        assertEquals("validPassword", dataAccess.getPass("validUsername"));
    }

    @Test
    public void registerTestAlreadyTaken() throws Exception {
        var registerService = new RegisterService(dataAccess);
        registerService.register(new UserData("validUsername", "validPassword", "email@gmail.com"));
        DataAccessException e = assertThrows(DataAccessException.class, () -> registerService.register(new UserData("validUsername", "pass", "email2@gmail.com")));

        assertEquals("Error: already taken", e.getMessage());
    }

}
