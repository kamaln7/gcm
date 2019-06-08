package gcm.database.models;

import org.mindrot.jbcrypt.BCrypt;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.HashMap;

public class User extends Model {
    // fields
    private Integer id;
    private String username, password, email, phone, role, first_name, last_name;
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

    public User(String username, String password, String email, String phone, String first_name, String last_name) {
        this(username, password, email, phone, "user", first_name, last_name);
    }

//    public User(String username, String password, String email, String phone, String role) {
//        this.username = username;
//        this.setPassword(password);
//        this.email = email;
//        this.phone = phone;
//        this.role = role;
//    }

    public User(String username, String password, String email, String phone, String role, String first_name, String last_name) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.phone = phone;
        this.role = role;
        this.first_name = first_name;
        this.last_name = last_name;
    }

    public static User fakeGuestUser() {
        User user = new User("guest", "", "", "", "guest","guest","guest");
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

    public User register() throws SQLException, NotFound, AlreadyExists {
        // insert user to table
        try (PreparedStatement preparedStatement = getDb().prepareStatement("insert into users (username, password, email, phone, role, first_name, last_name) values (?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, this.getUsername());
            preparedStatement.setString(2, this.getPassword());
            preparedStatement.setString(3, this.getEmail());
            preparedStatement.setString(4, this.getPhone());
            preparedStatement.setString(5, this.getRole());
            preparedStatement.setString(6, this.getFirst_name());
            preparedStatement.setString(7, this.getLast_name());
            // run the insert command
            preparedStatement.executeUpdate();
            // get the auto generated id
            ResultSet rs = preparedStatement.getGeneratedKeys();
            if (!rs.next()) {
                throw new NotFound();
            }

            // find the new user details
            Integer id = rs.getInt(1);

            try (PreparedStatement preparedStatement1 = getDb().prepareStatement("select * from users where id = ?")) {
                preparedStatement1.setInt(1, id);

                try (ResultSet rs1 = preparedStatement1.executeQuery()) {
                    if (!rs1.next()) {
                        throw new NotFound();
                    }

                    this.fillFieldsFromResultSet(rs1);
                }
            }
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
        try (PreparedStatement preparedStatement = getDb().prepareStatement("select * from users where id = ?")) {
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
        try (PreparedStatement preparedStatement = getDb().prepareStatement("select * from users where username = ?")) {
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
        try (PreparedStatement preparedStatement = getDb().prepareStatement("select * from users where username = ?")) {
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
}
