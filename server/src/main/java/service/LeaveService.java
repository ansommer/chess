package service;

import chess.ChessGame;
import dataaccess.DataAccess;
import dataaccess.MySQLDataAccessException;
import datamodel.GameData;

import static chess.ChessGame.TeamColor.BLACK;
import static chess.ChessGame.TeamColor.WHITE;

public class LeaveService {
    private final DataAccess dataAccess;

    public LeaveService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public String leave(String authToken, int gameID, ChessGame.TeamColor playerColor) throws MySQLDataAccessException {
        GameData game = dataAccess.getOneGame(gameID);
        String username = dataAccess.getUserFromAuthToken(authToken);

        if (authToken == null || !dataAccess.authExists(authToken)) {
            throw new UnauthorizedException("Error: unauthorized");
        } else if (game == null) {
            throw new LogoutService.BadRequestException("Error: bad request");
        }
        if (playerColor != BLACK && playerColor != WHITE) {
            System.out.print("observer");
        } else {
            if (playerColor.equals(WHITE)) {
                if (game.whiteUsername() != null) {
                    GameData updatedGame = new GameData(game.gameID(), null, game.blackUsername(), game.gameName(), game.game());
                    dataAccess.createGame(updatedGame);
                } else {
                    throw new TakenException("Error: no one is there to leave");
                }
            } else { //black is the only other option so it didn't like that i was checking that
                if (game.blackUsername() != null) {
                    GameData updatedGame = new GameData(game.gameID(), game.whiteUsername(), null, game.gameName(), game.game());
                    dataAccess.createGame(updatedGame);
                } else {
                    throw new TakenException("Error: no one is there to leave");
                }
            }
        }

        return "{}";
    }
}
