package dataaccess;

import chess.ChessGame;
import datamodel.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.CreateGameService;

import static org.junit.jupiter.api.Assertions.*;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class CreateGameTest {

    private DataAccess dataAccess;

    @BeforeEach
    void setUp() throws DataAccessException {
        dataAccess = new MySQLDataAccess();
        dataAccess.clear();
    }

    @Test
    public void createGamePositiveTest() throws Exception {
        GameData game = new GameData(1, null, null, "coolGame", new ChessGame());
        assertDoesNotThrow(() -> dataAccess.createGame(game));

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT gameName FROM games WHERE gameID = ?")) {
            ps.setInt(1, 1);
            ResultSet rs = ps.executeQuery();
            assertTrue(rs.next(), "Game should exist in the database");
            assertEquals("coolGame", rs.getString("gameName"));
        }
    }

    @Test
    public void createGameNegativeTest() {
        GameData game = new GameData(1, null, null, null, new ChessGame());
        assertThrows(MySQLDataAccessException.class, () -> dataAccess.createGame(game));
    }
}
