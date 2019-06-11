package gcm.commands;

import gcm.database.models.User;
import gcm.server.Server;
import ocsf.server.ConnectionToClient;

public class UpdateUserCommand implements Command {
    public static class Input extends gcm.commands.Input {
        int user_id;
        public String email;
        public String phone;
        public String first_name;
        public String last_name;
        public String ccNumber;
        public String ccCVV;
        public Integer ccMonth;
        public Integer ccYear;

        public Input(int user_id, String email, String phone, String first_name, String last_name, String ccNumber, String ccCVV, Integer ccMonth, Integer ccYear) {
            this.user_id = user_id;
            this.email = email;
            this.phone = phone;
            this.first_name = first_name;
            this.last_name = last_name;
            this.ccNumber = ccNumber;
            this.ccCVV = ccCVV;
            this.ccMonth = ccMonth;
            this.ccYear = ccYear;
        }
    }

    public static class Output extends gcm.commands.Output {
        public User user;

        public Output(User user) {
            this.user = user;
        }
    }

    @Override
    public Output runOnServer(Request request, Server server, ConnectionToClient client) throws Exception {
        Input input = request.getInput(Input.class);

        User.updateUser(input.user_id, input.email, input.phone, input.first_name, input.last_name, input.ccNumber,input.ccCVV,input.ccMonth,input.ccYear);
        User user=User.findById(input.user_id);
        return new Output(user);
    }
}
