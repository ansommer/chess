package ui;


import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessGame.TeamColor;
import chess.ChessPiece;
import chess.ChessPiece.PieceType;
import chess.ChessPosition;
import datamodel.AuthData;
import datamodel.GameData;
import websocket.WebSocketFacade;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.util.Arrays;
import java.util.Scanner;

import static chess.ChessGame.TeamColor.BLACK;
import static chess.ChessGame.TeamColor.WHITE;
import static ui.GameState.*;
import static websocket.commands.UserGameCommand.CommandType.*;
import static websocket.messages.ServerMessage.ServerMessageType.LOAD_GAME;


public class GameUI {

    private final ServerFacade server;
    private final WebSocketFacade webSocket = new WebSocketFacade(this);
    private State state;
    private AuthData auth;
    private TeamColor player;
    private BoardPrint boardPrint = new BoardPrint();
    private boolean draw = true;
    private final ChessBoard chessBoard = new ChessBoard();
    private ChessGame chessGame = new ChessGame();
    private GameData gameData;
    private GameState gameState = TURN_WHITE;


    public GameUI(ServerFacade server, State state, AuthData auth, TeamColor player, GameData gameData) throws Exception {
        this.server = server;
        this.state = state;
        this.auth = auth;
        this.player = player;
        this.gameData = gameData;
    }

    public void run() throws Exception {
        chessBoard.resetBoard(); //I am worried about this messing things up. Maybe create from the previous UI...?
        System.out.print(help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (state == State.IN_GAME) {
            if (result.equals("Goodbye!")) {
                return;
            }
            UserGameCommand userGameCommand = new UserGameCommand(CONNECT, auth.authToken(), gameData.gameID());
            webSocket.send(userGameCommand);

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
        return "";
    }

    public void handleServerMessage(ServerMessage serverMessage) throws Exception {
        ServerMessage.ServerMessageType messageType = serverMessage.getServerMessageType();
        if (messageType == LOAD_GAME) {
            if ((player == WHITE || player == null) && draw) {
                boardPrint.print(WHITE, null, chessBoard, chessGame);
            } else if (player == BLACK && draw) {
                boardPrint.print(BLACK, null, chessBoard, chessGame);
            }
            System.out.println("Your message has arrived");
            printPrompt(state);
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

    private void printPrompt(State state) {
        System.out.print("\n" + state + " >>> ");
    }

}