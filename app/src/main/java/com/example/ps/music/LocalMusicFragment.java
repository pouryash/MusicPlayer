package com.example.ps.music;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ps.music.model.Song;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FilenameFilter;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class LocalMusicFragment extends Fragment implements SongAdapterLocal.OnCityItemClick {


    private SongAdapterLocal adapter;
    private RecyclerView recyclerView;
    private View view;
    private static final String TAG = "LocalMusicFragment";
    private Song song;
    private TextView songName;
    private ImageButton previousButton;
    private ImageButton nextButton;
    private ImageButton playPaousButton;
    private LinearLayout lin_playSongMain;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        view = inflater.inflate(R.layout.fragment_local_music, container, false);


        if(getPathSong().isEmpty()){
            //--TODO fix if local empty
            Toast.makeText(getActivity(),"empty",Toast.LENGTH_SHORT).show();
        }else {
            recyclerView = (RecyclerView) view.findViewById(R.id.rv_activityMain);
            recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2, LinearLayoutManager.VERTICAL, false));
            adapter = new SongAdapterLocal(getContext(), this);

            List<Song> ff = getPathSong();
            adapter.addSong(getPathSong());
            recyclerView.setAdapter(adapter);
        }

        return view;
    }


    public List<Song> getPathSong() {

        ContentResolver cr = getActivity().getContentResolver();
        List<Song> songList = new ArrayList<>();
        final Uri albumArtUri = Uri.parse("content://media/external/audio/albumart");

        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0";
        String sortOrder = MediaStore.Audio.Media.TITLE + " ASC";
        Cursor cur = cr.query(uri, null, selection, null, sortOrder);
        int count = 0;

        if (cur != null) {
            count = cur.getCount();

            if (count > 0) {
                while (cur.moveToNext()) {
                    Song song = new Song();
                    song.setTrackFile(cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.DATA)));
                    song.setSongName(cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME)));
                    int m = Integer.parseInt(cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.DURATION)));
                    String hours = Integer.toString((int) ((m / (1000 * 60 * 60)) % 24));
                    String minutes = Integer.toString((int) (m % (1000 * 60 * 60) / (1000 * 60)));
                    String seconds = Integer.toString((int) ((m % (1000 * 60 * 60)) % (1000 * 60) / 1000));
                    if (!(Integer.parseInt(seconds) > 9)) {
                        seconds = "0"+seconds;
//                        song.setDuration(hours + ":" + minutes + ":0" + seconds);
                    } else {
//                        song.setDuration(hours + ":" + minutes + ":" + seconds);
                    }
                    if (!(Integer.parseInt(minutes) > 9) || Integer.parseInt(minutes) == 0) {
                        minutes = "0"+minutes;
//                        song.setDuration(hours + ":0" + minutes + ":" + seconds);
                    } else {
//                        song.setDuration(hours + ":" + minutes + ":" + seconds);
                    }
                    if (!(Integer.parseInt(hours) > 9) || Integer.parseInt(hours) == 0) {
                        hours = "0"+hours;
//                        song.setDuration("0" + hours + ":" + minutes + ":" + seconds);
                    } else {
//                        song.setDuration(hours + ":" + minutes + ":" + seconds);
                    }
                    song.setDuration(hours + ":" + minutes + ":" + seconds);
                    song.setArtist(cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.ARTIST)));
                    song.setAlbumName(cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.ALBUM)));
                    int albumId = cur.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID);
                    song.setSongImageUri(ContentUris.withAppendedId(albumArtUri, cur.getLong(albumId)).toString());
                    songList.add(song);
                }

            }
        }
        cur.close();
        return songList;
    }

    public List<Song> getLocalSongList(String rootPath) {
        List<Song> songList = new ArrayList<>();


        try {
            File rootFolder = new File(rootPath);
            File[] files = rootFolder.listFiles(); //here you will get NPE if directory doesn't contains  any file,handle it like this.
            for (File file : files) {
                if (file.isDirectory()) {
                    if (getLocalSongList(file.getAbsolutePath()) != null) {
                        songList.addAll(getLocalSongList(file.getAbsolutePath()));
                    } else {
                        break;
                    }
                } else if (file.getName().endsWith(".mp3")) {

                    MediaMetadataRetriever metaRetriver;
                    metaRetriver = new MediaMetadataRetriever();
                    metaRetriver.setDataSource(file.getAbsolutePath());
//                    byte[] art;
//                    art = metaRetriver.getEmbeddedPicture();
//                    Bitmap songImage = BitmapFactory
//                            .decodeByteArray(art, 0, art.length);

                    Song song = new Song();
//                    song.setSongImageBitmap(songImage);
//                    song.setArtist(metaRetriver.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST));
                    song.setTrackFile(file.getAbsolutePath());
                    song.setSongName(file.getName());
                    int m = Integer.parseInt(metaRetriver.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
                    int minutes = (int) (m % (1000 * 60 * 60)) / (1000 * 60);
                    int seconds = (int) ((m % (1000 * 60 * 60)) % (1000 * 60) / 1000);
                    if (!(seconds > 9)) {
                        song.setDuration(minutes + ":0" + seconds);
                    } else {
                        song.setDuration(minutes + ":" + seconds);
                    }
                    song.setAlbumName(metaRetriver.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM));
                    songList.add(song);
                }
            }
            return songList;
        } catch (Exception e) {
            Log.e(TAG, "getLocalSongList: " + e);
            return null;
        }
    }

    @Override
    public void OnSongItemClick(Song song, int position) {
        Intent intent = new Intent(getContext(), PlaySongLocal.class);
        if (PlaySongLocal.isActivityCreate) {
            if (PlaySongLocal.song.getSongName().equals(song.getSongName()) & PlaySongLocal.mediaPlayer.isPlaying()) {
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                PlaySongLocal.audioWidget.hide();
            } else {
//                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                PlaySongLocal.audioWidget.hide();
                PlaySongLocal.mediaPlayer.stop();
            }
        }
        intent.putExtra("song", song);
        intent.putExtra("imageUri", song.getSongImageUri());
        intent.putExtra("position", position);
        startActivity(intent);
    }

}

