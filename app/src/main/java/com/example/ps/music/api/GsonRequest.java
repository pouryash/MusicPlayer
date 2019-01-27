package com.example.ps.music.api;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.Map;

/**
 * Created by poorya on 8/18/2018.
 */

public class GsonRequest<T> extends Request<T> {

    private static final String TAG = "GsonRequest";
    private Gson gson = new Gson();
    private Response.Listener<T> responseListener;
    private Type type;

    public GsonRequest(int method, String url, Response.Listener<T> responseListener,
                       Response.ErrorListener listener , Type type) {
        super(method, url, listener);
        this.type = type;
        this.responseListener = responseListener;
        setRetryPolicy(new DefaultRetryPolicy(5000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        try {


            String data = new String(response.data, "UTF-8");
                T result = null;
            try {

                JSONObject jsonObject = new JSONObject(new String(data));
                String jsonArray = jsonObject.getJSONArray("aTracks").toString();
                result = gson.fromJson(jsonArray,type);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return Response.success(result, null);


        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, "parseNetworkResponse: ", e);
            return Response.error(new ParseError(e));
        }
    }

    @Override
    protected void deliverResponse(T response) {
        responseListener.onResponse(response);
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return super.getHeaders();
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        return super.getBody();
    }
}
