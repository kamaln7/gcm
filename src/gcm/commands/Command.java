package gcm.commands;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import gcm.common.GsonSingleton;
import gcm.server.Server;
import ocsf.server.ConnectionToClient;

public interface Command {
    Gson gson = GsonSingleton.GsonSingleton().gson;

    JsonElement runOnServer(Request request, Server server, ConnectionToClient client);
}