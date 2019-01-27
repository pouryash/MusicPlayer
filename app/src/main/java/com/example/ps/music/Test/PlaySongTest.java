package com.example.ps.music.Test;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
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

import com.chibde.visualizer.BarVisualizer;
import com.example.ps.music.R;
import com.example.ps.music.SongAdapter;
import com.example.ps.music.model.Song;
import com.example.ps.music.transfer.BureTransfer;
import com.example.ps.music.transfer.CircleTransform;
import com.example.ps.music.transfer.ShadowTransfer;
import com.squareup.picasso.Picasso;

public class PlaySongTest extends AppCompatActivity implements View.OnClickListener,
        View.OnTouchListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnBufferingUpdateListener {


    private ImageButton buttonPlayPause;
    private Song song;
    private TextView duration;
    private TextView downloadCont;
    private TextView albumName;
    private TextView songName;
    private TextView songArtist;
    private SeekBar seekBarProgress;
    private MediaPlayer mediaPlayer;
    private ImageView songImg;
    private ProgressBar progressBar;
    private BarVisualizer barVisualizer;
    private ProgressBar progressBar_load_song;
    private ImageButton nextTrack;
    private ImageButton priviousTrack;
    private RelativeLayout rel_details, rel_play;
    private ImageView showDetails;
    private boolean bufferUpDate = false;
    int secondaryProgress;
    int currentPosition;
    ImageView circleImageView;
    int nextPosition;
    int priviousPosition;
    boolean trackFlagPrivious = false;
    boolean trackFlagNext = false;
    private int mediaFileLengthInMilliseconds; // this value contains the song duration in milliseconds. Look at getDuration() method in MediaPlayer class

    private final Handler handler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.play_song_online);

        initView();
        setupView(song);

    }


    private void setupView(Song song) {

        Picasso.get().load(song.getSongImage()).transform(new CircleTransform()).into(circleImageView);
        Picasso.get().load(song.getSongImage()).placeholder(R.drawable.no_image).transform(new ShadowTransfer(50)).transform(new BureTransfer(this)).into(songImg);
        barVisualizer.setColor(getResources().getColor(R.color.colorPrimary));
        //set next song on  mediaplayer
        nextTrack.setOnClickListener(this);
        priviousTrack.setOnClickListener(this);

        showDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (rel_details.getVisibility() == View.VISIBLE) {
                    //set animation for routate showDetail icon
                    showDetails.setAnimation(setAnim(R.anim.rotate));
                    //set multi animation for Details with Animationset
                    AnimationSet set = new AnimationSet(true);
                    set.addAnimation(setAnim(R.anim.slide_down));
                    set.addAnimation(setAnim(R.anim.alpha));
                    rel_details.setAnimation(set);
                    //change rel_play parametrs to show details
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) rel_play.getLayoutParams();
                    params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
                    rel_play.setLayoutParams(params);
                    rel_details.setVisibility(View.GONE);
                    //change drawable of show details
                    Resources resources = getResources();
                    Resources.Theme theme = getTheme();
                    Drawable drawable = VectorDrawableCompat.create(resources, R.drawable.ic_up_arrow_white, theme);
                    showDetails.setImageDrawable(drawable);

                } else {

                    //set animation for routate showDetail icon
                    showDetails.setAnimation(setAnim(R.anim.reverse_rotate));
                    //set animation for slideUp Details
                    rel_details.setAnimation(setAnim(R.anim.slide_up));
                    //change rel_play parametrs to show details
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) rel_play.getLayoutParams();
                    params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 0);
                    rel_play.setLayoutParams(params);
                    rel_details.setVisibility(View.VISIBLE);
                    //change drawable of show details
                    Resources resources = getResources();
                    Resources.Theme theme = getTheme();
                    Drawable drawable = VectorDrawableCompat.create(resources, R.drawable.ic_down_arrow_white, theme);
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

