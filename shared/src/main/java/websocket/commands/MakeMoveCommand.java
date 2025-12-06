package websocket.commands;

import chess.ChessMove;
import datamodel.GameData;

public class MakeMoveCommand extends UserGameCommand {
    private final ChessMove move;
    private final GameData gameData;

    public MakeMoveCommand(ChessMove move, String authToken, Integer gameID, GameData gameData) {
        super(CommandType.MAKE_MOVE, authToken, gameID);
        this.move = move;
        this.gameData = gameData;
    }

    public ChessMove getMove() {
        return move;
    }

    public GameData getGameData() {
        return gameData;
    }
}

