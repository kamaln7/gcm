package gcm.server.jobs;

import gcm.server.Server;

public class SubscriptionExpiryNotification extends Job {
    @Override
    public long getInterval() {
        return 10;
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


        return null;
    }
}
