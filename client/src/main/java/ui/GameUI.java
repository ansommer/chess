package ui;


import chess.*;
import chess.ChessGame.TeamColor;
import com.google.gson.Gson;
import datamodel.AuthData;
import datamodel.GameData;
import websocket.WebSocketFacade;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

import static chess.ChessGame.TeamColor.BLACK;
import static chess.ChessGame.TeamColor.WHITE;
import static ui.GameState.*;
import static websocket.commands.UserGameCommand.CommandType.*;
import static websocket.messages.ServerMessage.ServerMessageType.*;


public class GameUI {

    private final ServerFacade server;
    private final WebSocketFacade webSocket = new WebSocketFacade(this);
    private State state;
    private final AuthData auth;
    private final TeamColor player;
    private final BoardPrint boardPrint = new BoardPrint();
    private boolean draw = true;
    //private final ChessBoard chessBoard = new ChessBoard();
    //private final ChessGame chessGame = new ChessGame();
    private GameData gameData;
    private GameState gameState = TURN_WHITE;


    public GameUI(ServerFacade server, State state, AuthData auth, TeamColor player, GameData gameData) throws Exception {
        this.server = server;
        this.state = state;
        this.auth = auth;
        this.player = player;
        this.gameData = gameData;
    }

    public void connectToServer() throws IOException {
        UserGameCommand userGameCommand = new UserGameCommand(CONNECT, auth.authToken(), gameData.gameID());
        String commandJson = new Gson().toJson(userGameCommand);
        webSocket.send(commandJson);
    }


