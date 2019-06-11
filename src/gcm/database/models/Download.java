package gcm.database.models;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

public class Download extends Model {
    // fields
    private Integer id, user_id, model_id;
    private Date createdAt;
    private String model;

    // create User object with info from ResultSet
    public Download(ResultSet rs) throws SQLException {
        super();

        this.fillFieldsFromResultSet(rs);
    }

    public Download(Integer user_id, Integer model_id, String model) {
        this.user_id = user_id;
        this.model_id = model_id;
        this.model = model;
    }

    /**
     * fill an instance from result set
     *
     * @param rs result set
     * @throws SQLException
     */
    public void fillFieldsFromResultSet(ResultSet rs) throws SQLException {
        this.id = rs.getInt("id");
        this.user_id = rs.getInt("user_id");
        this.model_id = rs.getInt("model_id");
        this.model = rs.getString("model");
        this.createdAt = rs.getTimestamp("created_at");

    }

    /**
     * insert a download to the database
     *
     * @throws SQLException
     */
    public void insert() throws SQLException {
        // insert city to table
        try (PreparedStatement preparedStatement = getDb().prepareStatement("insert into downloads (user_id, model_id, model) values (?, ?, ?)")) {
            preparedStatement.setInt(1, this.getUser_id());
            preparedStatement.setInt(2, this.getModel_id());
            preparedStatement.setString(3, this.getModel());
            // run the insert command
            preparedStatement.executeUpdate();
        }
    }

    /**
     * Count rows in database by city id, in period of time between 2 selected dates
     *
     * @param id   of city
     * @param from date
     * @param to   date
     * @return number of matching rows
     * @throws SQLException
     */
    public static int countByPeriod(Integer id, Date from, Date to) throws SQLException {
        try (PreparedStatement preparedStatement = getDb().prepareStatement("select count(*) as total from maps, downloads where maps.city_id = ? and maps.id = downloads.model_id and downloads.model = 'map' and downloads.created_at >= ? and downloads.created_at <= ?")) {
            preparedStatement.setInt(1, id);
            preparedStatement.setTimestamp(2, new Timestamp(from.getTime()));
            preparedStatement.setTimestamp(3, new Timestamp(to.getTime()));

            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (!rs.next()) {
                    return 0;
                }

                return rs.getInt("total");
            }
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
