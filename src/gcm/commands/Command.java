package gcm.commands;

import gcm.server.Server;
import ocsf.server.ConnectionToClient;

public interface Command<Req extends Request, Res extends Response> {
    <C extends Command> C New(Server server);

    Res runOnServer(Req request, ConnectionToClient client);
}