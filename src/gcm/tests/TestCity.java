package gcm.tests;

import gcm.database.models.City;

import java.util.ArrayList;
import java.util.List;

public class TestCity extends City {
    public List<TestMap> maps = new ArrayList<>();
    public List<TestAttraction> attractions = new ArrayList<>();

    public TestCity(String name, String country) {
        super();
        this.setName(name);
        this.setCountry(country);
    }
}
