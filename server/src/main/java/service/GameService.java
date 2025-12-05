package service;

import dataaccess.DataAccess;
import websocket.ConnectionManager;
import websocket.commands.UserGameCommand;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import static websocket.messages.ServerMessage.ServerMessageType.LOAD_GAME;


public class GameService {

    private final DataAccess dataAccess;
    private final ConnectionManager connections;

    public GameService(DataAccess dataAccess, ConnectionManager connectionManager) {
        this.dataAccess = dataAccess;
        this.connections = connectionManager;
    }

    public void handleUserCommand(UserGameCommand userGameCommand, Session session) throws Exception {
        System.out.println("Step 6");

        UserGameCommand.CommandType type = userGameCommand.getCommandType();
        String auth = userGameCommand.getAuthToken();
        String username = dataAccess.getUserFromAuthToken(auth);
        switch (type) {
            case CONNECT -> handleConnect(username, session);
            case MAKE_MOVE -> handleMakeMove();
            case LEAVE -> handleLeave(session);
            case RESIGN -> handleResign(session);
        }
    }

    private void handleConnect(String username, Session session) throws Exception {
        System.out.println("Step 7");
        connections.add(session);
        ServerMessage serverMessage = new ServerMessage(LOAD_GAME);
        connections.broadcast(session, serverMessage);
    }

    private void handleMakeMove() {

    }

    private void handleLeave(Session session) throws Exception {
        connections.remove(session);
    }

    private void handleResign(Session session) throws Exception {
        connections.remove(session);
    }

}
