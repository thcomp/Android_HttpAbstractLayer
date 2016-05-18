package jp.co.thcomp.http_abstract_layer;

import android.content.Context;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

class URLConnectionApiAccessLayer extends HttpAccessLayer {
    protected URLConnectionApiAccessLayer(Context context) {
        super(context);
    }

    @Override
    public boolean get() {
        boolean ret = true;
        HttpURLConnection connection = null;

        try {
            connection = createUrlConnection(MethodType.GET);
            callResponseCallback(connection);
        } catch (IOException e) {
            callResponseCallback(connection);
            ret = false;
        }

        return ret;
    }

    @Override
    public boolean post() {
        boolean ret = true;
        HttpURLConnection connection = null;

        try{
            connection = createUrlConnection(HttpAccessLayer.MethodType.POST);
            callResponseCallback(connection);
        } catch (IOException e) {
            callResponseCallback(connection);
            ret = false;
        }

        return ret;
    }

    private HttpURLConnection createUrlConnection(HttpAccessLayer.MethodType methodType) throws IOException {
        HttpURLConnection ret = null;
        StringBuilder uriBuilder = new StringBuilder(mUri.toString());

        if (HttpAccessLayer.MethodType.GET.equals(methodType)){
            uriBuilder.append(createUriParameters());
        }
        URL url = new URL(uriBuilder.toString());

        if(url != null){
            try {
                ret = (HttpURLConnection)url.openConnection();
                if(mAuthentication != null) {
                    Authenticator.setDefault(mLocalAuthenticator);
                }
            } catch (IOException e) {
            }

            if(mHeaderList.size() > 0) {
                ret.setDoOutput(true);
                for (RequestHeader header : mHeaderList) {
                    ret.setRequestProperty(header.mName, header.mValue);
                }
            }

            ret.setRequestMethod(methodType.name());
            if(methodType == MethodType.POST){
                ret.setDoOutput(true);

                //  Content-Type
                LocalRequestBody body = createRequestBody(MethodType.POST);
                ret.setRequestProperty("Content-Type", body.contentType());

                OutputStream outStream = ret.getOutputStream();

                ret.connect();

                // output entity
                body.writeTo(outStream);
            }else{
                ret.connect();
            }
        }

        return ret;
    }

    private LocalRequestBody createRequestBody(MethodType methodType){
        LocalRequestBody ret = null;

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

    private void callResponseCallback(HttpURLConnection connection){
        ExternalResponse response = new ExternalResponse(connection);
        if(response.getStatusCode() == 200){
            callSuccessCallback(response);
        }else{
            callFailCallback(response);
        }
    }

    private Authenticator mLocalAuthenticator = new Authenticator() {
        @Override
        protected PasswordAuthentication getPasswordAuthentication() {
            return mAuthentication;
        }

        @Override
        protected URL getRequestingURL() {
            return super.getRequestingURL();
        }

        @Override
        protected RequestorType getRequestorType() {
            return super.getRequestorType();
        }
    };

    private static interface LocalRequestBody{
        public String contentType();
        public void writeTo(OutputStream sink) throws IOException;
    }

    private static class SinglePartRequestBody implements LocalRequestBody{
        private RequestParameter mTargetRequestParameter;

        public SinglePartRequestBody(RequestParameter requestParameter){
            mTargetRequestParameter = requestParameter;
        }

        public String contentType() {
            return mTargetRequestParameter.getMimeType();
        }

        public void writeTo(OutputStream sink) throws IOException {
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

    private static class MultiPartRequestBody implements LocalRequestBody{
        private byte[] mData;
        private IOException mReservedException;

        public MultiPartRequestBody(List<RequestParameter> requestParameterList){
            try {
                mData = Utility.getMultipartFormData(requestParameterList);
            }catch(IOException e){
                mReservedException = e;
            }
        }

        public long contentLength() throws IOException {
            if(mReservedException != null){
                throw mReservedException;
            }
            return mData.length;
        }

        public String contentType() {
            return "multipart/form-data; boundary=" + Constant.MultipartBoundary;
        }

        public void writeTo(OutputStream sink) throws IOException {
            sink.write(mData);
            sink.flush();
        }
    }

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
        private HttpURLConnection mConnection;
        private Exception mException;

        public ExternalResponse(HttpURLConnection connection){
            mConnection = connection;
        }

        public ExternalResponse(Exception exception){
            mException = exception;
        }

        @Override
        public int getStatusCode() {
            int statusCode = -1;

            try{
                statusCode = mConnection.getResponseCode();
            }catch(Exception e){
            }

            return statusCode;
        }

        @Override
        public String getMimeType() {
            String mimeType = null;

            try{
                mimeType = mConnection.getHeaderField("Content-Type");
            }catch (Exception e){
            }

            return mimeType;
        }

        @Override
        public String getRequestUrl() {
            String requestUrl = null;

            try{
                requestUrl = mConnection.getURL().toString();
            }catch (Exception e){
            }

            return requestUrl;
        }

        @Override
        public List<Header> getHeaders(String name) {
            ArrayList<Header> retList = new ArrayList<Header>();

            if(mConnection != null){
                Map<String, List<String>> valueMap = mConnection.getHeaderFields();

                if(name != null && name.length() > 0){
                    // get specified name of header
                    List<String> headerList = valueMap.get(name);
                    if(headerList != null && headerList.size() > 0) {
                        for (String value : headerList) {
                            retList.add(new ExternalHeader(name, value));
                        }
                    }
                }else{
                    // get all headers
                    Set<Map.Entry<String, List<String>>> entrySet = valueMap.entrySet();
                    Iterator<Map.Entry<String, List<String>>> iterator = entrySet.iterator();

                    while(iterator.hasNext()){
                        Map.Entry<String, List<String>> entry = iterator.next();
                        String headerName = entry.getKey();
                        List<String> headerValueList = entry.getValue();
                        if(headerValueList != null && headerValueList.size() > 0) {
                            for (String headerValue : headerValueList) {
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
            InputStream ret = null;

            try{
                ret = mConnection.getInputStream();
            }catch (IOException e){
            }

            return ret;
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
}
