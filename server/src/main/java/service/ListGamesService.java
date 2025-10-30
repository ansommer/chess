package service;

import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
import datamodel.GameData;
import datamodel.GameListResponse;

import java.util.ArrayList;
import java.util.HashMap;

public class ListGamesService {
    private final DataAccess dataAccess;

    public ListGamesService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public GameListResponse listGames(String authToken) {
        if (authToken == null || !dataAccess.authExists(authToken)) {
            throw new UnauthorizedException("Error: unauthorized");
        }

        HashMap<String, GameData> games = dataAccess.getGames();
        ArrayList<GameData> gamesList = new ArrayList<>(games.values());
        return new GameListResponse(gamesList);
    }
}
