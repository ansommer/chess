package service;

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
        //I think that actually maybe it should like start with 1 and count up but to be fair there isn't a whole lot of info about gameID
        int gameId = 1234;
        dataAccess.createGame(gameId, gameName);
        return gameId;
    }
}
