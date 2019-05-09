package gcm.commands;

import com.google.gson.Gson;
import gcm.common.GsonSingleton;

import java.io.IOException;
import java.io.Serializable;

public class Response implements Serializable {
    private transient Gson gson = GsonSingleton.GsonSingleton().gson;

    public final String id;
    public final Class<? extends Command> command;
    public final String output;
    public final Exception exception;

    public Response(Request request, Output output, Exception exception) {
        this.id = request.id;
        this.command = request.command;
        this.output = gson.toJson(output);
        this.exception = exception;
    }

    public <O extends Output> O getOutput(Class<O> c) throws Exception {
        if (this.exception != null) {
            throw this.exception;
        }

        return gson.fromJson(this.output, c);
    }

    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.gson = GsonSingleton.GsonSingleton().gson;
    }
}
