package jp.co.thcomp.http_abstract_layer;

public interface FailResponseCallback extends AbstractResponseCallback {
    public void onFail(Response response);
}
