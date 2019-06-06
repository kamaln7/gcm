package gcm.commands;

import gcm.database.models.Purchase;
import gcm.database.models.Subscription;
import gcm.server.Server;
import ocsf.server.ConnectionToClient;

import java.util.Date;

public class AddPurchaseToDataBaseCommand implements Command {
    public static class Input extends gcm.commands.Input {
        public int user_id,city_id;


        public Input(int user_id, int city_id) {

            this.user_id = user_id;
            this.city_id = city_id;




        }
    }

    public static class Output extends gcm.commands.Output {
        public Purchase purchase;

        public Output(Purchase purchase) {
            this.purchase = purchase;

        }
    }

    @Override
    public Output runOnServer(Request request, Server server, ConnectionToClient client) throws Exception{
        Input input = request.getInput(Input.class);

        Purchase purchase = new Purchase(input.user_id, input.city_id);
        purchase.insert();

        return new Output(purchase);
    }
}
