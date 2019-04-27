package gcm.commands;

import gcm.server.Server;
import ocsf.server.ConnectionToClient;

import java.io.IOException;

public class EchoCommand implements Command<EchoCommandRequest, EchoCommandResponse> {
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
