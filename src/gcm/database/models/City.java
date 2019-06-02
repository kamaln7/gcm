package gcm.database.models;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class City extends Model {
    // fields
    private Integer id;
    private String name, country;
    private Date createdAt, updatedAt;
    private double subscription_price, purchase_price, new_subscription_price, new_purchase_price;

    // create User object with info from ResultSet
    public City(ResultSet rs) throws SQLException {
        super();

        this.fillFieldsFromResultSet(rs);
    }


    public City(String name, String country) {
        this.name = name;
        this.country = country;
    }

    public City(String name, String country, double subscription_price, double purchase_price) {
        this.name = name;
        this.country = country;
        this.subscription_price = subscription_price;
        this.purchase_price = purchase_price;
    }

    public static List<City> searchByName(String searchQuery) throws SQLException {
        if (searchQuery.equals("")) {
            return findAll();
        }

        return new ArrayList<>();
    }

    private static List<City> findAll() throws SQLException {
        try (Statement statement = getDb().createStatement()) {
            ResultSet rs = statement.executeQuery("select * from cities order by country, name asc");

            List<City> cities = new ArrayList<>();
            while (rs.next()) {
                City city = new City(rs);
                cities.add(city);
            }

            return cities;
        }
    }

    public void fillFieldsFromResultSet(ResultSet rs) throws SQLException {
        this.id = rs.getInt("id");
        this.name = rs.getString("name");
        this.country = rs.getString("country");
        this.createdAt = rs.getTimestamp("created_at");
        this.updatedAt = rs.getTimestamp("updated_at");
        this.subscription_price = rs.getDouble("subscription_price");
        this.purchase_price = rs.getDouble("purchase_price");
        this.new_purchase_price = rs.getDouble("new_purchase_price");
        this.new_subscription_price = rs.getDouble("new_sub_price");
    }


    /* QUERIES */
    public static City findByUsername(String name) throws SQLException, NotFound {
        try (PreparedStatement preparedStatement = getDb().prepareStatement("select * from cities where name = ?")) {
            preparedStatement.setString(1, name);

            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (!rs.next()) {
                    throw new NotFound();
                }

                City city = new City(rs);
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
    public static City findById(Integer id) throws SQLException, NotFound {
        try (PreparedStatement preparedStatement = getDb().prepareStatement("select * from cities where id = ?")) {
            preparedStatement.setInt(1, id);

            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (!rs.next()) {
                    throw new City.NotFound();
                }

                City city = new City(rs);
                return city;
            }
        }
    }

    public void insert() throws SQLException, NotFound, AlreadyExists {
        //check if the city already exist
        try (PreparedStatement preparedStatement = getDb().prepareStatement("SELECT * FROM cities WHERE name = ? AND country = ?")) {
            preparedStatement.setString(1, this.getName());
            preparedStatement.setString(2, this.getCountry());
            //query
            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    throw new AlreadyExists();
                }
            }
        }
        // insert city to table
        try (PreparedStatement preparedStatement = getDb().prepareStatement("insert into cities (name, country, subscription_price, purchase_price) values (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, this.getName());
            preparedStatement.setString(2, this.getCountry());
            preparedStatement.setDouble(3, this.subscription_price);
            preparedStatement.setDouble(4, this.purchase_price);
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

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
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

    public double getSubscription_price() {
        return subscription_price;
    }

    public void setSubscription_price(double subscription_price) {
        this.subscription_price = subscription_price;
    }

    public double getPurchase_price() {
        return purchase_price;
    }

    public void setPurchase_price(double purchase_price) {
        this.purchase_price = purchase_price;
    }

    public double getNew_subscription_price() {
        return new_subscription_price;
    }

    public void setNew_subscription_price(double new_subscription_price) {
        this.new_subscription_price = new_subscription_price;
    }

    public double getNew_purchase_price() {
        return new_purchase_price;
    }

    public void setNew_purchase_price(double new_purchase_price) {
        this.new_purchase_price = new_purchase_price;
    }

    @Override
    public String toString() {
        return String.format("%s, %s", this.getName(), this.getCountry());
    }
}
