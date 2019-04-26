package gcm.commands;

import gcm.server.Server;

import java.io.Serializable;

public interface Command extends Serializable {
    static Response runOnServer(Server server, Request request) {
        throw new UnsupportedOperationException();
    }
}