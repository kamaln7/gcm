package gcm.commands;

import gcm.database.models.Attraction;
import gcm.database.models.City;
import gcm.server.Server;
import ocsf.server.ConnectionToClient;

import java.util.ArrayList;
import java.util.List;

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

        public Output() {
            this.cities = new ArrayList<>();
            this.attractions = new ArrayList<>();
        }

        public Output(List<City> cities, List<Attraction> attractions) {
            this.cities = cities;
            this.attractions = attractions;
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
        List<Attraction> attractions = Attraction.searchByNameOrDescription(input.searchQuery);

        return new Output(cities, attractions);
    }
}
