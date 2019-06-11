package gcm.server.jobs;

import gcm.database.models.Subscription;
import gcm.server.Server;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SubscriptionExpiryNotification extends Job {
    private static final Integer ExpiryDays = 3;

    @Override
    public long getInterval() {
        return 3600;
    }

    @Override
    public String getName() {
        return "subscription_expiry";
    }

    public SubscriptionExpiryNotification(Server server) {
        super(server);
    }

    /**
     * Computes a result, or throws an exception if unable to do so.
     *
     * @return computed result
     * @throws Exception if unable to compute a result
     */
    @Override
    public Void call() throws Exception {
        // get expiring subscriptions and group by user id
        List<Subscription> subscriptionsExpiringSoon = Subscription.findExpiringInNDays(ExpiryDays);
        if (subscriptionsExpiringSoon.isEmpty()) {
            server.getChatIF().display("No subscriptions expiring soon");
            return null;
        }

        // group by user id
        Map<Integer, List<Subscription>> userSubscriptions = subscriptionsExpiringSoon
                .stream()
                .collect(Collectors.groupingBy(Subscription::getUserId));

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
