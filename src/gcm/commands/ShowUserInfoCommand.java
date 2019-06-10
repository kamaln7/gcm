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

            public class UsersInfoCo{
            private int id, purchases, subscriptions;
            private String firstName, lastName, userName, email, phone;
            private Date date;

            public UsersInfoCo(User user, int purchases, int subscriptions)
            {
                this.id = user.getId();
                this.firstName = user.getFirst_name();
                this.lastName = user.getLast_name();
                this.userName = user.getUsername();
                this.email = user.getEmail();
                this.phone = user.getEmail();
                this.date = user.getCreatedAt();
                this.purchases = purchases;
                this.subscriptions = subscriptions;
            }

            public int getId() {
                return id;
            }

            public int getPurchases() {
                return purchases;
            }

            public int getSubscriptions() {
                return subscriptions;
            }

            public String getFirstName() {
                return firstName;
            }

            public String getLastName() {
                return lastName;
            }

            public String getUserName() {
                return userName;
            }

            public String getEmail() {
                return email;
            }

            public String getPhone() {
                return phone;
            }

            public Date getDate() {
                return date;
            }
        }
    public static class Input extends gcm.commands.Input {
    }

    public class Output extends gcm.commands.Output {

        public List<UsersInfoCo> userInfoList;

        public Output() {
            this.userInfoList = new ArrayList<>();
        }

        public Output(List<UsersInfoCo> userInfo) {
            this.userInfoList = userInfo;
        }
    }

    @Override
    public Output runOnServer(Request request, Server server, ConnectionToClient client) throws Exception {
        Input input = request.getInput(Input.class);

        List<User> users = User.findAllUsers();
        List<UsersInfoCo> userInfoList = new ArrayList<>();
        for (User user : users) {
            userInfoList.add(new UsersInfoCo(user, Purchase.countForUser(user.getId()), Subscription.countForUser(user.getId())));
        }

        return new Output(userInfoList);
    }
}
