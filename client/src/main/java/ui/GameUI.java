package ui;


import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessGame.TeamColor;
import chess.ChessPiece;
import chess.ChessPiece.PieceType;
import chess.ChessPosition;
import com.google.gson.Gson;
import datamodel.AuthData;
import datamodel.GameData;
import websocket.WebSocketFacade;
import websocket.commands.UserGameCommand;
import websocket.messages.LoadGameMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

import static chess.ChessGame.TeamColor.BLACK;
import static chess.ChessGame.TeamColor.WHITE;
import static ui.GameState.*;
import static websocket.commands.UserGameCommand.CommandType.*;
import static websocket.messages.LoadGameMessage.LoadGameType.*;
import static websocket.messages.ServerMessage.ServerMessageType.*;


public class GameUI {

    private final ServerFacade server;
    private final WebSocketFacade webSocket = new WebSocketFacade(this);
    private State state;
    private final AuthData auth;
    private final TeamColor player;
    private final BoardPrint boardPrint = new BoardPrint();
    private boolean draw = true;
    private final ChessBoard chessBoard = new ChessBoard();
    private final ChessGame chessGame = new ChessGame();
    private GameData gameData;
    private final GameState gameState = TURN_WHITE;


    public GameUI(ServerFacade server, State state, AuthData auth, TeamColor player, GameData gameData) throws Exception {
        this.server = server;
        this.state = state;
        this.auth = auth;
        this.player = player;
        this.gameData = gameData;
    }

    public void connectToServer() throws IOException {
        UserGameCommand userGameCommand = new UserGameCommand(CONNECT, auth.authToken(), gameData.gameID());
        webSocket.send(userGameCommand);
    }

    public void resetBoard() {
        chessBoard.resetBoard();
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
                case "move" -> makeMove();
                default -> help();
            };
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    private String makeMove() {
        //so I think this part just sends a thing to the websocket, and the handlemessage does stuff
        return "";
    }

    private String resign() {
        //doesn't make them leave the game, only lose
        return "";
    }

    public void handleServerMessage(String message) throws Exception {

        ServerMessage serverMessage = new Gson().fromJson(message, ServerMessage.class);
        ServerMessage.ServerMessageType messageType = serverMessage.getServerMessageType();

        if (messageType == LOAD_GAME) {
            LoadGameMessage loadGameMessage = new Gson().fromJson(message, LoadGameMessage.class);
            LoadGameMessage.LoadGameType loadGameType = loadGameMessage.getLoadGameType();
            GameData game = loadGameMessage.getGame();
            String joinUsername = loadGameMessage.getUsername();
            String joinColor = "observer";

            if (game.blackUsername() != null && game.blackUsername().equals(joinUsername)) {
                joinColor = "black";
            } else if (game.whiteUsername() != null && game.whiteUsername().equals(joinUsername)) {
                joinColor = "white";
            }

            if (loadGameType == LOAD_MY_GAME) {
                if ((player == WHITE || player == null) && draw) {
                    boardPrint.print(WHITE, null, chessBoard, chessGame);
                } else if (player == BLACK && draw) {
                    boardPrint.print(BLACK, null, chessBoard, chessGame);
                }

            } else if (loadGameType == LOAD_OTHER_USER_JOIN) {
                System.out.print("\n" + joinUsername + " has joined game " + game.gameName() + " as " + joinColor);
            }
            printPrompt(gameState);
        }
    }

    public String show(String... params) throws Exception {
        draw = false;
        if (params.length >= 3) {
            PieceType piece = null;
            ChessPiece chessPiece = null;
            try {
                piece = PieceType.valueOf(params[0].toUpperCase());
                chessPiece = new ChessPiece(player, piece);

                ChessPosition position = getPosition(params[1].toLowerCase(), Integer.parseInt(params[2]));
                ChessPiece pieceType = chessBoard.getPiece(position);
                if ((pieceType.getPieceType() != chessPiece.getPieceType())) {
                    throw new FacadeException("Error: position does not match piece position"); //this isn't working....
                }
                if (player == WHITE) {
                    boardPrint.print(WHITE, position, chessBoard, chessGame);
                } else if (player == BLACK) {
                    boardPrint.print(BLACK, position, chessBoard, chessGame);
                }
                return "";
            } catch (IllegalArgumentException e) {
                System.out.println("Error: Expected <CHESS PIECE>");
            }

        }
        throw new FacadeException("Error: Expected show <CHESS PIECE> <COLUMN> <ROW>");
    }

    public ChessPosition getPosition(String column, int row) throws Exception {
        if (row > 8 || row < 1) {
            throw new Exception("Error: Invalid row number");
        }
        int columnNumber = switch (column) {
            case "a" -> 8;
            case "b" -> 7;
            case "c" -> 6;
            case "d" -> 5;
            case "e" -> 4;
            case "f" -> 3;
            case "g" -> 2;
            case "h" -> 1;
            default -> throw new FacadeException("Error: Invalid column");
        };

        return new ChessPosition(row, columnNumber);
    }

    public String redraw() throws Exception {
        draw = false;
        if (player == WHITE || player == null) {
            boardPrint.print(WHITE, null, chessBoard, chessGame);
        } else if (player == BLACK) {
            boardPrint.print(BLACK, null, chessBoard, chessGame);
        }
        return "";
    }

    public String leave() throws Exception {
        GameData tempGame = gameData;
        if (player == WHITE) {
            gameData = new GameData(tempGame.gameID(), null, tempGame.blackUsername(),
                    tempGame.gameName(), tempGame.game());
        } else if (player == BLACK) {
            gameData = new GameData(tempGame.gameID(), tempGame.whiteUsername(), null,
                    tempGame.gameName(), tempGame.game());
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
                • make move <column row>
                • resign
                • show <CHESS PIECE> <COLUMN> <ROW>
                • quit
                """;
    }

    private void printPrompt(GameState state) {
        System.out.print("\n" + state + " >>> ");
    }

}