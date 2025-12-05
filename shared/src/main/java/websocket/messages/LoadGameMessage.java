package websocket.messages;

import datamodel.GameData;

public record LoadGameMessage(GameData game) {
    //maybe a state with who's turn, checkmate, and stalemate?
    //Used by the server to send the current game state to a client.
    // When a client receives this message, it will redraw the chess board.
}
