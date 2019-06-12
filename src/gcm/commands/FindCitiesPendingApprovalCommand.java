package gcm.commands;

import gcm.database.models.Attraction;
import gcm.database.models.Map;
import gcm.server.Server;
import ocsf.server.ConnectionToClient;

import java.util.HashMap;
import java.util.List;

public class FindCitiesPendingApprovalCommand implements Command {
    public static class Input extends gcm.commands.Input {
    }

    public static class Output extends gcm.commands.Output {
        public List<Attraction> attractions;
        public List<Map> maps;
        public java.util.Map<Integer, String> cities;

        public Output(List<Attraction> attractions, List<Map> maps, java.util.Map<Integer, String> cities) {
            this.attractions = attractions;
            this.maps = maps;
            this.cities = cities;
        }
    }

    @Override
    public Output runOnServer(Request request, Server server, ConnectionToClient client) throws Exception {
        List<Attraction> attractions = Attraction.findPendingApproval();
        List<Map> maps = Map.findPendingApproval();

        java.util.Map<Integer, String> cities = new HashMap<>();
        attractions.forEach(a -> cities.put(a.getCityId(), a._extraInfo.get("cityTitle")));
        maps.forEach(a -> cities.put(a.getCityId(), a._extraInfo.get("cityTitle")));

        return new Output(attractions, maps, cities);
    }
}
