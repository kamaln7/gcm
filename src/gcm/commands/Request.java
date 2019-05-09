package gcm.commands;

import com.google.gson.Gson;
import gcm.common.GsonSingleton;

import java.io.IOException;
import java.io.Serializable;
import java.util.UUID;

public class Request implements Serializable {
    private transient Gson gson = GsonSingleton.GsonSingleton().gson;

    public final String id;
    public final Class<? extends Command> command;
    public final String input;

    /**
     * Create a Request instance from an Input object.
     * Automatically find the Command class and store it
     *
     * @param input The Input object of the command
     */
    public Request(Input input) {
        this.id = UUID.randomUUID().toString();
        this.command = input.getClass().getEnclosingClass().asSubclass(Command.class);
        this.input = gson.toJson(input);
    }

    public <I extends Input> I getInput(Class<I> c) {
        return gson.fromJson(this.input, c);
    }

    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.gson = GsonSingleton.GsonSingleton().gson;
    }
}
