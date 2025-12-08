package websocket;

import com.google.gson.Gson;
import datamodel.GameData;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.*;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

import static websocket.messages.SendTo.ALL;
import static websocket.messages.SendTo.ME;
import static websocket.messages.ServerMessage.ServerMessageType.*;

public class ConnectionManager {
    public final ConcurrentHashMap<Session, Session> connections = new ConcurrentHashMap<>();

    public void add(Session session) {
        connections.put(session, session);
    }

    public void remove(Session session) {
        connections.remove(session);
    }

    public void broadcast(Session mySession, String jsonServerMessage, GameData gameData, String notification,
                          SendTo sendTo) throws IOException {
        //I think this will broadcast to all the games which is why I need the map
        //Sends only to itself unless sendToAll
        ServerMessage serverMessage = new Gson().fromJson(jsonServerMessage, ServerMessage.class);
        String message;
        if (serverMessage.getServerMessageType() == LOAD_GAME) {
            LoadGameMessage loadGameMessage = new LoadGameMessage(gameData);
            message = new Gson().toJson(loadGameMessage);
            for (Session c : connections.values()) {
                if (c.isOpen()) {
                    if (c.equals(mySession) || (sendTo == ALL)) {
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
                    if (!c.equals(mySession) || (sendTo == ALL)) {
                        c.getRemote().sendString(message);
                    } else if ((sendTo == ME) && c.equals(mySession)) {
                        c.getRemote().sendString(message);
                    }
                }
            }
        } else if (serverMessage.getServerMessageType() == ERROR) {
            ErrorMessage errorMessage = new ErrorMessage(notification);

            message = new Gson().toJson(errorMessage);

            for (Session c : connections.values()) {
                if (c.isOpen()) {
                    if (c.equals(mySession) || (sendTo == ALL)) {
                        c.getRemote().sendString(message);
                    }
                }
            }
        }
    }

}
