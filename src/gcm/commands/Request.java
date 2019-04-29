package gcm.commands;

import com.google.gson.JsonElement;

import java.io.Serializable;

public class Request implements Serializable {
    public final String id;
    public final Class<? extends Command> command;
    public final JsonElement input;

    public Request(String id, Class<? extends Command> command, JsonElement input) {
        this.id = id;
        this.command = command;
        this.input = input;
    }
}
