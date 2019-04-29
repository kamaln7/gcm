package gcm.common;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GsonSingleton {
    private static GsonSingleton instance = null;
    public Gson gson;

    private GsonSingleton() {
        this.gson = new GsonBuilder().create();
    }

    public static GsonSingleton GsonSingleton() {
        if (instance == null) {
            instance = new GsonSingleton();
        }

        return instance;
    }
}