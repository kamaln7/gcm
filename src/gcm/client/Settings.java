package gcm.client;

// Settings for the Client
// Host and Port are the server to connect to
public class Settings {
    public String host;
    public Integer port;

    public Settings(String host, Integer port) {
        this.host = host;
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }
}
