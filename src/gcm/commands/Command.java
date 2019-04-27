package gcm.commands;

import gcm.server.Server;
import ocsf.server.ConnectionToClient;

import java.io.Serializable;

public interface Command<Req extends Request, Res extends Response> extends Serializable {
    <C extends Command> C New(Server server);

    Res runOnServer(Req request, ConnectionToClient client);
}