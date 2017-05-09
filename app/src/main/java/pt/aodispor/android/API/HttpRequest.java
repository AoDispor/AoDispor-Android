package pt.aodispor.android.api;

/**
 * Interface for classes that want to send am HTTP request and want to do something when the
 * request is finished.
 */
public interface HttpRequest {
    int GET_PROFILE = 1;
    int UPDATE_PROFILE = 2;
    int GET_LOCATION = 3;
    int UPDATE_IMAGE = 4;

    void onHttpRequestSuccessful(ApiJSON answer, int extra);

    void onHttpRequestFailed(ApiJSON errorData, int type);
}
