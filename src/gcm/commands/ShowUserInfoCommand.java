package gcm.commands;

import gcm.database.models.*;
import gcm.server.Server;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import ocsf.server.ConnectionToClient;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ShowUserInfoCommand implements Command {

    public static class Input extends gcm.commands.Input {
    }

    public class Output extends gcm.commands.Output {

        public List<User> usersList;
        public List<Integer> purchasesList;
        public List<Integer> subscriptionsList;

        public Output(List<User> users, List<Integer> purchases, List<Integer> subscriptions  ) {
            this.usersList = users;
            this.purchasesList = purchases;
            this.subscriptionsList = subscriptions;
        }
    }

    @Override
    public Output runOnServer(Request request, Server server, ConnectionToClient client) throws Exception {
        Input input = request.getInput(Input.class);

        List<User> users = User.findAllUsers();
        List<Integer> purchases = new ArrayList<>();
        List<Integer> subscriptions = new ArrayList<>();
        for (User user : users) {
            purchases.add(Purchase.countForUser(user.getId()));
            subscriptions.add(Subscription.countForUser(user.getId()));
        }

        return new Output(users,purchases,subscriptions);
    }
}