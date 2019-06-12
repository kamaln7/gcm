package gcm.database.models;

import java.sql.*;
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

    /**
     * Search cities by name
     * @param searchQuery
     * @return List of CITY
     * @throws SQLException
     */
    public static List<City> searchByName(String searchQuery) throws SQLException {
        if (searchQuery.equals("")) {
            return findAll();
        }

        try (Connection db = getDb();
             PreparedStatement preparedStatement = db.prepareStatement("select * from cities where name like ? or country like ? order by country, name asc")) {
            preparedStatement.setString(1, '%' + searchQuery + '%');
            preparedStatement.setString(2, '%' + searchQuery + '%');

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

    public static List<City> searchByNameWithCounts(String searchQuery) throws SQLException {
        if (searchQuery.equals("")) {
            return new ArrayList<>();
        }

        try (Connection db = getDb();
             PreparedStatement preparedStatement = db.prepareStatement(
                     "select cities.*," +
                             " (select count(*) from maps where maps.city_id = cities.id and maps.verification = 1) as map_count," +
                             " (select count(*) from attractions where attractions.city_id = cities.id) as attraction_count," +
                             " (select count(*) from tours where tours.city_id = cities.id) as tour_count" +
                             " from cities" +
                             " where name like ? or country like ?" +
                             " order by country, name asc"
             )) {
            preparedStatement.setString(1, '%' + searchQuery + '%');
            preparedStatement.setString(2, '%' + searchQuery + '%');

            try (ResultSet rs = preparedStatement.executeQuery()) {
                List<City> cities = new ArrayList<>();
                while (rs.next()) {
                    City city = new City(rs);
                    city._extraInfo.put("mapCount", String.valueOf(rs.getInt("map_count")));
                    city._extraInfo.put("attractionCount", String.valueOf(rs.getInt("attraction_count")));
                    city._extraInfo.put("tourCount", String.valueOf(rs.getInt("tour_count")));
                    cities.add(city);
                }

                return cities;
            }
        }
    }

    /**
     * Find all cities for Activity Report, for each city find number of maps, purchases, subscriptions, renewals, views, downloads
     * @param from date
     * @param to date
     * @return List of CITY
     * @throws SQLException
     */
    public static List<City> findAllWithCount(Date from, Date to) throws SQLException {
        try (Connection db = getDb();
             PreparedStatement preparedStatement = db.prepareStatement(

                     "SELECT\n" +
                             " *,\n" +

                             "(\n" +
                             "select count(*)\n" +
                             "from maps\n" +
                             "where maps.city_id = cities.id\n" +
                             ") AS maps_count,\n" +

                             "(\n" +
                             "select count(*)\n" +
                             "from purchases\n" +
                             "where purchases.city_id = cities.id AND purchases.created_at >= ? and purchases.created_at <= ?\n" +
                             ") AS purchases_count,\n" +

                             "(\n" +
                             "select count(*)\n" +
                             "from subscriptions\n" +
                             "where subscriptions.city_id = cities.id AND subscriptions.created_at >= ? and subscriptions.created_at <= ?\n" +
                             ") AS subscriptions_count,\n" +

                             "(\n" +
                             "select count(*)\n" +
                             "from subscriptions\n" +
                             "where subscriptions.city_id = cities.id AND subscriptions.renew = 1 AND subscriptions.created_at >= ? and subscriptions.created_at <= ?\n" +
                             ") AS renewals_count,\n" +

                             "(\n" +
                             "select count(*)\n" +
                             "from views, maps\n" +
                             "where maps.city_id = cities.id AND views.model_id = maps.id AND views.model = 'map' AND views.created_at >= ? and views.created_at <= ?\n" +
                             ") AS views_count,\n" +

                             "(\n" +
                             "select count(*)\n" +
                             "from downloads, maps\n" +
                             "where maps.city_id = cities.id AND downloads.model_id = maps.id AND downloads.model = 'map' AND downloads.created_at >= ? and downloads.created_at <= ?\n" +
                             ") AS downloads_count\n" +

                             "FROM cities\n"


             )) {
            preparedStatement.setTimestamp(1, new Timestamp(from.getTime()));
            preparedStatement.setTimestamp(2, new Timestamp(to.getTime()));
            preparedStatement.setTimestamp(3, new Timestamp(from.getTime()));
            preparedStatement.setTimestamp(4, new Timestamp(to.getTime()));
            preparedStatement.setTimestamp(5, new Timestamp(from.getTime()));
            preparedStatement.setTimestamp(6, new Timestamp(to.getTime()));
            preparedStatement.setTimestamp(7, new Timestamp(from.getTime()));
            preparedStatement.setTimestamp(8, new Timestamp(to.getTime()));
            preparedStatement.setTimestamp(9, new Timestamp(from.getTime()));
            preparedStatement.setTimestamp(10, new Timestamp(to.getTime()));

            try (ResultSet rs = preparedStatement.executeQuery()) {
                List<City> cities = new ArrayList<>();
                while (rs.next()) {
                    City city = new City(rs);
                    city._extraInfo.put("mapsCount", String.valueOf(rs.getInt("maps_count")));
                    city._extraInfo.put("purchasesCount", String.valueOf(rs.getInt("purchases_count")));
                    city._extraInfo.put("subscriptionsCount", String.valueOf(rs.getInt("subscriptions_count")));
                    city._extraInfo.put("renewalsCount", String.valueOf(rs.getInt("renewals_count")));
                    city._extraInfo.put("viewsCount", String.valueOf(rs.getInt("views_count")));
                    city._extraInfo.put("downloadsCount", String.valueOf(rs.getInt("downloads_count")));
                    cities.add(city);
                }

                return cities;
            }
        }
    }

    /**
     * find all cities in database, order them by country and name
     *
     * @return List of City
     * @throws SQLException
     */
    public static List<City> findAll() throws SQLException {
        try (Connection db = getDb();
             Statement statement = db.createStatement()) {
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


    /**
     * find cities by name of city and country name
     *
     * @param name    of city
     * @param country name
     * @return City
     * @throws SQLException
     * @throws NotFound     if not found
     */
    public static City findByNameAndCountry(String name, String country) throws SQLException, NotFound {
        try (Connection db = getDb();
             PreparedStatement preparedStatement = db.prepareStatement("select * from cities where name = ? and country = ?")) {
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

    /**
     * find all cities with unapproved new price
     *
     * @return List of all cities match
     * @throws SQLException
     * @throws NotFound     if not found such cities
     */
    public static List<City> findUnapproved() throws SQLException, NotFound {
        try (Connection db = getDb();
             PreparedStatement preparedStatement = db.prepareStatement("SELECT * FROM cities WHERE new_purchase_price IS NOT NULL OR new_sub_price IS NOT NULL")) {
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

    /**
     * Approve new price for selected city, Update in database: purchase and subscription columns with new prices
     *
     * @param id of city
     * @return City
     * @throws SQLException
     * @throws NotFound
     */
    public static City approvePrice(int id) /*, double new_purchase_price, double new_subscription_price)*/ throws SQLException, NotFound {
        try (Connection db = getDb();
             PreparedStatement preparedStatement = db.prepareStatement("UPDATE cities SET purchase_price = new_purchase_price , subscription_price = new_sub_price, new_purchase_price = null, new_sub_price = null  WHERE id = ?")) {
            preparedStatement.setInt(1, id);

            int status = preparedStatement.executeUpdate();
            if (status == 0) {
                throw new NotFound();
            }
            City city = findById(id);
            return city;
        }
    }

    /**
     * Decline price changes, in database: no changes in purchase and subscription columns
     * Set NULL values in new_purchase_price and new_subscription_price
     *
     * @param id of city
     * @return City matches
     * @throws SQLException
     * @throws NotFound
     */
    public static City declinePrice(int id) /*, double new_purchase_price, double new_subscription_price)*/ throws SQLException, NotFound {
        try (Connection db = getDb();
             PreparedStatement preparedStatement = db.prepareStatement("UPDATE cities SET new_purchase_price = null, new_sub_price = null  WHERE id = ?")) {

            preparedStatement.setInt(1, id);

            int status = preparedStatement.executeUpdate();
            if (status == 0) {
                throw new NotFound();
            }
            City city = findById(id);
            return city;
        }
    }

    /**
     * Set in new_purchase_price column, and new_subscription_price column new selected values sent by Content Manager
     *
     * @param new_purchase_price
     * @param new_sub_price
     * @throws SQLException
     * @throws NotFound
     */
    public void changePrice(double new_purchase_price, double new_sub_price) throws SQLException, NotFound {
        try (Connection db = getDb();
             PreparedStatement preparedStatement = db.prepareStatement("UPDATE cities SET new_purchase_price = ? , new_sub_price = ?  WHERE id = ?")) {
            preparedStatement.setDouble(1, new_purchase_price);
            preparedStatement.setDouble(2, new_sub_price);
            preparedStatement.setInt(3, getId());

            int status = preparedStatement.executeUpdate();
            if (status == 0) {
                throw new NotFound();
            }
        }
    }

    /**
     * Find city in database by its city ID
     *
     * @param id city
     * @return Matching City
     * @throws SQLException
     * @throws NotFound     if not found
     */
    public static City findById(Integer id) throws SQLException, NotFound {
        try (Connection db = getDb();
             PreparedStatement preparedStatement = db.prepareStatement("select * from cities where id = ?")) {
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
        try (Connection db = getDb();
             PreparedStatement preparedStatement = db.prepareStatement("insert into cities (name, country, subscription_price, purchase_price) values (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
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
        try (Connection db = getDb();
             PreparedStatement preparedStatement = db.prepareStatement(
                     "select\n" +
                             "(select count(*) from maps where maps.city_id = ? and maps.verification = 1) as map_count,\n" +
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

    public String getTitle() {
        return String.format("%s, %s", this.getName(), this.getCountry());
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
        return getTitle();
    }
}
