package jp.co.thcomp.http_abstract_layer;

class RequestHeader {
    public String mName;
    public String mValue;

    public RequestHeader(String name, String value){
        if(name == null){
            throw new NullPointerException();
        }
        mName = name;
        mValue = value;
    }
}
