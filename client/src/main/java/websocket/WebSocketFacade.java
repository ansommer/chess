package websocket;

import com.google.gson.Gson;

import jakarta.websocket.*;

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
                System.out.println("hello");
            }
        });

    }


    public void send(String message) throws IOException {
        session.getBasicRemote().sendText(message + "this was websocket"); //this just sends the message back
    }

    public void onOpen(Session session, EndpointConfig config) {
    }
}
