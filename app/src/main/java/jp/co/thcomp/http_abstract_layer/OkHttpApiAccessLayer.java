package jp.co.thcomp.http_abstract_layer;

import android.content.Context;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;

class OkHttpApiAccessLayer extends HttpAccessLayer {
    private OkHttpClient mClient = null;

    protected OkHttpApiAccessLayer(Context context) {
        super(context);
        mClient = new OkHttpClient();
    }

    @Override
    public boolean get() {
        Request request = createRequest(MethodType.GET);
        mClient.newCall(request).enqueue(mCallback);
        return true;
    }

    @Override
    public boolean post() {
        Request request = createRequest(MethodType.POST);
        mClient.newCall(request).enqueue(mCallback);
        return true;
    }

    private Request createRequest(MethodType methodType){
        Request.Builder builder = new Request.Builder();
        StringBuilder uriBuilder = new StringBuilder(mUri.toString());

        if (MethodType.GET.equals(methodType)){
            builder.get();
            uriBuilder.append(createUriParameters());
        }else if(MethodType.POST.equals(methodType)) {
            builder.post(createRequestBody(methodType));
        }
        builder.url(uriBuilder.toString());

        for(RequestHeader header : mHeaderList){
            builder.addHeader(header.mName, header.mValue);
        }

        return builder.build();
    }

    private RequestBody createRequestBody(MethodType methodType){
        RequestBody ret = null;

        if(MethodType.POST.equals(methodType)) {
            int parameterSize = mParameterList.size();

            if(parameterSize > 1){
                // multipart
                ret = new MultiPartRequestBody(mParameterList);
            }else if(parameterSize == 1){
                ret = new SinglePartRequestBody(mParameterList.get(0));
            }
        }

        return ret;
    }

    private okhttp3.Callback mCallback = new okhttp3.Callback() {
        @Override
        public void onFailure(okhttp3.Call call, IOException e) {
            callFailCallback(new ExternalResponse(e));
        }

        @Override
        public void onResponse(okhttp3.Call call, Response response) throws IOException {
            callSuccessCallback(new ExternalResponse(response));
        }
    };

    private static class ExternalHeader implements jp.co.thcomp.http_abstract_layer.Response.Header{
        private String mName;
        private String mValue;

        public ExternalHeader(String name, String value){
            if(name == null || value == null){
                throw new NullPointerException();
            }

            mName = name;
            mValue = value;
        }

        @Override
        public String getName() {
            return mName;
        }

        @Override
        public String getValue() {
            return mValue;
        }
    }

    private static class ExternalResponse implements jp.co.thcomp.http_abstract_layer.Response{
        private Response mResponse;
        private Exception mException;

        public ExternalResponse(Response response){
            mResponse = response;
        }

        public ExternalResponse(Exception exception){
            mException = exception;
        }

        @Override
        public int getStatusCode() {
            return mResponse != null ? mResponse.code() : -1;
        }

        @Override
        public String getMimeType() {
            return mResponse != null ? mResponse.header("Content-Type", null) : null;
        }

        @Override
        public String getRequestUrl() {
            return mResponse != null ? mResponse.request().url().toString() : null;
        }

        @Override
        public List<Header> getHeaders(String name) {
            ArrayList<Header> retList = new ArrayList<Header>();

            if(mResponse != null){
                if(name != null && name.length() > 0){
                    // get specified name of header
                    List<String> valueList = mResponse.headers(name);
                    if(valueList != null && valueList.size() > 0) {
                        for (String value : valueList) {
                            retList.add(new ExternalHeader(name, value));
                        }
                    }
                }else{
                    // get all headers
                    Map<String, List<String>> headerMap = mResponse.headers().toMultimap();
                    Set<Map.Entry<String, List<String>>> entrySet = headerMap.entrySet();
                    Iterator<Map.Entry<String, List<String>>> iterator = entrySet.iterator();

                    while(iterator.hasNext()){
                        Map.Entry<String, List<String>> entry = iterator.next();
                        String headerName = entry.getKey();
                        List<String> headerValueList = entry.getValue();

                        if(headerValueList != null && headerValueList.size() > 0){
                            for(String headerValue : headerValueList){
                                retList.add(new ExternalHeader(headerName, headerValue));
                            }
                        }
                    }
                }
            }

            return retList;
        }

        @Override
        public InputStream getEntity() {
            return mResponse != null ? mResponse.body().byteStream() : null;
        }

        @Override
        public Exception getException() {
            return mException;
        }

        @Override
        public void setException(Exception exception) {
            mException = exception;
        }
    }

    private static class SinglePartRequestBody extends RequestBody{
        private RequestParameter mTargetRequestParameter;

        public SinglePartRequestBody(RequestParameter requestParameter){
            mTargetRequestParameter = requestParameter;
        }

        @Override
        public MediaType contentType() {
            return MediaType.parse(mTargetRequestParameter.getMimeType());
        }

        @Override
        public void writeTo(BufferedSink sink) throws IOException {
            boolean readAgain = true;

            while(readAgain){
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                readAgain = !mTargetRequestParameter.output(outputStream);
                if(!readAgain){
                    sink.write(outputStream.toByteArray());
                }
            }
        }
    }

    private static class MultiPartRequestBody extends RequestBody{
        //private List<RequestParameter> mRequestParameterList;
        private byte[] mData;
        private IOException mReservedException;

        public MultiPartRequestBody(List<RequestParameter> requestParameterList){
            //mRequestParameterList = requestParameterList;
            try {
                mData = Utility.getMultipartFormData(requestParameterList);
            }catch(IOException e){
                mReservedException = e;
            }
        }

        @Override
        public long contentLength() throws IOException {
            if(mReservedException != null){
                throw mReservedException;
            }
            return mData.length;
        }

        @Override
        public MediaType contentType() {
            //return MediaType.parse("multipart/form-data; boundary=----" + Constant.MultipartBoundary);
            return MediaType.parse("multipart/form-data; boundary=" + Constant.MultipartBoundary);
        }

        @Override
        public void writeTo(BufferedSink sink) throws IOException {
            sink.write(mData);
            sink.flush();
        }
    }
}
