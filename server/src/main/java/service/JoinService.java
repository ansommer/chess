package service;

import dataaccess.MemoryDataAccess;
import datamodel.GameData;
import chess.ChessGame.TeamColor;

import static chess.ChessGame.TeamColor.BLACK;
import static chess.ChessGame.TeamColor.WHITE;

public class JoinService {
    private final MemoryDataAccess dataAccess;

    public JoinService(MemoryDataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public String join(String authToken, int gameID, TeamColor playerColor) {
        GameData game = dataAccess.getOneGame(gameID);
        String username = dataAccess.getUserFromAuthToken(authToken);

        if (authToken == null || !dataAccess.authExists(authToken)) {
            throw new UnauthorizedException("Error: unauthorized");
        } else if (game == null || (playerColor != BLACK && playerColor != WHITE)) { //I think that's what I'm supposed to do here
            throw new BadRequestException("Error: bad request");
        }

        if (playerColor.equals(WHITE) && game.whiteUsername() == null) {
            GameData updatedGame = new GameData(game.gameID(), username, game.blackUsername(), game.gameName(), game.game());
            dataAccess.createGame(updatedGame);
        } else if (playerColor.equals(BLACK) && game.blackUsername() == null) {
            GameData updatedGame = new GameData(game.gameID(), game.whiteUsername(), username, game.gameName(), game.game());
            dataAccess.createGame(updatedGame);
        } else {
            throw new TakenException("Error: already taken");
        }

        return "{}";
    }
}
