package gcm.database.models;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Attraction extends Model {
    public static final String[] types = {"Museum", "Historical Place", "Hotel", "Restaurant", "Public Institution", "Park", "Cinema", "Parking Lot", "Coffee Shop"};
    // fields
    private Integer id, cityId;
    private Boolean accessibleSpecial, accessibleSpecialNew = null;
    private String name, description, descriptionNew = null, type, typeNew = null, location, locationNew = null;
    private Date createdAt, updatedAt;

    public Attraction() {
    }

    // create User object with info from ResultSet
    public Attraction(ResultSet rs) throws SQLException {
        super();

        this.fillFieldsFromResultSet(rs);
    }

    public Attraction(Integer cityId, String name, String description, String type, String location, Boolean accessibleSpecial) {
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
        this.locationNew = rs.getString("location_new");
        this.type = rs.getString("type");
        this.typeNew = rs.getString("type_new");
        this.accessibleSpecial = rs.getBoolean("accessible_special");
        this.accessibleSpecialNew = rs.getBoolean("accessible_special_new");
        this.description = rs.getString("description");
        this.descriptionNew = rs.getString("description_new");
        this.createdAt = rs.getTimestamp("created_at");
        this.updatedAt = rs.getTimestamp("updated_at");
    }


    /* QUERIES */
    public static void updateAttraction(int attraction_id, String attraction_type, String attraction_location, String description, boolean accessibility) throws SQLException, NotFound {
        try (Connection db = getDb();
             PreparedStatement preparedStatement = db.prepareStatement("UPDATE attractions SET description_new=? ,type_new=? ,location_new=?, accessible_special_new=? WHERE id = ?")) {
            preparedStatement.setString(1, description);
            preparedStatement.setString(2, attraction_type);
            preparedStatement.setString(3, attraction_location);
            preparedStatement.setBoolean(4, accessibility);
            preparedStatement.setInt(5, attraction_id);
            preparedStatement.executeUpdate();
        }
    }

    public static List<Attraction> findAllByCityId(Integer cityId) throws SQLException {
        try (Connection db = getDb();
             PreparedStatement preparedStatement = db.prepareStatement("select * from attractions where city_id = ? order by name asc")) {
            preparedStatement.setInt(1, cityId);
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

    public static List<Attraction> searchByNameOrDescription(String searchQuery) throws SQLException {
        if (searchQuery.equals("")) {
            return findAll();
        }

        try (Connection db = getDb();
             PreparedStatement preparedStatement = db.prepareStatement(
                     "select attractions.*, concat(cities.name, \", \", cities.country) as city_title" +
                             " from attractions" +
                             " left join cities on attractions.city_id = cities.id" +
                             " where attractions.name like ? or attractions.description like ?" +
                             " order by attractions.name asc"
             )) {
            preparedStatement.setString(1, '%' + searchQuery + '%');
            preparedStatement.setString(2, '%' + searchQuery + '%');

            try (ResultSet rs = preparedStatement.executeQuery()) {
                List<Attraction> attractions = new ArrayList<>();
                while (rs.next()) {
                    Attraction attraction = new Attraction(rs);
                    attraction._extraInfo.put("cityTitle", rs.getString("city_title"));
                    attractions.add(attraction);
                }

                return attractions;
            }
        }
    }

    public static List<Attraction> findPendingApproval() throws SQLException {
        try (Connection db = getDb();
             Statement statement = db.createStatement()) {
            try (ResultSet rs = statement.executeQuery(
                    "select attractions.*, concat(cities.name, \", \", cities.country) as city_title\n" +
                            "from attractions \n" +
                            "left join cities\n" +
                            "on attractions.city_id = cities.id\n" +
                            "where \n" +
                            "description_new is not NULL\n" +
                            "or type_new is not NULL\n" +
                            "or location_new is not NULL\n" +
                            "or accessible_special_new is not null"
            )) {
                List<Attraction> attractions = new ArrayList<>();
                while (rs.next()) {
                    Attraction attraction = new Attraction(rs);
                    attraction._extraInfo.put("cityTitle", rs.getString("city_title"));
                    attractions.add(attraction);
                }

                return attractions;
            }
        }
    }

    private static List<Attraction> findAll() throws SQLException {
        try (Connection db = getDb();
             Statement statement = db.createStatement()) {
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
        try (Connection db = getDb();
             PreparedStatement preparedStatement = db.prepareStatement("select * from attractions where id = ?")) {
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
        // insert attraction to table
        try (Connection db = getDb();
             PreparedStatement preparedStatement = db.prepareStatement("insert into attractions (name, city_id, type, location, description, accessible_special) values (?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
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

    public static List<Attraction> getAttractionForCity(int city_id) throws SQLException, NotFound {
        try (Connection db = getDb();
             PreparedStatement preparedStatement = db.prepareStatement("SELECT * FROM attractions WHERE city_id = ?")) {
            preparedStatement.setInt(1, city_id);
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

    public Boolean getAccessibleSpecialNew() {
        return accessibleSpecialNew;
    }

    public void setAccessibleSpecialNew(Boolean accessibleSpecialNew) {
        this.accessibleSpecialNew = accessibleSpecialNew;
    }

    public String getDescriptionNew() {
        return descriptionNew;
    }

    public void setDescriptionNew(String descriptionNew) {
        this.descriptionNew = descriptionNew;
    }

    public String getTypeNew() {
        return typeNew;
    }

    public void setTypeNew(String typeNew) {
        this.typeNew = typeNew;
    }

    public String getLocationNew() {
        return locationNew;
    }

    public void setLocationNew(String locationNew) {
        this.locationNew = locationNew;
    }
}
