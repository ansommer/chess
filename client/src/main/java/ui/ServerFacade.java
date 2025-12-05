package ui;

import com.google.gson.Gson;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;

import chess.ChessGame;
import datamodel.*;

import java.net.http.*;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.HashMap;

public class ServerFacade {
    private final HttpClient client = HttpClient.newHttpClient();
    private final String serverUrl = "http://localhost:";
    private final int port;


    public ServerFacade(int port) {
        this.port = port;
    }

    public void clear() throws FacadeException {
        var request = buildRequest("DELETE", "/db", null, null);
        var response = sendRequest(request);
        handleResponse(response, Void.class);
    }

    public AuthData register(UserData user) throws FacadeException {
        //maybe we actually return a user. who knows
        var request = buildRequest("POST", "/user", user, null);
        var response = sendRequest(request);
        return handleResponse(response, AuthData.class);
    }

    public AuthData login(LoginRequest user) throws FacadeException {
        var request = buildRequest("POST", "/session", user, null);
        var response = sendRequest(request);
        return handleResponse(response, AuthData.class);
    }

    public void logout(AuthData authData) throws FacadeException {
        var request = buildRequest("DELETE", "/session", null, authData.authToken());
        var response = sendRequest(request);
        handleResponse(response, Void.class);
    }

    public GameResponse createGame(String gameName, String authToken) throws FacadeException {
        var request = buildRequest("POST", "/game", new GameRequest(gameName), authToken);
        var response = sendRequest(request);
        return handleResponse(response, GameResponse.class);
    }

    public GameListResponse listGames(AuthData authData) throws FacadeException {
        String authToken = authData.authToken();
        var request = buildRequest("GET", "/game", null, authToken);
        var response = sendRequest(request);
        return handleResponse(response, GameListResponse.class);
    }

    public void joinGame(String authToken, int gameID, ChessGame.TeamColor player) throws FacadeException {
        var request = buildRequest("PUT", "/game", new JoinRequest(player, gameID), authToken);
        var response = sendRequest(request);
        handleResponse(response, Void.class);
    }

    public void leaveGame(String authToken, int gameID, ChessGame.TeamColor player) throws FacadeException {
        var request = buildRequest("DELETE", "/game", new JoinRequest(player, gameID), authToken);
        var response = sendRequest(request);
        handleResponse(response, Void.class);
    }

    private HttpResponse<String> sendRequest(HttpRequest request) throws FacadeException {
        try {
            var response = client.send(request, BodyHandlers.ofString());
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            throw new FacadeException(e.getMessage());
        }
    }

    private HttpRequest buildRequest(String method, String path, Object body, String authToken) {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + port + path));

        if (method.equals("GET")) {
            request.method(method, BodyPublishers.noBody());
        } else if (method.equals("DELETE") && body == null) {
            request.method(method, BodyPublishers.noBody());
        } else {
            request.method(method, makeRequestBody(body));
            request.setHeader("Content-Type", "application/json");
        }

        if (authToken != null) {
            request.setHeader("Authorization", authToken);
        }
        return request.build();
    }

    private BodyPublisher makeRequestBody(Object request) {
        if (request != null) {
            return BodyPublishers.ofString(new Gson().toJson(request));
        } else {
            return BodyPublishers.noBody();
        }
    }

    private <T> T handleResponse(HttpResponse<String> response, Class<T> responseClass) throws FacadeException {
        var status = response.statusCode();
        if ((status / 100) != 2) { //checking that it's a 200 success response, but any (ex. 204)
            var body = response.body();
            if (body != null) {
                var map = new Gson().fromJson(body, HashMap.class);
                String message = map.get("message").toString();
                throw new FacadeException(message);
            }
            throw new FacadeException("Error: other failure - " + status);
        }
        if (responseClass != null) {
            if (response.body().equals("{}")) {
                return null;
            }
            return new Gson().fromJson(response.body(), responseClass);
        }
        return null;
    }

}
