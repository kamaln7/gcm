package gcm.commands;

import gcm.database.models.City;
import gcm.database.models.Subscription;
import gcm.server.Server;
import ocsf.server.ConnectionToClient;

import java.util.Date;

/**
 * find subscription by user ID, city ID and date
 */

public class FindSubscriptionCommand implements Command {
    public static class Input extends gcm.commands.Input {
        private Integer  userId, cityId;
        private Date to_date,from_date;

        public Input(Integer userId, Integer cityId,Date from_date) {
            this.userId = userId;
            this.cityId = cityId;

            this.from_date = from_date;

        }
    }

    public static class Output extends gcm.commands.Output {
        public Subscription subscription;

        public Output(Subscription subscription) {
            this.subscription = subscription;
        }
    }

    @Override
    public Output runOnServer(Request request, Server server, ConnectionToClient client) throws Exception {
        Input input = request.getInput(Input.class);
        Subscription subscription = Subscription.findSubscriptionbyIDs(input.userId, input.cityId, input.from_date);

        return new Output(subscription);
    }
}
