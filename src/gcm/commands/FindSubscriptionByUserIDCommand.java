package gcm.commands;

import gcm.database.models.Subscription;
import gcm.server.Server;
import ocsf.server.ConnectionToClient;

import java.util.List;

public class FindSubscriptionByUserIDCommand implements Command {
    public static class Input extends gcm.commands.Input {
        private Integer userId;

        public Input(Integer userId) {
            this.userId = userId;
        }
    }

    public static class Output extends gcm.commands.Output {
        public List<Subscription> subscriptions;

        public Output(List<Subscription> subscriptions) {
            this.subscriptions = subscriptions;
        }
    }

    @Override
    public Output runOnServer(Request request, Server server, ConnectionToClient client) throws Exception {
        Input input = request.getInput(Input.class);
        List<Subscription> subscriptions = Subscription.findAllByUserId(input.userId);

        return new Output(subscriptions);
    }
}
