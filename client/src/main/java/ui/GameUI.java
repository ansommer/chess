package ui;


import chess.*;
import chess.ChessGame.TeamColor;
import com.google.gson.Gson;
import datamodel.AuthData;
import datamodel.GameData;
import websocket.WebSocketFacade;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

import static chess.ChessGame.TeamColor.BLACK;
import static chess.ChessGame.TeamColor.WHITE;
import static chess.ChessPiece.PieceType.*;
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
                System.out.print(result);
                printPrompt(gameState);
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
                printPrompt(gameState);
            }

        }
        System.out.println();
        printPrompt(gameState);
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
                case "resign" -> resignRequest();
                case "move" -> makeMove(params);
                default -> help();
            };
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    private String makeMove(String... params) throws Exception {
        if (params.length >= 2) {
            if ((gameState == TURN_WHITE && player == WHITE) || (gameState == TURN_BLACK && player == BLACK)) {

                //First Position (Maybe I should make a function for this
                String square = params[0];
                ChessPosition start = getPosition(square);
                //Second Position
                String square2 = params[1];
                ChessPosition end = getPosition(square2);
                ChessBoard chessBoard = gameData.game().getBoard();

                MakeMoveCommand makeMoveCommand = getMakeMoveCommand(chessBoard.getPiece(start), end, start);
                String commandJson = new Gson().toJson(makeMoveCommand);
                webSocket.send(commandJson);
                return "";

            }
            throw new FacadeException("Error: not your turn");
        }
        throw new FacadeException("Error: Expected move <COLUMN><ROW> <COLUMN><ROW> (Ex: a2 a3)");

    }

    private MakeMoveCommand getMakeMoveCommand(ChessPiece piece, ChessPosition end, ChessPosition start) {
        ChessPiece.PieceType pieceType = piece.getPieceType();
        ChessPiece.PieceType promotionType = null;

        if (pieceType.equals(PAWN) && ((end.getRow() == 8) || (end.getRow() == 1))) {
            Scanner scanner = new Scanner(System.in);
            while (true) {
                String promotion = """
                        Your pawn can now promote! Please enter what you would like to promote it to:
                        -Knight
                        -Rook
                        -Bishop
                        -Queen
                        >>>""";
                System.out.print(promotion);
                String answer = scanner.nextLine().trim().toLowerCase();

                promotionType = switch (answer) {
                    case "knight" -> KNIGHT;
                    case "rook" -> ROOK;
                    case "bishop" -> BISHOP;
                    case "queen" -> QUEEN;
                    default -> null;
                };
                if (promotionType != null) {
                    break;
                }

                System.out.println("Error: Not a valid promotion type");
            }
        }
        ChessMove chessMove = new ChessMove(start, end, promotionType);
        return new MakeMoveCommand(chessMove, auth.authToken(), gameData.gameID());
    }

    private String resignRequest() throws Exception {
        Scanner scanner = new Scanner(System.in);
        String check = """
                Are you sure you want to resign?
                -yes
                -no
                """;
        System.out.print(check);
        String answer = scanner.nextLine();
        if (answer.equalsIgnoreCase("yes")) {
            UserGameCommand userGameCommand = new UserGameCommand(RESIGN, auth.authToken(), gameData.gameID());
            String commandJson = new Gson().toJson(userGameCommand);
            webSocket.send(commandJson);
        }
        return "";
    }


    public String show(String... params) throws Exception {
        if (params.length >= 1) {
            try {
                String square = params[0];
                ChessPosition position = getPosition(square);
                ChessBoard chessBoard = gameData.game().getBoard();
                ChessPiece pieceType = chessBoard.getPiece(position);
                if (pieceType == null) {
                    throw new FacadeException("Error: Not a valid piece");
                }
                ChessGame game = gameData.game();
                if (player == WHITE || player == null) {
                    boardPrint.print(WHITE, position, game.getBoard(), game);
                } else if (player == BLACK) {
                    boardPrint.print(BLACK, position, game.getBoard(), game);
                }
                return "";

            } catch (FacadeException e) {
                System.out.print("Error: Not a valid piece");
                return "";
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
            case 'a' -> 1;
            case 'b' -> 2;
            case 'c' -> 3;
            case 'd' -> 4;
            case 'e' -> 5;
            case 'f' -> 6;
            case 'g' -> 7;
            case 'h' -> 8;
            default -> throw new FacadeException("Error: Invalid column");
        };

        return new ChessPosition(row, columnNumber);
    }

    public String redraw() throws Exception {
        printBoard();
        //printPrompt(gameState);
        return "";
    }

    public String leave() throws Exception {
        try {
            UserGameCommand userGameCommand = new UserGameCommand(LEAVE, auth.authToken(), gameData.gameID());
            String commandJson = new Gson().toJson(userGameCommand);
            webSocket.send(commandJson);
            //server.leaveGame(auth.authToken(), gameData.gameID(), player);
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
            printPrompt(gameState);
        } else if (messageType == NOTIFICATION) {
            NotificationMessage notificationMessage = new Gson().fromJson(message, NotificationMessage.class);
            String announcement = notificationMessage.getMessage();
            System.out.print(notificationMessage.getMessage());
            if (announcement.contains("wins") || announcement.contains("checkmate")) {
                gameState = GAMEOVER;
            }
            printPrompt(gameState);
        } else if (messageType == ERROR) {
            ErrorMessage errorMessage = new Gson().fromJson(message, ErrorMessage.class);
            System.out.print(errorMessage.getErrorMessage());
            printPrompt(gameState);
        }

    }

    private void printBoard() {
        System.out.println();
        ChessGame game = gameData.game();
        if ((player == WHITE || player == null)) {
            boardPrint.print(WHITE, null, game.getBoard(), game);
        } else if (player == BLACK) {
            boardPrint.print(BLACK, null, game.getBoard(), game);
        }

    }

    private void printPrompt(GameState state) {
        System.out.print("\n" + state + " >>> ");
    }

}