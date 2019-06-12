package gcm.database.models;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Subscription extends Model {
    private Integer id, userId, cityId;
    private Date fromDate, toDate, createdAt, updatedAt;
    private double price;
    private boolean renew, sentExpiryNotification;

    @Override
    public void fillFieldsFromResultSet(ResultSet rs) throws SQLException {
        this.id = rs.getInt("id");
        this.userId = rs.getInt("user_id");
        this.cityId = rs.getInt("city_id");
        this.fromDate = rs.getTimestamp("from_date");
        this.toDate = rs.getTimestamp("to_date");
        this.createdAt = rs.getTimestamp("created_at");
        this.updatedAt = rs.getTimestamp("updated_at");
        this.price = rs.getDouble("price");
        this.renew = rs.getBoolean("renew");
        this.sentExpiryNotification = rs.getBoolean("sent_expiry_notification");
    }

    public Subscription(ResultSet rs) throws SQLException {
        super();

        this.fillFieldsFromResultSet(rs);
    }

    public Subscription(Integer userId, Integer cityId, Date fromDate, Date toDate, double price, boolean renew) {
        this.userId = userId;
        this.cityId = cityId;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.price = price;
        this.renew = renew;
    }

    /**
     * find Subscriptions in database by ID
     *
     * @param id of Subscription
     * @return matching Subscription
     * @throws SQLException
     * @throws NotFound     if not found
     */
    public static Subscription findById(Integer id) throws SQLException, NotFound {
        try (Connection db = getDb();
             PreparedStatement preparedStatement = db.prepareStatement("select * from subscriptions where id = ?")) {
            preparedStatement.setInt(1, id);

            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (!rs.next()) {
                    throw new NotFound();
                }

                Subscription subscription = new Subscription(rs);
                return subscription;
            }
        }
    }




    public static Subscription findSubscriptionbyIDs(Integer userId, Integer cityId, Date fromDate) throws SQLException, NotFound {
        try (Connection db = getDb();
             PreparedStatement preparedStatement = db.prepareStatement("select * from subscriptions where user_id = ? AND city_id = ? AND from_date <= ? AND to_date >= ?")) {
            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, cityId);
            preparedStatement.setTimestamp(3, new Timestamp(fromDate.getTime()));
            preparedStatement.setTimestamp(4, new Timestamp(fromDate.getTime()));


            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (!rs.next()) {
                    throw new NotFound();
                }

                Subscription subscription = new Subscription(rs);
                return subscription;
            }
        }
    }


    public static List<Subscription> findAllByUserId(Integer userId) throws SQLException {
        return findAllByUserId(userId, false);
    }

    public static List<Subscription> findAllByUserId(Integer userId, Boolean activeOnly) throws SQLException {
        try (Connection db = getDb();
             PreparedStatement preparedStatement = db.prepareStatement(
                String.format(
                        "select subscriptions.*, concat(cities.name, \", \", cities.country) as city_title" +
                                " from subscriptions" +
                                " left join cities" +
                                " on subscriptions.city_id = cities.id" +
                                " where user_id = ?" +
                                " %s" +
                                " order by created_at desc",
                        activeOnly ? "and from_date <= now() and to_date >= now()" : "")
        )) {
            preparedStatement.setInt(1, userId);
            try (ResultSet rs = preparedStatement.executeQuery()) {

                List<Subscription> subscriptions = new ArrayList<>();
                while (rs.next()) {
                    Subscription subscription = new Subscription(rs);
                    subscription._extraInfo.put("cityTitle", rs.getString("city_title"));
                    subscriptions.add(subscription);
                }

                return subscriptions;
            }
        }
    }

    public void insert() throws SQLException, NotFound, AlreadyExists {

        // insert city to table
        try (Connection db = getDb();
             PreparedStatement preparedStatement = db.prepareStatement("insert into subscriptions (user_id, city_id, from_date, to_date, price, renew) values (?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setInt(1, getUserId());
            preparedStatement.setInt(2, getCityId());
            preparedStatement.setTimestamp(3, new Timestamp(getFromDate().getTime()));
            preparedStatement.setTimestamp(4, new Timestamp(getToDate().getTime()));
            preparedStatement.setDouble(5, getPrice());
            preparedStatement.setBoolean(6, getRenew());
            // run the insert command
            preparedStatement.executeUpdate();
            // get the auto generated id
            try (ResultSet rsGenerated = preparedStatement.getGeneratedKeys()) {
                if (!rsGenerated.next()) {
                    throw new NotFound();
                }

                // find the new attraction details
                Integer id = rsGenerated.getInt(1);
                this.updateWithNewDetailsById(id, "subscriptions");
            }
        } catch (java.sql.SQLIntegrityConstraintViolationException e) {
            throw new AlreadyExists();
        }
    }

    public static void updateSentExpiryNotification(Set<Integer> ids, Boolean isSent) throws SQLException {
        if (ids.isEmpty()) {
            return;
        }
        List<Integer> idsList = new ArrayList<>(ids);

        // prepare query to update list of ids
        String query = String.format(
                "update subscriptions" +
                        " set sent_expiry_notification = ?" +
                        " where id in (%s)",
                IntStream
                        .range(0, idsList.size())
                        .mapToObj(s -> "?")
                        .collect(Collectors.joining(", "))
        );

        try (Connection db = getDb();
             PreparedStatement preparedStatement = db.prepareStatement(query)) {
            // bind status
            preparedStatement.setBoolean(1, isSent);

            // bind ids
            int bound = idsList.size();
            for (int i = 0; i < bound; i++) {
                preparedStatement.setInt(i + 2, idsList.get(i));
            }

            preparedStatement.executeUpdate();
        }
    }

    /**
     * @param days Number of days to check
     * @return A list of subscriptions that expire in N days or less and haven't been sent a notification yet
     * @throws SQLException
     */
    public static List<Subscription> findExpiringInNDays(Integer days) throws SQLException {
        List<Subscription> subscriptions = new ArrayList<>();

        try (Connection db = getDb();
             PreparedStatement preparedStatement = db.prepareStatement(
                "select t.*\n" +
                        "from subscriptions t\n" +
                        "inner join (\n" +
                        "    select user_id, max(to_date) as MaxDate\n" +
                        "    from subscriptions\n" +
                        "    group by user_id, city_id\n" +
                        ") tm on t.user_id = tm.user_id and t.to_date = tm.MaxDate\n" +
                        "where t.sent_expiry_notification = 0\n" +
                        "and now() >= (to_date - interval ? day)\n" +
                        "order by user_id, city_id"
        )) {
            preparedStatement.setInt(1, days);

            try (ResultSet rs = preparedStatement.executeQuery()) {
                while (rs.next()) {
                    subscriptions.add(new Subscription(rs));
                }
            }
        }

        return subscriptions;
    }


    public static class AlreadyExists extends Exception {
    }

    public static class NotFound extends Exception {
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getCityId() {
        return cityId;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getPrice() {
        return price;
    }

    public void setRenew(boolean renew) {
        this.renew = renew;
    }

    public boolean getRenew() {
        return renew;
    }

    public void setCityId(Integer cityId) {
        this.cityId = cityId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getFromDate() {
        return fromDate;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public Date getToDate() {
        return toDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
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

    public boolean isRenew() {
        return renew;
    }

    public boolean isSentExpiryNotification() {
        return sentExpiryNotification;
    }

    public void setSentExpiryNotification(boolean sentExpiryNotification) {
        this.sentExpiryNotification = sentExpiryNotification;
    }
}
