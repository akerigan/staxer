package org.staxer.util.http;

import javax.servlet.*;
import java.io.IOException;

/**
 * User: Ванин Борис
 *
 * @Version: 2.0
 * Date: 2011-08-29 11:42 (Moscow/Europe)
 */
public class EncodingFilter2 implements Filter {

    public static final String DEFAULT_ENCODING = "UTF-8";

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
