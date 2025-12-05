package service;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.DataAccess;
import datamodel.GameData;
import websocket.ConnectionManager;
import websocket.commands.UserGameCommand;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.LoadGameMessage;
import websocket.messages.ServerMessage;

import static websocket.messages.ServerMessage.ServerMessageType.*;


public class GameService {

    private final DataAccess dataAccess;
    private final ConnectionManager connections;
    private ServerMessage serverMessage;
    private GameData gameData;
    private int gameID;
    private String username;

    public GameService(DataAccess dataAccess, ConnectionManager connectionManager) {
        this.dataAccess = dataAccess;
        this.connections = connectionManager;
    }

    public void handleUserCommand(UserGameCommand userGameCommand, Session session) throws Exception {
        System.out.println("Step 6");

        UserGameCommand.CommandType type = userGameCommand.getCommandType();
        String auth = userGameCommand.getAuthToken();
        username = dataAccess.getUserFromAuthToken(auth);
        gameID = userGameCommand.getGameID();
        gameData = dataAccess.getOneGame(gameID);
        switch (type) {
            case CONNECT -> handleConnect(session);
            case MAKE_MOVE -> handleMakeMove();
            case LEAVE -> handleLeave(session);
            case RESIGN -> handleResign(session);
        }
    }

    private void handleConnect(Session session) throws Exception {
        System.out.println("Step 7");
        connections.add(session);
        String message;
        //I think this will broadcast to all the games which is why I need the map

        serverMessage = new ServerMessage(LOAD_GAME);
        message = new Gson().toJson(serverMessage);
        connections.broadcast(session, message, gameData, null);
        serverMessage = new ServerMessage(NOTIFICATION);
        message = new Gson().toJson(serverMessage);

        String notification = "\n" + username + " joined the game as " + getTeamColor(username);
        connections.broadcast(session, message, gameData, notification);
    }

    private void handleMakeMove() {

    }

    private void handleLeave(Session session) throws Exception {
        /*GameData gameData = dataAccess.getOneGame(gameID);
        serverMessage = new LoadGameMessage(gameData, username);
        String message = new Gson().toJson(serverMessage);
        connections.broadcast(session, message, gameData);*/
        connections.remove(session);
    }

    private void handleResign(Session session) throws Exception {
        connections.remove(session);
    }


    private String getTeamColor(String username) {
        if (gameData.whiteUsername() != null && gameData.whiteUsername().equals(username)) {
            return "white";
        } else if (gameData.blackUsername() != null && gameData.blackUsername().equals(username)) {
            return "black";
        }
        return "observer";
    }
}
