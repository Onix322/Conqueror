package loader.utilities;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class UrlAccessor {

    private UrlAccessor() {
    }

    private static class Holder {
        private static UrlAccessor INSTANCE = null;
    }

    public static synchronized void init() {
        if (UrlAccessor.Holder.INSTANCE == null) {
            UrlAccessor.Holder.INSTANCE = new UrlAccessor();
        }
    }

    public static UrlAccessor getInstance() {
        return UrlAccessor.Holder.INSTANCE;
    }

    public InputStream open(URL url) {
        try {
            URLConnection urlConnection = url.openConnection();
            return urlConnection.getInputStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