    public void run() throws Exception {
        System.out.print(help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (state == State.IN_GAME) {
            if (result.equals("Goodbye!")) {
                return;
            }
            String line = scanner.nextLine();
            try {
                result = eval(line);
                if (draw) {
                    System.out.println(result);
                }
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.println(msg);
            }
        }
        draw = true;
        System.out.println();
        printPrompt(gameState);
        System.out.print("  from run");
        if (state == State.LOGGED_IN) {
            new PostLoginUI(server, state, auth).run();
        }
    }


    public String eval(String input) {
        try {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                //actually probably it would make sense to do it not this way
                case "leave" -> leave();
                case "quit" -> "Goodbye!";
                case "redraw" -> redraw();
                case "show" -> show(params);
                case "resign" -> resign();
                case "move" -> makeMove(params);
                default -> help();
            };
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    private String makeMove(String... params) throws Exception {
        //draw = false;
        if (params.length >= 2) {
            if ((gameState == TURN_WHITE && player == WHITE) || (gameState == TURN_BLACK && player == BLACK)) {
                try {
                    //First Position (Maybe I should make a function for this
                    String square = params[0];
                    ChessPosition position = getPosition(square);

                    //Second Position
                    String square2 = params[1];
                    ChessPosition position2 = getPosition(square2);
                    //ChessPiece pieceType = chessBoard.getPiece(position2);
                    ChessMove chessMove = new ChessMove(position, position2, null);
                    //well what do we do if it actually is a promotion piece. I suppose we can check piecetype and
                    //position to know if it is or not
                    MakeMoveCommand makeMoveCommand = new MakeMoveCommand(chessMove, auth.authToken(), gameData.gameID(), gameData);
                    String commandJson = new Gson().toJson(makeMoveCommand);
                    webSocket.send(commandJson);


                    //this does not take into account if it's the end of the game
                    printPrompt(gameState);
                    return "";
                } catch (IllegalArgumentException e) {
                    System.out.println("Error: Some error I'm not sure about");
                }
            }
            throw new FacadeException("Error: not your turn");
        }
        throw new FacadeException("Error: Expected move <COLUMN><ROW> <COLUMN><ROW> (Ex: a2 a3)");
    }

    private String resign() {
        //doesn't make them leave the game, only lose
        return "";
    }


    public String show(String... params) throws Exception {
        draw = false;
        if (params.length >= 1) {
            try {
                String square = params[0];
                ChessPosition position = getPosition(square);

                if (player == WHITE) {
                    boardPrint.print(WHITE, position, gameData.game().getBoard());
                } else if (player == BLACK) {
                    boardPrint.print(BLACK, position, gameData.game().getBoard());
                }
                printPrompt(gameState);
                return "";

            } catch (IllegalArgumentException e) {
                System.out.println("Error: Some error I'm not sure about");
            }

        }
        throw new FacadeException("Error: Expected show <COLUMN><ROW> (Ex: a2)");
    }

    public ChessPosition getPosition(String square) throws Exception {
        if (square.length() != 2) {
            throw new FacadeException("Error: Expected move <COLUMN><ROW> <COLUMN><ROW> (Ex: a2 a3)");
        }
        char col = Character.toLowerCase(square.charAt(0));
        int row = Character.getNumericValue(square.charAt(1));

        if (row > 8 || row < 1) {
            throw new Exception("Error: Invalid row number");
        }
        int columnNumber = switch (col) {
            case 'a' -> 8;
            case 'b' -> 7;
            case 'c' -> 6;
            case 'd' -> 5;
            case 'e' -> 4;
            case 'f' -> 3;
            case 'g' -> 2;
            case 'h' -> 1;
            default -> throw new FacadeException("Error: Invalid column");
        };

        return new ChessPosition(row, columnNumber);
    }

    public String redraw() throws Exception {
        //draw = false;
        printBoard();
        printPrompt(gameState);
        return "";
    }

    public String leave() throws Exception {
        try {
            UserGameCommand userGameCommand = new UserGameCommand(LEAVE, auth.authToken(), gameData.gameID());
            String commandJson = new Gson().toJson(userGameCommand);
            webSocket.send(commandJson);
            server.leaveGame(auth.authToken(), gameData.gameID(), player);
        } catch (Exception e) {
            return e.getMessage();
        }
        state = State.LOGGED_IN;
        return "Leaving game";
    }

    public String help() {
        return """
                Type one of the following commands:
                • help
                • redraw
                • leave
                • move <COLUMN><ROW> <COLUMN><ROW>
                • resign
                • show <COLUMN><ROW>
                • quit
                """;
    }

    public void handleServerMessage(String message) throws Exception {

        ServerMessage serverMessage = new Gson().fromJson(message, ServerMessage.class);
        ServerMessage.ServerMessageType messageType = serverMessage.getServerMessageType();

        if (messageType == LOAD_GAME) {
            LoadGameMessage loadGameMessage = new Gson().fromJson(message, LoadGameMessage.class);
            gameData = loadGameMessage.getGame();
            gameState = (gameData.game().getTeamTurn() == BLACK) ? TURN_BLACK : TURN_WHITE;
            printBoard();
        } else if (messageType == NOTIFICATION) {
            NotificationMessage notificationMessage = new Gson().fromJson(message, NotificationMessage.class);
            System.out.print(notificationMessage.getMessage());
        }
        printPrompt(gameState);
    }

    private void printBoard() {
        String whiteUser = (gameData.whiteUsername() != null) ? gameData.whiteUsername() : " ";
        String blackUser = (gameData.blackUsername() != null) ? gameData.blackUsername() : " ";
        //I'm not using the updated board here
        //System.out.println("White: " + whiteUser + " Black: " + blackUser + "ONLY FOR MY OWN REFERENCE RN BC WILL " +
        //"NOT UPDATE IF FIRST USER TO JOIN");
        System.out.println();
        if ((player == WHITE || player == null) && draw) {
            boardPrint.print(WHITE, null, gameData.game().getBoard());
        } else if (player == BLACK && draw) {
            boardPrint.print(BLACK, null, gameData.game().getBoard());
        }
    }


    private void printPrompt(GameState state) {
        System.out.print("\n" + state + " >>> ");
    }

}