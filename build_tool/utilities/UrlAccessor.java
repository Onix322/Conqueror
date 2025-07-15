package build_tool.utilities;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * UrlAccessor is a utility class that provides methods to access URLs and retrieve their content.
 * It uses a singleton pattern to ensure that only one instance of the class is created.
 * The class provides a method to open a URL and return an InputStream for reading its content.
 */
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

    /**
     * Opens a URL and returns an InputStream to read its content.
     * If an IOException occurs, it returns a null InputStream.
     *
     * @param url the URL to open
     * @return an InputStream for reading the content of the URL, or null if an error occurs
     */
    public InputStream open(URL url) {
        try {
            URLConnection urlConnection = url.openConnection();
            return urlConnection.getInputStream();
        } catch (IOException e) {
            return InputStream.nullInputStream();
        }
    }
}
