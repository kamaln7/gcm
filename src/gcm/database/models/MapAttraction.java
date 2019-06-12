package gcm.database.models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MapAttraction extends Model {
    // fields
    private Integer mapId, attractionId;
    private Date createdAt, updatedAt;

    // create User object with info from ResultSet
    public MapAttraction(ResultSet rs) throws SQLException {
        super();

        this.fillFieldsFromResultSet(rs);
    }

    public MapAttraction(Integer mapId, Integer attractionId) {
        this.mapId = mapId;
        this.attractionId = attractionId;
    }

    public static java.util.Map<Integer, List<MapAttraction>> findAllForAttractions(Set<Integer> attractionIds) throws SQLException {
        if (attractionIds.isEmpty()) {
            return new java.util.HashMap<>();
        }

        List<Integer> attractionIdsList = new ArrayList<>(attractionIds);
        String query = String.format(
                "select * from maps_attractions where attraction_id in (%s)",
                IntStream
                        .range(0, attractionIdsList.size())
                        .mapToObj(s -> "?")
                        .collect(Collectors.joining(", "))
        );

        try (Connection db = getDb();
             PreparedStatement preparedStatement = db.prepareStatement(query)) {
            // bind ids
            int bound = attractionIdsList.size();
            for (int i = 0; i < bound; i++) {
                preparedStatement.setInt(i + 1, attractionIdsList.get(i));
            }

            try (ResultSet rs = preparedStatement.executeQuery()) {
                List<MapAttraction> mapAttractions = new ArrayList<>();
                while (rs.next()) {
                    MapAttraction mapAttraction = new MapAttraction(rs);
                    mapAttractions.add(mapAttraction);
                }

                return mapAttractions
                        .stream()
                        .collect(Collectors.groupingBy(MapAttraction::getAttractionId));
            }
        }
    }

    public void fillFieldsFromResultSet(ResultSet rs) throws SQLException {
        this.mapId = rs.getInt("map_id");
        this.attractionId = rs.getInt("attraction_id");
        this.createdAt = rs.getTimestamp("created_at");
        this.updatedAt = rs.getTimestamp("updated_at");
    }

    public static MapAttraction findByIds(Integer mapId, Integer attractionId) throws SQLException, NotFound {
        try (Connection db = getDb();
             PreparedStatement preparedStatement = db.prepareStatement("select * from maps_attractions where map_id = ? and attraction_id = ?")) {
            preparedStatement.setInt(1, mapId);
            preparedStatement.setInt(2, attractionId);

            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (!rs.next()) {
                    throw new NotFound();
                }

                MapAttraction mapAttraction = new MapAttraction(rs);
                return mapAttraction;
            }
        }
    }

    public void insert() throws SQLException, NotFound, AlreadyExists {
        // insert city to table
        try (Connection db = getDb();
             PreparedStatement preparedStatement = db.prepareStatement("insert into maps_attractions (map_id, attraction_id) values (?, ?)")) {
            preparedStatement.setInt(1, this.getMapId());
            preparedStatement.setInt(2, this.getAttractionId());
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
    public Integer getMapId() {
        return mapId;
    }

    public void setMapId(Integer mapId) {
        this.mapId = mapId;
    }

    public Integer getAttractionId() {
        return attractionId;
    }

    public void setAttractionId(Integer attractionId) {
        this.attractionId = attractionId;
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
