package es.elb4t.requestjava;

import android.content.Context;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


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

    //////// ----- METODOS REQUEST -----

    /**
     * Petición GET a una api rest
     *
     * @param path   String con la ruta de la api definidas en el fichero RutalUrl
     * @param params JSONObject para definir los parametros en la url ej:?id=xx
     * @return response, code - String con el resultado de la petición - Codigo de respuesta del servidor.
     */
    public void get(String path, JSONObject params, final ResponseListener listener) {
        mInstance.addToRequestQueue(
                new JsonObjectRequest(Request.Method.GET, path, params, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String code = response.getJSONObject("meta").getString("code");
                            Log.d(TAG, "/get $path request OK!");
                            Log.e(TAG, "Code: " + code + " - Response: " + response);
                            listener.onResponse(response.toString(), Integer.parseInt(code));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        try {
                            if (error instanceof TimeoutError) {
                                Toast.makeText(ctx,
                                        ctx.getString(R.string.error_network_timeout),
                                        Toast.LENGTH_LONG).show();
                            } else if (error instanceof AuthFailureError) {
                                Toast.makeText(ctx,
                                        ctx.getString(R.string.error_auth),
                                        Toast.LENGTH_LONG).show();
                                listener.onError(new String(error.networkResponse.data), error.networkResponse.statusCode);
                            } else if (error instanceof ServerError) {
                                Toast.makeText(ctx,
                                        ctx.getString(R.string.error_server),
                                        Toast.LENGTH_LONG).show();
                                listener.onError(new String(error.networkResponse.data), error.networkResponse.statusCode);
                            } else if (error instanceof NetworkError) {
                                listener.onError(new String(error.networkResponse.data), error.networkResponse.statusCode);
                            } else if (error instanceof NoConnectionError) {
                                //TODO
                            }
                        } catch (Exception e) {
                            listener.onError(e.getMessage(), 500);
                        }
                    }
                }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        return headers;
                    }
                }
        );
    }
}