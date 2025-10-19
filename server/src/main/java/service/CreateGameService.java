package service;

import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import datamodel.AuthData;
import datamodel.GameData;
import datamodel.UserData;

public class CreateGameService {
    private final MemoryDataAccess dataAccess;

    public CreateGameService(MemoryDataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public int createGame(String authToken, String gameName) {
        if (authToken == null || !dataAccess.authExists(authToken)) {
            throw new UnauthorizedException("Error: unauthorized");
        } else if (gameName == null) { //I think that's what I'm supposed to do here
            throw new BadRequestException("Error: bad request");
        }
        int gameId = dataAccess.getNextGameId();
        GameData newGame = new GameData(gameId, null, null, gameName, new ChessGame());
        dataAccess.createGame(newGame);
        return gameId;
    }
}
