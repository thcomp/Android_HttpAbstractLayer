package jp.co.thcomp.http_abstract_layer;

import java.io.InputStream;
import java.util.List;

public interface Response {
    public static interface Header{
        public String getName();
        public String getValue();
    }

    public String getRequestUrl();
    public int getStatusCode();
    public String getReasonPhrase();
    public String getMimeType();
    public List<Header> getHeaders(String name);
    public InputStream getEntity();
    public Exception getException();
    public void setException(Exception exception);
}
