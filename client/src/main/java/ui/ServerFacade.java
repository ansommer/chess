package ui;

import java.net.http.HttpClient;

import chess.ChessGame;
import datamodel.*;

public class ServerFacade {
    private final HttpClient client = HttpClient.newHttpClient();
    private final String serverUrl;

    public ServerFacade(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public void clear() throws Exception {

    }

    public void register(UserData user) throws Exception {
        //maybe we actually return a user. who knows
    }

    public AuthData login(UserData user) throws Exception {
        return null;
    }

    public void logout(AuthData authData) throws Exception {

    }

    public ChessGame createGame(AuthData authData) throws Exception {
        //actually not sure what params this needs or if it should return ChessGame
        return null;
    }

    public ChessGame listGames(AuthData authData) throws Exception {
        return null;
    }

    public void joinGame(AuthData authData, ChessGame chessGame) throws Exception {

    }

}
