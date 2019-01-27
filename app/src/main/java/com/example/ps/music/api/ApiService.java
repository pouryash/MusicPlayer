package com.example.ps.music.api;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.ps.music.model.Song;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by poorya on 8/9/2018.
 */

public class ApiService {

    private static final String BASE_URL = "https://freemusicarchive.org/featured.json";
    private static final String TAG = "APIServise";
    private Context context;
    private Response.Listener<List<Song>> response;
    private Response.ErrorListener errorListener;
    public static Request request;

    public ApiService(Context context, Response.Listener<List<Song>> response
            , Response.ErrorListener errorListener) {

        this.context = context;
        this.errorListener = errorListener;
        this.response = response;
    }
    public void getSongs() {

        GsonRequest gsonRequest = new GsonRequest(Request.Method.GET,BASE_URL, response, errorListener,
                new TypeToken<ArrayList<Song>>() {}.getType());

        request = Volley.newRequestQueue(context).add(gsonRequest);

    }
}
