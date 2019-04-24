package gcm.commands;

import gcm.server.Server;

import java.io.Serializable;

public abstract class Command implements Serializable {
    public static String NAME;
    public String args;

    public abstract void runOnServer(Server server);
}