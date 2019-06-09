package gcm.database.models;


import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Map extends Model {
    // fields
    private Integer id, cityId;
    private Boolean verification = false; // 1 is verified 0 is not verified
    private String title, description, version, img;
    private Date createdAt, updatedAt;

    // create User object with info from ResultSet
    public Map(ResultSet rs) throws SQLException {
        super();

        this.fillFieldsFromResultSet(rs);
    }

    public Map() {
    }

    public Map(String title, String description, String version, String img, int cityId) {
        this.title = title;
        this.description = description;
        this.version = version;
        this.img = img;
        this.cityId = cityId;
    }

    public static List<Map> findAllByCityId(Integer cityId) throws SQLException {
        return findAllByCityId(cityId, true);
    }

    public static List<Map> findAllByCityId(Integer cityId, Boolean verifiedOnly) throws SQLException {
        try (PreparedStatement preparedStatement = getDb().prepareStatement("select * from maps where city_id = ? and verification = ? order by title asc")) {
            preparedStatement.setInt(1, cityId);
            preparedStatement.setBoolean(2, verifiedOnly);
            try (ResultSet rs = preparedStatement.executeQuery()) {

                List<Map> maps = new ArrayList<>();
                while (rs.next()) {
                    Map map = new Map(rs);
                    maps.add(map);
                }

                return maps;
            }
        }
    }
public  static  int countAllForCities(int cityID) throws Exception
{
    try (PreparedStatement preparedStatement = getDb().prepareStatement("select count(*) AS total from maps where city_id = ?")) {
        preparedStatement.setInt(1, cityID);

        try (ResultSet rs = preparedStatement.executeQuery()) {
            if (!rs.next()) {
                return 0;
            }
            return rs.getInt("total");
        }
    }
}
    public static java.util.Map<Integer, List<Map>> findAllForCities(Set<Integer> cityIds) throws SQLException {
        if (cityIds.isEmpty()) {
            return new java.util.HashMap<>();
        }

        List<Integer> cityIdsList = new ArrayList<>(cityIds);


        String query = String.format(
                "select * from maps where city_id in (%s) order by title asc",
                IntStream
                        .range(0, cityIds.size())
                        .mapToObj(s -> "?")
                        .collect(Collectors.joining(", "))
        );

        try (PreparedStatement preparedStatement = getDb().prepareStatement(query)) {
            // bind ids
            int bound = cityIds.size();
            for (int i = 0; i < bound; i++) {
                preparedStatement.setInt(i + 1, cityIdsList.get(i));
            }

            try (ResultSet rs = preparedStatement.executeQuery()) {
                List<Map> maps = new ArrayList<>();
                while (rs.next()) {
                    Map map = new Map(rs);
                    maps.add(map);
                }

                return maps
                        .stream()
                        .collect(Collectors.groupingBy(Map::getCityId));
            }
        }
    }

    public static java.util.Map<Integer, List<Map>> findAllForAttractions(Set<Integer> attractionIds) throws SQLException {
        if (attractionIds.isEmpty()) {
            return new java.util.HashMap<>();
        }

        java.util.Map<Integer, List<MapAttraction>> mapAttractionsList = MapAttraction.findAllForAttractions(attractionIds);

        List<Integer> mapIdsList = mapAttractionsList
                .values()
                .parallelStream()
                .map(lma -> lma
                        .parallelStream()
                        .map(ma -> ma.getMapId())
                        .distinct()
                )
                .flatMap(Function.identity())
                .distinct()
                .collect(Collectors.toList());

        if (mapIdsList.isEmpty()) {
            return new java.util.HashMap<>();
        }

        String query = String.format(
                "select * from maps where id in (%s) order by title asc",
                IntStream
                        .range(0, mapIdsList.size())
                        .mapToObj(s -> "?")
                        .collect(Collectors.joining(", "))
        );
        try (PreparedStatement preparedStatement = getDb().prepareStatement(query)) {
            // bind ids
            int bound = mapIdsList.size();
            for (int i = 0; i < bound; i++) {
                preparedStatement.setInt(i + 1, mapIdsList.get(i));
            }

            try (ResultSet rs = preparedStatement.executeQuery()) {
                java.util.Map<Integer, Map> maps = new HashMap<>();
                while (rs.next()) {
                    Map map = new Map(rs);
                    maps.put(map.getId(), map);
                }

                java.util.Map<Integer, List<Map>> result = new HashMap<>();
                for (Integer attractionId : attractionIds) {
                    result.put(
                            attractionId,
                            mapAttractionsList
                                    .get(attractionId)
                                    .stream()
                                    .map(ma -> maps.get(ma.getMapId()))
                                    .collect(Collectors.toList())
                    );
                }
                return result;
            }
        }
    }

    public void fillFieldsFromResultSet(ResultSet rs) throws SQLException {
        this.id = rs.getInt("id");
        this.cityId = rs.getInt("city_id");
        this.title = rs.getString("title");
        this.description = rs.getString("description");
        this.version = rs.getString("version");
        this.createdAt = rs.getTimestamp("created_at");
        this.updatedAt = rs.getTimestamp("updated_at");
        this.img = rs.getString("img");
        this.verification = rs.getBoolean("verification");
    }


    /* QUERIES */
    public static Map findById(Integer id) throws Exception {
        try (PreparedStatement preparedStatement = getDb().prepareStatement("select * from maps where id = ?")) {
            preparedStatement.setInt(1, id);

            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (!rs.next()) {
                    throw new NotFound();
                }

                Map map = new Map(rs);
                return map;
            }
        }
    }

    public static Map findByTitle(String title) throws SQLException, NotFound {
        try (PreparedStatement preparedStatement = getDb().prepareStatement("select * from maps where title = ?")) {
            preparedStatement.setString(1, title);

            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (!rs.next()) {
                    throw new NotFound();
                }

                Map map = new Map(rs);
                return map;
            }
        }
    }

    public static Map findByTitleAndVersion(String title, String version) throws SQLException, NotFound {
        try (PreparedStatement preparedStatement = getDb().prepareStatement("select * from maps where title = ? And version = ?")) {
            preparedStatement.setString(1, title);
            preparedStatement.setString(2, version);

            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (!rs.next()) {
                    throw new NotFound();
                }
                Map map = new Map(rs);
                return map;
            }
        }
    }

    public static List<Map> findAllWithCityTitle() throws SQLException {
        try (Statement statement = getDb().createStatement()) {
            try (ResultSet rs = statement.executeQuery("select maps.*, concat(cities.name, \", \", cities.country) as city_title\n" +
                    "from maps\n" +
                    "left join cities\n" +
                    "on maps.city_id = cities.id")) {
                ArrayList<Map> maps = new ArrayList<>();
                while (rs.next()) {
                    Map map = new Map(rs);
                    map._extraInfo.put("cityTitle", rs.getString("city_title"));
                    maps.add(map);
                }
                return maps;
            }
        }
    }

    public void updateImage(String new_image) throws SQLException, NotFound {
        try (PreparedStatement preparedStatement = getDb().prepareStatement("UPDATE maps SET img = ? WHERE id = ?")) {
            preparedStatement.setString(1, new_image);
            preparedStatement.setInt(2, this.getId());

            preparedStatement.executeUpdate();
        }
    }

    public void updateDescriptionAndTitle() throws SQLException, NotFound {
        try (PreparedStatement preparedStatement = getDb().prepareStatement("UPDATE maps SET title = ?, description = ? WHERE id = ?")) {
            preparedStatement.setString(1, this.title);
            preparedStatement.setString(2, this.description);
            preparedStatement.setInt(3, this.id);
            preparedStatement.executeUpdate();
        }
    }


    public void insert() throws SQLException, NotFound {
        // insert map to table
        try (PreparedStatement preparedStatement = getDb().prepareStatement("insert into maps (title, version, description, img, city_id) values (?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setString(1, this.getTitle());
            preparedStatement.setString(2, this.getVersion());
            preparedStatement.setString(3, this.getDescription());
            preparedStatement.setString(4, this.getImg());
            preparedStatement.setInt(5, this.getCityId());

            // run the insert command
            preparedStatement.executeUpdate();
            // get the auto generated id
            try (ResultSet rsGenerated = preparedStatement.getGeneratedKeys()) {
                if (!rsGenerated.next()) {
                    throw new NotFound();
                }

                // find the new attraction details
                Integer id = rsGenerated.getInt(1);
                this.updateWithNewDetailsById(id, "maps");
            }
        }
    }


    // exceptions
    public static class NotFound extends Exception {
    }

    public static class AlreadyExists extends Exception {
    }

    public static class WrongType extends Exception {
    }


    // getters and setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
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

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public Integer getCityId() {
        return cityId;
    }

    public void setCityId(Integer cityId) {
        this.cityId = cityId;
    }

    public Boolean getVerification() {
        return verification;
    }

    public void setVerification(Boolean verification) {
        this.verification = verification;
    }
}
