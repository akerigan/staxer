package comtech.util;

import comtech.util.servlet.helper.HttpHelper;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Vlad Vinichenko (akerigan@gmail.com)
 * @since 2010-10-27 15:10:39 (Europe/Moscow)
 */
public class LogUtils {

    public static String getMemoryDetails() {
        StringBuilder sb = new StringBuilder();
        Runtime runtime = Runtime.getRuntime();

        long maxMemory = runtime.maxMemory();
        long allocatedMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();

        sb.append("\nfree memory: ");
        sb.append(freeMemory / 1024);
        sb.append("k\nallocated memory: ");
        sb.append(allocatedMemory / 1024);
        sb.append("k\nmax memory: ");
        sb.append(maxMemory / 1024);
        sb.append("k\ntotal free memory: ");
        sb.append((freeMemory + (maxMemory - allocatedMemory)) / 1024);
        sb.append("k");
        return sb.toString();
    }

    public static String getMemoryDetails2() {
        MemoryMXBean mbean = ManagementFactory.getMemoryMXBean();
        StringBuilder sb = new StringBuilder();
        sb.append("heap memory: ");
        sb.append(mbean.getHeapMemoryUsage());
        sb.append("\nnon-heap memory: ");
        sb.append(mbean.getNonHeapMemoryUsage());
        return sb.toString();
    }

    public static String getRequestDetails(HttpHelper httpHelper, int requestId) {
        return getRequestDetails(httpHelper, requestId, null);
    }

    public static String getRequestDetails(HttpHelper httpHelper, int requestId, String requestBody) {

        StringBuilder sb = new StringBuilder();
        sb.append("----- Request ------\n");

        sb.append("ID: ").append(requestId).append("\n");

        sb.append("Client IP: ").append(httpHelper.getRemoteHost()).append("\n");
        sb.append("Method: ").append(httpHelper.getMethod()).append("\n");
        sb.append("Servlet path: ").append(httpHelper.getServletPath()).append("\n");

        Map<String, String> headers = new LinkedHashMap<String, String>();
        for (String name : httpHelper.getRequestHeadersNames()) {
            headers.put(name, httpHelper.getRequestHeader(name));
        }
        sb.append("Headers: ").append(headers).append("\n");
        sb.append("Parameters: ").append(httpHelper.getRequestParametersMap()).append("\n");

        if (httpHelper.getCookiesNames().size() > 0) {
            Map<String, String> cookies = new LinkedHashMap<String, String>();
            for (String name : httpHelper.getCookiesNames()) {
                cookies.put(name, httpHelper.getCookie(name).getValue());
            }
            sb.append("Cookies: ").append(cookies).append("\n");
        }

        if (requestBody != null && requestBody.length() > 0) {
            sb.append(requestBody).append("\n");
        }
        sb.append("--------------------");

        return sb.toString();
    }

}
