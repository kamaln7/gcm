package gcm.server.bin;

import com.beust.jcommander.Parameter;

import java.util.ArrayList;
import java.util.List;

public class Args {
    @Parameter
    private List<String> parameters = new ArrayList<>();

    @Parameter(names = "--connectionString", description = "JDBC MySQL connection string")
    private String connectionString = "jdbc:mysql://f2Z8ihkJqY:15oK6k75bd@remotemysql.com/databaseName?useSSL=false";

    @Parameter(names = "--port", description = "Port to listen on")
    private Integer port = 5000;

    public String getConnectionString() {
        return connectionString;
    }

    public List<String> getParameters() {
        return parameters;
    }


    public Integer getPort() {
        return port;
    }
}