package dataaccess;

import datamodel.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;


public class GetGamesTest {

    private DataAccess dataAccess;

    @BeforeEach
    void setUp() throws DataAccessException {
        dataAccess = new MySQLDataAccess();
        dataAccess.clear();
    }

    @Test
    public void getGamesPositiveTest() throws Exception {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     """
                             INSERT INTO games (gameID, whiteUsername, blackUsername, gameName, game)
                             VALUES (
                                 1,
                                 NULL,
                                 NULL,
                                 'coolGame',
                                 '{"teamTurn":"WHITE","bKingOrRook1Moved":false,"bKingOrRook2Moved":false,"wKingOrRook1Moved":false,"wKingOrRook2Moved":false}'
                             )
                             """)) {

            ps.executeUpdate();
        }
        assertDoesNotThrow(() -> dataAccess.getGames());
        HashMap<String, GameData> games = dataAccess.getGames();
        assertEquals(games.size(), 1);
        assertEquals("coolGame", games.get("1").gameName());

    }

    @Test
    public void getGamesNegativeTest() throws Exception {
        HashMap<String, GameData> games = dataAccess.getGames();
        assertTrue(games.isEmpty());
    }
}
