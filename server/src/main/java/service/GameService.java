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
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.util.Scanner;


import static chess.ChessGame.TeamColor.BLACK;
import static chess.ChessGame.TeamColor.WHITE;
import static websocket.commands.UserGameCommand.CommandType.MAKE_MOVE;
import static websocket.messages.SendTo.*;
import static websocket.messages.ServerMessage.ServerMessageType.*;


public class GameService {

    private final DataAccess dataAccess;
    private final ConnectionManager connections;
    private ServerMessage serverMessage;
    private GameData gameData;
    private int gameID;
    private String username;
    private String team;

    public GameService(DataAccess dataAccess, ConnectionManager connectionManager) {
        this.dataAccess = dataAccess;
        this.connections = connectionManager;
    }

    public void handleUserCommand(String message, Session session) throws Exception {
        UserGameCommand userGameCommand = new Gson().fromJson(message, UserGameCommand.class);
        UserGameCommand.CommandType type = userGameCommand.getCommandType();
        String auth = userGameCommand.getAuthToken();
        gameID = userGameCommand.getGameID();
        gameData = dataAccess.getOneGame(gameID);
        if (gameData == null) {
            sendError(session, "Error: invalid game ID");
            return;
        }
        if (!dataAccess.authExists(auth)) {
            sendError(session, "Error: unauthorized");
            return;
        }

        username = dataAccess.getUserFromAuthToken(auth);

        team = getTeamColor(username);

        switch (type) {
            case CONNECT -> handleConnect(session, auth);
            case MAKE_MOVE -> handleMakeMove(new Gson().fromJson(message, MakeMoveCommand.class), session, auth);
            case LEAVE -> handleLeave(session, gameData);
            case RESIGN -> handleResign(session);
        }
    }

    private void handleConnect(Session session, String auth) throws Exception {
        String message;
        //I think this will broadcast to all the games which is why I need the map
        connections.add(session);
        serverMessage = new ServerMessage(LOAD_GAME);
        message = new Gson().toJson(serverMessage);
        connections.broadcast(session, message, gameData, null, ME);
        serverMessage = new ServerMessage(NOTIFICATION);
        message = new Gson().toJson(serverMessage);

        String notification = "\n" + username + " joined the game as " + team;
        connections.broadcast(session, message, gameData, notification, OTHERS);
    }

    private void handleMakeMove(MakeMoveCommand makeMoveCommand, Session session, String auth) throws Exception {
        if (gameData.game().getTeamTurn() == null) {
            sendError(session, "Error: game over");
            return;
        }
        ChessMove chessMove = makeMoveCommand.getMove();

        //gameData = makeMoveCommand.getGameData();
        //why was I doing this....?

        boolean whiteTurn = team.equals("white") && gameData.game().getTeamTurn() == WHITE;
        boolean blackTurn = team.equals("black") && gameData.game().getTeamTurn() == BLACK;
        if (!whiteTurn && !blackTurn) {
            sendError(session, "Error: not your turn");
            return;
        } else if (team.equals("observer")) {
            sendError(session, "Error: observers cannot make moves :(");
            return;
        }
        ChessGame game = gameData.game();
        dataAccess.updateGame(gameData);
        GameData updatedGame = dataAccess.getOneGame(gameID);
        try {
            game.makeMove(chessMove); //may need to be in a try catch?
        } catch (Exception e) {
            sendError(session, "Error: invalid move");
            return;
        }
        dataAccess.updateGame(gameData);
        serverMessage = new LoadGameMessage(gameData);
        String message = new Gson().toJson(serverMessage);
        connections.broadcast(session, message, gameData, null, ALL);

        String notification = chessMove.getStartPosition().getColumn() + chessMove.getStartPosition().getRow() +
                " moved to " + chessMove.getEndPosition().getColumn() + chessMove.getEndPosition().getRow();
        serverMessage = new NotificationMessage(notification);
        message = new Gson().toJson(serverMessage);
        connections.broadcast(session, message, gameData, notification, OTHERS);
        //if it's check, checkmate, or stalemate, a message is sent to everyone
        //could I make a function here? Yes. Yes I could
        if (game.isInCheckmate(BLACK)) {
            notification = "Black is in checkmate!";
            serverMessage = new NotificationMessage(notification);
            message = new Gson().toJson(serverMessage);
            connections.broadcast(session, message, gameData, notification, ALL);
        } else if (game.isInCheck(BLACK)) {
            notification = "Black is in check!";
            serverMessage = new NotificationMessage(notification);
            message = new Gson().toJson(serverMessage);
            connections.broadcast(session, message, gameData, notification, ALL);
        }
        if (game.isInCheckmate(WHITE)) {
            notification = "White is in checkmate!";
            serverMessage = new NotificationMessage(notification);
            message = new Gson().toJson(serverMessage);
            connections.broadcast(session, message, gameData, notification, ALL);
        } else if (game.isInCheck(WHITE)) {
            notification = "White is in check!";
            serverMessage = new NotificationMessage(notification);
            message = new Gson().toJson(serverMessage);
            connections.broadcast(session, message, gameData, notification, ALL);
        }
        if (game.isInStalemate(WHITE) || game.isInStalemate(BLACK)) {
            notification = "Stalemate!";
            serverMessage = new NotificationMessage(notification);
            message = new Gson().toJson(serverMessage);
            connections.broadcast(session, message, gameData, notification, ALL);
        }

    }

    private void handleLeave(Session session, GameData gameData) throws Exception {
        GameData newGame = gameData;
        if (team.equals("white")) {
            newGame = new GameData(gameData.gameID(), null, gameData.blackUsername(),
                    gameData.gameName(), gameData.game());
            dataAccess.updateGame(newGame);
            GameData gameData2 = dataAccess.getOneGame(newGame.gameID());
        } else if (team.equals("black")) {
            newGame = new GameData(gameData.gameID(), gameData.whiteUsername(), null,
                    gameData.gameName(), gameData.game());
            dataAccess.updateGame(newGame);
        }
        serverMessage = new ServerMessage(NOTIFICATION);

        String message = new Gson().toJson(serverMessage);
        String notification = "\n" + username + " left the game";
        if (team.equals("observer")) {
            notification = "\n" + username + " stopped observing game";
        }
        connections.broadcast(session, message, newGame, notification, OTHERS);
        connections.remove(session);
    }

    private void handleResign(Session session) throws Exception {
        if (gameData.game().getTeamTurn() == null) {
            sendError(session, "Error: game over");
            return;
        }
        Scanner scanner = new Scanner(System.in);


        serverMessage = new ServerMessage(NOTIFICATION);

        String message = new Gson().toJson(serverMessage);
        String notification = """
                Are you sure you want to resign?
                -yes
                -no
                """;
        if (team.equals("observer")) {
            sendError(session, "Error: observer cannot resign");
        }
        connections.broadcast(session, message, gameData, notification, ME);
    }


    private String getTeamColor(String username) {
        if (gameData.whiteUsername() != null && gameData.whiteUsername().equals(username)) {
            return "white";
        } else if (gameData.blackUsername() != null && gameData.blackUsername().equals(username)) {
            return "black";
        }
        return "observer";
    }

    private void sendError(Session session, String error) throws Exception {
        serverMessage = new ErrorMessage(error);
        String message = new Gson().toJson(serverMessage);
        session.getRemote().sendString(message);
    }
}
