package websocket.messages;

import datamodel.GameData;

public class ErrorMessage extends ServerMessage {
    //This message is sent to a client when it sends an invalid command.
    // The message must include the word Error.

    private final String errorMessage;

    public ErrorMessage(String errorMessage) {
        super(ServerMessage.ServerMessageType.ERROR);
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
