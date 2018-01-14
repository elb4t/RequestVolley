package es.elb4t.requestjava;

import android.content.Context;
import android.util.Base64;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;


/**
 * Created by eloy on 29/12/17.
 */

public class RequestVolley {
    private static final String TAG = RequestVolley.class.getSimpleName();
    private RequestQueue mRequestQueue;
    private Context ctx;

    private RequestVolley mInstance;
    private HashMap headers = new HashMap<String, String>();

    public RequestVolley(Context ctx) {
        this.ctx = ctx;
        mRequestQueue = getRequestQueue();
        if (mInstance == null) {
            mInstance = this;
        }
    }

    private RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(ctx);
        }
        return mRequestQueue;
    }

    private <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests() {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(TAG);
        }
    }

    //////// ----- METODOS DE AUTENTICACION -----

    /**
     * Autenticación basica para los headers de la peticón
     *
     * @param user Usuario a autenticar
     * @param pass Contraseña del usuario
     */
    public void basicAuth(String user, String pass) {
        String credentials = user + ":" + pass;
        Log.e("CREDENCIALS-----", credentials);
        String auth = "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
        headers.clear();
        headers.put("Authorization", auth);
        Log.e("Authorization-----", auth);
    }

    /**
     * Autenticación con Token para los headers de la peticón
     *
     * @param token Token obtenido con la autenticación del usuario
     */
    public void tokenAuth(String token) {
        headers.clear();
        headers.put("Content-Type", "application/json");
        headers.put("Authorization", "Bearer " + token);
        Log.e("Authorization-----", "Bearer " + token);
    }

}