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
import service.GameService;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import static websocket.messages.ServerMessage.ServerMessageType.LOAD_GAME;


public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private final ConnectionManager connections = new ConnectionManager();
    private GameService gameService;

    //DataAccess dataAccess;

    public void setDataAccess(DataAccess dataAccess) {
        this.gameService = new GameService(dataAccess, connections);
        //this.dataAccess = dataAccess;
    }

    @Override
    public void handleConnect(@NotNull WsConnectContext ctx) {
        System.out.println("Step 3");
        ctx.enableAutomaticPings();
    }

    public void handleMessage(@NotNull WsMessageContext ctx) throws Exception {
        System.out.println("Step 4");
        UserGameCommand userGameCommand = new Gson().fromJson(ctx.message(), UserGameCommand.class);
        gameService.handleUserCommand(userGameCommand, ctx.session);
    }

    @Override
    public void handleClose(@NotNull WsCloseContext ctx) {
        System.out.println("websocket Closed");
    }
}
