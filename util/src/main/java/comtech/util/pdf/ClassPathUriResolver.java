package comtech.util.pdf;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;
import java.io.InputStream;
import java.net.URL;

/**
 * User: Vlad Vinichenko (akerigan@gmail.com)
 * Date: 21.01.2010
 * Time: 13:30:32
 */
public class ClassPathUriResolver implements URIResolver {

    /**
     * The protocol name for the servlet context URIs.
     */
    public static final String CLASS_PATH_PROTOCOL = "classpath:";

    private Class resolvingClass;

    public ClassPathUriResolver(Class resolvingClass) {
        if (resolvingClass == null) {
            throw new IllegalStateException("Resolving class in null");
        }
        this.resolvingClass = resolvingClass;
    }

    public Source resolve(String href, String base) throws TransformerException {
        if (href.startsWith(CLASS_PATH_PROTOCOL)) {
            return resolveClassPathURI(href.substring(CLASS_PATH_PROTOCOL.length()));
        } else {
            return null;
        }
    }

    /**
     * Resolves the "classpath:" URI.
     *
     * @param path the path part after the protocol (should start with a "/")
     * @return the resolved Source or null if the resource was not found
     * @throws TransformerException if no URL can be constructed from the path
     */
    protected Source resolveClassPathURI(String path) throws TransformerException {
        while (path.startsWith("//")) {
            path = path.substring(1);
        }
        InputStream in = this.resolvingClass.getResourceAsStream(path);
        if (in != null) {
            URL url = this.resolvingClass.getResource(path);
            if (url != null) {
                return new StreamSource(in, url.toExternalForm());
            } else {
                return new StreamSource(in);
            }
        } else {
            throw new TransformerException("Resource does not exist. \"" + path
                    + "\" is not accessible through the class " + resolvingClass.getName() + ".");
        }
    }
}
