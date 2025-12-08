package websocket;

import com.google.gson.Gson;

import jakarta.websocket.*;
import ui.GameUI;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;

import java.io.IOException;
import java.net.URI;

import static websocket.commands.UserGameCommand.CommandType.MAKE_MOVE;

public class WebSocketFacade extends Endpoint {
    Session session;

    public WebSocketFacade(GameUI gameUI) throws Exception {
        URI uri = new URI("ws://localhost:8080/ws");
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        session = container.connectToServer(this, uri);

        this.session.addMessageHandler(new MessageHandler.Whole<String>() {
            public void onMessage(String message) {
                try {
                    gameUI.handleServerMessage(message);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });

    }


    public void send(String commandJson) throws IOException {
        UserGameCommand userGameCommand = new Gson().fromJson(commandJson, UserGameCommand.class);
        if (userGameCommand.getCommandType().equals(MAKE_MOVE)) {
            userGameCommand = new Gson().fromJson(commandJson, MakeMoveCommand.class);
        }
        session.getBasicRemote().sendText(new Gson().toJson(userGameCommand));
    }

    public void onOpen(Session session, EndpointConfig config) {
    }
}
