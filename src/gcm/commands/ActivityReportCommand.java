package gcm.commands;

import gcm.database.models.*;
import gcm.server.Server;
import ocsf.server.ConnectionToClient;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ActivityReportCommand implements Command {

    public static class Input extends gcm.commands.Input {
        public Date from, to;
        public int cityId;

        public Input(Date fromDate, Date toDate, int cityId) {
            this.from = fromDate;
            this.to = toDate;
            this.cityId = cityId;
        }
    }

    public class Output extends gcm.commands.Output {

        public List<City> cities;
        public List<Integer> maps;
        public List<Integer> purchases;
        public List<Integer> subscriptions;
        public List<Integer> renewals;
        public List<Integer> views;
        public List<Integer> downloads;

        public Output(List<City> cities, List<Integer> maps, List<Integer> purchases, List<Integer> subscriptions,
                      List<Integer> renewals, List<Integer> views, List<Integer> downloads) {
            this.cities = cities;
            this.maps = maps;
            this.purchases = purchases;
            this.subscriptions = subscriptions;
            this.renewals = renewals;
            this.views = views;
            this.downloads = downloads;
        }
    }

    @Override
    public Output runOnServer(Request request, Server server, ConnectionToClient client) throws Exception {
        Input input = request.getInput(Input.class);
        List<City> cities = new ArrayList<>();
        if (input.cityId == -1)
            cities = City.findAll();
        else {
            cities.add(City.findById(input.cityId));
        }

        List<Integer> maps = new ArrayList<>();
        List<Integer> purchases = new ArrayList<>();
        List<Integer> subscriptions = new ArrayList<>();
        List<Integer> renewals = new ArrayList<>();
        List<Integer> views = new ArrayList<>();
        List<Integer> downloads = new ArrayList<>();

        for (City city : cities) {
            maps.add(Map.countAllForCities(city.getId()));
            purchases.add(Purchase.countByPeriod(city.getId(), input.from, input.to));
            subscriptions.add(Subscription.countByPeriod(city.getId(), input.from, input.to));
            renewals.add(Subscription.countRenewals(city.getId(), input.from, input.to));
            views.add(View.countByPeriod(city.getId(), input.from, input.to));
            downloads.add(Download.countByPeriod(city.getId(), input.from, input.to));
        }
        return new Output(cities, maps, purchases, subscriptions, renewals, views, downloads);
    }
}
