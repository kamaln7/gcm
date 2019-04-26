package gcm.commands;

import java.io.Serializable;

public class BroadcastCommandRequest implements Request, Serializable {
    public String id, message;

    public BroadcastCommandRequest(String message) {
        this.message = message;
    }

    @Override
    public String getID() {
        return this.id;
    }
}
