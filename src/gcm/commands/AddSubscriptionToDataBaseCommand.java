package gcm.commands;

import gcm.database.models.City;
import gcm.database.models.Subscription;
import gcm.server.Server;
import ocsf.server.ConnectionToClient;

import java.util.Date;

/**
 * add subscription to the data base
 * user ID, city ID, starting date, ending date, price, renew(0 new, 1 renew)
 */
public class AddSubscriptionToDataBaseCommand implements Command {
    public static class Input extends gcm.commands.Input {
        public int user_id, city_id;
        public Date from, to;
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
    public Output runOnServer(Request request, Server server, ConnectionToClient client) throws Exception {
        Input input = request.getInput(Input.class);

        City city = City.findById(input.city_id);
        Double price = input.renew ? city.getSubscriptionPrice() * 0.9 : city.getSubscriptionPrice();

        Subscription subscription = new Subscription(input.user_id, input.city_id, input.from, input.to, price, input.renew);
        subscription.insert();
        subscription._extraInfo.put("cityTitle", city.getTitle());

        return new Output(subscription);
    }
}
