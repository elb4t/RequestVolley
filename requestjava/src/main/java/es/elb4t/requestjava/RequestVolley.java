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

}