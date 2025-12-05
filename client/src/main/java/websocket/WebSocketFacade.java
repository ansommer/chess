package websocket;

import com.google.gson.Gson;

import jakarta.websocket.*;
import ui.GameUI;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import static websocket.messages.ServerMessage.ServerMessageType.*;

public class WebSocketFacade extends Endpoint {
    Session session;

    public WebSocketFacade(GameUI gameUI) throws Exception {
        URI uri = new URI("ws://localhost:8080/ws");
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        session = container.connectToServer(this, uri);

        this.session.addMessageHandler(new MessageHandler.Whole<String>() {
            public void onMessage(String message) {
                //System.out.println("Step 10");
                ServerMessage serverMessage = new Gson().fromJson(message, ServerMessage.class);
                try {
                    gameUI.handleServerMessage(serverMessage);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });

    }


    public void send(UserGameCommand userGameCommand) throws IOException {
        //System.out.println("Step 2");
        session.getBasicRemote().sendText(new Gson().toJson(userGameCommand));
    }

    public void onOpen(Session session, EndpointConfig config) {
    }
}
