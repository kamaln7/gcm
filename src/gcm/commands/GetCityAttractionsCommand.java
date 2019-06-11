package gcm.commands;

import gcm.database.models.Attraction;
import gcm.server.Server;
import ocsf.server.ConnectionToClient;

import java.util.List;

/**
 * used to get the attractions in a city
 */
public class GetCityAttractionsCommand implements Command {
    public static class Input extends gcm.commands.Input {
        int city_id;

        public Input(int city_id) {
            this.city_id = city_id;
        }
    }

    public static class Output extends gcm.commands.Output {
        public List<Attraction> result;


        public Output(List<Attraction> result) {
            this.result = result;
        }
    }

    /**
     * runs the command on the server
     * @param request
     * @param server
     * @param client
     * @return
     * @throws Exception
     */
    @Override
    public Output runOnServer(Request request, Server server, ConnectionToClient client) throws Exception {
        GetCityAttractionsCommand.Input input = request.getInput(GetCityAttractionsCommand.Input.class);
        List<Attraction> result = Attraction.getAttractionForCity(input.city_id);
        return new Output(result);
    }
}
