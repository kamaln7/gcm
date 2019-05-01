package gcm.commands;

import com.google.gson.Gson;
import gcm.common.GsonSingleton;
import gcm.server.Server;
import ocsf.server.ConnectionToClient;

public interface Command {
    Gson gson = GsonSingleton.GsonSingleton().gson;

    Output runOnServer(Request request, Server server, ConnectionToClient client);
}