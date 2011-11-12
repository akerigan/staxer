package comtech.swm.servlet.filter;

import comtech.util.StringUtils;

import javax.servlet.*;
import java.io.IOException;

/**
 * User: Голубкова Анна
 * Date and time: 16.08.11 13:42
 */
public class EncodingIndexformFilter implements Filter {

    public static final String DEFAULT_ENCODING = "utf-8";

    private String encoding = DEFAULT_ENCODING;

    public void init(FilterConfig filterConfig) throws ServletException {
        if (filterConfig.getInitParameter("encoding") != null) {
            this.encoding = filterConfig.getInitParameter("encoding");
        }
    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        String cp = servletRequest.getParameter("charset");
        if (!StringUtils.isEmpty(cp)) {
            servletRequest.setCharacterEncoding(cp);
            //String o2=servletRequest.getParameter("origin-city-name");
        } else {
            servletRequest.setCharacterEncoding(encoding);
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    public void destroy() {
        // do nothing
    }
}
