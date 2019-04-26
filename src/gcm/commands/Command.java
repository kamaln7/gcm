package gcm.commands;

import gcm.server.Server;

import java.io.Serializable;

public abstract class Command implements Serializable {
    public String id, clientId;
    public Object input, output;

    public abstract void runOnServer(Server server);
}