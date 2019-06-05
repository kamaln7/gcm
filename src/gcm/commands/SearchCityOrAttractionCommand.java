package gcm.commands;

import gcm.database.models.Attraction;
import gcm.database.models.City;
import gcm.database.models.Map;
import gcm.server.Server;
import ocsf.server.ConnectionToClient;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SearchCityOrAttractionCommand implements Command {
    public static class Input extends gcm.commands.Input {
        public String searchQuery;

        public Input(String searchQuery) {
            this.searchQuery = searchQuery;
        }
    }

    public static class Output extends gcm.commands.Output {
        public List<City> cities;
        public List<Attraction> attractions;
        public java.util.Map<Integer, List<Map>> cityMaps;

        public Output() {
            this.cities = new ArrayList<>();
            this.attractions = new ArrayList<>();
        }

        public Output(List<City> cities, List<Attraction> attractions, java.util.Map<Integer, List<Map>> cityMaps) {
            this.cities = cities;
            this.attractions = attractions;
            this.cityMaps = cityMaps;
        }
    }

    @Override
    public Output runOnServer(Request request, Server server, ConnectionToClient client) throws Exception {
        Input input = request.getInput(Input.class);

        if (input.searchQuery.equals("")) {
            // return no results
            return new Output();
        }

        List<City> cities = City.searchByName(input.searchQuery);
        for (City city : cities) {
            city.lookupCountsOfRelated();
        }
        java.util.Map<Integer, List<Map>> cityMaps = Map.findAllForCities(cities.stream().map(city -> city.getId()).collect(Collectors.toSet()));
        List<Attraction> attractions = Attraction.searchByNameOrDescription(input.searchQuery);

        return new Output(cities, attractions, cityMaps);
    }
}
