package jp.co.thcomp.http_abstract_layer;

import java.io.IOException;
import java.io.OutputStream;

public class StringRequestParameter extends RequestParameter {
    private String mValue;

    public StringRequestParameter(String name, String value) {
        super(name);
        mValue = value;
    }

    @Override
    public boolean output(OutputStream stream) {
        try {
            stream.write(mValue.getBytes());
        } catch (IOException e) {
        }

        return true;
    }

    @Override
    public String getMimeType() {
        return "text/plain; charset=utf-8";
    }
}
