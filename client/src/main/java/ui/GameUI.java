package ui;

import datamodel.AuthData;

import java.util.Arrays;
import java.util.Scanner;

public class GameUI {

    private final ServerFacade server;
    private State state;
    private AuthData auth;

    public GameUI(ServerFacade server, State state, AuthData auth) throws Exception {
        this.server = server;
        this.state = state;
        this.auth = auth;
    }

    public void run() throws Exception {
        System.out.print(help());
        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (state == State.PLAYING_GAME) {
            if (result.equals("Goodbye!")) {
                return;
            }
            printBoard();
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
    }


    public String eval(String input) {
        try {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                //actually probably it would make sense to do it not this way
                case "quit" -> "Goodbye!";
                default -> help();
            };
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    public String help() {
        return """
                Type one of the following commands:
                • quit
                • help
                """;
    }

    private void printBoard() {
        System.out.print("Imagine this is a really pretty board");
    }

    private void printPrompt(State state) {
        System.out.print("\n" + state + " >>> ");
    }

}