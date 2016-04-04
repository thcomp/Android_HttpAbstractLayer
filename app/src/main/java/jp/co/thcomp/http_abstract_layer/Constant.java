package jp.co.thcomp.http_abstract_layer;

public class Constant {
    public static final String MultipartBoundary = "Boundary0123456789";
    public static final String MultipartBoundaryPrefix = "------";
    public static final String MultipartBoundarySuffix = "--";
    public static final byte[] MultipartCRLF = new byte[]{0x0D, 0x0A};
    public static final String MultipartDQuote = "\"";
    public static final String MultipartSemiColon = ";";
    public static final String MultipartContentDisposition = "Content-Disposition: form-data;";
    public static final String MultipartCD_FileName = "filename=";
    public static final String MultipartCD_Name = "name=";
    public static final String MultipartContentType = "Content-Type: ";
    public static final String MIME_OctetStream = "application/octet-stream";
    public static final String MIME_JSON = "application/json";
    public static final String MIME_MessagePack = "application/x-msgpack";

    public static final String HeaderAcceptEncoding = "Accept-Encoding";
    public static final String HeaderContentEncoding = "Content-Encoding";
}
