package gcm.database.models;

import java.sql.*;
import java.util.Date;

public class Subscription extends Model {
    private Integer id, userId, cityId;
    private Date fromDate, toDate, createdAt, updatedAt;

    @Override
    public void fillFieldsFromResultSet(ResultSet rs) throws SQLException {
        this.id = rs.getInt("id");
        this.userId = rs.getInt("user_id");
        this.cityId = rs.getInt("city_id");
        this.fromDate = rs.getTimestamp("from_date");
        this.toDate = rs.getTimestamp("to_date");
        this.createdAt = rs.getTimestamp("created_at");
        this.updatedAt = rs.getTimestamp("updated_at");
    }

    public Subscription(ResultSet rs) throws SQLException {
        super();

        this.fillFieldsFromResultSet(rs);
    }

    public Subscription(Integer userId, Integer cityId, Date fromDate, Date toDate) {
        this.userId = userId;
        this.cityId = cityId;
        this.fromDate = fromDate;
        this.toDate = toDate;
    }

    public static Subscription findById(Integer id) throws SQLException, NotFound {
        try (PreparedStatement preparedStatement = getDb().prepareStatement("select * from subscriptions where id = ?")) {
            preparedStatement.setInt(1, id);

            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (!rs.next()) {
                    throw new NotFound();
                }

                Subscription subscription = new Subscription(rs);
                return subscription;
            }
        }
    }

    public void insert() throws SQLException, NotFound, AlreadyExists {
        // insert city to table
        try (PreparedStatement preparedStatement = getDb().prepareStatement("insert into subscriptions (user_id, city_id, from_date, to_date) values (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setInt(1, getUserId());
            preparedStatement.setInt(2, getCityId());
            preparedStatement.setTimestamp(3, new Timestamp(getFromDate().getTime()));
            preparedStatement.setTimestamp(4, new Timestamp(getToDate().getTime()));
            // run the insert command
            preparedStatement.executeUpdate();
            // get the auto generated id
            try (ResultSet rsGenerated = preparedStatement.getGeneratedKeys()) {
                if (!rsGenerated.next()) {
                    throw new NotFound();
                }

                // find the new attraction details
                Integer id = rsGenerated.getInt(1);
                this.updateWithNewDetailsById(id, "subscriptions");
            }
        } catch (java.sql.SQLIntegrityConstraintViolationException e) {
            throw new AlreadyExists();
        }
    }

    public static class AlreadyExists extends Exception {
    }

    public static class NotFound extends Exception {
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getCityId() {
        return cityId;
    }

    public void setCityId(Integer cityId) {
        this.cityId = cityId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getFromDate() {
        return fromDate;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public Date getToDate() {
        return toDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
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
