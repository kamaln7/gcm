package gcm.database.models;

import org.mindrot.jbcrypt.BCrypt;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

public class Map extends Model {
    // fields
    private Integer id, one_off_price, subscription_price ;
    private String title, description, version;
    private Date createdAt, updatedAt;

    // create User object with info from ResultSet
    public Map(ResultSet rs) throws SQLException {
        super();

        this.fillFieldsFromResultSet(rs);
    }

    public Map( Integer one_off_price, Integer subscription_price, String title, String description, String version) {
        this.one_off_price = one_off_price;
        this.subscription_price = subscription_price;
        this.title = title;
        this.description = description;
        this.version = version;
    }

    public void fillFieldsFromResultSet(ResultSet rs) throws SQLException {
        this.id = rs.getInt("id");
        this.one_off_price = rs.getInt("one_off_price");
        this.subscription_price = rs.getInt("subscription_price");
        this.title = rs.getString("title");
        this.description = rs.getString("description");
        this.version = rs.getString("version");
        this.createdAt = rs.getTimestamp("created_at");
        this.updatedAt = rs.getTimestamp("updated_at");
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
    public static Map findById(Integer id) throws SQLException, NotFound {
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
    public void insert() throws SQLException, Map.NotFound, Map.AlreadyExists {
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
        // insert city to table
        try (PreparedStatement preparedStatement = getDb().prepareStatement("insert into maps (title, version, one_off_price, subscription_price, description) values (?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, this.getTitle());
            preparedStatement.setString(2, this.getVersion());
            preparedStatement.setInt(3, this.getOne_off_price());
            preparedStatement.setInt(4, this.getSubscription_price());
            preparedStatement.setString(5, this.getDescription());
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

    public Integer getOne_off_price() {
        return one_off_price;
    }

    public void setOne_off_price(Integer one_off_price) {
        this.one_off_price = one_off_price;
    }

    public Integer getSubscription_price() {
        return subscription_price;
    }

    public void setSubscription_price(Integer subscription_price) {
        this.subscription_price = subscription_price;
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
}
