package gcm.commands;

import java.io.Serializable;

public interface Request extends Serializable {
    public String getID();
}
