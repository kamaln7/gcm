package gcm.database.models;

import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

// Models keeps one copy of the database connection to be used by models
public abstract class Model {
    protected static HikariDataSource ds;
    public HashMap<String, String> _extraInfo;

    public Model() {
        this._extraInfo = new HashMap<>();
    }

    public static void setDs(HikariDataSource ds) {
        Model.ds = ds;
    }

    public static Connection getDb() throws SQLException {
        return Model.ds.getConnection();
    }

    public abstract void fillFieldsFromResultSet(ResultSet rs) throws SQLException;

    protected void updateWithNewDetailsById(Integer id, String tableName) throws SQLException {
        try (Connection db = getDb();
             PreparedStatement preparedStatement = db.prepareStatement("select * from " + tableName + " where id = ?")) {
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