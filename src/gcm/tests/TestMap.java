package gcm.tests;

import gcm.database.models.Map;

/**
 * Wraps the Map model for testing
 * Provides relationship storage
 */
public class TestMap extends Map {
    public TestMap(String title, String description, String version, String img, int cityId) {
        super(title, description, version, img, cityId);
    }

    public TestMap() {
    }
}
