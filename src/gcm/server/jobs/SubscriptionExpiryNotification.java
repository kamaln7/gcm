package gcm.server.jobs;

import gcm.database.models.Subscription;
import gcm.server.Server;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * A job that runs once every 24 hours and emails users with subscriptions that end in 3 days or sooner
 */
public class SubscriptionExpiryNotification extends Job {
    private static final Integer ExpiryDays = 3;

    @Override
    public long getInterval() {
        return 24 * 60 * 60;
    }

    @Override
    public String getName() {
        return "subscription_expiry";
    }

    public SubscriptionExpiryNotification(Server server) {
        super(server);
    }


    @Override
    public Void call() throws Exception {
        // get expiring subscriptions and group by user id
        List<Subscription> subscriptionsExpiringSoon = Subscription.findExpiringInNDays(ExpiryDays);
        if (subscriptionsExpiringSoon.isEmpty()) {
            server.getChatIF().display("No subscriptions expiring soon");
            logRun();
            return null;
        }

        // group by user id
        Map<Integer, List<Subscription>> userSubscriptions = subscriptionsExpiringSoon
                .stream()
                .collect(Collectors.groupingBy(Subscription::getUserId));

        // go over list and notify users
        for (Map.Entry<Integer, List<Subscription>> entry : userSubscriptions.entrySet()) {
            Integer userId = entry.getKey();
            List<Subscription> subscriptions = entry.getValue();

            server.getChatIF().displayf(
                    "User id=%d has %d subscriptions (cities: %s) expiring in less than %d days. Sending email.",
                    userId,
                    subscriptions.size(),
                    subscriptions.stream().map(s -> String.valueOf(s.getCityId())).collect(Collectors.joining(", ")),
                    ExpiryDays
            );

            // mark the subscription as "notification sent" so we don't keep sending emails
            Subscription.updateSentExpiryNotification(
                    subscriptions
                            .stream()
                            .map(Subscription::getId)
                            .collect(Collectors.toSet()),
                    true
            );
        }

        logRun();
        return null;
    }
}
