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
    private double subscriptionPrice, purchasePrice, newSubscriptionPrice, newPurchasePrice;

    // create User object with info from ResultSet
    public City(ResultSet rs) throws SQLException {
        super();

        this.fillFieldsFromResultSet(rs);
    }

    public City() {

    }

    public City(String name, String country, double subscriptionPrice, double purchasePrice) {
        this.name = name;
        this.country = country;
        this.subscriptionPrice = subscriptionPrice;
        this.purchasePrice = purchasePrice;
    }

    public static List<City> searchByName(String searchQuery) throws SQLException {
        if (searchQuery.equals("")) {
            return findAll();
        }

        try (PreparedStatement preparedStatement = getDb().prepareStatement("select * from cities where match (name, country) against (?) order by country, name asc")) {
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

    public static List<City> findAll() throws SQLException {
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
        this.createdAt = rs.getTimestamp("created_at");
        this.updatedAt = rs.getTimestamp("updated_at");
        this.subscriptionPrice = rs.getDouble("subscription_price");
        this.purchasePrice = rs.getDouble("purchase_price");
        this.newPurchasePrice = rs.getDouble("new_purchase_price");
        this.newSubscriptionPrice = rs.getDouble("new_sub_price");
    }


    /* QUERIES */
    public static City findByNameAndCountry(String name, String country) throws SQLException, NotFound {
        try (PreparedStatement preparedStatement = getDb().prepareStatement("select * from cities where name = ? and country = ?")) {
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, country);

            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (!rs.next()) {
                    throw new NotFound();
                }

                City city = new City(rs);
                return city;
            }
        }
    }

    public static List<City> findUnapproved() throws SQLException, NotFound {
        try (PreparedStatement preparedStatement = getDb().prepareStatement("SELECT * FROM cities WHERE new_purchase_price IS NOT NULL OR new_sub_price IS NOT NULL")) {
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

    public static City approvePrice(int id) /*, double new_purchase_price, double new_subscription_price)*/ throws SQLException, NotFound {
        try (PreparedStatement preparedStatement = getDb().prepareStatement("UPDATE cities SET purchase_price = new_purchase_price , subscription_price = new_sub_price, new_purchase_price = null, new_sub_price = null  WHERE id = ?")) {
            preparedStatement.setInt(1, id);

            int status = preparedStatement.executeUpdate();
            if (status == 0) {
                throw new NotFound();
            }
            City city = findById(id);
            return city;
        }
    }

    public static City declinePrice(int id) /*, double new_purchase_price, double new_subscription_price)*/ throws SQLException, NotFound {
        try (PreparedStatement preparedStatement = getDb().prepareStatement("UPDATE cities SET new_purchase_price = null, new_sub_price = null  WHERE id = ?")) {

            preparedStatement.setInt(1, id);

            int status = preparedStatement.executeUpdate();
            if (status == 0) {
                throw new NotFound();
            }
            City city = findById(id);
            return city;
        }
    }

    public void changePrice(double new_purchase_price, double new_sub_price) throws SQLException, NotFound {
        try (PreparedStatement preparedStatement = getDb().prepareStatement("UPDATE cities SET new_purchase_price = ? , new_sub_price = ?  WHERE id = ?")) {
            preparedStatement.setDouble(1, new_purchase_price);
            preparedStatement.setDouble(2, new_sub_price);
            preparedStatement.setInt(3, getId());

            int status = preparedStatement.executeUpdate();
            if (status == 0) {
                throw new NotFound();
            }
        }
    }

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
        try (PreparedStatement preparedStatement = getDb().prepareStatement("insert into cities (name, country, subscription_price, purchase_price) values (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, this.getName());
            preparedStatement.setString(2, this.getCountry());
            preparedStatement.setDouble(3, this.subscriptionPrice);
            preparedStatement.setDouble(4, this.purchasePrice);
            // run the insert command
            preparedStatement.executeUpdate();

            // get the auto generated id
            try (ResultSet rsGenerated = preparedStatement.getGeneratedKeys()) {
                if (!rsGenerated.next()) {
                    throw new NotFound();
                }

                // find the new attraction details
                Integer id = rsGenerated.getInt(1);
                this.updateWithNewDetailsById(id, "cities");
            }
        } catch (java.sql.SQLIntegrityConstraintViolationException e) {
            throw new AlreadyExists();
        }
    }

    public void lookupCountsOfRelated() throws SQLException {
        try (PreparedStatement preparedStatement = getDb().prepareStatement(
                "select\n" +
                        "(select count(*) from maps where maps.city_id = ?) as map_count,\n" +
                        "(select count(*) from attractions where attractions.city_id = ?) as attraction_count, \n" +
                        "(select count(*) from tours where tours.city_id = ?) as tour_count"
        )) {
            preparedStatement.setInt(1, this.getId());
            preparedStatement.setInt(2, this.getId());
            preparedStatement.setInt(3, this.getId());
            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    this._extraInfo.put("mapCount", String.valueOf(rs.getInt("map_count")));
                    this._extraInfo.put("attractionCount", String.valueOf(rs.getInt("attraction_count")));
                    this._extraInfo.put("tourCount", String.valueOf(rs.getInt("tour_count")));
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

    public double getSubscriptionPrice() {
        return subscriptionPrice;
    }

    public void setSubscriptionPrice(double subscriptionPrice) {
        this.subscriptionPrice = subscriptionPrice;
    }

    public double getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(double purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    public double getNewSubscriptionPrice() {
        return newSubscriptionPrice;
    }

    public void setNewSubscriptionPrice(double newSubscriptionPrice) {
        this.newSubscriptionPrice = newSubscriptionPrice;
    }

    public double getNewPurchasePrice() {
        return newPurchasePrice;
    }

    public void setNewPurchasePrice(double newPurchasePrice) {
        this.newPurchasePrice = newPurchasePrice;
    }

    @Override
    public String toString() {
        return String.format("%s, %s", this.getName(), this.getCountry());
    }
}
