package gcm.commands;

import gcm.database.models.City;
import gcm.database.models.User;
import gcm.server.Server;
import ocsf.server.ConnectionToClient;

public class AddCityToDataBaseCommand implements Command {
    public static class Input extends gcm.commands.Input {
        public String name, country;
        public double subscription_price, purchase_price;

        public Input(String name, String country, double subscription_price, double purchase_price) {
            this.name = name;
            this.country = country;
            this.subscription_price = subscription_price;
            this.purchase_price = purchase_price;
        }
    }

    public static class Output extends gcm.commands.Output {
        public City city;

        public Output(City city) {
            this.city = city;
        }
    }

    @Override
    public Output runOnServer(Request request, Server server, ConnectionToClient client) throws Exception{
        Input input = request.getInput(Input.class);

        City city = new City(input.name, input.country, input.subscription_price, input.purchase_price);
        city.insert();

        return new Output(city);
    }
}
