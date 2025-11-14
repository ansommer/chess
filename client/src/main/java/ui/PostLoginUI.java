package ui;

import datamodel.*;

import java.util.Arrays;
import java.util.Scanner;

public class PostLoginUI {

    private final ServerFacade server;
    private State state;
    private AuthData auth;

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
        }
        return;
    }

    public String eval(String input) {
        try {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "create" -> create(params);
                case "quit" -> "Goodbye!";
                case "logout" -> logout();
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
