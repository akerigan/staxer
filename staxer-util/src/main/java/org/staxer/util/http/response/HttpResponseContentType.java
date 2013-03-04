package org.staxer.util.http.response;

/**
 * User: Vlad Vinichenko (akerigan@gmail.com)
 * Date: 14.01.2010
 * Time: 11:27:10
 */
public enum HttpResponseContentType {

    HTML("text/html; charset=utf-8"),
    XML("text/xml; charset=utf-8"),
    PLAIN("text/plain; charset=utf-8"),
    VCS("text/X-vCalendar; charset=utf-8"),
    PDF("application/pdf"),
    JS("text/javascript; charset=utf-8"),
    EXCEL("application/excel"),
    FLASH("application/x-shockwave-flash"),
    IMAGE("image/gif"),
    IMAGE2("image/png");

    private String value;

    HttpResponseContentType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
