package org.staxer.util.staxer.client;

import org.apache.commons.httpclient.methods.RequestEntity;

import java.io.IOException;
import java.io.OutputStream;

/**
 * User: Vlad Vinichenko (akerigan@gmail.com)
 * Date: 14.09.2009
 * Time: 18:29:09
 */
public class ByteArrayRequestEntity implements RequestEntity {

    private byte[] data;
    private String mime;

    public ByteArrayRequestEntity(byte[] data, String mime) {
        this.data = data;
        this.mime = mime;
    }

    public boolean isRepeatable() {
        return false;
    }

    public void writeRequest(OutputStream outputStream) throws IOException {
        outputStream.write(data);
    }

    public long getContentLength() {
        return data.length;
    }

    public String getContentType() {
        return mime;
    }
}
