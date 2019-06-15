package gcm.tests;

import gcm.commands.Request;
import gcm.commands.SearchCityOrAttractionCommand;
import gcm.database.models.Attraction;
import gcm.database.models.City;
import gcm.database.models.Map;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.expect;
import static org.powermock.api.easymock.PowerMock.*;

@PrepareForTest({City.class, Map.class, Attraction.class})
@RunWith(PowerMockRunner.class)
public class SearchTest extends TestCase {
    // fake database for tests
    // with fixed, predictable data
    private java.util.Map<Integer, TestCity> cities = new HashMap<>();
    private java.util.Map<Integer, TestMap> maps = new HashMap<>();
    private java.util.Map<Integer, TestAttraction> attractions = new HashMap<>();

    private int cityIdCounter = 1;

    // ID counter for the fake rows
    private enum IDs {
        HaifaCityId,
        HaifaUniId,
        MountCarmelId,
    }

    @Before
    public void setUp() {
        addCityHaifa();
    }

    /**
     * Add fake testing data
     */
    private void addCityHaifa() {
        int cityId = cityIdCounter++;

        TestCity haifa = new TestCity("Haifa", "Israel");
        haifa.setId(IDs.HaifaCityId.ordinal());

        TestAttraction haifaUni = new TestAttraction(cityId, "University of Haifa", "It's uni!", "Historical Place", "Abba Khoushy", true);
        haifaUni.setId(IDs.HaifaUniId.ordinal());

        TestMap mountCarmelMap = new TestMap("Mount Carmel", "Mountain carmel map", "", "", cityId);
        mountCarmelMap.setId(IDs.MountCarmelId.ordinal());

        // set relationships
        haifaUni.maps.add(mountCarmelMap);
        haifa.maps.add(mountCarmelMap);
        haifa.attractions.add(haifaUni);

        // insert into fake db
        attractions.put(haifaUni.getId(), haifaUni);
        maps.put(mountCarmelMap.getId(), mountCarmelMap);
        cities.put(mountCarmelMap.getId(), haifa);
    }

    /**
     * Test that searching for something that doesn't exist returns no results
     *
     * @throws Exception
     */
    @Test
    public void testNoMatches() throws Exception {
        mockStatic(City.class);
        mockStatic(Map.class);
        mockStatic(Attraction.class);

        // mock methods based on the fake data
        expectModelResults("aokdoaskdoaskdo");

        replayAll();

        // sends search command
        SearchCityOrAttractionCommand cmd = new SearchCityOrAttractionCommand();
        Request request = new Request(new SearchCityOrAttractionCommand.Input("aokdoaskdoaskdo"));
        SearchCityOrAttractionCommand.Output output = cmd.runOnServer(request, null, null);

        assertNotNull(output.cityMaps);
        assertNotNull(output.attractions);
        assertNotNull(output.cities);
        assertNotNull(output.attractionMaps);

        assertEquals(output.attractions.size(), 0);
        assertEquals(output.cityMaps.size(), 0);
        assertEquals(output.cities.size(), 0);
        assertEquals(output.attractionMaps.size(), 0);

        verifyAll();
    }

    /**
     * Test that searching for "haifa" returns the fake data we added above
     *
     * @throws Exception
     */
    @Test
    public void testSearchForHaifa() throws Exception {
        mockStatic(City.class);
        mockStatic(Map.class);
        mockStatic(Attraction.class);

        expectModelResults("haifa");

        replayAll();

        SearchCityOrAttractionCommand cmd = new SearchCityOrAttractionCommand();

        Request request = new Request(new SearchCityOrAttractionCommand.Input("haifa"));
        SearchCityOrAttractionCommand.Output output = cmd.runOnServer(request, null, null);

        assertNotNull(output.cityMaps);
        assertNotNull(output.attractions);
        assertNotNull(output.cities);
        assertNotNull(output.attractionMaps);

        assertEquals(output.attractions.size(), 1);
        assertEquals(output.cityMaps.size(), 1);
        assertEquals(output.cities.size(), 1);
        assertEquals(output.attractionMaps.size(), 1);

        // make sure all ids of objects we're looking for are returned
        assertTrue(
                output.cities
                        .stream()
                        .map(c -> c.getId())
                        .collect(Collectors.toList())
                        .contains(IDs.HaifaCityId.ordinal())
        );
        assertTrue(
                output.attractions
                        .stream()
                        .map(c -> c.getId())
                        .collect(Collectors.toList())
                        .contains(IDs.HaifaUniId.ordinal())
        );

        verifyAll();
    }

    // mock Models to return fake data instead of connecting to the database
    private void expectModelResults(String query) throws SQLException {
        // find matching cities in the fake database
        List<City> matchingCities = cities.values()
                .parallelStream()
                .filter(city -> contains(city.getName(), query) || contains(city.getCountry(), query))
                .collect(Collectors.toList());

        // find maps for the matching cities
        java.util.Map<Integer, List<Map>> mapsForMatchingCities = matchingCities
                .parallelStream()
                .map(city -> (TestCity) city)
                .collect(Collectors.toMap(
                        TestCity::getId,
                        city -> city.maps.stream().map(m -> (Map) m).collect(Collectors.toList())
                ));

        // mock City and Map methods to return the matches we found above
        expect(City.searchByNameWithCounts(query)).andReturn(matchingCities);
        expect(Map.findAllForCities(anyObject())).andReturn(mapsForMatchingCities);

        // find matching attractions
        List<Attraction> matchingAttractions = attractions.values()
                .parallelStream()
                .filter(attraction -> contains(attraction.getName(), query) || contains(attraction.getDescription(), query))
                .collect(Collectors.toList());

        // find maps for matching attractions
        java.util.Map<Integer, List<Map>> mapsForMatchingAttractions = matchingAttractions
                .parallelStream()
                .map(attraction -> (TestAttraction) attraction)
                .collect(Collectors.toMap(
                        TestAttraction::getId,
                        attraction -> attraction.maps.stream().map(m -> (Map) m).collect(Collectors.toList())
                ));

        // again, mock to return fake data
        expect(Attraction.searchByNameOrDescription(query)).andReturn(matchingAttractions);
        expect(Map.findAllForAttractions(anyObject())).andReturn(mapsForMatchingAttractions);
    }

    private static boolean contains(String outer, String inner) {
        if (outer == null || inner == null) {
            return false;
        }

        return outer.toLowerCase().contains(inner.toLowerCase());
    }

    /**
     * Test that an empty search query returns nothing
     *
     * @throws Exception
     */
    @Test
    public void testEmptySearchQuery() throws Exception {
        SearchCityOrAttractionCommand cmd = new SearchCityOrAttractionCommand();
        Request request = new Request(new SearchCityOrAttractionCommand.Input(""));
        SearchCityOrAttractionCommand.Output output = cmd.runOnServer(request, null, null);

        Assert.assertNotNull(output);
        assertNull(output.cityMaps);
        assertNull(output.attractionMaps);

        assertNotNull(output.attractions);
        assertNotNull(output.cities);

        assertEquals(output.attractions.size(), 0);
        assertEquals(output.cities.size(), 0);
    }
}
