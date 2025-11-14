package ui;

import chess.ChessGame.TeamColor;
import datamodel.AuthData;

import java.util.Arrays;
import java.util.Scanner;

import static chess.ChessGame.TeamColor.BLACK;
import static chess.ChessGame.TeamColor.WHITE;
import static ui.EscapeSequences.*;

public class GameUI {

    private final ServerFacade server;
    private State state;
    private AuthData auth;
    private TeamColor player;

    public GameUI(ServerFacade server, State state, AuthData auth, TeamColor player) throws Exception {
        this.server = server;
        this.state = state;
        this.auth = auth;
        this.player = player;
    }

    public void run() throws Exception {
        System.out.print(help());
        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (state == State.IN_GAME) {
            if (result.equals("Goodbye!")) {
                return;
            }
            if (player == WHITE || player == null) {
                WhiteBoard.print(SET_TEXT_COLOR_WHITE, SET_TEXT_COLOR_BLACK);
            } else if (player == BLACK) {
                BlackBoard.print(SET_TEXT_COLOR_BLACK, SET_TEXT_COLOR_WHITE);
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
                default -> help();
            };
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    public String leave() throws Exception {
        state = State.LOGGED_IN;
        return "Leaving game";
    }

    public String help() {
        return """
                Type one of the following commands:
                • leave
                • help
                • quit
                """;
    }

    private void printPrompt(State state) {
        System.out.print("\n" + state + " >>> ");
    }

}