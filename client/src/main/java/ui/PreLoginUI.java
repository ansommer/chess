package ui;

import datamodel.*;

import java.util.Arrays;
import java.util.Scanner;

public class PreLoginUI {
    private String username = null;
    private String password = null;
    private String email = null;
    private AuthData auth = null;
    private final ServerFacade server;
    private State state = State.LOGGED_OUT;

    public PreLoginUI() throws Exception {
        server = new ServerFacade(8080);
    }

    public void run() {
        System.out.println("Welcome to Chess! Please sign in or register a new account.");
        System.out.print(help());
        Scanner scanner = new Scanner(System.in);
        var result = "";

        while (state == State.LOGGED_OUT) {
            if (result.equals("Goodbye!")) {
                return;
            }
            printPrompt();
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
        try {
            new PostLoginUI(server, state, auth).run();
        } catch (Throwable ex) {
            System.out.printf("Unable to start login server: %s%n", ex.getMessage());
        }

    }

    public String eval(String input) {
        try {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "register" -> register(params);
                case "login" -> login(params);
                case "clear" -> clear();
                case "quit" -> "Goodbye!";
                default -> help();
            };
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    private void printPrompt() {
        System.out.print("\n" + state + " >>> ");
    }


    public String help() {
        return """
                Type one of the following commands:
                • register <USERNAME> <PASSWORD> <EMAIL>
                • login <USERNAME> <PASSWORD>
                • quit
                • help
                """;
    }

    public String clear() {
        server.clear();
        return "all data cleared";
    }


    public String register(String... params) throws FacadeException {
        if (params.length >= 3) {
            username = params[0];
            password = params[1];
            email = params[2];
            auth = server.register(new UserData(username, password, email));
            state = State.LOGGED_IN;
            return String.format("You signed in as %s.", username);
        }
        throw new FacadeException("Error: Expected <USERNAME> <PASSWORD> <EMAIL>");
    }

    public String login(String... params) throws FacadeException {
        if (params.length >= 2) {
            username = params[0];
            password = params[1];
            auth = server.login(new LoginRequest(username, password));
            state = State.LOGGED_IN;
            return String.format("You signed in as %s.", username);
        }
        throw new FacadeException("Error: Expected <USERNAME> <PASSWORD>");
    }
}

