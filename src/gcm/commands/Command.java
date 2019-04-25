package gcm.commands;

import gcm.server.Server;

import java.io.Serializable;

public abstract class Command implements Serializable {
    public Object input, output;

    public abstract void runOnServer(Server server);
}