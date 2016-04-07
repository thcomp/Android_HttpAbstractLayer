package jp.co.thcomp.http_abstract_layer;

public interface FailResponseCallback extends AbstractResponseCallback {
    // call this interface on worker thread, because any network library wants to connect network for entity.
    public void onFail(Response response);
}
