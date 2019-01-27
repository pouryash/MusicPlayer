package com.example.ps.music;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.android.volley.NetworkError;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.example.ps.music.api.ApiService;
import com.example.ps.music.model.Song;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import ss.com.infinitescrollprovider.InfiniteScrollProvider;
import ss.com.infinitescrollprovider.OnLoadMoreListener;

public class MainActivity extends AppCompatActivity implements TabLayout.OnTabSelectedListener {


    private TabLayout tabLayout;
    private ViewPager viewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        View view2 = findViewById(R.id.ActivitiMain_toolbar_backArrow);
        view2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        tabLayout = findViewById(R.id.tabLayout_main);

        tabLayout.addTab(tabLayout.newTab().setText("Online"));
        tabLayout.addTab(tabLayout.newTab().setText("Local"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);


        viewPager = (ViewPager) findViewById(R.id.view_pager_main);

        pager adapter = new pager(getSupportFragmentManager(), tabLayout.getTabCount());

        viewPager.setAdapter(adapter);

        tabLayout.setOnTabSelectedListener((TabLayout.OnTabSelectedListener) MainActivity.this);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

    }


    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        viewPager.setCurrentItem(tab.getPosition());

    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }
}
