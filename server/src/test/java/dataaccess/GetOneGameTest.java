package dataaccess;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.CreateGameService;

import java.sql.Connection;
import java.sql.PreparedStatement;

import static org.junit.jupiter.api.Assertions.*;

public class GetOneGameTest {

    private DataAccess dataAccess;

    @BeforeEach
    void setUp() throws DataAccessException {
        dataAccess = new MySQLDataAccess();
        dataAccess.clear();
    }

    @Test
    public void getOneGamePositiveTest() throws Exception {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "INSERT INTO authTokens (authToken, username) VALUES ('randomAuth123', 'validUsername')")) {
            ps.executeUpdate();
        }

        var createGameService = new CreateGameService(dataAccess);
        createGameService.createGame("randomAuth123", "CoolGame");
        assertDoesNotThrow(() -> dataAccess.getOneGame(1));
        var res = dataAccess.getOneGame(1);
        assertEquals("CoolGame", res.gameName());
    }

    @Test
    public void getOneGameNegativeTest() {
        assertNull(dataAccess.getOneGame(1));
    }
}
