package gcm.commands;

import com.google.gson.JsonElement;

import java.io.Serializable;

public class Response implements Serializable {
    public final String id;
    public final Class<? extends Command> command;
    public final JsonElement output;

    public Response(String id, Class<? extends Command> command, JsonElement output) {
        this.id = id;
        this.command = command;
        this.output = output;
    }
}
