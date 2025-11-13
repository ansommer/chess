package ui;

import datamodel.*;

import java.util.Arrays;
import java.util.Scanner;

public class PreLoginUI {
    private String username = null;
    private String password = null;
    private String email = null;
    private final ServerFacade server;
    private State state = State.LOGGED_OUT;

    public PreLoginUI(String serverUrl) throws Exception {
        server = new ServerFacade(serverUrl);
    }

    public void run() {
        System.out.println("Welcome to Chess! Please sign in or register a new account.");
        System.out.print(help());
        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
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
    }

    public String eval(String input) {
        try {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "register" -> register(params);
                case "login" -> login(params);
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

    public String register(String... params) throws FacadeException {
        if (params.length >= 3) {
            username = params[0];
            password = params[1];
            email = params[2];
            server.register(new UserData(username, password, email));
            state = State.LOGGED_IN;
            //where did the authData go?
            return String.format("You signed in as %s.", username);
        }
        throw new FacadeException("Error: Expected <username> <password> <email>");
    }

    public String login(String... params) throws FacadeException {
        if (params.length >= 2) {
            username = params[0];
            password = params[1];
            server.login(new LoginRequest(username, password));
            state = State.LOGGED_IN;
            //same here, what do I do with the auth?
            return String.format("You signed in as %s.", username);
        }
        throw new FacadeException("Error: Expected <username> <password>");
        //actually this isn't right because it needs to not work if the password is wrong....

    }
}

