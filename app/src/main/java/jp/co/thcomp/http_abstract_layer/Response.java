package jp.co.thcomp.http_abstract_layer;

import java.io.InputStream;
import java.util.List;

public interface Response {
    public interface Header{
        String getName();
        String getValue();
    }

    String getRequestUrl();
    int getStatusCode();
    String getReasonPhrase();
    String getMimeType();
    List<Header> getHeaders(String name);

    /**
     * 受信したエンティティをContent-Encodingに記載されたコード方法でデコードした状態で返却
     * Content-Encodingに記載されたエンコード方法が未知の場合、nullを返却
     * エンコードされていない場合、そのまま返却
     * @return
     */
    InputStream getEntity();

    /**
     * 受信したエンティティをそのまま返却
     * @return InputStream
     */
    InputStream getRawEntity();
    Exception getException();
    void setException(Exception exception);
    void close();
}
