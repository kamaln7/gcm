package gcm.commands;

import java.io.Serializable;

public class Request implements Serializable {
    public final String id;
    public final Class<? extends Command> command;
    public final String input;

    public Request(String id, Class<? extends Command> command, String input) {
        this.id = id;
        this.command = command;
        this.input = input;
    }
}
