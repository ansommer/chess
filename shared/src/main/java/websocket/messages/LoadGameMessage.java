package websocket.messages;

import datamodel.GameData;

public class LoadGameMessage extends ServerMessage {
    private final GameData game;
    private final LoadGameType loadGameType;
    private final String username;


    public LoadGameMessage() {
        super(ServerMessageType.LOAD_GAME);
        this.game = null;
        this.loadGameType = null;
        this.username = null;
    }

    public LoadGameMessage(GameData game, LoadGameType loadGameType, String username) {
        super(ServerMessageType.LOAD_GAME);
        this.game = game;
        this.loadGameType = loadGameType;
        this.username = username;
    }

    public enum LoadGameType {
        LOAD_MY_GAME,
        LOAD_OTHER_USER_JOIN,
        LOAD_OTHER_USER_LEAVE,
        LOAD_OBSERVER_JOIN,
        LOAD_AFTER_MOVE
    }

    public GameData getGame() {
        return game;
    }

    public String getUsername() {
        return username;
    }

    public LoadGameType getLoadGameType() {
        return this.loadGameType;
    }
}