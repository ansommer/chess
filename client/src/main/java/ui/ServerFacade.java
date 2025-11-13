package ui;

import com.google.gson.Gson;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;

import chess.ChessGame;
import datamodel.*;

import java.net.*;
import java.net.http.*;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.HashMap;

public class ServerFacade {
    private final HttpClient client = HttpClient.newHttpClient();
    private final String serverUrl;

    public ServerFacade(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public void clear() throws FacadeException {

    }


    // I should probably make a special kind of exception
    public AuthData register(UserData user) throws FacadeException {
        //maybe we actually return a user. who knows
        var request = buildRequest("POST", "/user", user);
        var response = sendRequest(request);
        return handleResponse(response, AuthData.class);
    }

    public AuthData login(LoginRequest user) throws FacadeException {
        var request = buildRequest("POST", "/session", user);
        var response = sendRequest(request);
        return handleResponse(response, AuthData.class);
    }

    public void logout(AuthData authData) throws FacadeException {

    }

    public ChessGame createGame(AuthData authData) throws FacadeException {
        //actually not sure what params this needs or if it should return ChessGame
        return null;
    }

    public ChessGame listGames(AuthData authData) throws FacadeException {
        return null;
    }

    public void joinGame(AuthData authData, ChessGame chessGame) throws FacadeException {

    }

    private HttpResponse<String> sendRequest(HttpRequest request) throws FacadeException {
        try {
            return client.send(request, BodyHandlers.ofString());
        } catch (Exception e) {
            throw new FacadeException(e.getMessage());
        }
    }

    private HttpRequest buildRequest(String method, String path, Object body) {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + path))
                .method(method, makeRequestBody(body));
        if (body != null) {
            request.setHeader("Content-Type", "application/json");
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
            return new Gson().fromJson(response.body(), responseClass);
        }
        return null;
    }

}
