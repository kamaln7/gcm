package gcm.database.models;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Tour extends Model {
    // fields
    private Integer id, cityId;
    private String description;
    private Date createdAt, updatedAt;

    /**
     * create object with info from ResultSet
     *
     * @param rs
     * @throws SQLException
     */
    public Tour(ResultSet rs) throws SQLException {
        super();

        this.fillFieldsFromResultSet(rs);
    }

    public Tour(Integer cityId, String description) {
        this.cityId = cityId;
        this.description = description;
    }

    /**
     * fill object with info from ResultSet
     *
     * @param rs
     * @throws SQLException
     */
    public void fillFieldsFromResultSet(ResultSet rs) throws SQLException {
        this.id = rs.getInt("id");
        this.cityId = rs.getInt("city_id");
        this.description = rs.getString("description");
        this.createdAt = rs.getTimestamp("created_at");
        this.updatedAt = rs.getTimestamp("updated_at");
    }

    /**
     * Find a tour by its id
     *
     * @param id
     * @return tour
     * @throws SQLException
     * @throws NotFound
     */
    public static Tour findById(Integer id) throws SQLException, NotFound {
        try (Connection db = getDb();
             PreparedStatement preparedStatement = db.prepareStatement("select * from tours where id = ?")) {
            preparedStatement.setInt(1, id);

            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (!rs.next()) {
                    throw new NotFound();
                }

                Tour tour = new Tour(rs);
                return tour;
            }
        }
    }

    /**
     * find all tours inside city
     *
     * @param city_id
     * @return List<Tour> tours
     * @throws SQLException
     * @throws NotFound
     */
    public static List<Tour> findAllByCityId(Integer city_id) throws SQLException, NotFound {
        try (Connection db = getDb();
             PreparedStatement preparedStatement = db.prepareStatement("select * from tours where city_id = ?")) {
            preparedStatement.setInt(1, city_id);

            try (ResultSet rs = preparedStatement.executeQuery()) {
                List<Tour> tours = new ArrayList<>();
                while (rs.next()) {
                    Tour tour = new Tour(rs);
                    tours.add(tour);
                }
                return tours;
            }
        }
    }

    /**
     * insert a new object to the database
     *
     * @throws SQLException
     * @throws NotFound
     * @throws AlreadyExists
     */
    public void insert() throws SQLException, NotFound, AlreadyExists {
        // insert city to table
        try (Connection db = getDb();
             PreparedStatement preparedStatement = db.prepareStatement("insert into tours (city_id, description) values (?, ?)", Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setInt(1, this.getCityId());
            preparedStatement.setString(2, this.getDescription());
            // run the insert command
            preparedStatement.executeUpdate();
            // get the auto generated id
            try (ResultSet rsGenerated = preparedStatement.getGeneratedKeys()) {
                if (!rsGenerated.next()) {
                    throw new NotFound();
                }

                // find the new attraction details
                Integer id = rsGenerated.getInt(1);
                this.updateWithNewDetailsById(id, "tours");
            }
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

    public Integer getCityId() {
        return cityId;
    }

    public void setCityId(Integer cityId) {
        this.cityId = cityId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
