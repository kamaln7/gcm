package gcm.commands;

import gcm.server.Server;
import ocsf.server.ConnectionToClient;

import java.io.Serializable;

public class BroadcastCommand implements Command<BroadcastCommandRequest, BroadcastCommandResponse>, Serializable {
    Server server;

    public BroadcastCommand New(Server server) {
        this.server = server;

        return this;
    }

    @Override
    public BroadcastCommandResponse runOnServer(BroadcastCommandRequest request, ConnectionToClient client) {
        this.server.sendToAllClients("Broadcasted: " + request.message);
        return new BroadcastCommandResponse(request.id, true);
    }
}
