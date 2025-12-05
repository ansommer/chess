package websocket.messages;

import datamodel.GameData;

public class LoadGameMessage extends ServerMessage {

    private final GameData game;

    public LoadGameMessage(GameData game) {
        super(ServerMessageType.LOAD_GAME);
        this.game = game;
    }


    public enum LoadGameType {
        LOAD_MY_GAME,
        LOAD_OTHER_USER_JOIN,
        LOAD_OTHER_USER_LEAVE,
        LOAD_OBSERVER_JOIN,
        LOAD_AFTER_MOVE
    }
}
