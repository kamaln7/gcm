package gcm.database.models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TourAttraction extends Model {
    // fields
    private Integer tourId, attractionId, orderIndex;
    private String time;
    private Date createdAt, updatedAt;

    /**
     * create object with info from ResultSet
     *
     * @param rs
     * @throws SQLException
     */
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

    /**
     * fill object with info from ResultSet
     *
     * @param rs
     * @throws SQLException
     */
    public void fillFieldsFromResultSet(ResultSet rs) throws SQLException {
        this.tourId = rs.getInt("tour_id");
        this.attractionId = rs.getInt("attraction_id");
        this.orderIndex = rs.getInt("order_index");
        this.time = rs.getString("time");
        this.createdAt = rs.getTimestamp("created_at");
        this.updatedAt = rs.getTimestamp("updated_at");
    }

    /**
     * find the attractions in the tour
     *
     * @param tourId
     * @return List<Attraction> attractions
     * @throws SQLException
     * @throws NotFound
     */
    public static List<Attraction> findAttractionsByTourId(Integer tourId) throws SQLException, NotFound {
        try (Connection db = getDb();
             PreparedStatement preparedStatement = db.prepareStatement("select * from tours_attractions, attractions where tour_id = ? and tours_attractions.attraction_id = attractions.id ORDER BY tours_attractions.order_index ASC")) {
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

    /**
     * find the attraction ids connected with tourId
     *
     * @param tourId
     * @return List<TourAttraction> tourAttractionList
     * @throws SQLException
     * @throws NotFound
     */
    public static List<TourAttraction> findTourAttractionsByTourId(Integer tourId) throws SQLException, NotFound {
        try (Connection db = getDb();
             PreparedStatement preparedStatement = db.prepareStatement("select * from tours_attractions where tour_id = ? ORDER BY tours_attractions.order_index ASC")) {
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

    /**
     * insert a new object to the database
     *
     * @throws SQLException
     * @throws NotFound
     * @throws AlreadyExists
     */
    public void insert() throws SQLException, NotFound, AlreadyExists {
        // insert city to table
        try (Connection db = getDb();
             PreparedStatement preparedStatement = db.prepareStatement("insert into tours_attractions (tour_id, attraction_id, time, order_index) values (?, ?, ?, ?)")) {
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
