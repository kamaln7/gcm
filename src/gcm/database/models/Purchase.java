package gcm.database.models;


import org.omg.CosNaming.NamingContextPackage.NotFound;

import java.sql.*;
import java.time.LocalDate;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import java.util.Date;
import java.util.List;

public class Purchase extends Model {
    private Integer id, userId, cityId;
    private Date createdAt, updatedAt;
    private double price;

    @Override
    public void fillFieldsFromResultSet(ResultSet rs) throws SQLException {
        this.id = rs.getInt("id");
        this.userId = rs.getInt("user_id");
        this.cityId = rs.getInt("city_id");
        this.createdAt = rs.getTimestamp("created_at");
        this.updatedAt = rs.getTimestamp("updated_at");
        this.price = rs.getDouble("price");
    }

    public Purchase(ResultSet rs) throws SQLException {
        super();

        this.fillFieldsFromResultSet(rs);
    }

    public Purchase(Integer userId, Integer cityId, double price) {
        this.userId = userId;
        this.cityId = cityId;
        this.price = price;
    }

    public static Purchase findById() throws SQLException, NotFound {
        return findById();
    }

    public static Purchase findById(Integer id) throws SQLException, NotFound {
        try (PreparedStatement preparedStatement = getDb().prepareStatement("select * from purchases where id = ?")) {
            preparedStatement.setInt(1, id);

            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (!rs.next()) {
                    throw new NotFound();
                }

                Purchase purchase = new Purchase(rs);
                return purchase;
            }
        }
    }

    public static int countForUser (Integer id) throws SQLException, NotFound {
        try (PreparedStatement preparedStatement = getDb().prepareStatement("select count(*) as total from purchases where user_id = ?")){
            preparedStatement.setInt(1, id);

            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (!rs.next()) {
                    return 0;
                }
                return rs.getInt("total");
            }
        }
    }
    public static int countByPeriod(Integer id, LocalDate from, LocalDate to) throws SQLException, NotFound {
        Timestamp fromDate = Timestamp.valueOf(from.atTime(0, 0, 0));
        Timestamp toDate = Timestamp.valueOf(to.atTime(23, 59, 59));
        try (PreparedStatement preparedStatement = getDb().prepareStatement("select count(*) as total from purchases where city_id = ? and created_at >= ? and created_at <= ?")) {
            preparedStatement.setInt(1, id);
            preparedStatement.setTimestamp(2, fromDate);
            preparedStatement.setTimestamp(3, toDate);

            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (!rs.next()) {
                    return 0;
                }

                return rs.getInt("total");
            }
        }
    }


    public static List<Purchase> findAllByUserId(Integer user_id) throws SQLException, NotFound {
        try (PreparedStatement preparedStatement = getDb().prepareStatement("select * from purchases where user_id = ? order by id asc")) {
            preparedStatement.setInt(1, user_id);

            try (ResultSet rs = preparedStatement.executeQuery()) {
                List<Purchase> purchases = new ArrayList<>();
                while (rs.next()) {
                    Purchase purchase = new Purchase(rs);
                    purchases.add(purchase);
                }


                return purchases;

            }
        }
    }

    public void insert() throws SQLException, NotFound, AlreadyExists {
        // insert city to table
        try (PreparedStatement preparedStatement = getDb().prepareStatement("insert into purchases (user_id, city_id, price) values (?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setInt(1, getUserId());
            preparedStatement.setInt(2, getCityId());
            preparedStatement.setDouble(3,getPrice());
            // run the insert command
            preparedStatement.executeUpdate();
            // get the auto generated id
            try (ResultSet rsGenerated = preparedStatement.getGeneratedKeys()) {
                if (!rsGenerated.next()) {
                    throw new NotFound();
                }

                // find the new attraction details
                Integer id = rsGenerated.getInt(1);
                this.updateWithNewDetailsById(id, "purchases");
            }
        } catch (java.sql.SQLIntegrityConstraintViolationException e) {
            throw new AlreadyExists();
        }
    }

    public static class AlreadyExists extends Exception {
    }

    public static class NotFound extends Exception {
    }

    public Date getCreatedAt(){
        return createdAt;
    }

    public void setCreatedAt(Date createdAt){
        this.createdAt = createdAt;
    }



    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public void setCityId(Integer cityId) {
        this.cityId = cityId;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getPrice() {
        return price;
    }


}
