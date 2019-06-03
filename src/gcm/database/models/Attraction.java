package gcm.database.models;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Attraction extends Model {
    // fields
    private Integer id, cityId;
    private Boolean accessibleSpecial;
    private String name, description, type, location;
    private Date createdAt, updatedAt;

    // create User object with info from ResultSet
    public Attraction(ResultSet rs) throws SQLException {
        super();

        this.fillFieldsFromResultSet(rs);
    }

    public Attraction(Integer cityId, Boolean accessibleSpecial, String name, String description, String type, String location) {
        this.cityId = cityId;
        this.accessibleSpecial = accessibleSpecial;
        this.name = name;
        this.description = description;
        this.type = type;
        this.location = location;
    }

    public void fillFieldsFromResultSet(ResultSet rs) throws SQLException {
        this.id = rs.getInt("id");
        this.name = rs.getString("name");
        this.cityId = rs.getInt("city_id");
        this.location = rs.getString("location");
        this.type = rs.getString("type");
        this.accessibleSpecial = rs.getBoolean("accessible_special");
        this.description = rs.getString("description");
        this.createdAt = rs.getTimestamp("created_at");
        this.updatedAt = rs.getTimestamp("updated_at");
    }


    /* QUERIES */
    public static Attraction findByName(String name) throws SQLException, NotFound {
        try (PreparedStatement preparedStatement = getDb().prepareStatement("select * from attractions where name = ?")) {
            preparedStatement.setString(1, name);

            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (!rs.next()) {
                    throw new NotFound();
                }

                Attraction attraction = new Attraction(rs);
                return attraction;
            }
        }
    }

    public static List<Attraction> searchByNameOrDescription(String searchQuery) throws SQLException {
        if (searchQuery.equals("")) {
            return findAll();
        }

        try (PreparedStatement preparedStatement = getDb().prepareStatement("select * from attractions where match (name, description) against (?) order by name asc")) {
            preparedStatement.setString(1, searchQuery);

            try (ResultSet rs = preparedStatement.executeQuery()) {
                List<Attraction> attractions = new ArrayList<>();
                while (rs.next()) {
                    Attraction attraction = new Attraction(rs);
                    attractions.add(attraction);
                }

                return attractions;
            }
        }
    }

    private static List<Attraction> findAll() throws SQLException {
        try (Statement statement = getDb().createStatement()) {
            try (ResultSet rs = statement.executeQuery("select * from attractions order by name asc")) {
                List<Attraction> attractions = new ArrayList<>();
                while (rs.next()) {
                    Attraction attraction = new Attraction(rs);
                    attractions.add(attraction);
                }

                return attractions;
            }
        }
    }

    /**
     * Find a user by its id
     *
     * @param id The user id to find
     * @return User The requested user
     * @throws SQLException
     * @throws NotFound     if no such user
     */
    public static Attraction findById(Integer id) throws SQLException, NotFound {
        try (PreparedStatement preparedStatement = getDb().prepareStatement("select * from attractions where id = ?")) {
            preparedStatement.setInt(1, id);

            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (!rs.next()) {
                    throw new NotFound();
                }

                Attraction attraction = new Attraction(rs);
                return attraction;
            }
        }
    }

    public void insert() throws SQLException, NotFound, AlreadyExists {
        // insert city to table
        try (PreparedStatement preparedStatement = getDb().prepareStatement("insert into attractions (name, city_id, type, location, description, accessible_special) values (?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, this.getName());
            preparedStatement.setInt(2, this.getCityId());
            preparedStatement.setString(3, this.getType());
            preparedStatement.setString(4, this.getLocation());
            preparedStatement.setString(5, this.getDescription());
            preparedStatement.setBoolean(6, this.getAccessibleSpecial());
            // run the insert command
            preparedStatement.executeUpdate();
            // get the auto generated id
            try (ResultSet rsGenerated = preparedStatement.getGeneratedKeys()) {
                if (!rsGenerated.next()) {
                    throw new NotFound();
                }

                // find the new attraction details
                Integer id = rsGenerated.getInt(1);
                this.updateWithNewDetailsById(id, "attractions");
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCityId() {
        return cityId;
    }

    public void setCityId(Integer cityId) {
        this.cityId = cityId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getAccessibleSpecial() {
        return accessibleSpecial;
    }

    public void setAccessibleSpecial(Boolean accessibleSpecial) {
        this.accessibleSpecial = accessibleSpecial;
    }
}
