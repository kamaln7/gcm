package gcm.database.models;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

public class Attraction extends Model {
    // fields
    private Integer id;
    private String name, city, type, location;
    private Date createdAt, updatedAt;

    // create User object with info from ResultSet
    public Attraction(ResultSet rs) throws SQLException {
        super();

        this.fillFieldsFromResultSet(rs);
    }

    public Attraction(String name, String city, String type, String location) {
        this.name = name;
        this.city = city;
        this.type = type;
        this.location = location;
    }



    public void fillFieldsFromResultSet(ResultSet rs) throws SQLException {
        this.id = rs.getInt("id");
        this.name = rs.getString("name");
        this.city = rs.getString("city");
        this.location = rs.getString("location");
        this.type = rs.getString("type");
        this.createdAt = rs.getTimestamp("created_at");
        this.updatedAt = rs.getTimestamp("updated_at");

    }


    /* QUERIES */
    public static Attraction findByUsername(String name) throws SQLException, NotFound {
        try (PreparedStatement preparedStatement = getDb().prepareStatement("select * from cities where name = ?")) {
            preparedStatement.setString(1, name);

            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (!rs.next()) {
                    throw new NotFound();
                }

                Attraction city = new Attraction(rs);
                return city;
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
        try (PreparedStatement preparedStatement = getDb().prepareStatement("select * from cities where id = ?")) {
            preparedStatement.setInt(1, id);

            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (!rs.next()) {
                    throw new Attraction.NotFound();
                }

                Attraction city = new Attraction(rs);
                return city;
            }
        }
    }

    public void insert() throws SQLException, NotFound , AlreadyExists{
            //check if the attraction already exist
            try (PreparedStatement preparedStatement = getDb().prepareStatement("SELECT * FROM attractions WHERE name = ? AND city = ?")){
                preparedStatement.setString(1, this.getName());
                preparedStatement.setString(2, this.getCity());
                //query
                try(ResultSet rs = preparedStatement.executeQuery()) {
                    if (rs.next()){
                        throw new AlreadyExists();
                    }
                }
            }
            // insert city to table
            try (PreparedStatement preparedStatement = getDb().prepareStatement("insert into attractions (name, city, type, location) values (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
                preparedStatement.setString(1, this.getName());
                preparedStatement.setString(2, this.getCity());
                preparedStatement.setString(3, this.getType());
                preparedStatement.setString(4, this.getLocation());
                // run the insert command
                preparedStatement.executeUpdate();
                // get the auto generated id
                ResultSet rs = preparedStatement.getGeneratedKeys();
                if (!rs.next()) {
                    throw new NotFound();
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

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
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
}
