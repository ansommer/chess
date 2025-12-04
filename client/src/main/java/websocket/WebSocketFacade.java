package websocket;

import com.google.gson.Gson;

import jakarta.websocket.*;
import websocket.commands.UserGameCommand;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketFacade extends Endpoint {
    Session session;

    public WebSocketFacade() throws Exception {
        URI uri = new URI("ws://localhost:8080/ws");
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        session = container.connectToServer(this, uri);

        this.session.addMessageHandler(new MessageHandler.Whole<String>() {
            public void onMessage(String message) {
                System.out.println(message);
            }
        });

    }


    public void send(UserGameCommand userGameCommand) throws IOException {
        session.getBasicRemote().sendText(new Gson().toJson(userGameCommand)); //this just sends the message back
    }

    public void onOpen(Session session, EndpointConfig config) {
    }
}
