package gcm.commands;

import gcm.server.Server;

import java.io.Serializable;

public class BroadcastCommand implements Command, Serializable {
    public static BroadcastCommandResponse runOnServer(Server server, BroadcastCommandRequest request) {
        server.sendToAllClients("Broadcast from a client: " + request.message);
        return new BroadcastCommandResponse(true);
    }
}
