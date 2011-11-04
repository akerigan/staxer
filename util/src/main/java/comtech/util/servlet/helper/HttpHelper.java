package comtech.util.servlet.helper;

import comtech.util.servlet.ServletContextURIResolver;
import comtech.util.servlet.response.HttpResponseContentType;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.URIResolver;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

/**
 * User: Vlad Vinichenko (akerigan@gmail.com)
 * Date: 13.01.2010
 * Time: 13:48:39
 */
public class HttpHelper extends HttpRequestHelper {

    private ServletContext servletContext;
    private HttpServletResponse response;

    public HttpHelper(ServletContext servletContext, HttpServletRequest request, HttpServletResponse response) {
        super(request);
        this.servletContext = servletContext;
        this.response = response;
    }

    public void setResponseHeader(String name, String value) {
        response.setHeader(name, value);
    }

    public void setDateResponseHeader(String name, long value) {
        response.setDateHeader(name, value);
    }

    public void addCookie(Cookie cookie) {
        response.addCookie(cookie);
    }

    public List<String> getApplicationAttributesNames() {
        List<String> result = new LinkedList<String>();
        if (servletContext != null) {
            Enumeration attributeNames = servletContext.getAttributeNames();
            while (attributeNames.hasMoreElements()) {
                result.add((String) attributeNames.nextElement());
            }
        }
        return result;
    }

    @SuppressWarnings({"unchecked"})
    public <T> T getApplicationAttribute(String name) {
        if (servletContext != null) {
            return (T) servletContext.getAttribute(name);
        } else {
            return null;
        }
    }

    public boolean isApplicationAttributeExists(String name) {
        return servletContext != null && servletContext.getAttribute(name) != null;
    }

    public void setApplicationAttribute(String name, Object value) {
        if (servletContext != null) {
            servletContext.setAttribute(name, value);
        }
    }

    public ServletOutputStream getResponseOutputStream() throws IOException {
        return response.getOutputStream();
    }

    public PrintWriter getResponseWriter() throws IOException {
        return response.getWriter();
    }

    public void setResponseStatus(int statusCode) {
        response.setStatus(statusCode);
    }

    public void setResponseCharacterEncoding(String charset) {
        response.setCharacterEncoding(charset);
    }

    public void setResponseContentType(HttpResponseContentType type) {
        if (type != null) {
            response.setContentType(type.getValue());
        }
    }

    public void setResponseNoCache() {
        response.setHeader("Expires", "Mon, 26 Jul 1997 05:00:00 GMT");
        response.setDateHeader("Last-Modified", new Date().getTime());
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "max-age=0, must-revalidate, proxy-revalidate, no-cache, no-store, private");
    }

    public void redirect(String location) throws IOException {
        response.sendRedirect(location);
    }

    public void forward(String location) throws IOException, ServletException {
        servletContext.getRequestDispatcher(location).forward(request, response);
    }

    public URIResolver getUriResolver() {
        if (servletContext != null) {
            return new ServletContextURIResolver(servletContext);
        } else {
            return null;
        }
    }

    public InputStream getApplicationResourceAsStream(String path) {
        if (servletContext != null) {
            return servletContext.getResourceAsStream(path);
        } else {
            return null;
        }
    }

    public File getAppResource(String relativePath) {
        if (servletContext != null) {
            String filePath = servletContext.getRealPath(relativePath);
            if (filePath != null) {
                return new File(filePath);
            }
        }
        return null;
    }

    public String getRealPath(String path) {
        if (servletContext != null) {
            return servletContext.getRealPath(path);
        } else {
            return null;
        }
    }


}
