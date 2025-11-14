package service;

import chess.ChessGame;
import commonMisconceptions.BadRequestException;
import dataaccess.DataAccess;
import dataaccess.MySQLDataAccessException;
import datamodel.GameData;

public class CreateGameService {
    private final DataAccess dataAccess;

    public CreateGameService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public int createGame(String authToken, String gameName) throws MySQLDataAccessException {
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
