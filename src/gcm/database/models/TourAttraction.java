package gcm.database.models;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TourAttraction extends Model {
    // fields
    private Integer id, tourId, attractionId, orderIndex;
    private String time;
    private Date createdAt, updatedAt;

    // create User object with info from ResultSet
    public TourAttraction(ResultSet rs) throws SQLException {
        super();

        this.fillFieldsFromResultSet(rs);
    }

    public TourAttraction(Integer tourId, Integer attractionId, Integer orderIndex, String time) {
        this.tourId = tourId;
        this.attractionId = attractionId;
        this.orderIndex = orderIndex;
        this.time = time;
    }

    public void fillFieldsFromResultSet(ResultSet rs) throws SQLException {
        this.tourId = rs.getInt("tour_id");
        this.attractionId = rs.getInt("attraction_id");
        this.orderIndex = rs.getInt("order_index");
        this.time = rs.getString("time");
        this.createdAt = rs.getTimestamp("created_at");
        this.updatedAt = rs.getTimestamp("updated_at");
    }

    public static TourAttraction findByIds(Integer tourId, Integer attractionId) throws SQLException, NotFound {
        try (PreparedStatement preparedStatement = getDb().prepareStatement("select * from tours_attractions where tour_id = ? and attraction_id = ?")) {
            preparedStatement.setInt(1, tourId);
            preparedStatement.setInt(2, attractionId);

            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (!rs.next()) {
                    throw new NotFound();
                }

                TourAttraction tour = new TourAttraction(rs);
                return tour;
            }
        }
    }
    public static List<Attraction> findAttractionsByTourId(Integer tourId) throws SQLException, NotFound {
        try (PreparedStatement preparedStatement = getDb().prepareStatement("select * from tours_attractions, attractions where tour_id = ? and tours_attractions.attraction_id = attractions.id ORDER BY tours_attractions.order_index ASC")) {
            preparedStatement.setInt(1, tourId);

            try (ResultSet rs = preparedStatement.executeQuery()) {
                List<Attraction> attractions = new ArrayList<>();
                while (rs.next()) {
                    Attraction attraction = new Attraction(rs);
                    attractions.add(attraction);
                }

                return attractions;
            }
        }
    }
    public static List<TourAttraction> findTourAttractionsByTourId(Integer tourId) throws SQLException, NotFound {
        try (PreparedStatement preparedStatement = getDb().prepareStatement("select * from tours_attractions where tour_id = ? ORDER BY tours_attractions.order_index ASC")) {
            preparedStatement.setInt(1, tourId);

            try (ResultSet rs = preparedStatement.executeQuery()) {
                List<TourAttraction> tourAttractionList = new ArrayList<>();
                while (rs.next()) {
                    TourAttraction tourAttraction = new TourAttraction(rs);
                    tourAttractionList.add(tourAttraction);
                }

                return tourAttractionList;
            }
        }
    }

    public void insert() throws SQLException, NotFound, AlreadyExists {
        // insert city to table
        try (PreparedStatement preparedStatement = getDb().prepareStatement("insert into tours_attractions (tour_id, attraction_id, time, order_index) values (?, ?, ?, ?)")) {
            preparedStatement.setInt(1, this.getTourId());
            preparedStatement.setInt(2, this.getAttractionId());
            preparedStatement.setString(3, this.getTime());
            preparedStatement.setInt(4, this.getOrderIndex());
            // run the insert command
            preparedStatement.executeUpdate();
        } catch (java.sql.SQLIntegrityConstraintViolationException e) {
            throw new AlreadyExists();
        }
    }

    // exceptions
    public static class NotFound extends Exception {
    }

    public static class AlreadyExists extends Exception {
    }

    // getters and setters

    public Integer getTourId() {
        return tourId;
    }

    public void setTourId(Integer tourId) {
        this.tourId = tourId;
    }

    public Integer getAttractionId() {
        return attractionId;
    }

    public void setAttractionId(Integer attractionId) {
        this.attractionId = attractionId;
    }

    public Integer getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(Integer orderIndex) {
        this.orderIndex = orderIndex;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
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
