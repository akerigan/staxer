package comtech.util.servlet;

import comtech.util.StringUtils;

import javax.servlet.*;
import java.io.IOException;
import java.util.Map;

/**
 * User: Ванин Борис
 *
 * @Version: 1.0
 * Date: 03.10.2007 13:59:00
 */
public class EncodingFilter implements Filter {

    public static final String DEFAULT_ENCODING = "utf-8";

    private String encoding = DEFAULT_ENCODING;

    public void init(FilterConfig filterConfig) throws ServletException {
        if (filterConfig.getInitParameter("encoding") != null) {
            this.encoding = filterConfig.getInitParameter("encoding");
        }
    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        servletRequest.setCharacterEncoding(encoding);
        filterChain.doFilter(servletRequest, servletResponse);
    }

    public void destroy() {
        // do nothing
    }
}
