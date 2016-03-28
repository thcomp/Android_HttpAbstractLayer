package jp.co.thcomp.http_abstract_layer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class StreamRequestParameter extends RequestParameter {
    private String mMimeType = "application/octet-stream";
    private InputStream mInputStream;

    public StreamRequestParameter(String name, InputStream stream){
        super(name);
        if(stream == null){
            throw new NullPointerException();
        }

        mInputStream = stream;
    }

    public StreamRequestParameter(String name, InputStream stream, String mimeType){
        this(name, stream);
        if(mimeType != null && mimeType.length() > 0){
            mMimeType = mimeType;
        }
    }

    @Override
    public boolean output(OutputStream stream) {
        int bufferSize = 100 * 1024;
        int readSize = 0;

        if(mInputStream != null){
            try{
                byte[] buffer = new byte[bufferSize];

                if((readSize = mInputStream.read(buffer)) > 0){
                    stream.write(buffer, 0, readSize);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if(readSize < bufferSize){
                    // output finished
                    if(mInputStream != null){
                        try {
                            mInputStream.close();
                        } catch (IOException e) {
                        } finally {
                            mInputStream = null;
                        }
                    }
                }
            }
        }

        return readSize == bufferSize;
    }

    @Override
    public String getMimeType() {
        return mMimeType;
    }
}
