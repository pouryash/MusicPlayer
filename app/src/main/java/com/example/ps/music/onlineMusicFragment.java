package com.example.ps.music;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.android.volley.NetworkError;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.example.ps.music.api.ApiService;
import com.example.ps.music.model.Song;

import java.util.List;
import ss.com.infinitescrollprovider.InfiniteScrollProvider;
import ss.com.infinitescrollprovider.OnLoadMoreListener;

import static android.content.Context.MODE_PRIVATE;


public class onlineMusicFragment extends Fragment implements Response.Listener<List<Song>> ,
Response.ErrorListener , SongAdapter.OnCityItemClick{

    private SongAdapter adapter;
    private RecyclerView recyclerView;
    private ApiService apiService;
    private ProgressBar Main_progressBar;
    private ProgressBar Footer_progressBar;
    private ImageButton img_retry;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        apiService = new ApiService(getContext(), this, this);
        apiService.getSongs();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_online_music, container, false);


        showWellcomeOnce();
        setupViewa(view);




        return view;
    }


    private void showWellcomeOnce() {
        SharedPreferences showOnce = getActivity().getSharedPreferences("showOnce",MODE_PRIVATE);
        if (!showOnce.getBoolean("showOnce",false)){

            startActivity(new Intent(getContext(), Wellcom.class));

        }
    }


    private void setupViewa(View view) {


        img_retry =(ImageButton) view.findViewById(R.id.iv_retry_fragment_online);
        Main_progressBar = (ProgressBar) view.findViewById(R.id.progressBar_Main);
        Footer_progressBar = (ProgressBar) view.findViewById(R.id.progressBar_footer);
        recyclerView = (RecyclerView) view.findViewById(R.id.rv_activityMain);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2, LinearLayoutManager.VERTICAL, false));
        adapter = new SongAdapter(getContext(), this);
        recyclerView.setAdapter(adapter);

        InfiniteScrollProvider infiniteScrollProvider = new InfiniteScrollProvider();
        infiniteScrollProvider.attach(recyclerView, new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {

                Toast.makeText(getContext(),"cant load more this is end of list!!", Toast.LENGTH_SHORT).show();

            }
        });

        img_retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                img_retry.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                Main_progressBar.setVisibility(View.VISIBLE);
                apiService.getSongs();
            }
        });

    }


    @Override
    public void onErrorResponse(VolleyError error) {

        if (error instanceof NetworkError) {

            Toast.makeText(getContext(), "Cannot connect to Internet...Please check your connection and try again", Toast.LENGTH_LONG).show();
            ApiService.request.cancel();
            img_retry.setVisibility(View.VISIBLE);
            Main_progressBar.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);

        } else if (error instanceof TimeoutError) {

            Toast.makeText(getContext(), "Connection TimeOut! Please check your internet connection and try again", Toast.LENGTH_LONG).show();
            ApiService.request.cancel();
            img_retry.setVisibility(View.VISIBLE);
            Main_progressBar.setVisibility(View.GONE);

        } else if (error instanceof ServerError) {

            Toast.makeText(getContext(), "The server could not be found. Please try again after some time and try again", Toast.LENGTH_LONG).show();
            ApiService.request.cancel();
            img_retry.setVisibility(View.VISIBLE);
            Main_progressBar.setVisibility(View.GONE);

        } else {

            Toast.makeText(getContext(), error + "", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onResponse(List<Song> response) {
        Main_progressBar.setVisibility(View.GONE);
        Footer_progressBar.setVisibility(View.GONE);
        adapter.addSong(response);
    }

    @Override
    public void OnSongItemClick(Song song, int position) {
        Intent intent = new Intent(getContext(), PlaySongOnline.class);
        if (PlaySongOnline.isActivityCreate) {
            if (PlaySongOnline.song.getSongName().equals(song.getSongName()) & PlaySongOnline.mediaPlayer.isPlaying()) {
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                PlaySongOnline.audioWidget.hide();
            } else {
//                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                PlaySongOnline.audioWidget.hide();
                PlaySongOnline.mediaPlayer.stop();
            }
        }
        intent.putExtra("song", song);
        intent.putExtra("position", position);
        startActivity(intent);
    }
}
