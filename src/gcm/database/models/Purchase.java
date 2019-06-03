package gcm.database.models;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

public class Purchase extends Model {
    private Integer id, userId, cityId;
    private Date createdAt, updatedAt;

    @Override
    public void fillFieldsFromResultSet(ResultSet rs) throws SQLException {
        this.id = rs.getInt("id");
        this.userId = rs.getInt("user_id");
        this.cityId = rs.getInt("city_id");
        this.createdAt = rs.getTimestamp("created_at");
        this.updatedAt = rs.getTimestamp("updated_at");
    }

    public Purchase(ResultSet rs) throws SQLException {
        super();

        this.fillFieldsFromResultSet(rs);
    }

    public Purchase(Integer userId, Integer cityId) {
        this.userId = userId;
        this.cityId = cityId;
    }

    public static Purchase findById(Integer id) throws SQLException, NotFound {
        try (PreparedStatement preparedStatement = getDb().prepareStatement("select * from purchases where id = ?")) {
            preparedStatement.setInt(1, id);

            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (!rs.next()) {
                    throw new NotFound();
                }

                Purchase purchase = new Purchase(rs);
                return purchase;
            }
        }
    }

    public void insert() throws SQLException, NotFound, AlreadyExists {
        // insert city to table
        try (PreparedStatement preparedStatement = getDb().prepareStatement("insert into purchases (user_id, city_id) values (?, ?)", Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setInt(1, getUserId());
            preparedStatement.setInt(2, getCityId());
            // run the insert command
            preparedStatement.executeUpdate();
            // get the auto generated id
            try (ResultSet rsGenerated = preparedStatement.getGeneratedKeys()) {
                if (!rsGenerated.next()) {
                    throw new NotFound();
                }

                // find the new attraction details
                Integer id = rsGenerated.getInt(1);
                this.updateWithNewDetailsById(id, "purchases");
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
}
