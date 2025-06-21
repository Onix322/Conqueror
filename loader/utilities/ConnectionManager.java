package loader.utilities;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.http.HttpRequest;

public class ConnectionManager {

    public ConnectionManager() {
    }

    private static class Holder {
        private static final ConnectionManager INSTANCE = new ConnectionManager();
    }

    public static ConnectionManager getInstance() {
        return ConnectionManager.Holder.INSTANCE;
    }

    public InputStream open(URL url) {
        try {
            URLConnection urlConnection = url.openConnection();
            System.out.println();
            urlConnection.getHeaderFields()
                    .forEach((k, v) -> System.out.println(k + " = " + v));
            return urlConnection.getInputStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
