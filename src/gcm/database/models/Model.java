package gcm.database.models;

import java.sql.Connection;
import java.util.HashMap;

// Models keeps one copy of the database connection to be used by models
public abstract class Model {
    protected static Connection db;
    public HashMap<String, String> _extraInfo;

    public Model() {
        this._extraInfo = new HashMap<>();
    }

    public static void setDb(Connection db) {
        Model.db = db;
    }

    public static Connection getDb() {
        return Model.db;
    }
}