package gcm.commands;

import gcm.database.models.*;
import gcm.server.Server;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import ocsf.server.ConnectionToClient;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class ActivityReportCommand implements Command {
    public static class ActivityReportCo {
        private String cityName;
        private int mapsNo, purchasesNo, subscriptionsNo, renewalsNo, viewsNo, downloadsNo;



        public  ActivityReportCo(String city, int maps, int purchases, int subscriptions, int renewals, int views, int downloads) {
            this.cityName = city;
            this.mapsNo = maps;
            this.purchasesNo = purchases;
            this.subscriptionsNo = subscriptions;
            this.renewalsNo = renewals;
            this.viewsNo = views;
            this.downloadsNo = downloads;
        }

        public String getCity() {
            return cityName;
        }

        public  int getMapsNo() {
            return mapsNo;
        }

        public  int getPurchasesNo() {
            return purchasesNo;
        }

        public  int getSubscriptionsNo() {
            return subscriptionsNo;
        }

        public  int getRenewalsNo() {
            return renewalsNo;
        }

        public  int getViewsNo() {
            return viewsNo;
        }

        public  int getDownloadsNo() {
            return downloadsNo;
        }
    }
    public static class Input extends gcm.commands.Input {
        public LocalDate from, to;



        public Input(LocalDate fromDate, LocalDate toDate)  {
            this.from = fromDate;
            this.to = toDate;
        }
    }

    public class Output extends gcm.commands.Output {

        public List<ActivityReportCo> activityReportList;

        public Output() {
            this.activityReportList = new ArrayList<>();
        }

        public Output(List<ActivityReportCo> activityReports) {
            this.activityReportList = activityReports;
        }
    }

    @Override
    public Output runOnServer(Request request, Server server, ConnectionToClient client) throws Exception {
        Input input = request.getInput(Input.class);

        List<City> cities = City.findAll();
        List<ActivityReportCo> activityReportsList = new ArrayList<>();
        for (City city : cities) {
            activityReportsList.add(new ActivityReportCo(city.getName() + ", " + city.getCountry(),
                    Map.countAllForCities(city.getId()),
                    Purchase.countByPeriod(city.getId(), input.from , input.to),
                    Subscription.countByPeriod(city.getId(), input.from , input.to),
                    Subscription.countRenewals(city.getId(), input.from , input.to),
                    0, 0 ));
        }

        return new Output(activityReportsList);
    }
}
