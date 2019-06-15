package gcm.tests;

import gcm.database.models.Attraction;

import java.util.ArrayList;
import java.util.List;

/**
 * Wraps the Attraction model for testing
 * Provides relationship storage
 */
public class TestAttraction extends Attraction {
    public List<TestMap> maps = new ArrayList<>();

    public TestAttraction(Integer cityId, String name, String description, String type, String location, Boolean accessibleSpecial) {
        super(cityId, name, description, type, location, accessibleSpecial);
    }
}
