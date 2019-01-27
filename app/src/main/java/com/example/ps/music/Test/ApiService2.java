package com.example.ps.music.Test;

import android.content.Context;
import android.util.Log;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.ps.music.model.Song;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by poorya on 8/9/2018.
 */

public class ApiService2 {
    public static final String BASE_URL = "http://localhost:4439";
    public static final String END_POINT = "/api/shar/all";
    private static final String TAG = "APIServise";
    private Context context;
    Gson gson = new Gson();

    public ApiService2(Context context) {

        this.context = context;
    }

    public void getSongs(final OnResultCallBack<List<Song>> onResultCallBack) {

        final JsonObjectRequest jsonArrayRequest = new JsonObjectRequest(Request.Method.GET,
                "https://freemusicarchive.org/featured.json",
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {


                    try {
                        JSONArray songJsonArray = response.getJSONArray("nav_curators");

                        Type type = new TypeToken<List<Song>>(){}.getType();
                        List<Song> listSong = gson.fromJson(songJsonArray.toString(),type);

                        onResultCallBack.OnRecived(listSong);
                    } catch (JSONException e) {
                        Log.e(TAG , "Response:"+ e.toString());
                    }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                onResultCallBack.OnError(error+"");
                error.printStackTrace();
            }
        });
        Volley.newRequestQueue(context).add(jsonArrayRequest);
    }

    public interface OnResultCallBack<T>{
        void OnRecived(T t);
        void OnError(String message);
    }
}
