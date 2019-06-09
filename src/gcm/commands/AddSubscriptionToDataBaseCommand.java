package gcm.commands;

import gcm.database.models.City;
import gcm.database.models.Subscription;
import gcm.server.Server;
import ocsf.server.ConnectionToClient;

import java.util.Calendar;
import java.util.Date;

public class AddSubscriptionToDataBaseCommand implements Command {
    public static class Input extends gcm.commands.Input {
        public int user_id,city_id;
        public Date from,to;
        public double price;
        public boolean renew;

        public Input(int user_id, int city_id, Date from, Date to, double price, boolean renew) {

            this.user_id = user_id;
            this.city_id = city_id;
            this.from = from;
            this.to = to;
            this.price = price;
            this.renew = renew;



        }
    }

    public static class Output extends gcm.commands.Output {
        public Subscription subscription;

        public Output(Subscription subscription) {
            this.subscription = subscription;

        }
    }

    @Override
    public Output runOnServer(Request request, Server server, ConnectionToClient client) throws Exception{
        Input input = request.getInput(Input.class);

        Subscription subscription = new Subscription(input.user_id, input.city_id, input.from, input.to, input.price, input.renew);
        subscription.insert();

        return new Output(subscription);
    }
}
