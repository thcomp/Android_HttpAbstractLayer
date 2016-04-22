package jp.co.thcomp.http_abstract_layer;

import android.content.Context;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

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
        } catch (IOException e) {
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
        } catch (IOException e) {
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
                readAgain = mTargetRequestParameter.output(outputStream);
                if(readAgain){
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
}