//    public static int getDominantColor(Bitmap bitmap) {
//        List<Palette.Swatch> swatchesTemp = Palette.from(bitmap).generate().getSwatches();
//        List<Palette.Swatch> swatches = new ArrayList<Palette.Swatch>(swatchesTemp);
//        Collections.sort(swatches, new Comparator<Palette.Swatch>() {
//            @Override
//            public int compare(Palette.Swatch swatch1, Palette.Swatch swatch2) {
//                return swatch2.getPopulation() - swatch1.getPopulation();
//            }
//        });
//        return swatches.size() > 0 ? swatches.get(0).getRgb() : Color.argb(50, 50, 50, 50);
//    }


    private void initView() {

        nextPosition = getIntent().getIntExtra("position", 0);
        priviousPosition = getIntent().getIntExtra("position", 0);
        song = getIntent().getParcelableExtra("song");
        circleImageView = (ImageView) findViewById(R.id.iv_circle_image);
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
        barVisualizer = (BarVisualizer) findViewById(R.id.test);
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

        Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), animation);

        return anim;
    }

    private void primarySeekBarProgressUpdater() {
        seekBarProgress.setProgress((int) (((float) mediaPlayer.getCurrentPosition() / mediaFileLengthInMilliseconds) * 100)); // This math construction give a percentage of "was playing"/"song length"
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

        if (v.getId() == R.id.ButtonTestPreviousTrack) {
            if (!trackFlagPrivious & !trackFlagNext){
                trackFlagPrivious = true;
            }
            if (priviousPosition-1 == -1){

                Toast.makeText(getApplicationContext(),"this is first track of list!!", Toast.LENGTH_SHORT).show();
            }
            if (trackFlagPrivious & priviousPosition-1 != -1){
                priviousPosition = priviousPosition - 1;
                nextPosition = priviousPosition + 1;
            }

            song = SongAdapter.getSong(priviousPosition);
            setupView(song);

            if (!trackFlagPrivious & priviousPosition !=0){
                priviousPosition = priviousPosition - 1;
                nextPosition = priviousPosition + 1;
            }
        }

        if (v.getId() == R.id.ButtonTestNextTrack) {
            if (!trackFlagPrivious & !trackFlagNext){
                trackFlagNext = true;
            }

            if (trackFlagNext){
                nextPosition = nextPosition + 1;
                priviousPosition = nextPosition - 1;
            }

            song = SongAdapter.getSong(nextPosition);
            setupView(song);

            if (!trackFlagNext){
                nextPosition = nextPosition + 1;
                priviousPosition = nextPosition - 1;
            }

        }

        if (v.getId() == R.id.ButtonTestPlayPause) {
            if (!bufferUpDate) {
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                progressBar_load_song.setVisibility(View.VISIBLE);
            }

            /*ImageButton onClick event handler. Method which start/pause mediaplayer playing */
            try {
                mediaPlayer.setDataSource(song.getTrackFile() + "/download"); // setup song from http://www.hrupin.com/wp-content/uploads/mp3/testsong_20_sec.mp3 URL to mediaplayer data source
                barVisualizer.setPlayer(mediaPlayer.getAudioSessionId()); //setup barVisualizer player
                mediaPlayer.prepareAsync(); // you must call this method after setup the datasource in setDataSource method. After calling prepare() the instance of MediaPlayer starts load data from URL to internal buffer.
            } catch (Exception e) {
                e.printStackTrace();
            }

            mediaFileLengthInMilliseconds = mediaPlayer.getDuration(); // gets the song length in milliseconds from URL

            if (!mediaPlayer.isPlaying()) {
                if (bufferUpDate) {
                    mediaPlayer.start();
                    buttonPlayPause.setImageResource(R.drawable.ic_pause);
                    barVisualizer.setVisibility(View.VISIBLE);
                    progressBar_load_song.setVisibility(View.GONE);
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                } else {
                    mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mediaPlayer) {
                            mediaPlayer.start();
                            buttonPlayPause.setImageResource(R.drawable.ic_pause);
                            barVisualizer.setVisibility(View.VISIBLE);
                            progressBar_load_song.setVisibility(View.GONE);
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        }
                    });
                }
                primarySeekBarProgressUpdater();

            } else {
                mediaPlayer.pause();
                buttonPlayPause.setImageResource(R.drawable.ic_play_button);
            }
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (v.getId() == R.id.SeekBarTestPlay) {
            /* Seekbar onTouch event handler. Method which seeks MediaPlayer to seekBar primary progress secondaryProgress*/
            if (mediaPlayer.isPlaying()) {
                SeekBar sb = (SeekBar) v;
                int playPositionInMillisecconds = (mediaFileLengthInMilliseconds / 100) * sb.getProgress();

                if (seekBarProgress.getProgress() > secondaryProgress) {
                    Toast.makeText(getApplicationContext(), "this section is not loaded yet!!!", Toast.LENGTH_SHORT).show();
                    mediaPlayer.seekTo(currentPosition);
                } else {
                    mediaPlayer.seekTo(playPositionInMillisecconds);
                }
            }
        }
        return false;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        /** MediaPlayer onCompletion event handler. Method which calls then song playing is complete*/
        buttonPlayPause.setImageResource(R.drawable.ic_play_button);
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        bufferUpDate = true;
        /** Method which updates the SeekBar secondary progress by current song loading from URL secondaryProgress*/
        seekBarProgress.setSecondaryProgress(percent);
        secondaryProgress = percent;
        currentPosition = mediaPlayer.getCurrentPosition();
    }

    @Override
    public void onBackPressed() {
        this.finish();
        mediaPlayer.stop();
    }


}
