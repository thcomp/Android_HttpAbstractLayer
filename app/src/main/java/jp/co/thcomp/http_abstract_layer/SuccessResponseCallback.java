package jp.co.thcomp.http_abstract_layer;

public interface SuccessResponseCallback extends AbstractResponseCallback {
    // call this interface on worker thread, because any network library wants to connect network for entity.
    public void onSuccess(Response response);
}
