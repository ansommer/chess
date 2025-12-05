package websocket.messages;

import datamodel.GameData;

public class LoadGameMessage extends ServerMessage {
    private final GameData game;


    public LoadGameMessage() {
        super(ServerMessageType.LOAD_GAME);
        this.game = null;
    }

    public LoadGameMessage(GameData game) {
        super(ServerMessageType.LOAD_GAME);
        this.game = game;
    }


    public GameData getGame() {
        return game;
    }

}