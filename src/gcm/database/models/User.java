package gcm.database.models;

import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class User extends Model {
    // fields
    private Integer id, ccMonth, ccYear;
    private String username, password, email, phone, role, first_name, last_name, ccNumber, ccCVV;
    private Date createdAt, updatedAt;

    public static HashMap<String, Integer> ROLES = new HashMap<>();

    static {
        ROLES.put("guest", 0);
        ROLES.put("user", 1);
        ROLES.put("employee", 2);
        ROLES.put("content_manager", 3);
        ROLES.put("company_manager", 4);
        ROLES.put("admin", 5);
    }

    // create User object with info from ResultSet
    public User(ResultSet rs) throws SQLException {
        super();

        this.fillFieldsFromResultSet(rs);
    }

    /**
     * Create a new user for registration
     *
     * @param username   Username
     * @param password   Password
     * @param email      Email address
     * @param phone      Phone number
     * @param first_name First Name
     * @param last_name  Last Name
     * @param ccNumber   Credit Card Number
     * @param ccCVV      Credit Card CVV
     * @param ccMonth    Credit Card Expiry Month
     * @param ccYear     Credit Card Expiry Year
     */
    public User(String username, String password, String email, String phone, String first_name, String last_name, String ccNumber, String ccCVV, Integer ccMonth, Integer ccYear) {
        this(username, password, email, phone, "user", first_name, last_name);
        this.ccNumber = ccNumber;
        this.ccCVV = ccCVV;
        this.ccMonth = ccMonth;
        this.ccYear = ccYear;
    }

    public User(String username, String password, String email, String phone, String role, String first_name, String last_name) {
        this.username = username;
        this.setPassword(password);
        this.email = email;
        this.phone = phone;
        this.role = role;
        this.first_name = first_name;
        this.last_name = last_name;
    }

    public static User fakeGuestUser() {
        User user = new User("guest", "", "", "", "guest", "guest", "guest");
        user.id = -1;
        user.setCreatedAt(new Date());
        user.setUpdatedAt(new Date());

        return user;
    }

    public void fillFieldsFromResultSet(ResultSet rs) throws SQLException {
        this.id = rs.getInt("id");
        this.username = rs.getString("username");
        this.password = rs.getString("password");
        this.email = rs.getString("email");
        this.phone = rs.getString("phone");
        this.role = rs.getString("role");
        this.first_name = rs.getString("first_name");
        this.last_name = rs.getString("last_name");
        this.createdAt = rs.getTimestamp("created_at");
        this.updatedAt = rs.getTimestamp("updated_at");
    }

    /**
     * Register a user and insert their info to the database
     *
     * @return
     * @throws SQLException
     * @throws NotFound
     * @throws AlreadyExists
     */
    public User register() throws SQLException, NotFound, AlreadyExists {
        // insert user to table
        try (Connection db = getDb();
             PreparedStatement preparedStatement = db.prepareStatement("insert into users (username, password, email, phone, role, first_name, last_name, credit_card_number, credit_card_cvv, credit_card_month, credit_card_year) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, this.getUsername());
            preparedStatement.setString(2, this.getPassword());
            preparedStatement.setString(3, this.getEmail());
            preparedStatement.setString(4, this.getPhone());
            preparedStatement.setString(5, this.getRole());
            preparedStatement.setString(6, this.getFirst_name());
            preparedStatement.setString(7, this.getLast_name());
            preparedStatement.setString(8, getCcNumber());
            preparedStatement.setString(9, getCcCVV());
            preparedStatement.setInt(10, getCcMonth());
            preparedStatement.setInt(11, getCcYear());
            // run the insert command
            preparedStatement.executeUpdate();
            // get the auto generated id
            ResultSet rs = preparedStatement.getGeneratedKeys();
            if (!rs.next()) {
                throw new NotFound();
            }

            // find the new user details
            Integer id = rs.getInt(1);

            this.updateWithNewDetailsById(id, "users");
        } catch (java.sql.SQLIntegrityConstraintViolationException e) {
            throw new AlreadyExists();
        }

        return this;
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
    public static User findById(Integer id) throws SQLException, NotFound {
        try (Connection db = getDb();
             PreparedStatement preparedStatement = db.prepareStatement("select * from users where id = ?")) {
            preparedStatement.setInt(1, id);

            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (!rs.next()) {
                    throw new User.NotFound();
                }

                User user = new User(rs);
                return user;
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
    public static User findByUsername(String username) throws SQLException, NotFound {
        try (Connection db = getDb();
             PreparedStatement preparedStatement = db.prepareStatement("select * from users where username = ?")) {
            preparedStatement.setString(1, username);

            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (!rs.next()) {
                    throw new User.NotFound();
                }

                User user = new User(rs);
                return user;
            }
        }
    }

    /**
     * Try to log in user
     *
     * @param username Input username
     * @param password Input password
     * @return User
     */
    public static User login(String username, String password) throws NotFound, SQLException {
        try (
                Connection db = getDb();
                PreparedStatement preparedStatement = db.prepareStatement("select * from users where username = ?")
        ) {
            preparedStatement.setString(1, username);

            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (!rs.next()) {
                    throw new User.NotFound();
                }

                User user = new User(rs);
                if (!user.checkPassword(password)) {
                    throw new User.NotFound();
                }

                return user;
            }
        }
    }


    public static void updateUser(int user_id, String email, String phone, String first_name, String last_name, String ccNumber, String ccCVV, Integer ccMonth, Integer ccYear) throws SQLException, NotFound {
        try (Connection db = getDb();
             PreparedStatement preparedStatement = db.prepareStatement("UPDATE users SET email=? ,phone=? ,first_name=?, last_name=?, credit_card_number=?, credit_card_cvv=?, credit_card_month=?, credit_card_year=? WHERE id = ?")) {
            preparedStatement.setString(1, email);
            preparedStatement.setString(2, phone);
            preparedStatement.setString(3, first_name);
            preparedStatement.setString(4, last_name);
            preparedStatement.setString(5, ccNumber);
            preparedStatement.setString(6, ccCVV);
            preparedStatement.setInt(7, ccMonth);
            preparedStatement.setInt(8, ccYear);
            preparedStatement.setInt(9, user_id);
            preparedStatement.executeUpdate();
        }
    }

    /**
     * Test if password is correct
     *
     * @param password Input password
     * @return Boolean if password is correct
     */
    public Boolean checkPassword(String password) {
        return BCrypt.checkpw(password, this.getPassword());
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
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

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public Boolean hasExactRole(String role) {
        Integer userRole = ROLES.getOrDefault(getRole(), -1);

        return userRole == ROLES.getOrDefault(role, -2);
    }

    public Integer getCcMonth() {
        return ccMonth;
    }

    public void setCcMonth(Integer ccMonth) {
        this.ccMonth = ccMonth;
    }

    public Integer getCcYear() {
        return ccYear;
    }

    public void setCcYear(Integer ccYear) {
        this.ccYear = ccYear;
    }

    public String getCcNumber() {
        return ccNumber;
    }

    public void setCcNumber(String ccNumber) {
        this.ccNumber = ccNumber;
    }

    public String getCcCVV() {
        return ccCVV;
    }

    public void setCcCVV(String ccCVV) {
        this.ccCVV = ccCVV;
    }

    /**
     * @param role Role to check if user has access to
     * @return Boolean if user has given role or higher
     */
    public Boolean hasRole(Integer role) {
        Integer userRole = ROLES.getOrDefault(getRole(), ROLES.get("guest"));

        return userRole >= role;
    }

    /**
     * @param role Role to check if user has access to
     * @return Boolean if user has given role or higher
     */
    public Boolean hasRole(String role) {
        return this.hasRole(ROLES.getOrDefault(role, 100000));
    }


    /**
     * Find all Users and all information needed:  number of  purchases and subscriptions
     * @return
     * @throws SQLException
     */
    public static List<User> findAllUsersWithCounts() throws SQLException {
        try (Connection db = getDb();
             Statement statement = db.createStatement()) {
            try (ResultSet rs = statement.executeQuery(
                    "SELECT\n" +
                            " users.*,\n" +
                            "(\n" +
                            "select count(*)\n" +
                            "from purchases\n" +
                            "where purchases.user_id = users.id\n" +
                            ") AS purchases_count,\n" +
                            "(\n" +
                            "select count(*)\n" +
                            "from subscriptions\n" +
                            "where subscriptions.user_id = users.id\n" +
                            ") AS subscriptions_count\n" +
                            "FROM users\n" +
                            "where role = 'user'\n"
            )) {
                List<User> users = new ArrayList<>();
                while (rs.next()) {
                    User user = new User(rs);
                    user._extraInfo.put("purchasesCount", String.valueOf(rs.getInt("purchases_count")));
                    user._extraInfo.put("subscriptionsCount", String.valueOf(rs.getInt("subscriptions_count")));
                    users.add(user);
                }

                return users;
            }
        }
    }
}
