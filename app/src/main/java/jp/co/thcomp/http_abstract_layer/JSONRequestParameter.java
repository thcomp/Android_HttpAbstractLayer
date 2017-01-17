package jp.co.thcomp.http_abstract_layer;

import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;

import jp.co.thcomp.util.LogUtil;

public class JSONRequestParameter extends RequestParameter {
    private String mValue;

    public JSONRequestParameter(String value) {
        super(""/* ダミー名称 */);
        mValue = value;
    }

    public JSONRequestParameter(JSONObject jsonObject) {
        super(""/* ダミー名称 */);
        mValue = jsonObject.toString();
    }

    @Override
    public boolean output(OutputStream stream) {
        try {
            stream.write(mValue.getBytes());
        } catch (IOException e) {
            LogUtil.exception(getClass().getSimpleName(), e);
        }

        return true;
    }

    @Override
    public String getMimeType() {
        return Constant.MIME_JSON + "; charset=utf-8";
    }
}
