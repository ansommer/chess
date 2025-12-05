package websocket;

import com.google.gson.Gson;

import jakarta.websocket.*;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import static websocket.messages.ServerMessage.ServerMessageType.*;

public class WebSocketFacade extends Endpoint {
    Session session;

    public WebSocketFacade() throws Exception {
        URI uri = new URI("ws://localhost:8080/ws");
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        session = container.connectToServer(this, uri);

        this.session.addMessageHandler(new MessageHandler.Whole<String>() {
            public void onMessage(String message) {
                ServerMessage serverMessage = new Gson().fromJson(message, ServerMessage.class);
                ServerMessage.ServerMessageType messageType = serverMessage.getServerMessageType();
                //somehow i need the gameID and a gameData object?
                if (messageType == LOAD_GAME) {
                    System.out.println("\n Some user entered the game");
                }

            }
        });

    }


    public void send(UserGameCommand userGameCommand) throws IOException {
        session.getBasicRemote().sendText(new Gson().toJson(userGameCommand));
    }

    public void onOpen(Session session, EndpointConfig config) {
    }
}
