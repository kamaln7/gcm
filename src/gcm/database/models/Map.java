package gcm.database.models;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Date;

public class Map extends Model {
    // fields
    private Integer id, cityId, verification; // 1 is verified 0 is not verified
    private String title, description, version, img, cityName;
    private Date createdAt, updatedAt;
    // create User object with info from ResultSet
    public Map(ResultSet rs) throws SQLException {
        super();

        this.fillFieldsFromResultSet(rs);
    }

    public Map(String title, String description, String version, String img) {

        this.title = title;
        this.description = description;
        this.version = version;
        this.img = img;

    }
    public Map(String title, String description, String version, String img, String cityName) {

        this.title = title;
        this.description = description;
        this.version = version;
        this.img = img;
        this.cityName= cityName;

    }
    public Map(String title, String description, String version, String img, int cityId) {

        this.title = title;
        this.description = description;
        this.version = version;
        this.img = img;
        this.cityId=cityId;
    }

    public void fillFieldsFromResultSet(ResultSet rs) throws SQLException {
        this.id = rs.getInt("id");
        this.cityId = rs.getInt("cityId");
        this.title = rs.getString("title");
        this.description = rs.getString("description");
        this.version = rs.getString("version");
        this.createdAt = rs.getTimestamp("created_at");
        this.updatedAt = rs.getTimestamp("updated_at");
        this.img = rs.getString("img");
        this.verification= rs.getInt("verification");
    }



    /* QUERIES */

    /**
     * Find a user by its id
     *
     * @param id The user id to find
     * @return User The requested user
     * @throws SQLException
     * @throws NotFound     if no such user
     */
    public static Map findById(Integer id) throws Exception {
        try (PreparedStatement preparedStatement = getDb().prepareStatement("select * from maps where id = ?")) {
            preparedStatement.setInt(1, id);

            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (!rs.next()) {
                    throw new Map.NotFound();
                }

                Map map = new Map(rs);
                return map;
            }
        }
    }

    /**
     * Find a user by its username
     *
     * @param username The username to find
     * @return User The requested user
     * @throws SQLException
     * @throws NotFound     if no such user
     */
    public static Map findByTitle(String title) throws SQLException, NotFound {
        try (PreparedStatement preparedStatement = getDb().prepareStatement("select * from maps where title = ?")) {
            preparedStatement.setString(1, title);

            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (!rs.next()) {
                    throw new Map.NotFound();
                }

                Map map = new Map(rs);
                return map;
            }
        }
    }


    public static Map findByTitleAndVersion(String title, String version) throws SQLException, NotFound {
        try (PreparedStatement preparedStatement = getDb().prepareStatement("select * from maps where title = ? And version = ?")) {
            preparedStatement.setString(1, title);
            preparedStatement.setString(2, version);

            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (!rs.next()) {
                    throw new Map.NotFound();
                }
                Map map = new Map(rs);
                return map;
            }
        }
    }



    public void insert() throws SQLException, Map.NotFound, Map.AlreadyExists, City.NotFound {

        //check if the city already exist
        try (PreparedStatement preparedStatement = getDb().prepareStatement("SELECT * FROM maps WHERE title = ? AND version = ?")){
            preparedStatement.setString(1, this.getTitle());
            preparedStatement.setString(2, this.getVersion());
            //query
            try(ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()){
                    throw new Map.AlreadyExists();
                }
            }
        }

        // get city id
        try (PreparedStatement preparedStatement = getDb().prepareStatement("Select id from cities where name = ?")) {
            preparedStatement.setString(1, this.getCityName());

            // run the insert command
            preparedStatement.executeQuery();
            // get the auto generated id
            ResultSet rs = preparedStatement.getResultSet();
            if (!rs.next()) {
                throw new City.NotFound();
            }
            this.cityId= rs.getInt("id");
        }

        // insert map to table
        try (PreparedStatement preparedStatement = getDb().prepareStatement("insert into maps (title, version, description, img, cityId) values (?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setString(1, this.getTitle());
            preparedStatement.setString(2, this.getVersion());
            preparedStatement.setString(3, this.getDescription());
            preparedStatement.setString(4, this.getImg());
            preparedStatement.setInt(5, this.getCityId());

            // run the insert command
            preparedStatement.executeUpdate();
            // get the auto generated id
            ResultSet rs = preparedStatement.getGeneratedKeys();
            if (!rs.next()) {
                throw new Map.NotFound();
            }


        }
    }



    // exceptions
    public static class NotFound extends Exception {
    }
    public static class AlreadyExists extends Exception {
    }
    public static class WrongType extends Exception {
    }


    // getters and setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
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
    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }
    public Integer getCityId() {
        return cityId;
    }

    public void setCityId(Integer cityId) {
        this.cityId = cityId;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

}
