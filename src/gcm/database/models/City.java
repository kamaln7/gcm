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
    private String name, country, description;
    private Date createdAt, updatedAt;
    private double subscription_price, purchase_price, new_subscription_price, new_purchase_price;

    // create User object with info from ResultSet
    public City(ResultSet rs) throws SQLException {
        super();

        this.fillFieldsFromResultSet(rs);
    }

public City(){

}
    public City(String name, String country) {
        super();

        this.name = name;
        this.country = country;
    }

    public City(String name, String country, double subscription_price, double purchase_price) {
        super();

        this.name = name;
        this.country = country;
        this.subscription_price = subscription_price;
        this.purchase_price = purchase_price;
    }
    public City(String name, String country, double subscription_price, double purchase_price, double new_purchase_price, double new_subscription_price) {
        this.name = name;
        this.country = country;
        this.subscription_price = subscription_price;
        this.purchase_price = purchase_price;
        this.new_purchase_price = new_purchase_price;
        this.new_subscription_price = new_subscription_price;
    }

    public static List<City> searchByName(String searchQuery) throws SQLException {
        if (searchQuery.equals("")) {
            return findAll();
        }

        try (PreparedStatement preparedStatement = getDb().prepareStatement("select * from cities where match (name, country, description) against (?) order by country, name asc")) {
            preparedStatement.setString(1, searchQuery);

            try (ResultSet rs = preparedStatement.executeQuery()) {
                List<City> cities = new ArrayList<>();
                while (rs.next()) {
                    City city = new City(rs);
                    cities.add(city);
                }

                return cities;
            }
        }
    }

    private static List<City> findAll() throws SQLException {
        try (Statement statement = getDb().createStatement()) {
            try (ResultSet rs = statement.executeQuery("select * from cities order by country, name asc")) {
                List<City> cities = new ArrayList<>();
                while (rs.next()) {
                    City city = new City(rs);
                    cities.add(city);
                }

                return cities;
            }
        }
    }

    public void fillFieldsFromResultSet(ResultSet rs) throws SQLException {
        this.id = rs.getInt("id");
        this.name = rs.getString("name");
        this.country = rs.getString("country");
        this.description = rs.getString("description");
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

    /* Amin update: find city by name and country */

    public static City findCity(String cityName, String countryName) throws SQLException, NotFound {
        try (PreparedStatement preparedStatement = getDb().prepareStatement("SELECT * FROM cities WHERE name = ? AND country = ?")) {
            preparedStatement.setString(1, cityName);
            preparedStatement.setString(2, countryName);

            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (!rs.next()) {
                    throw new NotFound();
                }

                City city = new City(rs);
                return city;
            }
        }
    }

    public static ArrayList<City> findUnapproved() throws SQLException, NotFound {
        ArrayList result = new ArrayList<City>();

        try (PreparedStatement preparedStatement = getDb().prepareStatement("SELECT * FROM cities WHERE new_purchase_price > 0 OR new_sub_price > 0 ")){


            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (!rs.next()) {
                    throw new NotFound();
                }
                do{
                    result.add(new City(rs));
                } while(rs.next());



                return result;
            }

        }
    }
    public static City changePrice(String cityName, String countryName, double new_purchase_price, double new_sub_price) throws SQLException, NotFound {

        try (PreparedStatement preparedStatement = getDb().prepareStatement("UPDATE cities SET new_purchase_price = ? , new_sub_price = ?  WHERE name = ? AND country = ?")) {
            preparedStatement.setDouble(1, new_purchase_price);
            preparedStatement.setDouble(2, new_sub_price);
            preparedStatement.setString(3, cityName);
            preparedStatement.setString(4, countryName);

            int status = preparedStatement.executeUpdate();
                if (status == 0) {
                    throw new NotFound();
                }
            City city = findCity(cityName,countryName);
                return city;

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
        // insert city to table
        try (PreparedStatement preparedStatement = getDb().prepareStatement("insert into cities (name, country, subscription_price, purchase_price, description) values (?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, this.getName());
            preparedStatement.setString(2, this.getCountry());
            preparedStatement.setDouble(3, this.subscription_price);
            preparedStatement.setDouble(4, this.purchase_price);
            preparedStatement.setString(5, this.description);
            // run the insert command
            preparedStatement.executeUpdate();
            // get the auto generated id
            try (ResultSet rs = preparedStatement.getGeneratedKeys()) {
                if (!rs.next()) {
                    throw new NotFound();
                }
            }
        } catch (java.sql.SQLIntegrityConstraintViolationException e) {
            throw new AlreadyExists();
        }
    }

    public void lookupCountsOfRelated() throws SQLException {
        try (PreparedStatement preparedStatement = getDb().prepareStatement(
                "select\n" +
                        "(select count(*) from maps where maps.cityId = ?) as map_count,\n" +
                        "(select count(*) from attractions where attractions.city_id = ?) as attraction_count"
        )) {
            preparedStatement.setInt(1, this.getId());
            preparedStatement.setInt(2, this.getId());
            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    this._extraInfo.put("mapCount", String.valueOf(rs.getInt("map_count")));
                    this._extraInfo.put("attractionCount", String.valueOf(rs.getInt("attraction_count")));
                }
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return String.format("%s, %s", this.getName(), this.getCountry());
    }
}
