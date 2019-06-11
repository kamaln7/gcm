package gcm.server.jobs;

import gcm.database.models.ServerJob;
import gcm.server.Server;

import java.sql.SQLException;
import java.util.concurrent.Callable;

public abstract class Job implements Callable<Void> {
    /**
     * @return Interval in seconds
     */
    public abstract long getInterval();

    public abstract String getName();

    public void logRun() throws SQLException, ServerJob.NotFound {
        (new ServerJob(getName())).insert();
    }

    public Server server;

    public Job(Server server) {
        this.server = server;
    }
}
