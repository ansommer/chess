package ui;

import java.util.Arrays;
import java.util.Scanner;

public class PostLoginUI {
    //private final ServerFacade server;

    public PostLoginUI() throws Exception {
    }

    public void run(ServerFacade server, State state) throws Exception {
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
        return;
    }

    public String eval(String input) {
        try {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
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
                • create <GAME NAME>
                • list
                • join <ID> [WHITE|BLACK]
                • observe <ID>
                • logout
                • quit
                • help
                """;
    }

    private void printPrompt(State state) {
        System.out.print("\n" + state + " >>> ");
    }
}
