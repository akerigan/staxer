package org.staxer.util.http.response;

import org.staxer.util.http.helper.HttpHelper;

import javax.servlet.ServletException;
import java.io.IOException;

/**
 * User: Vlad Vinichenko (akerigan@gmail.com)
 * Date: 23.10.2009
 * Time: 10:22:35
 */
public interface HttpResponse {

    public void respond(HttpHelper httpHelper) throws IOException, ServletException;

}
