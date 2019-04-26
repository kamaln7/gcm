package gcm.server;

import gcm.commands.Command;

public class CommandRunner implements Runnable {
    private Command command;
    private Server server;

    public CommandRunner(Command command, Server server) {
        this.command = command;
        this.server = server;
    }

    @Override
    public void run() {
        this.command.runOnServer(this.server);
        this.server.sendCommandReply(this.command);
    }
}
