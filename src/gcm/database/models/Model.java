package gcm.database.models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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

    public abstract void fillFieldsFromResultSet(ResultSet rs) throws SQLException;

    protected void updateWithNewDetailsById(Integer id, String tableName) throws SQLException {
        try (PreparedStatement preparedStatement = getDb().prepareStatement("select * from " + tableName + " where id = ?")) {
            preparedStatement.setInt(1, id);

            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (!rs.next()) {
                    return;
                }

                this.fillFieldsFromResultSet(rs);
            }
        }
    }
}