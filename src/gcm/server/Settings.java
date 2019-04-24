package gcm.server;

public class Settings {
    public String connectionString;
    public Integer port;

    public Settings(Integer port, String connectionString) {
        this.port = port;
        this.connectionString = connectionString;
    }
}
