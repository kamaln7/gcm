package gcm.database.models;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

public class ServerJob extends Model {
    // fields
    private Integer id;
    private String name;
    private Date createdAt, updatedAt;

    // create User object with info from ResultSet
    public ServerJob(ResultSet rs) throws SQLException {
        super();

        this.fillFieldsFromResultSet(rs);
    }

    public ServerJob(String name) {
        this.name = name;
    }

    public static ServerJob findLatestByName(String name) throws SQLException, NotFound {
        try (PreparedStatement preparedStatement = getDb().prepareStatement(
                "select *" +
                        " from server_jobs" +
                        " where name = ?" +
                        " order by created_at desc" +
                        " limit 1"
        )) {
            preparedStatement.setString(1, name);

            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (!rs.next()) {
                    throw new NotFound();
                }

                return new ServerJob(rs);
            }
        }
    }

    public ServerJob insert() throws SQLException, NotFound {
        // insert user to table
        try (PreparedStatement preparedStatement = getDb().prepareStatement(
                "insert into server_jobs" +
                        " (name) values (?)", Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, this.getName());
            // run the insert command
            preparedStatement.executeUpdate();
            // get the auto generated id
            ResultSet rs = preparedStatement.getGeneratedKeys();
            if (!rs.next()) {
                throw new NotFound();
            }

            // find the new model details
            Integer id = rs.getInt(1);
            this.updateWithNewDetailsById(id, "server_jobs");
        }

        return this;
    }

    public void fillFieldsFromResultSet(ResultSet rs) throws SQLException {
        this.id = rs.getInt("id");
        this.name = rs.getString("name");
        this.createdAt = rs.getTimestamp("created_at");
        this.updatedAt = rs.getTimestamp("updated_at");
    }


    /* QUERIES */


    // exceptions
    public static class NotFound extends Exception {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}
