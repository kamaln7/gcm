package gcm.client.bin;

import com.beust.jcommander.Parameter;

import java.util.ArrayList;
import java.util.List;

public class Args {
    @Parameter
    private List<String> parameters = new ArrayList<>();

    @Parameter(names = "--host", description = "Server host to connect to")
    private String host = "127.0.0.1";
    @Parameter(names = "--port", description = "Server port to connect to")
    private Integer port = 5000;

    public List<String> getParameters() {
        return parameters;
    }

    public Integer getPort() {
        return port;
    }

    public String getHost() {
        return host;
    }
}