package jp.co.thcomp.http_abstract_layer;

import java.io.OutputStream;

public abstract class RequestParameter {
    protected String mName;

    public RequestParameter(String name){
        if(name == null){
            throw new NullPointerException();
        }
        mName = name;
    }

    public String name() {
        return mName;
    }

    /**
     * data output
     * @param stream
     * @return true: output finished, false: output not finished and need to call again
     */
    abstract public boolean output(OutputStream stream);

    abstract public String getMimeType();
}
