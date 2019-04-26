package gcm.commands;

import java.io.Serializable;

public class BroadcastCommandResponse implements Response, Serializable {
    public String id;
    public Boolean ok;

    public BroadcastCommandResponse(boolean ok) {
        this.ok = ok;
    }

    @Override
    public String getID() {
        return this.id;
    }
}
