package websocket;

import com.google.gson.Gson;
import datamodel.GameData;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

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

    public void broadcast(Session mySession, String jsonServerMessage, GameData gameData, String notification, boolean sendToAll) throws IOException {
        //I think this will broadcast to all the games which is why I need the map
        //Sends only to itself unless sendToAll
        ServerMessage serverMessage = new Gson().fromJson(jsonServerMessage, ServerMessage.class);
        String message;
        if (serverMessage.getServerMessageType() == LOAD_GAME) {
            LoadGameMessage loadGameMessage = new LoadGameMessage(gameData);
            message = new Gson().toJson(loadGameMessage);
            for (Session c : connections.values()) {
                if (c.isOpen()) {
                    if (c.equals(mySession) || sendToAll) {
                        c.getRemote().sendString(message);
                    }
                }
            }
        } else if (serverMessage.getServerMessageType() == NOTIFICATION) {
            //Sends to all other clients unless sendToAll
            NotificationMessage notificationMessage = new NotificationMessage(notification);
            message = new Gson().toJson(notificationMessage);
            for (Session c : connections.values()) {
                if (c.isOpen()) {
                    if (!c.equals(mySession) || sendToAll) {
                        c.getRemote().sendString(message);
                    }
                }
            }
        }
    }

}
