package ui;

import chess.ChessGame.TeamColor;
import datamodel.*;

import java.util.Arrays;
import java.util.Scanner;

public class PostLoginUI {

    private final ServerFacade server;
    private State state;
    private AuthData auth;
    private TeamColor player = null;
    private GameListResponse gameList;

    public PostLoginUI(ServerFacade server, State state, AuthData auth) throws Exception {
        this.server = server;
        this.state = state;
        this.auth = auth;
    }

    public void run() throws Exception {
        gameList = server.listGames(auth);
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
            new PreLoginUI().run();
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
                //case "clear" -> clear();
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
        //server.listGames(auth);
        if (params.length >= 1) {
            int id = 0;
            try {
                id = Integer.parseInt(params[0]);
            } catch (NumberFormatException e) {
                System.out.println("Error: Expected <ID>");
            }
            if (id > 0 && id <= gameList.games().size()) {
                state = State.IN_GAME;
                return String.format("Observing game %s", id);
            } else {
                throw new FacadeException("Not a valid game ID");
            }
        }
        throw new FacadeException("Error: Expected <ID>");
    }

    public String list() throws FacadeException {
        gameList = server.listGames(auth);
        int i = 1;
        for (GameData game : gameList.games()) {
            String whitePlayer = (game.whiteUsername() != null) ? game.whiteUsername() : " ";
            String blackPlayer = (game.blackUsername() != null) ? game.blackUsername() : " ";
            System.out.printf("Game ID: %d, Name: %s, White player: %s, Black player: %s%n", i, game.gameName(), whitePlayer, blackPlayer);
            i++;
        }
        if (gameList.games().isEmpty()) {
            System.out.print("No Games Yet!");
        }
        return "";
    }

    public String join(String... params) throws FacadeException {
        //server.listGames(auth);
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
            if (id > 0 && id <= gameList.games().size()) {
                GameData game = gameList.games().get(id - 1);

                try {
                    server.joinGame(auth.authToken(), game.gameID(), player);
                } catch (FacadeException e) {
                    if (e.getMessage().equals("Error: bad request")) {
                        throw new FacadeException("Not a valid game ID");
                    } else if (e.getMessage().equals("Error: already taken")) {
                        throw new FacadeException("Already Taken");
                    } else {
                        throw new FacadeException("Error: Expected <ID> [WHITE|BLACK]");
                    }

                }


                state = State.IN_GAME;
                return String.format("You joined game %s as %s", id, player);
            } else {
                throw new FacadeException("Not a valid game ID");
            }

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
