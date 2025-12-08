package service;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
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
        connections.add(session, gameID);
        serverMessage = new ServerMessage(LOAD_GAME);
        message = new Gson().toJson(serverMessage);
        connections.broadcast(session, message, gameData, null, ME, gameID);
        serverMessage = new ServerMessage(NOTIFICATION);
        message = new Gson().toJson(serverMessage);

        String notification = "\n" + username + " joined the game as " + team;
        connections.broadcast(session, message, gameData, notification, OTHERS, gameID);
    }

    private void handleMakeMove(MakeMoveCommand makeMoveCommand, Session session, String auth) throws Exception {
        if (gameData.game().isGameOver()) {
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
        connections.broadcast(session, message, gameData, null, ALL, gameID);
        ChessPosition start = chessMove.getStartPosition();
        ChessPosition end = chessMove.getEndPosition();

        String notification = String.format("%s %c%d moved to %c%d", username,
                reverseGetPosition(start.getColumn()), start.getRow(),
                reverseGetPosition(end.getColumn()), end.getRow());


        serverMessage = new NotificationMessage(notification);
        message = new Gson().toJson(serverMessage);
        connections.broadcast(session, message, gameData, notification, OTHERS, gameID);

        //if it's check, checkmate, or stalemate, a message is sent to everyone
        //idk if this will pass being so nested
        if (!checkGameStatus(session, (game.isInCheckmate(BLACK)), "is in checkmate!", BLACK)) {
            if (!checkGameStatus(session, (game.isInCheck(BLACK)), "is in check!", BLACK)) {
                if (!checkGameStatus(session, (game.isInCheckmate(WHITE)), "is in checkmate!", WHITE)) {
                    if (!checkGameStatus(session, (game.isInCheck(WHITE)), "is in check!", WHITE)) {
                        checkGameStatus(session, (game.isInStalemate(WHITE) || game.isInStalemate(BLACK)),
                                "Stalemate!", null);
                    }
                }
            }
        }


    }

    private boolean checkGameStatus(Session session, boolean condition, String notification, ChessGame.TeamColor teamColor)
            throws Exception {
        String finalNotification;
        if (teamColor != null) {
            String user = (team.equals("white") && teamColor == WHITE) ? username : gameData.blackUsername();
            finalNotification = user + " " + notification;
        } else {
            finalNotification = notification;
        }
        if (condition) {
            serverMessage = new NotificationMessage(finalNotification);
            String message = new Gson().toJson(serverMessage);
            connections.broadcast(session, message, gameData, finalNotification, ALL, gameID);
            return true;
        }
        return false;
    }

    private String positionToString(ChessPosition pos) throws Exception {
        char col = reverseGetPosition(pos.getColumn()); // should be 1–8
        int row = pos.getRow(); // should be 1–8
        return "" + col + row;
    }

    public char reverseGetPosition(int col) throws Exception {
        return switch (col) {
            case 8 -> 'a';
            case 7 -> 'b';
            case 6 -> 'c';
            case 5 -> 'd';
            case 4 -> 'e';
            case 3 -> 'f';
            case 2 -> 'g';
            case 1 -> 'h';
            default -> throw new Exception("Invalid column number: " + col);
        };
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
        connections.broadcast(session, message, newGame, notification, OTHERS, gameID);
        connections.remove(session, gameID);
    }

    private void handleResign(Session session) throws Exception {
        if (gameData.game().isGameOver()) {
            sendError(session, "Error: game over");
            return;
        }
        if (team.equals("observer")) {
            sendError(session, "Error: observer cannot resign");
            return;
        }
        gameData.game().setGameOver(true);
        dataAccess.updateGame(gameData);
        serverMessage = new ServerMessage(NOTIFICATION);
        String notification = (team.equals("white")) ? "White resigned. Team black wins!" : "Black resigned. " +
                "Team white wins!";
        String message = new Gson().toJson(serverMessage);
        connections.broadcast(session, message, gameData, notification, ALL, gameID);

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
