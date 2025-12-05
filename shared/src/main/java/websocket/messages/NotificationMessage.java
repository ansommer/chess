package websocket.messages;

public class NotificationMessage extends ServerMessage {
    //This is a message meant to inform a player when another player made an action.
    private final String message;

    public NotificationMessage(String message) {
        super(ServerMessage.ServerMessageType.NOTIFICATION);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
