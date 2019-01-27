package com.example.ps.music.Test;

import android.content.Context;
import android.util.Log;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.ps.music.model.Song;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by poorya on 8/9/2018.
 */

public class ApiServiceTest {
    public static final String BASE_URL = "http://localhost:4439";
    public static final String END_POINT = "/api/shar/all";
    private static final String TAG = "APIServise";
    private Context context;

    public ApiServiceTest(Context context) {

        this.context = context;
    }

    public void getCity(final OnResultCallBack<List<Song>> onResultCallBack) {

        final JsonObjectRequest jsonArrayRequest = new JsonObjectRequest(Request.Method.GET,
                "https://freemusicarchive.org/featured.json",
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                List<Song> listCity = new ArrayList<>();

//                for (int i = 0; i < response.length(); i++) {

                    try {
                        JSONArray songJsonArray = response.getJSONArray("nav_curators");
                        for (int i = 0; i <songJsonArray.length() ; i++) {
                            JSONObject songJsonObject = songJsonArray.getJSONObject(i);
                            Song city = new Song();

                            city.setSongName(songJsonObject.getString("curator_title"));
                            city.setSongImage(songJsonObject.getString("curator_image_url"));

                            listCity.add(city);
                        }
//                        Song Song = new Song();
//                        Song.setCityName(cityJsonObject.getString("name"));

//                        JSONArray domin = cityJsonObject.getJSONArray("topLevelDomain");
//                        Song.setCityName(domin.getString(0));
//                        listCity.add(Song);
                    } catch (JSONException e) {
                        Log.e(TAG , "Response:"+ e.toString());
                    }
//                }
                onResultCallBack.OnRecived(listCity);
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
