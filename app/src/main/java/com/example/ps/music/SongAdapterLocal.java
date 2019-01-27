package com.example.ps.music;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.ps.music.model.Song;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.BlurTransformation;

/**
 * Created by poorya on 8/8/2018.
 */

public class SongAdapterLocal extends RecyclerView.Adapter<SongAdapterLocal.VHcity> {

    private Context context;
    private static List<Song> songList = new ArrayList<>();
    private OnCityItemClick onCityItemClick;


    public SongAdapterLocal(Context context, OnCityItemClick onCityItemClick) {

        this.context = context;
        this.onCityItemClick=onCityItemClick;
    }

    //--TODO check this more
    void addSong(List<Song> song){
        if (songList.isEmpty()){
            songList.addAll(song);
        }
        notifyDataSetChanged();
    }

    public static Song getSong(int position){

        return songList.get(position);
    }

    public static Boolean isEndOfList (int position){

        if (songList.size() == position){
            return true;
        }else {
            return false;
        }

    }

    @Override
    public VHcity onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.adp_rv, parent, false);
        return new VHcity(view, onCityItemClick);
    }

    @Override
    public void onBindViewHolder(VHcity holder, int position) {
        holder.bindCity(songList.get(position), context , position);
    }


    @Override
    public int getItemCount() {
        return songList.size();
    }

    public static class VHcity extends RecyclerView.ViewHolder {

        private TextView songName;
        private TextView songArtist;
        private ImageView songImage;
        private OnCityItemClick onCityItemClick;
        ProgressBar loadImg;

        public VHcity(View itemView, OnCityItemClick onCityItemClick) {
            super(itemView);
            loadImg = itemView.findViewById(R.id.progressBar_loadImg);
            songName = itemView.findViewById(R.id.tv_songName);
            songArtist = itemView.findViewById(R.id.tv_artistName);
            songImage = itemView.findViewById(R.id.iv_song_image);
            this.onCityItemClick=onCityItemClick;
        }

        public void bindCity(final Song song , final Context context , final int position) {

            loadImg.setVisibility(View.GONE);
            songName.setText(song.getSongName());
            songArtist.setText(song.getArtist());
            RequestOptions requestOptions = new RequestOptions();
            requestOptions.placeholder(R.drawable.no_image);
            Glide.with(context).asBitmap().apply(requestOptions)
                    .load(Uri.parse(song.getSongImageUri()))
                    .into(songImage);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onCityItemClick.OnSongItemClick(song, position);
                }
            });


        }

    }

    public interface OnCityItemClick {
        void OnSongItemClick(Song song, int position);
    }
}
