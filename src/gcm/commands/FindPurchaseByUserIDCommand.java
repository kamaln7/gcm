package gcm.commands;

import gcm.database.models.Purchase;
import gcm.server.Server;
import ocsf.server.ConnectionToClient;

import java.util.List;

/**
 * find purchases by the user ID
 */

public class FindPurchaseByUserIDCommand implements Command {
    public static class Input extends gcm.commands.Input {
        private Integer userId;

        public Input(Integer userId) {
            this.userId = userId;
        }
    }

    public static class Output extends gcm.commands.Output {
        public List<Purchase> purchases;

        public Output(List<Purchase> purchases) {
            this.purchases = purchases;
        }
    }

    @Override
    public Output runOnServer(Request request, Server server, ConnectionToClient client) throws Exception {
        Input input = request.getInput(Input.class);
        List<Purchase> purchases = Purchase.findAllByUserId(input.userId);

        return new Output(purchases);
    }
}
