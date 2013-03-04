package org.staxer.util.http.helper;

import org.staxer.util.StringUtils;
import org.staxer.util.date.DateTimeUtils;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.servlet.ServletRequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.XMLGregorianCalendar;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.*;

/**
 * @author Vlad Vinichenko (akerigan@gmail.com)
 * @since 2011-01-26 18:09 (Europe/Moscow)
 */
public class HttpRequestHelper {

    private static final String STORED_REQUEST_PARAMETERS = "storedRequestParameters";
    private static Logger log = LoggerFactory.getLogger(HttpHelper.class);
    private static Set<String> deniedParametersNames;

    static {
        deniedParametersNames = new HashSet<String>();
        deniedParametersNames.add("password");
    }

    protected HttpServletRequest request;
    protected HttpSession session;
    private Map<String, String> stringParams = new LinkedHashMap<String, String>();
    private Map<String, FileItem> binaryParams = new LinkedHashMap<String, FileItem>();
    private Map<String, Cookie> cookies = new LinkedHashMap<String, Cookie>();
    private boolean sessionCreated;
    private Set<String> requestParametersNames;

    public HttpRequestHelper(HttpServletRequest request) {
        this.request = request;
        if (request.getContentType() != null && request.getContentType().startsWith("multipart/form-data")) {
            ServletFileUpload fileUpload = new ServletFileUpload(new DiskFileItemFactory());
            try {
                for (Object o : fileUpload.parseRequest(new ServletRequestContext(request))) {
                    FileItem fileItem = (FileItem) o;
                    binaryParams.put(fileItem.getFieldName(), fileItem);
                }
            } catch (FileUploadException e) {
                log.error("", e);
            }
        } else {
            Enumeration en = request.getParameterNames();
            while (en.hasMoreElements()) {
                String name = (String) en.nextElement();
                String value = request.getParameter(name);
                if (value != null) {
                    stringParams.put(name, value.replaceAll("<", "&lt;").replaceAll(">", "&gt;"));
                }
            }
        }

        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                cookies.put(cookie.getName(), cookie);
            }
        }

        session = request.getSession(false);
        if (session == null) {
            session = request.getSession();
            sessionCreated = true;
        }
    }

    public Map<String, String> getRequestParametersMap() {
        Map<String, String> result = new TreeMap<String, String>();
        for (String name : getRequestParametersNames()) {
            if (deniedParametersNames.contains(name)) {
                result.put(name, "***");
            } else {
                String value = getSafeRequestParameter(name);
                if (value != null) {
                    result.put(name, value);
                }
            }
        }
        return result;
    }

    public String getSafeRequestParameter(String name, String defaultValue) {
        String value = getRequestParameter(name, true);
        return value != null ? value : defaultValue;
    }

    public String getSafeRequestParameter(String name) {
        return getRequestParameter(name, true);
    }

    public String getRequestParameter(String name) {
        return getRequestParameter(name, false);
    }

    public String getRequestParameter(String name, boolean preventXSS) {
        if (name != null) {
            String value = stringParams.get(name);
            if (value == null) {
                FileItem fileItem = binaryParams.get(name);
                if (fileItem != null && fileItem.isFormField()) {
                    try {
                        value = fileItem.getString("UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        return Base64.encodeBase64String(fileItem.get());
                    }
                }
            }
            if (preventXSS) {
                return StringUtils.preventXSS(StringUtils.notEmptyTrimmedElseNull(value));
            } else {
                return StringUtils.notEmptyTrimmedElseNull(value);
            }
        } else {
            return null;
        }
    }

    public String getRequestParameter(String name, String defaultValue) {
        String value = getRequestParameter(name, true);
        return value != null ? value : defaultValue;
    }

    public String getBoRequestParameter(String name) {
        return request.getParameter(name);
    }

    public Integer getIntInstanceRequestParameter(String name) {
        return StringUtils.parseIntInstance(getSafeRequestParameter(name), null);
    }

    public Integer getIntInstanceRequestParameter(String name, Integer defaultValue) {
        return StringUtils.parseIntInstance(getSafeRequestParameter(name), defaultValue);
    }

    public Long getLongInstanceRequestParameter(String name) {
        return StringUtils.parseLongInstance(getSafeRequestParameter(name), null);
    }

    public Long getLongInstanceRequestParameter(String name, Long defaultValue) {
        return StringUtils.parseLongInstance(getSafeRequestParameter(name), defaultValue);
    }

    public int getIntRequestParameter(String name) {
        return StringUtils.parseInt(getSafeRequestParameter(name), 0);
    }

    public int getIntRequestParameter(String name, int defaultValue) {
        return StringUtils.parseInt(getSafeRequestParameter(name), defaultValue);
    }

    public float getFloatRequestParameter(String name) {
        return StringUtils.parseFloat(getSafeRequestParameter(name), 0);
    }

    public Float getFloatInstanceRequestParameter(String name) {
        return StringUtils.parseFloatInstance(getSafeRequestParameter(name), null);
    }

    public float getFloatRequestParameter(String name, float defaultValue) {
        return StringUtils.parseFloat(getSafeRequestParameter(name), defaultValue);
    }

    public double getDoubleRequestParameter(String name) {
        return StringUtils.parseDouble(getSafeRequestParameter(name), 0);
    }

    public Double getDoubleInstanceRequestParameter(String name) {
        return StringUtils.parseDoubleInstance(getSafeRequestParameter(name), null);
    }

    public double getDoubleRequestParameter(String name, double defaultValue) {
        return StringUtils.parseDouble(getSafeRequestParameter(name), defaultValue);
    }

    public boolean getBooleanRequestParameter(String name) {
        return StringUtils.parseBoolean(getSafeRequestParameter(name), false);
    }

    public Boolean getBooleanInstanceRequestParameter(String name) {
        return StringUtils.parseBooleanInstance(getSafeRequestParameter(name), null);
    }

    public boolean getBooleanRequestParameter(String name, boolean defaultValue) {
        return StringUtils.parseBoolean(getSafeRequestParameter(name), defaultValue);
    }

    public BigDecimal getBigDecimalRequestParameter(String name) {
        return StringUtils.parseBigDecimal(getSafeRequestParameter(name), null);
    }

    public Character getCharacterRequestParameter(String name) {
        return StringUtils.parseCharacter(getSafeRequestParameter(name), null);
    }

    public Date getDateRequestParameter(String name) {
        return DateTimeUtils.parseDate(getSafeRequestParameter(name));
    }

    public Date getDateRequestParameter(String name, Date defaultDate) {
        Date result = DateTimeUtils.parseDate(getSafeRequestParameter(name));
        if (result != null) {
            return result;
        } else {
            return defaultDate;
        }
    }

    public Date getDateRequestParameter(String name, String format) {
        return DateTimeUtils.parseDate(getSafeRequestParameter(name), format);
    }

    public XMLGregorianCalendar getXMLGregorianCalendarRequestParameter(String name) {
        try {
            return DateTimeUtils.parseXMLGregorianCalendar(getSafeRequestParameter(name));
        } catch (DatatypeConfigurationException e) {
            log.error("", e);
            return null;
        }
    }

    public List<String> getRequestParameters(String name) {
        String[] values = request.getParameterValues(name);
        if (values != null) {
            List<String> result = new ArrayList<String>();
            for (int i = 0, length = values.length; i < length; ++i) {
                result.add(StringUtils.notEmptyElseNull(values[i]));
            }
            return result;
        } else {
            return null;
        }
    }

    public String[] getSafeRequestParameters(String name) {
        String[] values = request.getParameterValues(name);
        if (values != null) {
            for (int i = 0, length = values.length; i < length; ++i) {
                values[i] = StringUtils.preventXSS(StringUtils.notEmptyElseNull(values[i]));
            }
        }
        return values;
    }

    public int[] getIntRequestParameters(String name) {
        return StringUtils.parseInts(getSafeRequestParameters(name), 0);
    }

    public int[] getIntRequestParameters(String name, int defaultValue) {
        return StringUtils.parseInts(getSafeRequestParameters(name), defaultValue);
    }

    public double[] getDoubleRequestParameters(String name) {
        return StringUtils.parseDoubles(getSafeRequestParameters(name), 0);
    }

    public double[] getDoubleRequestParameters(String name, double defaultValue) {
        return StringUtils.parseDoubles(getSafeRequestParameters(name), defaultValue);
    }

    public boolean[] getBooleanRequestParameters(String name) {
        return StringUtils.parseBooleans(getSafeRequestParameters(name), false);
    }

    public boolean[] getBooleanRequestParameters(String name, boolean defaultValue) {
        return StringUtils.parseBooleans(getSafeRequestParameters(name), defaultValue);
    }

    public byte[] getByteArrayRequestParameter(String name) {
        if (binaryParams.containsKey(name)) {
            return binaryParams.get(name).get();
        } else {
            String value = getSafeRequestParameter(name);
            if (value != null) {
                return value.getBytes();
            } else {
                return null;
            }
        }
    }

    public boolean isRequestParameterNotEmpty(String name) {
        return getSafeRequestParameter(name) != null;
    }

    public boolean isRequestParameterExists(String name) {
        return getRequestParametersNames().contains(name) && !StringUtils.isEmpty(getSafeRequestParameter(name));
    }

    public Date[] getDateRequestParameters(String name, String format) {
        try {
            return DateTimeUtils.parseDates(getSafeRequestParameters(name), format);
        } catch (ParseException e) {
            log.error("", e);
            return null;
        }
    }

    public Set<String> getRequestParametersNames() {
        if (requestParametersNames == null) {
            requestParametersNames = new HashSet<String>();
            Enumeration en = request.getParameterNames();
            while (en.hasMoreElements()) {
                requestParametersNames.add((String) en.nextElement());
            }
            requestParametersNames.addAll(binaryParams.keySet());
        }
        return requestParametersNames;
    }

    protected void addRequestParameter(String name, String value) {
        stringParams.put(name, value);
    }

    public String getRequestParametersString() {
        return getRequestParametersString(null);
    }

    public String getRequestParametersString(
            List<String> additionalParams
    ) {
        StringBuilder result = new StringBuilder();
        for (String name : getRequestParametersNames()) {
            if (result.length() == 0) {
                result.append("?");
            } else {
                result.append("&");
            }
            result.append(name);
            result.append("=");
            result.append(getRequestParameter(name));
        }
        if (additionalParams != null && !additionalParams.isEmpty()) {
            boolean odd = true;
            for (String additionalParam : additionalParams) {
                if (odd) {
                    if (result.length() == 0) {
                        result.append("?");
                    } else {
                        result.append("&");
                    }
                    result.append(additionalParam);
                    odd = false;
                } else {
                    result.append("=");
                    result.append(additionalParam);
                    odd = true;
                }
            }
        }
        return result.toString();
    }

    @SuppressWarnings({"unchecked"})
    public <T> T getRequestAttribute(String name) {
        return (T) request.getAttribute(name);
    }

    public boolean isRequestAttributeExists(String name) {
        return request.getAttribute(name) != null;
    }

    public void setRequestAttribute(String name, Object value) {
        request.setAttribute(name, value);
    }

    public List<String> getRequestAttributesNames() {
        List<String> result = new ArrayList<String>();
        Enumeration attributeNames = request.getAttributeNames();
        while (attributeNames.hasMoreElements()) {
            result.add((String) attributeNames.nextElement());
        }
        return result;
    }

    public String getRequestHeader(String name) {
        return request.getHeader(name);
    }

    public int getIntRequestHeader(String name) {
        return StringUtils.parseInt(getRequestHeader(name), 0);
    }

    public List<String> getRequestHeadersNames() {
        List<String> result = new LinkedList<String>();
        Enumeration names = request.getHeaderNames();
        while (names.hasMoreElements()) {
            result.add((String) names.nextElement());
        }
        return result;
    }

    public Cookie getCookie(String name) {
        return cookies.get(name);
    }

    public Set<String> getCookiesNames() {
        return cookies.keySet();
    }

    public boolean isSessionCreated() {
        return sessionCreated;
    }

    public void setSessionCreated(boolean sessionCreated) {
        this.sessionCreated = sessionCreated;
    }

    @SuppressWarnings({"unchecked"})
    public <T> T getSessionAttribute(String name) {
        return (T) session.getAttribute(name);
    }

    public void setSessionAttribute(String name, Object value) {
        session.setAttribute(name, value);
    }

    public void removeSessionAttribute(String name) {
        session.removeAttribute(name);
    }

    public void clearSession() {
        Enumeration<String> attributeNames = session.getAttributeNames();
        while (attributeNames.hasMoreElements()) {
            session.setAttribute(attributeNames.nextElement(), null);
        }
//        session.invalidate();
    }

    public boolean isSessionAttributeExists(String name) {
        return session.getAttribute(name) != null;
    }

    public List<String> getSessionAttributesNames() {
        List<String> result = new ArrayList<String>();
        Enumeration attributeNames = session.getAttributeNames();
        while (attributeNames.hasMoreElements()) {
            result.add((String) attributeNames.nextElement());
        }
        return result;
    }

    public String getServletPath() {
        return request.getServletPath();
    }

    public String getRemoteHost() {
        return request.getRemoteHost();
    }

    public String getRemoteAddr() {
        return request.getRemoteAddr();
    }

    public HttpMethod getMethod() {
        return HttpMethod.valueOf(request.getMethod().toUpperCase());
    }

    public String getContextPath() {
        return request.getContextPath();
    }

    public String getRequestURI() {
        return request.getRequestURI();
    }

    public String getRequestURL() {
        return request.getRequestURL().toString();
    }

    public String getWebappURL() {
        int serverPort = request.getServerPort();
        String scheme = request.getScheme();
        if (("http".equalsIgnoreCase(scheme) && serverPort != 80)
                || ("https".equalsIgnoreCase(scheme) && serverPort != 443)) {
            return scheme + "://" + request.getServerName() + ":" +
                    serverPort + request.getContextPath();
        } else {
            return scheme + "://" + request.getServerName() + request.getContextPath();
        }
    }

    public String getServerName() {
        return request.getServerName();
    }

    public String getQueryString() {
        return request.getQueryString();
    }

    public BufferedReader getRequestReader() throws IOException {
        return request.getReader();
    }

    public void forward(String location, HttpServletResponse response) throws IOException, ServletException {
        request.getRequestDispatcher(location).forward(request, response);
    }

    public String getPathInfo() {
        return request.getPathInfo();
    }

    public void storeRequestParameters() {
        setSessionAttribute(STORED_REQUEST_PARAMETERS, getRequestParametersMap());
    }

    public void restoreRequestParameters() {
        Map<String, String> storedRequestParameters = getSessionAttribute(STORED_REQUEST_PARAMETERS);
        if (storedRequestParameters != null) {
            session.removeAttribute(STORED_REQUEST_PARAMETERS);
            stringParams.putAll(storedRequestParameters);
        }
    }

    public XMLGregorianCalendar[] getXMLGregorianCalendarRequestParameters(String name) {
        try {
            return DateTimeUtils.parseXMLGregorianCalendars(getSafeRequestParameters(name));
        } catch (DatatypeConfigurationException e) {
            log.error("", e);
            return null;
        }
    }

    public Integer[] getIntInstanceRequestParameters(String name) {
        return StringUtils.parseIntInstances(getSafeRequestParameters(name), null);
    }

    public Double[] getDoubleInstanceRequestParameters(String name) {
        return StringUtils.parseDoubleInstances(getSafeRequestParameters(name), null);
    }

    public Float[] getFloatInstanceRequestParameters(String name) {
        return StringUtils.parseFloatInstances(getSafeRequestParameters(name), null);
    }

    public BigDecimal[] getBigDecimalRequestParameters(String name) {
        return StringUtils.parseBigDecimalInstances(getSafeRequestParameters(name), null);
    }

    public Boolean[] getBooleanInstanceRequestParameters(String name) {
        return StringUtils.parseBooleanInstances(getSafeRequestParameters(name), null);
    }

    public Character[] getCharacterRequestParameters(String name) {
        return StringUtils.parseCharacterInstances(getSafeRequestParameters(name), null);
    }

    public String createRequestParametersString() throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        if (stringParams != null && !stringParams.isEmpty()) {
            result.append('?');
            for (Map.Entry<String, String> entry : stringParams.entrySet()) {
                if (result.length() > 1) {
                    result.append('&');
                }
                result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                result.append('=');
                result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            }
        }
        return result.toString();
    }

}
