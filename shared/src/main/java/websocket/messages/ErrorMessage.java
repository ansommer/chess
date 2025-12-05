package websocket.messages;

public record ErrorMessage(String errorMessage) {
    //This message is sent to a client when it sends an invalid command.
    // The message must include the word Error.
}
