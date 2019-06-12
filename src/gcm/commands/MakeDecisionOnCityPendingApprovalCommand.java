package gcm.commands;

import gcm.database.models.City;
import gcm.server.Server;
import ocsf.server.ConnectionToClient;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class MakeDecisionOnCityPendingApprovalCommand implements Command {
    public static class Input extends gcm.commands.Input {
        public Integer cityId;
        public Boolean approved;

        public Input(Integer cityId, Boolean approved) {
            this.cityId = cityId;
            this.approved = approved;
        }
    }

    public static class Output extends gcm.commands.Output {
    }

    @Override
    public Output runOnServer(Request request, Server server, ConnectionToClient client) throws Exception {
        Input input = request.getInput(Input.class);

        List<String> pathsToDelete = City.makeApprovalDecision(input.cityId, input.approved);
        // delete old image files
        for (String path : pathsToDelete) {
            try {
                Files.delete(Paths.get(server.getFilesPath(), path));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (input.approved) {
            server.getChatIF().displayf("Send email to users who subscribe to city id=%d that a new version is published.", input.cityId);
        }

        return new Output();
    }
}
