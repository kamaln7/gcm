package gcm.commands;

import java.io.Serializable;

public class EchoCommandResponse implements Response, Serializable {
    public String id;
    public Boolean ok;

    public EchoCommandResponse(String id, boolean ok) {
        this.id = id;
        this.ok = ok;
    }
}
