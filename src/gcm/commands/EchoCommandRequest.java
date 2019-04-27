package gcm.commands;

import java.io.Serializable;

public class EchoCommandRequest implements Request, Serializable {
    public String id, message;

    public EchoCommandRequest(String id, String message) {
        this.id = id;
        this.message = message;
    }
}
