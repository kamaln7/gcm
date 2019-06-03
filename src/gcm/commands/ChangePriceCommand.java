package gcm.commands;

import gcm.database.models.City;
import gcm.server.Server;
import ocsf.server.ConnectionToClient;

public class ChangePriceCommand implements Command {
    public static class Input extends gcm.commands.Input {
        public String cityName;
        public String countryName;
        public double new_purchase_price;
        public double new_sub_price;

        public Input(String city, String country, double new_purchase_price, double new_sub_price) {
            this.cityName = city; this.countryName=country; this.new_purchase_price = new_purchase_price ; this.new_sub_price = new_sub_price;
        }
    }

    public static class Output extends gcm.commands.Output {
        public City city;

        public Output(City city) {
            this.city = city;
        }
    }

    @Override
    public Output runOnServer(Request request, Server server, ConnectionToClient client) throws Exception {
        Input input = request.getInput(Input.class);
        City city = City.changePrice(input.cityName, input.countryName, input.new_purchase_price, input.new_sub_price);

        return new Output(city);
    }
}
