package service;

import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import dataaccess.DataAccess;
import datamodel.GameData;
import websocket.ConnectionManager;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import static chess.ChessGame.TeamColor.BLACK;
import static chess.ChessGame.TeamColor.WHITE;
import static websocket.commands.UserGameCommand.CommandType.MAKE_MOVE;
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

    public void handleUserCommand(String message, Session session) throws Exception {
        System.out.println("Step 6");
        UserGameCommand userGameCommand = new Gson().fromJson(message, UserGameCommand.class);
        UserGameCommand.CommandType type = userGameCommand.getCommandType();
        String auth = userGameCommand.getAuthToken();
        username = dataAccess.getUserFromAuthToken(auth);
        gameID = userGameCommand.getGameID();
        gameData = dataAccess.getOneGame(gameID);
        switch (type) {
            case CONNECT -> handleConnect(session);
            case MAKE_MOVE -> handleMakeMove(new Gson().fromJson(message, MakeMoveCommand.class), session);
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
        connections.broadcast(session, message, gameData, null, false);
        serverMessage = new ServerMessage(NOTIFICATION);
        message = new Gson().toJson(serverMessage);

        String notification = "\n" + username + " joined the game as " + getTeamColor(username);
        connections.broadcast(session, message, gameData, notification, false);
    }

    private void handleMakeMove(MakeMoveCommand makeMoveCommand, Session session) throws Exception {
        ChessMove chessMove = makeMoveCommand.getMove();

        gameData = makeMoveCommand.getGameData();
        ChessGame game = gameData.game();
        dataAccess.updateGame(gameData);
        try {
            game.makeMove(chessMove); //may need to be in a try catch?
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        serverMessage = new LoadGameMessage(gameData);
        String message = new Gson().toJson(serverMessage);
        connections.broadcast(session, message, gameData, null, true);

        String notification = chessMove.getStartPosition().getColumn() + chessMove.getStartPosition().getRow() +
                " moved to " + chessMove.getEndPosition().getColumn() + chessMove.getEndPosition().getRow();
        serverMessage = new NotificationMessage(notification);
        message = new Gson().toJson(serverMessage);
        connections.broadcast(session, message, gameData, notification, false);
        //if it's check, checkmate, or stalemate, a message is sent to everyone
        //could I make a function here? Yes. Yes I could
        if (game.isInCheckmate(BLACK)) {
            notification = "Black is in checkmate!";
            serverMessage = new NotificationMessage(notification);
            message = new Gson().toJson(serverMessage);
            connections.broadcast(session, message, gameData, notification, true);
        } else if (game.isInCheck(BLACK)) {
            notification = "Black is in check!";
            serverMessage = new NotificationMessage(notification);
            message = new Gson().toJson(serverMessage);
            connections.broadcast(session, message, gameData, notification, true);
        }
        if (game.isInCheckmate(WHITE)) {
            notification = "White is in checkmate!";
            serverMessage = new NotificationMessage(notification);
            message = new Gson().toJson(serverMessage);
            connections.broadcast(session, message, gameData, notification, true);
        } else if (game.isInCheck(WHITE)) {
            notification = "White is in check!";
            serverMessage = new NotificationMessage(notification);
            message = new Gson().toJson(serverMessage);
            connections.broadcast(session, message, gameData, notification, true);
        }
        if (game.isInStalemate(WHITE) || game.isInStalemate(BLACK)) {
            notification = "Stalemate!";
            serverMessage = new NotificationMessage(notification);
            message = new Gson().toJson(serverMessage);
            connections.broadcast(session, message, gameData, notification, true);
        }

    }

    private void handleLeave(Session session) throws Exception {
        serverMessage = new ServerMessage(NOTIFICATION);
        String message = new Gson().toJson(serverMessage);
        String notification = "\n" + username + " left the game";
        if (getTeamColor(username).equals("observer")) {
            notification = "\n" + username + " stopped observing game";
        }
        connections.broadcast(session, message, gameData, notification, false);
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
