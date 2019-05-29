package gcm.database.models;

import org.mindrot.jbcrypt.BCrypt;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

public class User extends Model {
    // fields
    private Integer id;
    private String username, password, email, phone, role;
    private Date createdAt, updatedAt;

    // create User object with info from ResultSet
    public User(ResultSet rs) throws SQLException {
        super();

        this.fillFieldsFromResultSet(rs);
    }

    public User(String username, String password, String email, String phone) {
        this(username, password, email, phone, "user");
    }

    public User(String username, String password, String email, String phone, String role) {
        this.username = username;
        this.setPassword(password);
        this.email = email;
        this.phone = phone;
        this.role = role;
    }

    public static User fakeGuestUser() {
        User user = new User("guest", "", "", "", "guest");
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
        this.createdAt = rs.getTimestamp("created_at");
        this.updatedAt = rs.getTimestamp("updated_at");
    }

    public User register() throws SQLException, NotFound, AlreadyExists {
        // insert user to table
        try (PreparedStatement preparedStatement = getDb().prepareStatement("insert into users (username, password, email, phone, role) values (?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, this.getUsername());
            preparedStatement.setString(2, this.getPassword());
            preparedStatement.setString(3, this.getEmail());
            preparedStatement.setString(4, this.getPhone());
            preparedStatement.setString(5, this.getRole());

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
                        throw new User.NotFound();
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
}
