package com.example.ps.music.Test;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.ps.music.R;
import com.example.ps.music.SongAdapter;
import com.example.ps.music.api.ApiService;
import com.example.ps.music.model.Song;
import java.util.ArrayList;
import java.util.List;

import ss.com.infinitescrollprovider.InfiniteScrollProvider;
import ss.com.infinitescrollprovider.OnLoadMoreListener;

public class MainActivitytest extends AppCompatActivity implements SongAdapter.OnCityItemClick {
//        ApiService.OnResultCallBack<List<Song>> {

    private List<Song> list = new ArrayList<>();
    private SongAdapter adapter;
    private RecyclerView recyclerView;
    private ApiService apiService;
    private ProgressBar Main_progressBar;
    private ProgressBar Footer_progressBar;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupViewa();

//        apiService = new ApiService(this);
//        apiService.getSongs(this);


    }

    private void setupViewa() {

        Main_progressBar = (ProgressBar) findViewById(R.id.progressBar_Main);
        Footer_progressBar = (ProgressBar) findViewById(R.id.progressBar_footer);
        recyclerView = (RecyclerView) findViewById(R.id.rv_activityMain);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2, LinearLayoutManager.VERTICAL, false));
//        adapter = new SongAdapter(MainActivitytest.this, MainActivitytest.this);
        recyclerView.setAdapter(adapter);

        InfiniteScrollProvider infiniteScrollProvider = new InfiniteScrollProvider();
        infiniteScrollProvider.attach(recyclerView, new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                Footer_progressBar.setVisibility(View.VISIBLE);
//                apiService.getSongs(MainActivitytest.this);
            }
        });

    }

    @Override
    public void OnSongItemClick(Song song, int position) {
        Toast.makeText(this, song.getSongName(), Toast.LENGTH_SHORT).show();

    }

//    @Override
//    public void OnRecived(List<Song> songs) {
//        Main_progressBar.setVisibility(View.GONE);
//        Footer_progressBar.setVisibility(View.GONE);
//        adapter.addSong(songs);
//    }
//
//    @Override
//    public void OnError(String message) {
//        Toast.makeText(getApplicationContext(), message + "", Toast.LENGTH_LONG).show();
//    }
}
