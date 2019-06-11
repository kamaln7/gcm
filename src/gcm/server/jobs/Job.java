package gcm.server.jobs;

import gcm.server.Server;

import java.util.concurrent.Callable;

public abstract class Job implements Callable<Void> {
    /**
     * @return Interval in seconds
     */
    public abstract long getInterval();

    public abstract String getName();

    public Server server;

    public Job(Server server) {
        this.server = server;
    }
}
