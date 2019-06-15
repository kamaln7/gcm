package gcm.server.jobs;

import gcm.database.models.ServerJob;
import gcm.server.Server;

import java.sql.SQLException;
import java.util.concurrent.Callable;

/**
 * A job describes a task that runs on the server.
 */
public abstract class Job implements Callable<Void> {
    /**
     * @return Interval in seconds. Set to 0 or negative to stop scheduling.
     */
    public abstract long getInterval();

    /**
     * @return A unique identifier for the job
     */
    public abstract String getName();

    /**
     * Inserts a "run" into the database
     *
     * @throws SQLException
     * @throws ServerJob.NotFound
     */
    public void logRun() throws SQLException, ServerJob.NotFound {
        (new ServerJob(getName())).insert();
    }

    public Server server;

    public Job(Server server) {
        this.server = server;
    }
}
