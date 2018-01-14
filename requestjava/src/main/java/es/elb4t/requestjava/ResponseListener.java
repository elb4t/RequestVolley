package es.elb4t.requestjava;

public interface ResponseListener {
    void onResponse(Object response, int code);

    void onError(String data, int statusCode);
}
