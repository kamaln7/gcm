package gcm.tests;

import gcm.commands.Request;
import gcm.commands.SearchCityOrAttractionCommand;
import gcm.database.models.Attraction;
import gcm.database.models.City;
import gcm.database.models.Map;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.expect;
import static org.powermock.api.easymock.PowerMock.*;

@PrepareForTest({City.class, Map.class, Attraction.class})
@RunWith(PowerMockRunner.class)
public class SearchTest extends TestCase {

    private List<TestCity> cities = new ArrayList<>();
    private List<TestAttraction> attractions = new ArrayList<>();
    private List<TestMap> maps = new ArrayList<>();

    @Test
    public void testNoMatches() throws Exception {
        mockStatic(City.class);
        mockStatic(Map.class);
        mockStatic(Attraction.class);

        expectModelResults("aokdoaskdoaskdo");

        replayAll();

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

    private static void expectModelResults(String query) throws SQLException {
        expect(City.searchByNameWithCounts(query)).andReturn(new ArrayList<>());
        expect(Map.findAllForCities(anyObject())).andReturn(new HashMap<>());
        expect(Attraction.searchByNameOrDescription(query)).andReturn(new ArrayList<>());
        expect(Map.findAllForAttractions(anyObject())).andReturn(new HashMap<>());
    }

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
