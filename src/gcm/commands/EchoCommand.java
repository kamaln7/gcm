package gcm.commands;

import gcm.server.Server;
import ocsf.server.ConnectionToClient;

import java.io.IOException;
import java.io.Serializable;

public class EchoCommand implements Command<EchoCommandRequest, EchoCommandResponse>, Serializable {
    Server server;

    public EchoCommand New(Server server) {
        this.server = server;

        return this;
    }

    @Override
    public EchoCommandResponse runOnServer(EchoCommandRequest request, ConnectionToClient client) {
        try {
            client.sendToClient("You said: " + request.message);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new EchoCommandResponse(request.id, true);
    }
}
