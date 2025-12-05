package websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.LoadGameMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

import static websocket.messages.LoadGameMessage.LoadGameType.*;
import static websocket.messages.ServerMessage.ServerMessageType.*;

public class ConnectionManager {
    public final ConcurrentHashMap<Session, Session> connections = new ConcurrentHashMap<>();

    public void add(Session session) {
        System.out.println("Step 8");
        connections.put(session, session);
    }

    public void remove(Session session) {
        connections.remove(session);
    }

    public void broadcast(Session excludeSession, String jsonServerMessage) throws IOException {
        System.out.println("Step 9");
        //System.out.println(jsonServerMessage);
        ServerMessage serverMessage = new Gson().fromJson(jsonServerMessage, ServerMessage.class);
        //System.out.println(serverMessage);
        String message;
        if (serverMessage.getServerMessageType() == LOAD_GAME) {
            //System.out.println("correct");
            LoadGameMessage loadGameMessage = new Gson().fromJson(jsonServerMessage, LoadGameMessage.class);
            message = new Gson().toJson(loadGameMessage);
            if (loadGameMessage.getLoadGameType().equals(LOAD_OTHER_USER_JOIN)) {
                for (Session c : connections.values()) {
                    if (c.isOpen()) {
                        if (!c.equals(excludeSession)) {
                            c.getRemote().sendString(message);
                        } else {
                            LoadGameMessage oponentLoadGameMessage = new LoadGameMessage(loadGameMessage.getGame(),
                                    LOAD_MY_GAME, loadGameMessage.getUsername());
                            message = new Gson().toJson(oponentLoadGameMessage);
                            c.getRemote().sendString(message);
                        }
                    }
                }
            }
        } else {
            message = new Gson().toJson(serverMessage);
            //sends to everyone
            for (Session c : connections.values()) {
                if (c.isOpen()) {
                    c.getRemote().sendString(message);
                }
            }
        }

    }
}