package jp.co.thcomp.http_abstract_layer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;

import jp.co.thcomp.util.LogUtil;

public class FileRequestParameter extends RequestParameter {
    private String mFilepath;
    private String mMimeType = "application/octet-stream";
    private FileInputStream mInputStream;

    public FileRequestParameter(String name, String filepath) {
        super(name);
        if (filepath == null) {
            throw new NullPointerException();
        }

        mFilepath = filepath;
    }

    public FileRequestParameter(String name, String filepath, String mimeType) {
        this(name, filepath);
        if (mimeType != null && mimeType.length() > 0) {
            mMimeType = mimeType;
        }
    }

    public String getFileName() {
        return new File(mFilepath).getName();
    }

    @Override
    public boolean output(OutputStream stream) {
        int bufferSize = 100 * 1024;
        int readSize = 0;

        try {
            if (mInputStream == null) {
                mInputStream = new FileInputStream(mFilepath);
            }
            byte[] buffer = new byte[bufferSize];

            if ((readSize = mInputStream.read(buffer)) > 0) {
                stream.write(buffer, 0, readSize);
            }
        } catch (FileNotFoundException e) {
            LogUtil.exception(getClass().getSimpleName(), e);
        } catch (IOException e) {
            LogUtil.exception(getClass().getSimpleName(), e);
        } finally {
            if (readSize < bufferSize) {
                // output finished
                if (mInputStream != null) {
                    try {
                        mInputStream.close();
                    } catch (IOException e) {
                    } finally {
                        mInputStream = null;
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
