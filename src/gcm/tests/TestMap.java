package gcm.tests;

import gcm.database.models.Map;

public class TestMap extends Map {
    public TestMap(String title, String description, String version, String img, int cityId) {
        super(title, description, version, img, cityId);
    }

    public TestMap() {
    }
}
