package jp.co.thcomp.http_abstract_layer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class Utility {
    public static String getStringParameter(RequestParameter parameter){
        String ret = null;

        if(parameter instanceof StringRequestParameter){
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            try {
                parameter.output(outputStream);
                ret = new String(outputStream.toByteArray());
            } finally {
                try {
                    outputStream.close();
                } catch (IOException e) {
                }
            }
        }

        return ret;
    }

    public static byte[] getMultipartFormData(List<RequestParameter> requestParamList) throws IOException{
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        for(RequestParameter param : requestParamList){
            outputMultipartParam(outputStream, param);
        }
        outputStream.write(Constant.MultipartBoundaryPrefix.getBytes());
        outputStream.write(Constant.MultipartBoundary.getBytes());
        outputStream.write(Constant.MultipartBoundarySuffix.getBytes());
        outputStream.write(Constant.MultipartCRLF);

        return outputStream.toByteArray();
    }

    private static void outputMultipartParam(OutputStream outputSink, RequestParameter parameter){
        try {
            outputSink.write(Constant.MultipartBoundaryPrefix.getBytes());
            outputSink.write(Constant.MultipartBoundary.getBytes());
            outputSink.write(Constant.MultipartCRLF);
            outputSink.write(Constant.MultipartContentDisposition.getBytes());
            outputSink.write(Constant.MultipartCD_Name.getBytes());
            outputSink.write(Constant.MultipartDQuote.getBytes());
            outputSink.write(parameter.name().getBytes());
            outputSink.write(Constant.MultipartDQuote.getBytes());
        } catch (IOException e) {
        }

        try {
            if (parameter instanceof FileRequestParameter) {
                FileRequestParameter fileParameter = (FileRequestParameter)parameter;

                // output file
                outputSink.write(Constant.MultipartSemiColon.getBytes());
                outputSink.write(Constant.MultipartCD_FileName.getBytes());
                outputSink.write(Constant.MultipartDQuote.getBytes());
                outputSink.write(fileParameter.getFileName().getBytes());
                outputSink.write(Constant.MultipartDQuote.getBytes());
                outputSink.write(Constant.MultipartCRLF);
                outputSink.write(Constant.MultipartContentType.getBytes());
                outputSink.write(fileParameter.getMimeType().getBytes());
                outputSink.write(Constant.MultipartCRLF);
                outputSink.write(Constant.MultipartCRLF);

                boolean readAgain = true;

                while(readAgain){
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    readAgain = fileParameter.output(outputStream);
                    if(readAgain){
                        outputSink.write(outputStream.toByteArray());
                    }
                }

                outputSink.write(Constant.MultipartCRLF);
            } else {
                outputSink.write(Constant.MultipartCRLF);
                outputSink.write(Constant.MultipartCRLF);

                String value = Utility.getStringParameter(parameter);
                if(value != null && value.length() > 0) {
                    outputSink.write(value.getBytes());
                }
                outputSink.write(Constant.MultipartCRLF);
            }
        } catch (IOException e) {
        }
    }
}
