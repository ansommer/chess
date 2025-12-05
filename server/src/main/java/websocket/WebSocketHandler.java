package websocket;

import com.google.gson.Gson;
import dataaccess.DataAccess;
import dataaccess.MySQLDataAccess;
import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsCloseHandler;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsConnectHandler;
import io.javalin.websocket.WsMessageContext;
import io.javalin.websocket.WsMessageHandler;
import org.eclipse.jetty.websocket.api.Session;
import org.jetbrains.annotations.NotNull;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import static websocket.messages.ServerMessage.ServerMessageType.LOAD_GAME;


public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private final ConnectionManager connections = new ConnectionManager();
    DataAccess dataAccess;

    public void setDataAccess(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    @Override
    public void handleConnect(@NotNull WsConnectContext ctx) {
        System.out.println("websocket Connected");
        ctx.enableAutomaticPings();
    }

    public void handleMessage(@NotNull WsMessageContext ctx) throws Exception {
        UserGameCommand userGameCommand = new Gson().fromJson(ctx.message(), UserGameCommand.class);
        String username = dataAccess.getUserFromAuthToken(userGameCommand.getAuthToken());
        //I may not need this because I will somehow have access to the GameData
        switch (userGameCommand.getCommandType()) {
            case CONNECT -> connect(username, ctx.session);
        }
    }

    public void connect(String username, Session session) throws Exception {
        connections.add(session);
        ServerMessage serverMessage = new ServerMessage(LOAD_GAME);
        connections.broadcast(session, serverMessage);
    }


    @Override
    public void handleClose(@NotNull WsCloseContext ctx) {
        System.out.println("websocket Closed");
    }
}
