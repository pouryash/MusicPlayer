package com.example.ps.music;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.module.AppGlideModule;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.example.ps.music.commen.Image;
import com.example.ps.music.model.Song;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by poorya on 8/8/2018.
 */

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.VHcity> {

    private Context context;
    private static List<Song> songList = new ArrayList<>();
    private OnCityItemClick onCityItemClick;


    public SongAdapter(Context context, OnCityItemClick onCityItemClick) {

        this.context = context;
        this.onCityItemClick=onCityItemClick;
    }
    void addSong(List<Song> song){
        songList.addAll(song);
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

            songName.setText(song.getSongName());
            songArtist.setText(song.getArtist());
            if (song.getSongImage() == null || song.getSongImage().trim().length() == 0|| song.getSongImage().isEmpty() && songImage.getDrawable() == null){

                songImage.setImageResource(R.drawable.no_image);

            }else {
                RequestOptions requestOptions = new RequestOptions();
//                requestOptions.error(R.drawable.no_image);
//                requestOptions.placeholder(R.drawable.no_image);
                requestOptions.isDiskCacheStrategySet();
                requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL);


                if (Image.isValidImage(song.getSongImage())){

                    Glide.with(context).load(song.getSongImage()).apply(requestOptions).listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            songImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
                            songImage.setImageResource(R.drawable.no_image);
                            return true;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            loadImg.setVisibility(View.GONE);
                            return false;
                        }
                    }).into(songImage);
                    songImage.setScaleType(ImageView.ScaleType.FIT_XY);
                }else {
                    loadImg.setVisibility(View.GONE);
                    songImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    Glide.with(context).load(R.drawable.no_image).into(songImage);
                }
            }
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    onCityItemClick.OnSongItemClick(song , position);
                }
            });
        }

    }

    public interface OnCityItemClick {
        void OnSongItemClick(Song song, int position);
    }
}
