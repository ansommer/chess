package dataaccess;

import chess.ChessGame;
import datamodel.AuthData;
import datamodel.GameData;
import datamodel.UserData;

import java.util.HashMap;

public class MemoryDataAccess implements DataAccess {
    private HashMap<String, UserData> users = new HashMap<>();
    private HashMap<String, String> authTokens = new HashMap<>(); //token, username
    private HashMap<String, GameData> games = new HashMap<>();

    @Override
    public void saveUser(UserData user) {
        users.put(user.username(), user);
    }

    @Override
    public void getUser(String username) {
        users.get(username);
    }

    public HashMap<String, GameData> getGames() {
        return games;
    }

    public void createGame(int gameId, String gameName) {
        GameData newGame = new GameData(gameId, null, null, gameName, new ChessGame());
        games.put(gameName, newGame);
    }

    public void deleteAuth(String auth) {
        authTokens.remove(auth);
    }

    public void saveAuth(AuthData auth) {
        authTokens.put(auth.authToken(), auth.username());
    }

    public boolean authExists(String auth) {
        return authTokens.containsKey(auth);
    }

    public String getPass(String username) {
        UserData user = users.get(username);
        return (user == null) ? null : user.password();
    }

    public boolean userExists(String username) {
        return users.containsKey(username);
    }

    public void clear() {
        users.clear();
        authTokens.clear();
        games.clear();
    }


}