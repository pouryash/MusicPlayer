package com.example.ps.music;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.chibde.visualizer.CircleBarVisualizer;
import com.cleveroad.audiowidget.AudioWidget;
import com.example.ps.music.commen.Image;
import com.example.ps.music.model.Song;
import com.example.ps.music.transfer.ShadowTransfer;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.squareup.picasso.Transformation;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import info.abdolahi.CircularMusicProgressBar;
import jp.wasabeef.picasso.transformations.BlurTransformation;

import static com.example.ps.music.PlaySongLocal.audioWidget;

public class PlaySongOnline extends AppCompatActivity implements View.OnClickListener,
        View.OnTouchListener, MediaPlayer.OnCompletionListener,
        MediaPlayer.OnBufferingUpdateListener, NotificationService.onServiceStateChange,
        AudioWidget.OnControlsClickListener {

    public static boolean isOncreate = false;
    private ImageButton buttonPlayPause;
    public static Song song;
    private TextView duration;
    private TextView downloadCont;
    private TextView albumName;
    private TextView songName;
    private TextView songArtist;
    private SeekBar seekBarProgress;
    public static MediaPlayer mediaPlayer;
    private ImageView songImg;
    private ProgressBar progressBar;
    private CircleBarVisualizer barVisualizer;
    private ProgressBar progressBar_load_song;
    private ImageButton nextTrack;
    private ImageButton priviousTrack;
    private RelativeLayout rel_details, rel_play;
    private ImageView showDetails;
    private boolean bufferUpDate = false;
    public static AudioWidget audioWidget;
    private ImageView back;
    int secondaryProgress;
    int currentPosition;
    int nextPosition;
    int priviousPosition;
    boolean trackFlagPrivious = false;
    boolean trackFlagNext = false;
    private CircularMusicProgressBar musicProgressBar;
    private int mediaFileLengthInMilliseconds; // this value contains the song duration in milliseconds. Look at getDuration() method in MediaPlayer class
    private Handler handler = new Handler();
    private Intent serviceIntent;
    private Boolean isBooberActive = false;
    private Boolean hasBoober = false;
    public static Boolean isActivityCreate = false;
    private boolean isSeekbarTouch;
    private boolean isFirstBuffering = false;
    private int arrowResourceUp = R.drawable.ic_up_arrow_white;
    private int arrowResourceDown = R.drawable.ic_down_arrow_white;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.play_song_online);

        isActivityCreate = true;
        initView();
        setupView(song);

    }

    private void startMusicBobber() {
        audioWidget = new AudioWidget.Builder(this).build();
        audioWidget.controller().onControlsClickListener(this);
        audioWidget.show(100, 100);
        Picasso.get().load(song.getSongImage()).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                audioWidget.controller().albumCoverBitmap(bitmap);
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        });
        audioWidget.controller().duration(mediaPlayer.getDuration());
    }

    private void setupView(final Song song) {

        //on backButton click
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PlaySongOnline.this.finish();
                mediaPlayer.stop();
            }
        });
        //set image on image progressbar
        if (!Image.isValidImage(song.getSongImage())) {

            musicProgressBar.setVisibility(View.INVISIBLE);

        } else if (Image.isValidImage(song.getSongImage())) {
            musicProgressBar.setVisibility(View.VISIBLE);
            Picasso.get().load(song.getSongImage()).into(musicProgressBar);
        }

        final List<Transformation> transformations = new ArrayList<>();
        transformations.add(new ShadowTransfer(50));
        transformations.add(new BlurTransformation(this));

        if (!Image.isValidImage(song.getSongImage())) {
            duration.setTextColor(Color.BLACK);
            back.setImageResource(R.drawable.ic_arrow_back_black_24dp);
            songImg.setImageResource(R.drawable.no_image);
            if (rel_details.getVisibility() == View.VISIBLE) {
                showDetails.setImageResource(R.drawable.ic_up_arrow_black);
                arrowResourceUp = R.drawable.ic_up_arrow_black;
            } else {
                showDetails.setImageResource(R.drawable.ic_down_arrow_black);
                arrowResourceDown = R.drawable.ic_down_arrow_black;
            }

        } else if (Image.isValidImage(song.getSongImage())) {
            duration.setTextColor(Color.WHITE);
            back.setImageResource(R.drawable.ic_arrow_back_white_24dp);
            if (rel_details.getVisibility() == View.VISIBLE) {
                showDetails.setImageResource(R.drawable.ic_up_arrow_white);
                arrowResourceUp = R.drawable.ic_up_arrow_white;
                arrowResourceDown = R.drawable.ic_down_arrow_white;
            } else {
                showDetails.setImageResource(R.drawable.ic_down_arrow_white);
                arrowResourceUp = R.drawable.ic_up_arrow_white;
                arrowResourceDown = R.drawable.ic_down_arrow_white;
            }
            Picasso.get().load(song.getSongImage()).transform(transformations).into(songImg);
        }


        barVisualizer.setColor(getResources().getColor(R.color.colorPrimary));
        //set next and privious song on  mediaplayer
        nextTrack.setOnClickListener(this);
        priviousTrack.setOnClickListener(this);

        showDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (rel_details.getVisibility() == View.VISIBLE & !Image.isValidImage(song.getSongImage())) {
                    arrowResourceDown = R.drawable.ic_down_arrow_black;
                } else if (rel_details.getVisibility() == View.GONE & !Image.isValidImage(song.getSongImage())) {
                    arrowResourceUp = R.drawable.ic_up_arrow_black;
                }
                if (rel_details.getVisibility() == View.VISIBLE) {
                    //set animation for routate showDetail icon
                    AnimationSet setShowDetails = new AnimationSet(true);
                    setShowDetails.addAnimation(setAnim(R.anim.rotate));
                    showDetails.setAnimation(setShowDetails);
                    //set multi animation for Details with Animationset
                    AnimationSet set = new AnimationSet(true);
                    set.addAnimation(setAnim(R.anim.slide_up));
                    set.addAnimation(setAnim(R.anim.alpha));
                    rel_details.setAnimation(set);
                    //change rel_play parametrs isSeekbarTouch show details
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) showDetails.getLayoutParams();
                    params.addRule(RelativeLayout.BELOW, 0);
                    showDetails.setLayoutParams(params);
                    rel_details.setVisibility(View.GONE);
                    //change drawable of show details
                    Resources resources = getResources();
                    Resources.Theme theme = getTheme();
                    Drawable drawable = VectorDrawableCompat.create(resources, arrowResourceDown, theme);
                    showDetails.setImageDrawable(drawable);

                } else {

                    //set animation for routate showDetail icon
                    AnimationSet setShowDetails = new AnimationSet(true);
                    setShowDetails.addAnimation(setAnim(R.anim.reverse_rotate));
                    showDetails.setAnimation(setShowDetails);
                    //set animation for slideUp Details
                    AnimationSet set = new AnimationSet(true);
                    set.addAnimation(setAnim(R.anim.slide_down));
                    set.addAnimation(setAnim(R.anim.reverse_alpha));
                    rel_details.setAnimation(set);
                    //change rel_play parametrs isSeekbarTouch show details
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) showDetails.getLayoutParams();
                    params.addRule(RelativeLayout.BELOW, R.id.rel_details);
                    showDetails.setLayoutParams(params);
                    rel_details.setVisibility(View.VISIBLE);
                    //change drawable of show details
                    Resources resources = getResources();
                    Resources.Theme theme = getTheme();
                    Drawable drawable = VectorDrawableCompat.create(resources, arrowResourceUp, theme);
                    showDetails.setImageDrawable(drawable);
                }
            }
        });


        albumName.setText(song.getAlbumName());
        duration.setText(song.getDuration());
        downloadCont.setText(song.getDownloadCount());
        songName.setText(song.getSongName());
        songArtist.setText(song.getArtist());
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initView() {

        back = (ImageView) findViewById(R.id.iv_mp3_back);
        musicProgressBar = (CircularMusicProgressBar) findViewById(R.id.circle_image);
        nextPosition = getIntent().getIntExtra("position", 0);
        priviousPosition = getIntent().getIntExtra("position", 0);
        song = getIntent().getParcelableExtra("song");
        priviousTrack = (ImageButton) findViewById(R.id.ButtonTestPreviousTrack);
        nextTrack = (ImageButton) findViewById(R.id.ButtonTestNextTrack);
        albumName = (TextView) findViewById(R.id.tv_albumName);
        duration = (TextView) findViewById(R.id.tv_duration);
        downloadCont = (TextView) findViewById(R.id.tv_downloadCount);
        songArtist = (TextView) findViewById(R.id.tv_artistName);
        songName = (TextView) findViewById(R.id.tv_songName);
        showDetails = (ImageView) findViewById(R.id.iv_show_details);
        rel_details = (RelativeLayout) findViewById(R.id.rel_details);
        rel_play = (RelativeLayout) findViewById(R.id.rel_play);
        songImg = (ImageView) findViewById(R.id.iv_songImage);
        progressBar_load_song = (ProgressBar) findViewById(R.id.progressBar_load_song);
        barVisualizer = (CircleBarVisualizer) findViewById(R.id.test);
        buttonPlayPause = (ImageButton) findViewById(R.id.ButtonTestPlayPause);
        buttonPlayPause.setOnClickListener(this);
        seekBarProgress = (SeekBar) findViewById(R.id.SeekBarTestPlay);
        seekBarProgress.setMax(99); // It means 100% .0-99


        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnBufferingUpdateListener(this);
        mediaPlayer.setOnCompletionListener(this);
        seekBarProgress.setOnTouchListener(this);
    }

    private Animation setAnim(int animation) {

        return AnimationUtils.loadAnimation(getApplicationContext(), animation);
    }

    private void primarySeekBarProgressUpdater() {
        seekBarProgress.setProgress((int) (((float) mediaPlayer.getCurrentPosition() / mediaFileLengthInMilliseconds) * 100)); // This math construction give a percentage of "was playing"/"song length"
        musicProgressBar.setValue((((float) mediaPlayer.getCurrentPosition() / mediaFileLengthInMilliseconds) * 100));
        if (mediaPlayer.isPlaying()) {
            Runnable notification = new Runnable() {
                public void run() {
                    primarySeekBarProgressUpdater();
                }
            };
            handler.postDelayed(notification, 1000);
        }
    }

    @Override
    public void onClick(View v) {
//previousTrack click listener
        if (v.getId() == R.id.ButtonTestPreviousTrack) {

            previousTrack();
        }
// nextTrack click Listener
        if (v.getId() == R.id.ButtonTestNextTrack) {

            nextTrack();

        }
//play or pause Button click Listener
        if (v.getId() == R.id.ButtonTestPlayPause) {
            if (!bufferUpDate) {
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                progressBar_load_song.setVisibility(View.VISIBLE);
            }

            /*ImageButton onClick event handler. Method which start/pause mediaplayer playing */
            try {
                mediaPlayer.setDataSource(song.getTrackFile() + "/download"); // setup song from http://www.hrupin.com/wp-content/uploads/mp3/testsong_20_sec.mp3 URL isSeekbarTouch mediaplayer data source
                barVisualizer.setPlayer(mediaPlayer.getAudioSessionId()); //setup barVisualizer player
                mediaPlayer.prepareAsync(); // you must call this method after setup the datasource in setDataSource method. After calling prepare() the instance of MediaPlayer starts load data from URL isSeekbarTouch internal buffer.
            } catch (Exception e) {
                e.printStackTrace();
            }

            mediaFileLengthInMilliseconds = mediaPlayer.getDuration(); // gets the song length in milliseconds from URL

            if (!mediaPlayer.isPlaying()) {
                isFirstBuffering = false;
                playMediaPlayer();


            } else {
                if (bufferUpDate)
                    isFirstBuffering = true;
                stopMediaPlayer();
                stopService(serviceIntent);
            }
            NotificationService.onServiceStateChange = this;
        }
    }

    private void nextTrack() {

        if (SongAdapter.isEndOfList(nextPosition + 1)) {
            Toast.makeText(getApplicationContext(), "this is the last track in list!!!", Toast.LENGTH_LONG).show();
        } else {
            if (!trackFlagPrivious & !trackFlagNext) {
                trackFlagNext = true;
            }

            if (trackFlagNext) {
                nextPosition = nextPosition + 1;
                priviousPosition = nextPosition - 1;
            }

            song = SongAdapter.getSong(nextPosition);
            setupView(song);
            try {
                buttonPlayPause.setImageResource(R.drawable.ic_play_button);
                isSeekbarTouch = false;
                bufferUpDate = false;
                isFirstBuffering = false;
                mediaPlayer.stop();
                mediaPlayer.reset();
                seekBarProgress.setSecondaryProgress(0);
                mediaPlayer.setDataSource(song.getTrackFile());
                mediaPlayer.prepareAsync();

            } catch (IOException e) {
                e.printStackTrace();
            }
            if (!trackFlagNext) {
                nextPosition = nextPosition + 1;
                priviousPosition = nextPosition - 1;
            }
        }
    }

    private void previousTrack() {
        if (!trackFlagPrivious & !trackFlagNext) {
            trackFlagPrivious = true;
        }
        if (priviousPosition - 1 == -1) {

            Toast.makeText(getApplicationContext(), "this is first track of list!!", Toast.LENGTH_SHORT).show();
        }
        if (trackFlagPrivious & priviousPosition - 1 != -1) {
            priviousPosition = priviousPosition - 1;
            nextPosition = priviousPosition + 1;
        }

        song = SongAdapter.getSong(priviousPosition);
        setupView(song);
        try {
            buttonPlayPause.setImageResource(R.drawable.ic_play_button);
            bufferUpDate = false;
            isSeekbarTouch = false;
            isFirstBuffering = false;
            mediaPlayer.stop();
            mediaPlayer.reset();
            seekBarProgress.setSecondaryProgress(0);
            mediaPlayer.setDataSource(song.getTrackFile());
            mediaPlayer.prepareAsync();

        } catch (IOException e) {
            e.printStackTrace();
        }

        if (!trackFlagPrivious & priviousPosition != 0) {
            priviousPosition = priviousPosition - 1;
            nextPosition = priviousPosition + 1;
        }
    }

    private void stopMediaPlayer() {
        mediaPlayer.pause();
        buttonPlayPause.setImageResource(R.drawable.ic_play_button);
    }

    private void playMediaplayerSecound() {
        mediaPlayer.start();
        buttonPlayPause.setImageResource(R.drawable.ic_pause);
        barVisualizer.setVisibility(View.VISIBLE);
        progressBar_load_song.setVisibility(View.GONE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

//        serviceIntent = new Intent(this, NotificationService.class);
//        serviceIntent.setAction(Constants.ACTION.STARTFOREGROUND_ACTION);
//        serviceIntent.putExtra("song", song);
//        serviceIntent.putExtra("isPlaying", true);
//        startService(serviceIntent);
    }

    private void playMediaplayerFirstTime() {

        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {

                progressBar_load_song.setVisibility(View.GONE);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }
        });

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                if (!bufferUpDate) {
                    Toast.makeText(getApplicationContext(), "your internet is slow or your disconnected cant access the song!!!", Toast.LENGTH_LONG).show();
                }

            }
        }, 12000);
    }

    private void playMediaPlayer() {

        if (bufferUpDate) {

            playMediaplayerSecound();

        } else {

            playMediaplayerFirstTime();

        }
        primarySeekBarProgressUpdater();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        isSeekbarTouch = true;
        if (v.getId() == R.id.SeekBarTestPlay) {
            /* Seekbar onTouch event handler. Method which seeks MediaPlayer isSeekbarTouch seekBar primary progress secondaryProgress*/
//            if (mediaPlayer.isPlaying()) {
            SeekBar sb = (SeekBar) v;
            int playPositionInMillisecconds = (mediaFileLengthInMilliseconds / 100) * sb.getProgress();

            if (seekBarProgress.getProgress() > secondaryProgress) {
                Toast.makeText(getApplicationContext(), "this section is not loaded yet!!!", Toast.LENGTH_SHORT).show();
                mediaPlayer.seekTo(currentPosition);
            } else {
                mediaPlayer.seekTo(playPositionInMillisecconds);
            }
//            }
        }
        return false;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        /* MediaPlayer onCompletion event handler. Method which calls then song playing is complete*/
        buttonPlayPause.setImageResource(R.drawable.ic_play_button);
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {

        bufferUpDate = true;
        /* Method which updates the SeekBar secondary progress by current song loading from URL secondaryProgress*/
        seekBarProgress.setSecondaryProgress(percent);
        secondaryProgress = percent;
        currentPosition = mediaPlayer.getCurrentPosition();

        if (!isSeekbarTouch & !isFirstBuffering) {
            mediaFileLengthInMilliseconds = mediaPlayer.getDuration();
            playMediaPlayer();

        }

    }

   @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
//        this.finish();
//        mediaPlayer.stop();
//        if (NotificationService.isServiceRun) {
//            stopService(serviceIntent);
//        }
    }

    @Override
    public void onServiceChange(boolean isPlaying) {
        if (!isPlaying) {
            isFirstBuffering = false;
            playMediaPlayer();
        } else {
            if (bufferUpDate) {
                isFirstBuffering = true;
            }
            stopMediaPlayer();
        }
    }

    @Override
    public void onStopService() {
        if (bufferUpDate) {
            isFirstBuffering = true;
        }
        stopMediaPlayer();
    }

    @Override
    public void onChangeTrack(String tracState) {
        if (tracState.equals("next")) {
            nextTrack();

        } else {
            previousTrack();

        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        isOncreate = true;
        NotificationService.onServiceStateChange = this;

        if (hasBoober) {
            audioWidget.show(100, 100);
            audioWidget.expand();
            if (mediaPlayer.isPlaying()) {
                audioWidget.controller().start();
            } else {
                audioWidget.controller().stop();
            }
        } else {
            startMusicBobber();
            audioWidget.expand();
            if (mediaPlayer.isPlaying()) {
                audioWidget.controller().start();
            } else {
                audioWidget.controller().stop();
            }
            isBooberActive = true;
            Picasso.get().load(song.getSongImage()).into(new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    audioWidget.controller().albumCoverBitmap(bitmap);
                }

                @Override
                public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            });
        }
        isOncreate = true;
        NotificationService.onServiceStateChange = this;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (NotificationService.isServiceRun) {
            stopService(serviceIntent);
        }
    }


    public boolean onPlaylistClicked() {
        Intent intent = new Intent(PlaySongOnline.this, PlaySongLocal.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
        audioWidget.hide();
        return false;
    }

    @Override
    public void onPlaylistLongClicked() {
        Toast.makeText(getApplicationContext(), "onPlaylistLongClicked", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPreviousClicked() {
        previousTrack();
        audioWidget.controller().albumCover(Image.uriToDrawable(song.getSongImageUri(), this));

        try {
            mediaPlayer = MediaPlayer.create(this, Uri.parse(song.getTrackFile()));
            barVisualizer.setPlayer(mediaPlayer.getAudioSessionId()); //setup barVisualizer player
        } catch (Exception e) {
            e.printStackTrace();
        }

        mediaFileLengthInMilliseconds = mediaPlayer.getDuration(); // gets the song length in milliseconds from URL

        playMediaPlayer();
    }

    @Override
    public void onPreviousLongClicked() {
        Toast.makeText(getApplicationContext(), "onPreviousLongClicked", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onPlayPauseClicked() {
        if (!isFirstBuffering) {
            try {
                mediaFileLengthInMilliseconds = mediaPlayer.getDuration();
                playMediaPlayer();
                isFirstBuffering = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        mediaFileLengthInMilliseconds = mediaPlayer.getDuration(); // gets the song length in milliseconds from URL

        if (!mediaPlayer.isPlaying()) {
            playMediaPlayer();

        } else {
            stopMediaPlayer();
        }
        return false;
    }

    @Override
    public void onPlayPauseLongClicked() {
        Toast.makeText(getApplicationContext(), "onPlayPauseLongClicked", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNextClicked() {
        nextTrack();
        audioWidget.controller().albumCover(Image.uriToDrawable(song.getSongImageUri(), this));

        try {
            mediaPlayer = MediaPlayer.create(this, Uri.parse(song.getTrackFile()));
            barVisualizer.setPlayer(mediaPlayer.getAudioSessionId()); //setup barVisualizer player
        } catch (Exception e) {
            e.printStackTrace();
        }

        mediaFileLengthInMilliseconds = mediaPlayer.getDuration(); // gets the song length in milliseconds from URL

        playMediaPlayer();

    }

    @Override
    public void onNextLongClicked() {
        Toast.makeText(getApplicationContext(), "onNextLongClicked", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAlbumClicked() {

    }

    @Override
    public void onAlbumLongClicked() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isBooberActive) {
            hasBoober = true;
            audioWidget.collapse();
            audioWidget.hide();
            isBooberActive = false;
        }

    }

}
