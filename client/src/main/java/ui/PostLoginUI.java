package ui;

import chess.ChessGame.TeamColor;
import commonMisconceptions.BadRequestException;
import datamodel.*;

import java.util.Arrays;
import java.util.Scanner;

public class PostLoginUI {

    private final ServerFacade server;
    private State state;
    private AuthData auth;
    private TeamColor player = null;

    public PostLoginUI(ServerFacade server, State state, AuthData auth) throws Exception {
        this.server = server;
        this.state = state;
        this.auth = auth;
    }

    public void run() throws Exception {
        System.out.print(help());
        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (state == State.LOGGED_IN) {
            if (result.equals("Goodbye!")) {
                return;
            }
            printPrompt(state);
            String line = scanner.nextLine();
            try {
                result = eval(line);
                System.out.println(result);
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.println(msg);
            }
        }
        System.out.println();
        if (state == State.LOGGED_OUT) {
            new PreLoginUI("http://localhost:8080").run();
        } else if (state == State.IN_GAME) {
            new GameUI(server, state, auth, player).run();
        }
    }

    public String eval(String input) {
        try {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "create" -> create(params);
                case "list" -> list();
                case "quit" -> "Goodbye!";
                case "logout" -> logout();
                case "join" -> join(params);
                case "observe" -> observe(params);
                default -> help();
            };
        } catch (Exception e) {
            return e.getMessage();
        }
    }


    public String help() {
        return """
                Type one of the following commands:
                • create <GAME NAME>
                • list
                • join <ID> [WHITE|BLACK]
                • observe <ID>
                • logout
                • quit
                • help
                """;
    }

    public String observe(String... params) throws FacadeException {
        //currently can observe a game that doesn't exist :/
        if (params.length >= 1) {
            int id = 0;
            try {
                id = Integer.parseInt(params[0]);
            } catch (NumberFormatException e) {
                System.out.println("Error: Expected <ID>");
            }
            state = State.IN_GAME;
            return String.format("Observing game %s", id);
        }
        throw new FacadeException("Error: Expected <ID>");
    }

    public String list() throws FacadeException {
        GameListResponse gameResult = server.listGames(auth);
        for (GameData game : gameResult.games()) {
            String whitePlayer = (game.whiteUsername() != null) ? game.whiteUsername() : " ";
            String blackPlayer = (game.blackUsername() != null) ? game.blackUsername() : " ";
            System.out.printf("Game ID: %d, Name: %s, White player: %s, Black player: %s%n", game.gameID(), game.gameName(), whitePlayer, blackPlayer);
        }
        return "";
    }

    public String join(String... params) throws FacadeException {
        if (params.length >= 2) {
            int id = 0;
            try {
                id = Integer.parseInt(params[0]);
            } catch (NumberFormatException e) {
                System.out.println("Error: Expected <ID> [WHITE|BLACK]");
            }
            var playerInput = params[1].toUpperCase();
            if (playerInput.equals("WHITE")) {
                player = TeamColor.WHITE;
            } else if (playerInput.equals("BLACK")) {
                player = TeamColor.BLACK;
            } else {
                throw new FacadeException("Error: Expected <ID> [WHITE|BLACK]");
            }
            try {
                server.joinGame(auth.authToken(), id, player);
            } catch (FacadeException e) {
                throw new FacadeException("Not a valid game ID");
            }

            state = State.IN_GAME;
            return String.format("You joined game %s as %s", id, player);

        }
        throw new FacadeException("Error: Expected <ID> [WHITE|BLACK]");
    }

    public String logout() {
        server.logout(auth);
        state = State.LOGGED_OUT;
        return "You have been logged out.";
    }

    public String create(String... params) throws FacadeException {
        if (params.length >= 1) {
            String gameName = params[0];
            String authToken = auth.authToken();
            server.createGame(gameName, authToken);
            return String.format("Game %s created!", gameName);
        }
        throw new FacadeException("Error: Expected <GAME NAME>");
    }

    private void printPrompt(State state) {
        System.out.print("\n" + state + " >>> ");
    }
}
