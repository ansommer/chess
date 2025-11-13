package ui;

import java.util.Scanner;

public class PreLoginUI {
    private String user = null;
    private final ServerFacade server;
    private State state = State.PRELOGIN;

    public PreLoginUI(String serverUrl) throws Exception {
        server = new ServerFacade(serverUrl);
    }

    public void run() {
        System.out.println("Welcome to Chess! Please sign in or register a new account.");
        System.out.print(help());
        Scanner scanner = new Scanner(System.in);
        var result = "";


    }


    public String help() {
        return "This is an unhelpful string and you should change that";
    }
}

