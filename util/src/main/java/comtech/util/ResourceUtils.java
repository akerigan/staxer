package comtech.util;

import comtech.util.servlet.helper.HttpHelper;
import org.apache.commons.io.IOUtils;
import org.eclipse.jetty.client.ContentExchange;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.http.HttpStatus;

import javax.servlet.ServletContext;
import java.io.*;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterOutputStream;

/**
 * User: Vlad Vinichenko (akerigan@gmail.com)
 * Date: 24.12.2009
 * Time: 8:11:50
 */
public class ResourceUtils {

    // class resources

    public static String getResourceAsString(Class cls, String resourceName) throws IOException {
        return inputStreamAsString(cls.getResourceAsStream(resourceName));
    }

    public static byte[] getResourceAsBytes(Class cls, String classpath) throws IOException {
        return inputStreamAsBytes(cls.getResourceAsStream(classpath));
    }

    public static Reader getResourceReader(Class cls, String resourceName) throws IOException {
        return new InputStreamReader(getResourceInputStream(cls, resourceName), "UTF-8");
    }

    public static InputStream getResourceInputStream(Class cls, String resourceName) throws IOException {
        InputStream stream = cls.getResourceAsStream(resourceName);
        if (stream != null) {
            return stream;
        } else {
            return null;
        }
    }

    // web resources

    public static String getWebResourceAsString(
            ServletContext servletContext, String path
    ) throws IOException {
        return inputStreamAsString(getWebResourceInputStream(servletContext, path));
    }

    public static byte[] getWebResourceAsBytes(
            ServletContext servletContext, String path
    ) throws IOException {
        return inputStreamAsBytes(getWebResourceInputStream(servletContext, path));
    }

    public static InputStream getWebResourceInputStream(
            HttpHelper httpHelper, String path
    ) throws IOException {
        if (!StringUtils.isEmpty(path)) {
            String normalizedPath = StringUtils.normalizePath(path);
            if (normalizedPath != null) {
                return new FileInputStream(new File(
                        httpHelper.getRealPath(normalizedPath)
                ));
            }
        }
        return null;
    }

    public static InputStream getWebResourceInputStream(
            ServletContext servletContext, String path
    ) throws IOException {
        if (!StringUtils.isEmpty(path)) {
            String normalizedPath = StringUtils.normalizePath(path);
            if (normalizedPath != null) {
                return new FileInputStream(new File(
                        servletContext.getRealPath(normalizedPath)
                ));
            }
        }
        return null;
    }

    public static String inputStreamAsString(
            InputStream inputStream
    ) throws IOException {
        if (inputStream != null) {
            InputStreamReader reader = new InputStreamReader(inputStream, "UTF-8");
            StringWriter writer = new StringWriter();
            IOUtils.copy(reader, writer);
            writer.flush();
            writer.close();
            reader.close();
            return writer.toString();
        }
        return null;
    }

    public static byte[] inputStreamAsBytes(
            InputStream inputStream
    ) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        IOUtils.copy(inputStream, baos);
        inputStream.close();
        return baos.toByteArray();
    }

    public static byte[] getStreamData(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        IOUtils.copy(inputStream, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    public static void deflate(byte[] bytes, OutputStream out) throws IOException {
        DeflaterOutputStream dOut = new DeflaterOutputStream(out, new Deflater(9));
        dOut.write(bytes);
        dOut.flush();
        dOut.close();
    }

    public static byte[] deflate(String s) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        deflate(s.getBytes("UTF-8"), out);
        return out.toByteArray();
    }

    public static byte[] deflate(byte[] bytes) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        deflate(bytes, out);
        return out.toByteArray();
    }

    public static byte[] deflate(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        DeflaterOutputStream dOut = new DeflaterOutputStream(out, new Deflater(9));
        IOUtils.copy(in, dOut);
        dOut.close();
        return out.toByteArray();
    }

    public static byte[] inflate(byte[] bytes) throws IOException {
        return inflate(new ByteArrayInputStream(bytes));
    }

    public static byte[] inflate(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        InflaterOutputStream inflaterOutputStream = new InflaterOutputStream(out);
        IOUtils.copy(in, inflaterOutputStream);
        in.close();
        inflaterOutputStream.close();
        return out.toByteArray();
    }

    public static boolean isDeflated(byte[] bytes) {
        return bytes != null && bytes.length > 2 && bytes[0] == 120 && bytes[1] == -38;
    }

    public static String getUrlContentAsString(
            String url, String httpUser, String httpPassword
    ) throws Exception {
        ContentExchange contentExchange = getUrlContentExchange(url, httpUser, httpPassword);
        if (contentExchange != null) {
            return contentExchange.getResponseContent();
        } else {
            return null;
        }
    }

    public static byte[] getUrlContentAsBytes(
            String url, String httpUser, String httpPassword
    ) throws Exception {
        ContentExchange contentExchange = getUrlContentExchange(url, httpUser, httpPassword);
        if (contentExchange != null) {
            return contentExchange.getResponseContentBytes();
        } else {
            return null;
        }
    }

    private static ContentExchange getUrlContentExchange(
            String url, String httpUser, String httpPassword
    ) throws Exception {
        HttpClient httpClient = new HttpClient();
        httpClient.setConnectorType(HttpClient.CONNECTOR_SOCKET);
        httpClient.setConnectTimeout(10000);
        httpClient.setIdleTimeout(30000);
        ContentExchange contentExchange = new ContentExchange(true);
        contentExchange.setURL(url);
        contentExchange.setMethod("GET");
        if (!StringUtils.isEmpty(httpUser) && !StringUtils.isEmpty(httpPassword)) {
            contentExchange.addRequestHeader("Authorization", SecurityUtils.getBasicHttpAuth(
                    httpUser, httpPassword
            ));
        }
        httpClient.start();
        httpClient.send(contentExchange);
        contentExchange.waitForDone();
        httpClient.stop();
        int statusCode = contentExchange.getResponseStatus();
        if (statusCode != HttpStatus.OK_200) {
            return contentExchange;
        } else {
            return null;
        }
    }

}

