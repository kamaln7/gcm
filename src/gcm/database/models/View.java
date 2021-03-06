package gcm.database.models;

import java.sql.*;
import java.util.Date;

public class View extends Model {
    // fields
    private Integer id, user_id, model_id;
    private Date createdAt;
    private String model;

    // create View object with info from ResultSet
    public View(ResultSet rs) throws SQLException {
        super();

        this.fillFieldsFromResultSet(rs);
    }

    public View(Integer user_id, Integer model_id, String model) {
        this.user_id = user_id;
        this.model_id = model_id;
        this.model = model;
    }

    public void fillFieldsFromResultSet(ResultSet rs) throws SQLException {
        this.id = rs.getInt("id");
        this.user_id = rs.getInt("user_id");
        this.model_id = rs.getInt("model_id");
        this.model = rs.getString("model");
        this.createdAt = rs.getTimestamp("created_at");

    }


    /**
     * insert a new view to the database
     *
     * @throws SQLException
     * @throws NotFound
     * @throws AlreadyExists
     */

    public void insert() throws SQLException, NotFound, AlreadyExists {
        // insert city to table
        try (Connection db = getDb();
             PreparedStatement preparedStatement = db.prepareStatement("insert into views (user_id, model_id, model) values (?, ?, ?)")) {
            preparedStatement.setInt(1, this.getUser_id());
            preparedStatement.setInt(2, this.getModel_id());
            preparedStatement.setString(3, this.getModel());
            // run the insert command
            preparedStatement.executeUpdate();
        } catch (java.sql.SQLIntegrityConstraintViolationException e) {
            throw new AlreadyExists();
        }
    }


    // exceptions
    public static class NotFound extends Exception {
    }

    public static class AlreadyExists extends Exception {
    }

    // getters and setters


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUser_id() {
        return user_id;
    }

    public void setUser_id(Integer user_id) {
        this.user_id = user_id;
    }

    public Integer getModel_id() {
        return model_id;
    }

    public void setModel_id(Integer model_id) {
        this.model_id = model_id;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }
}
