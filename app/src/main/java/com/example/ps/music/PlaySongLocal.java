package com.example.ps.music;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chibde.visualizer.CircleBarVisualizer;
import com.cleveroad.audiowidget.AudioWidget;
import com.example.ps.music.commen.Image;
import com.example.ps.music.model.Song;
import com.example.ps.music.transfer.ShadowTransfer;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import info.abdolahi.CircularMusicProgressBar;
import jp.wasabeef.glide.transformations.BlurTransformation;

public class PlaySongLocal extends AppCompatActivity implements View.OnClickListener,
        View.OnTouchListener, MediaPlayer.OnCompletionListener
        , NotificationService.onServiceStateChange, AudioWidget.OnControlsClickListener {

    public static boolean isOncreate = false;
    private ImageButton buttonPlayPause;
    public static Song song;
    private TextView duration;
    private TextView filePath;
    private TextView albumName;
    private TextView songName;
    private TextView songArtist;
    private SeekBar seekBarProgress;
    public static MediaPlayer mediaPlayer;
    private ImageView songImg;
    private CircleBarVisualizer barVisualizer;
    private ImageButton nextTrack;
    private ImageButton priviousTrack;
    private RelativeLayout rel_details, rel_play;
    private ImageView showDetails;
    private boolean bufferUpDate = false;
    private ImageView back;
    int nextPosition;
    int priviousPosition;
    boolean trackFlagPrivious = false;
    boolean trackFlagNext = false;
    private CircularMusicProgressBar musicProgressBar;
    private int mediaFileLengthInMilliseconds; // this value contains the song duration in milliseconds. Look at getDuration() method in MediaPlayer class
    private final Handler handler = new Handler();
    private Intent serviceIntent;
    private boolean isFirstPlay = false;
    public static AudioWidget audioWidget;
    private Boolean isBooberActive = false;
    private Boolean hasBoober = false;
    private Boolean isFirstPlayInList = false;
    public static Boolean isActivityCreate = false;
    private int arrowResourceUp = R.drawable.ic_up_arrow_white;
    private int arrowResourceDown = R.drawable.ic_down_arrow_white;
    String imageUri;
    ImageView imgReplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.play_song_local);

        isActivityCreate = true;
        initView();
        setupView(song);

    }


    private void startMusicBobber() {
        audioWidget = new AudioWidget.Builder(this).build();
        audioWidget.controller().onControlsClickListener(this);
        audioWidget.show(100, 100);
        audioWidget.controller().albumCover(Image.uriToDrawable(imageUri, this));
        audioWidget.controller().duration(mediaPlayer.getDuration());
    }

    private void setupView(final Song song) {

        //on backButton click
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PlaySongLocal.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
//                PlaySongLocal.this.finish();
//                mediaPlayer.stop();
            }
        });
        final List<Transformation> transformations = new ArrayList<>();
        transformations.add(new ShadowTransfer(50));
        transformations.add(new jp.wasabeef.picasso.transformations.BlurTransformation(this));

        RequestOptions requestOptions = new RequestOptions();
        requestOptions.transform(new BlurTransformation(25, 2));
        requestOptions.placeholder(R.drawable.no_image);

        Glide.with(this).asBitmap()
                .load(Uri.parse(imageUri)).apply(requestOptions)
                .into(songImg);
        Glide.with(this).load(Uri.parse(imageUri)).into(musicProgressBar);

        Drawable mainImgDrawable = Image.uriToDrawable(imageUri, this);
        Bitmap mainImgBitmap = null;
        Bitmap noImgBitmap = Image.drawableToBitmap(getResources().getDrawable(R.drawable.no_image));
        if (mainImgDrawable == null) {
            mainImgBitmap = noImgBitmap;
        } else {
            mainImgBitmap = Image.drawableToBitmap(mainImgDrawable);
        }
        if (mainImgBitmap.equals(noImgBitmap)) {
            duration.setTextColor(Color.BLACK);
            back.setImageResource(R.drawable.ic_arrow_back_black_24dp);
            if (rel_details.getVisibility() == View.VISIBLE) {

                showDetails.setImageResource(R.drawable.ic_up_arrow_black);
                arrowResourceUp = R.drawable.ic_up_arrow_black;
            } else {
                showDetails.setImageResource(R.drawable.ic_down_arrow_black);
                arrowResourceDown = R.drawable.ic_down_arrow_black;
            }

        } else {
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
        }

        barVisualizer.setColor(getResources().getColor(R.color.colorPrimary));
        nextTrack.setOnClickListener(this);
        priviousTrack.setOnClickListener(this);

        showDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Drawable drawable3 = songImg.getDrawable();
                if (rel_details.getVisibility() == View.VISIBLE & Image.drawableToBitmap(songImg.getDrawable()).equals(Image.drawableToBitmap(getResources().getDrawable(R.drawable.no_image)))) {
                    arrowResourceDown = R.drawable.ic_down_arrow_black;
                } else if (rel_details.getVisibility() == View.GONE & Image.drawableToBitmap(songImg.getDrawable()).equals(Image.drawableToBitmap(getResources().getDrawable(R.drawable.no_image)))) {
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
        filePath.setText(song.getTrackFile());
        songName.setText(song.getSongName());
        songArtist.setText(song.getArtist());
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initView() {

        nextPosition = getIntent().getIntExtra("position", 0);
        priviousPosition = getIntent().getIntExtra("position", 0);
        song = getIntent().getParcelableExtra("song");
        imageUri = getIntent().getStringExtra("imageUri");
        imgReplay = findViewById(R.id.iv_repeat_play_song_local);
        back = (ImageView) findViewById(R.id.iv_mp3_back);
        musicProgressBar = (CircularMusicProgressBar) findViewById(R.id.circle_image);
        priviousTrack = (ImageButton) findViewById(R.id.ButtonTestPreviousTrack);
        nextTrack = (ImageButton) findViewById(R.id.ButtonTestNextTrack);
        albumName = (TextView) findViewById(R.id.tv_albumName);
        duration = (TextView) findViewById(R.id.tv_duration);
        filePath = (TextView) findViewById(R.id.tv_filePath);
        songArtist = (TextView) findViewById(R.id.tv_artistName);
        songName = (TextView) findViewById(R.id.tv_songName);
        showDetails = (ImageView) findViewById(R.id.iv_show_details);
        rel_details = (RelativeLayout) findViewById(R.id.rel_details);
        rel_play = (RelativeLayout) findViewById(R.id.rel_play);
        songImg = (ImageView) findViewById(R.id.iv_songImage);
        barVisualizer = (CircleBarVisualizer) findViewById(R.id.test);
        buttonPlayPause = (ImageButton) findViewById(R.id.ButtonTestPlayPause);
        buttonPlayPause.setOnClickListener(this);
        seekBarProgress = (SeekBar) findViewById(R.id.SeekBarTestPlay);
        seekBarProgress.setMax(99); // It means 100% .0-99


        mediaPlayer = new MediaPlayer();
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
        //--ToDO test below more
        if (seekBarProgress.getProgress() == 99 || seekBarProgress.getProgress() == 98 || seekBarProgress.getProgress() == 100) {
//            buttonPlayPause.setImageResource(R.drawable.ic_play_button);
            if (!mediaPlayer.isLooping() & !mediaPlayer.isPlaying()) {
                nextTrack();

                if (!isFirstPlayInList) {
                    try {
                        mediaPlayer = MediaPlayer.create(this, Uri.parse(song.getTrackFile()));
                        barVisualizer.setPlayer(mediaPlayer.getAudioSessionId()); //setup barVisualizer player
                        isFirstPlayInList = true;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                mediaFileLengthInMilliseconds = mediaPlayer.getDuration(); // gets the song length in milliseconds from URL


                playMediaPlayer();

            }
        }
    }

    @Override
    public void onClick(View v) {
//previousTrack click listener
        if (v.getId() == R.id.ButtonTestPreviousTrack) {

            previousTrack();
//            audioWidget.controller().stop();
        }
// nextTrack click Listener
        if (v.getId() == R.id.ButtonTestNextTrack) {

            nextTrack();
//            audioWidget.controller().stop();
        }
//play or pause Button click Listener
        if (v.getId() == R.id.ButtonTestPlayPause) {

            if (!isFirstPlay) {
                try {
                    mediaPlayer = MediaPlayer.create(this, Uri.parse(song.getTrackFile()));
                    barVisualizer.setPlayer(mediaPlayer.getAudioSessionId()); //setup barVisualizer player
                    isFirstPlay = true;
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
//            NotificationService.onServiceStateChange = this;
        }
    }

    private void nextTrack() {

        if (SongAdapterLocal.isEndOfList(nextPosition + 1)) {
            Toast.makeText(getApplicationContext(), "this is the last track in list!!!", Toast.LENGTH_LONG).show();
        } else {
            if (!trackFlagPrivious & !trackFlagNext) {
                trackFlagNext = true;
            }

            if (trackFlagNext) {
                nextPosition = nextPosition + 1;
                priviousPosition = nextPosition - 1;
            }

            song = SongAdapterLocal.getSong(nextPosition);
            imageUri = song.getSongImageUri();
            setupView(song);
            try {
                buttonPlayPause.setImageResource(R.drawable.ic_play_button);
                bufferUpDate = false;
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

        song = SongAdapterLocal.getSong(priviousPosition);
        imageUri = song.getSongImageUri();
        setupView(song);
        try {
            buttonPlayPause.setImageResource(R.drawable.ic_play_button);
            bufferUpDate = false;
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
        if (Image.drawableToBitmap(imgReplay.getDrawable()).equals(Image.drawableToBitmap(getResources().getDrawable(R.drawable.replaycurrent)))) {
            mediaPlayer.setLooping(true);
        } else {
            mediaPlayer.setLooping(false);
        }
//--TODO debug service
//        serviceIntent = new Intent(this, NotificationService.class);
//        serviceIntent.setAction(Constants.ACTION.STARTFOREGROUND_ACTION);
//        serviceIntent.putExtra("song", song);
//        serviceIntent.putExtra("isPlaying", true);
//        startService(serviceIntent);
    }

    private void playMediaPlayer() {

        playMediaplayerSecound();

        primarySeekBarProgressUpdater();

    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (v.getId() == R.id.SeekBarTestPlay) {
            /* Seekbar onTouch event handler. Method which seeks MediaPlayer isSeekbarTouch seekBar primary progress secondaryProgress*/

            SeekBar sb = (SeekBar) v;
            int playPositionInMillisecconds = (mediaFileLengthInMilliseconds / 100) * sb.getProgress();
            mediaPlayer.seekTo(playPositionInMillisecconds);

        }
        return false;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        /* MediaPlayer onCompletion event handler. Method which calls then song playing is complete*/
        buttonPlayPause.setImageResource(R.drawable.ic_play_button);
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
            playMediaPlayer();
        } else {
            if (bufferUpDate) {
            }
            stopMediaPlayer();
        }
    }

    @Override
    public void onStopService() {
        if (bufferUpDate) {
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
            audioWidget.controller().albumCover(Image.uriToDrawable(imageUri, this));
        }
        isOncreate = true;
//        NotificationService.onServiceStateChange = this;
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (NotificationService.isServiceRun) {
            stopService(serviceIntent);
        }
    }

    public void replay(View view) {
        Bitmap bmpCurrent = Image.drawableToBitmap(imgReplay.getDrawable());
        Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.replaylist, null);
        Bitmap bmpReplayList = Image.drawableToBitmap(drawable);
        if (!bmpCurrent.equals(bmpReplayList)) {
            imgReplay.setImageDrawable(drawable);
            Toast.makeText(getApplicationContext(), "Play All Song In List", Toast.LENGTH_SHORT).show();
            mediaPlayer.setLooping(false);
        } else {
            mediaPlayer.setLooping(true);
            imgReplay.setImageResource(R.drawable.replaycurrent);
            Toast.makeText(getApplicationContext(), "Replay Current Song", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public boolean onPlaylistClicked() {
        Intent intent = new Intent(PlaySongLocal.this, PlaySongLocal.class);
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
        audioWidget.controller().albumCover(Image.uriToDrawable(imageUri, this));

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
        if (!isFirstPlay) {
            try {
                mediaPlayer = MediaPlayer.create(this, Uri.parse(song.getTrackFile()));
                barVisualizer.setPlayer(mediaPlayer.getAudioSessionId()); //setup barVisualizer player
                isFirstPlay = true;
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
        audioWidget.controller().albumCover(Image.uriToDrawable(imageUri, this));

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
}
