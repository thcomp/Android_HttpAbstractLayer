package jp.co.thcomp.http_abstract_layer;

import android.content.Context;
import android.net.Uri;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public abstract class ApiAccessLayer {
    public static enum Accessor{
        OkHttp,
        Volley,
        URLConnection,
    }
    protected static final Accessor DefaultAccessor = Accessor.OkHttp;

    public static enum MethodType{
        HEAD,
        OPTION,
        GET,
        POST,
        DELETE,
        PUT;
    }

    public static ApiAccessLayer getInstance(Context context){
        return getInstance(context, DefaultAccessor);
    }

    public static synchronized ApiAccessLayer getInstance(Context context, Accessor accessor){
        ApiAccessLayer ret = null;

        if(accessor == null){
            accessor = DefaultAccessor;
        }
        if(accessor.equals(Accessor.OkHttp)){
            ret = new OkHttpApiAccessLayer(context);
        }else if(accessor.equals(Accessor.Volley)){
            // TODO: make ApiAccessLayer for Volley
            ret = new OkHttpApiAccessLayer(context);
        }else if(accessor.equals(Accessor.URLConnection)){
            // TODO: make ApiAccessLayer for URLConnection
            ret = new OkHttpApiAccessLayer(context);
        }

        return ret;
    }

    protected Context mContext;
    protected Uri mUri;
    protected ArrayList<RequestParameter> mParameterList = new ArrayList<RequestParameter>();
    protected ArrayList<RequestHeader> mHeaderList = new ArrayList<RequestHeader>();
    protected AbstractResponseCallback mResponseCallback;

    protected ApiAccessLayer(Context context){
        mContext = context;
    }

    public ApiAccessLayer uri(String uri){
        mUri = Uri.parse(uri);
        return this;
    }

    public ApiAccessLayer requestHeader(String name, String value){
        mHeaderList.add(new RequestHeader(name, value));
        return this;
    }

    public ApiAccessLayer requestParam(RequestParameter param){
        mParameterList.add(param);
        return this;
    }

    public ApiAccessLayer responseCallback(AbstractResponseCallback callback){
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

    public abstract boolean get();

    public abstract boolean post();
}
