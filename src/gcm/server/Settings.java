package gcm.server;

public class Settings {
    public String connectionString, filesPath;
    public Integer port;

    public Settings(Integer port, String connectionString, String filesPath) {
        this.port = port;
        this.connectionString = connectionString;
        this.filesPath = filesPath;
    }
}
