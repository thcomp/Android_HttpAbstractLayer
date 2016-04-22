package jp.co.thcomp.http_abstract_layer;

import android.content.Context;
import android.net.Uri;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public abstract class HttpAccessLayer {
    public static enum Accessor{
        OkHttp,
        Volley,
        URLConnection,
    }
    protected static final Accessor DefaultAccessor = Accessor.URLConnection;

    public static enum MethodType{
        HEAD,
        OPTION,
        GET,
        POST,
        DELETE,
        PUT;
    }

    public static HttpAccessLayer getInstance(Context context){
        return getInstance(context, DefaultAccessor);
    }

    public static synchronized HttpAccessLayer getInstance(Context context, Accessor accessor){
        HttpAccessLayer ret = null;

        if(accessor == null){
            accessor = DefaultAccessor;
        }
        if(accessor.equals(Accessor.OkHttp)){
            ret = new OkHttpApiAccessLayer(context);
        }else if(accessor.equals(Accessor.Volley)){
            // TODO: make HttpAccessLayer for Volley
            ret = new OkHttpApiAccessLayer(context);
        }else if(accessor.equals(Accessor.URLConnection)){
            ret = new URLConnectionApiAccessLayer(context);
        }

        return ret;
    }

    protected Context mContext;
    protected Uri mUri;
    protected ArrayList<RequestParameter> mParameterList = new ArrayList<RequestParameter>();
    protected ArrayList<RequestHeader> mHeaderList = new ArrayList<RequestHeader>();
    protected AbstractResponseCallback mResponseCallback;

    protected HttpAccessLayer(Context context){
        mContext = context;

        // set support encoding type
        requestHeader(Constant.HeaderAcceptEncoding, "gzip");
    }

    public HttpAccessLayer uri(String uri){
        mUri = Uri.parse(uri);
        return this;
    }

    public HttpAccessLayer requestHeader(String name, String value){
        mHeaderList.add(new RequestHeader(name, value));
        return this;
    }

    public HttpAccessLayer requestParam(RequestParameter param){
        mParameterList.add(param);
        return this;
    }

    public HttpAccessLayer responseCallback(AbstractResponseCallback callback){
        mResponseCallback = callback;
        return this;
    }

    protected String createUriParameters(){
        StringBuilder builder = new StringBuilder();
        RequestParameter[] parameterArray = mParameterList.toArray(new RequestParameter[0]);


        for(int i=0, size=parameterArray.length; i<size; i++){
            if(i == 0){
                builder.append("?");
            }else{
                builder.append("&");
            }

            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            parameterArray[i].output(outStream);
            String value = outStream.toString();
            builder.append(parameterArray[i].name()).append("=").append(Uri.encode(value));

            try {
                outStream.close();
            } catch (IOException e) {
            }
        }

        return builder.toString();
    }

    protected void callSuccessCallback(final Response response){
        if(mResponseCallback != null && mResponseCallback instanceof SuccessResponseCallback){
            if(mContext.getMainLooper().getThread().equals(Thread.currentThread())){
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        callSuccessCallback(response);
                    }
                }).start();
            }else{
                ((SuccessResponseCallback)mResponseCallback).onSuccess(response);
            }
        }
    }

    protected void callFailCallback(final Response response){
        if(mResponseCallback != null && mResponseCallback instanceof FailResponseCallback){
            if(mContext.getMainLooper().getThread().equals(Thread.currentThread())){
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        callFailCallback(response);
                    }
                }).start();
            }else{
                ((FailResponseCallback)mResponseCallback).onFail(response);
            }
        }
    }

    public abstract boolean get();

    public abstract boolean post();
}
