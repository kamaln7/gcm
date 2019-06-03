package gcm.server.bin;

import com.beust.jcommander.Parameter;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Args {
    @Parameter
    private List<String> parameters = new ArrayList<>();

    @Parameter(names = "--connectionString", description = "JDBC MySQL connection string")
    private String connectionString = "jdbc:mysql://gcm:928c43b1f9d4928c43b1f9d4@gcm.kar.sh/gcm?useSSL=false";

    @Parameter(names = "--port", description = "Port to listen on")
    private Integer port = 5000;

    @Parameter(names = "--filesPath", description = "Path to folder with files")
    private String filesPath = Paths.get(
            Paths.get(System.getProperty("user.home")).toString(),
            "Desktop",
            "gcm-files"
    ).toString();

    public String getConnectionString() {
        return connectionString;
    }

    public List<String> getParameters() {
        return parameters;
    }

    public Integer getPort() {
        return port;
    }

    public String getFilesPath() {
        return filesPath;
    }
}