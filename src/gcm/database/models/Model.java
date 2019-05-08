package gcm.database.models;

import java.sql.Connection;

public abstract class Model {
    protected static Connection db;

    public static void setDb(Connection db) {
        Model.db = db;
    }

    public static Connection getDb() {
        return Model.db;
    }
}