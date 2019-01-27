package com.example.ps.music.model;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore;

import com.google.gson.annotations.SerializedName;

/**
 * Created by poorya on 8/8/2018.
 */

public class Song implements Parcelable {
    @SerializedName("track_title")
    private String songName;
    @SerializedName("track_image_file")
    private String songImage;
    @SerializedName("artist_name")
    private String artist;
    @SerializedName("track_file_url")
    private String trackFile;
    @SerializedName("album_title")
    private String albumName;
    @SerializedName("track_duration")
    private String duration;
    @SerializedName("track_downloads")
    private String downloadCount;
    private String songImageUri;

    public String getSongImageUri() {
        return songImageUri;
    }

    public void setSongImageUri(String songImageUri) {
        this.songImageUri = songImageUri;
    }

    public static Creator<Song> getCREATOR() {
        return CREATOR;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getDownloadCount() {
        return downloadCount;
    }

    public void setDownloadCount(String downloadCount) {
        this.downloadCount = downloadCount;
    }

    public String getTrackFile() {
        return trackFile;
    }

    public void setTrackFile(String trackFile) {
        this.trackFile = trackFile;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getSongImage() {
        return songImage;
    }

    public void setSongImage(String songImage) {
        this.songImage = songImage;
    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.songName);
        dest.writeString(this.songImage);
        dest.writeString(this.artist);
        dest.writeString(this.trackFile);
        dest.writeString(this.albumName);
        dest.writeString(this.duration);
        dest.writeString(this.downloadCount);
    }

    public Song() {
    }

    protected Song(Parcel in) {
        this.songName = in.readString();
        this.songImage = in.readString();
        this.artist = in.readString();
        this.trackFile = in.readString();
        this.albumName = in.readString();
        this.duration = in.readString();
        this.downloadCount = in.readString();
    }

    public static final Parcelable.Creator<Song> CREATOR = new Parcelable.Creator<Song>() {
        @Override
        public Song createFromParcel(Parcel source) {
            return new Song(source);
        }

        @Override
        public Song[] newArray(int size) {
            return new Song[size];
        }
    };

}
