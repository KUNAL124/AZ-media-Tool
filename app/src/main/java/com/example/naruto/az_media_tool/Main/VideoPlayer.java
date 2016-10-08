package com.example.naruto.az_media_tool.Main;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.naruto.az_media_tool.Extras.OnSwipeTouchListener;
import com.example.naruto.az_media_tool.R;

import java.util.concurrent.TimeUnit;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class VideoPlayer extends AppCompatActivity {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */

    private Window window;
    private int brightnesslevel;
    private ContentResolver cResolver;
    private ImageButton settings;
    private TextView filename;
    private ImageButton destroy;
    private static final int PICK_VIDEO_REQUEST = 1001;
    private static final String TAG = "SurfaceSwitch";
    private MediaPlayer mMediaPlayer;
    private SurfaceHolder mFirstSurface;
    private Uri mVideoUri;
    private String name_video;
    private boolean complete;
    private TextView currentTime;
    private Handler SysHandler=new Handler();
    private boolean sys_visible=false;
    private TextView totalTime;
    private ImageButton play;
    private boolean Scrolled=false;
    private boolean playing = false;
    private SeekBar seek_bar;
    private View frame_up;
    private View frame_bot;
    Handler seekHandler = new Handler();

    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private View mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }

    };
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_video_player);

        mVisible = true;
        mContentView = findViewById(R.id.fullscreen_content);
        window=getWindow();
        frame_up=(FrameLayout)findViewById(R.id.frame_up);
        frame_bot=(FrameLayout)findViewById(R.id.frame_down);
        cResolver=getContentResolver();
        destroy=(ImageButton)findViewById(R.id.back);
        destroy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        filename=(TextView)findViewById(R.id.video_name);
        settings=(ImageButton)findViewById(R.id.settings);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createSettingsDialog();
            }
        });
        SurfaceView first = (SurfaceView) findViewById(R.id.firstSurface);
        seek_bar = (SeekBar) findViewById(R.id.player_progress);
        seek_bar.setPadding(3, 3, 3, 3);
        play = (ImageButton) findViewById(R.id.video_pause);
        currentTime = (TextView) findViewById(R.id.textView2);
        totalTime = (TextView) findViewById(R.id.textView3);
        Bundle bundle = getIntent().getExtras();
        name_video=bundle.getString("name");
        filename.setText(name_video);
        mVideoUri = Uri.parse(bundle.getString("videofile"));
        first.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                Log.d(TAG, "First surface created!");
                mFirstSurface = surfaceHolder;
                if (mVideoUri != null) {
                    createPlayer();
                    mMediaPlayer.start();
                    int duration = mMediaPlayer.getDuration();
                    seek_bar.setMax(duration);
                    String length = String.format("%02d:%02d",
                            TimeUnit.MILLISECONDS.toMinutes(duration),
                            TimeUnit.MILLISECONDS.toSeconds(duration) -
                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration))
                    );
                    totalTime.setText(length);
                    seekUpdation();
                    Sys_toogle(4000);
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                Log.d(TAG, "First surface destroyed!");
            }
        });

        first.setOnTouchListener(new OnSwipeTouchListener(this) {
            @Override
            protected void release() {
                hide();
                    frame_bot.setVisibility(View.VISIBLE);
                    frame_up.setVisibility(View.VISIBLE);
                    mMediaPlayer.start();
            }

            @Override
            protected void onLongPressed() {
            }
            @Override
            public  void onSingleTap(){
            }

            @Override
            protected void onScrollLeft(float diffX) {
                mMediaPlayer.pause();
                Scrolled=true;
                mMediaPlayer.seekTo((int) (mMediaPlayer.getCurrentPosition() - (1000)));
            }


            @Override
            protected void onScrollRight(float diffX) {
                mMediaPlayer.pause();
                Scrolled=true;
                mMediaPlayer.seekTo((int) (mMediaPlayer.getCurrentPosition() + (1000)));
            }
        });



        // Set up the user interaction to manually show or hide the system UI.
         mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.v("called","calling");
                toggle();
            }
        });

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
    }

    private void createSettingsDialog() {
        final Dialog settingsDialog = new Dialog(this);
        settingsDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        settingsDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = getLayoutInflater().inflate(R.layout.settings_dialog, null);
        settingsDialog.setContentView(view);
        SeekBar sound=(SeekBar)view.findViewById(R.id.seekBar);
        final SeekBar brightness=(SeekBar)view.findViewById(R.id.seekBar2);
        final AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        sound.setMax(audioManager
                .getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        sound.setProgress(audioManager
                .getStreamVolume(AudioManager.STREAM_MUSIC));
        sound.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onStopTrackingTouch(SeekBar arg0)
            {
            }

            @Override
            public void onStartTrackingTouch(SeekBar arg0)
            {
            }

            @Override
            public void onProgressChanged(SeekBar arg0, int progress, boolean arg2)
            {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                        progress, 0);
            }
        });
        brightness.setMax(255);
        //set the seek bar progress to 1float curBrightnessValue = 0;
        float curBrightnessValue = 0;
        try {
            curBrightnessValue = Settings.System.getInt(
                    getApplicationContext().getContentResolver(),Settings.System.SCREEN_BRIGHTNESS);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        Log.v("CurrentBright",""+curBrightnessValue);
        int screen_brightness = (int) curBrightnessValue;
        brightness.setProgress(screen_brightness);
        final int[] progress = new int[1];
        brightness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progresValue,
                                          boolean fromUser){
                progress[0] =progresValue;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Settings.System.putInt(getApplicationContext().getContentResolver(), Settings.System.SCREEN_BRIGHTNESS,
                        progress[0]);
            }
        });
        settingsDialog.show();
    }

    private void createPlayer() {
        mMediaPlayer = MediaPlayer.create(getApplicationContext(),
                mVideoUri, mFirstSurface);
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                complete = true;
                seek_bar.setProgress(seek_bar.getMax());
                playing = true;
                play.setImageResource(R.drawable.video_play);
            }
        });

        seek_bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser)
                    mMediaPlayer.seekTo(progress);
                if (complete && fromUser) {
                    {
                        mMediaPlayer.start();
                        mMediaPlayer.seekTo(progress);
                        complete = false;
                        seekUpdation();
                        playing = false;
                        play.setImageResource(R.drawable.video_pause);
                    }

                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }
    Runnable run = new Runnable() {
        @Override
        public void run() {
            int duration=mMediaPlayer.getCurrentPosition();
            currentTime.setText( String.format("%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(duration),
                    TimeUnit.MILLISECONDS.toSeconds(duration) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration))
            ))  ;
            seekUpdation();
        }
    };
    Runnable sys_run=new Runnable() {

        @Override
        public void run() {
            frame_bot.setVisibility(View.INVISIBLE);
            frame_up.setVisibility(View.INVISIBLE);
            Sys_toogle(3000);
        }

    };

    public void seekUpdation() {
        seek_bar.setProgress(mMediaPlayer.getCurrentPosition());
        seekHandler.postDelayed(run, 1000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        seekHandler.removeCallbacks(run);
        mMediaPlayer.stop();
        mMediaPlayer.release();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    public void Pause_play(View v){
        if (playing== false) {
            mMediaPlayer.pause();
            playing = true;
            play.setImageResource(R.drawable.video_play);
        } else {
            if (playing == true && complete!= true) {
                mMediaPlayer.start();
                playing = false;
                seekUpdation();
                play.setImageResource(R.drawable.video_pause);
            } else {
                mMediaPlayer.seekTo(0);
                mMediaPlayer.start();
                playing= false;
                seekUpdation();
                play.setImageResource(R.drawable.video_pause);
            }
        }
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        frame_up.setVisibility(View.INVISIBLE);
        frame_bot.setVisibility(View.INVISIBLE);
        mVisible = false;
        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }
    public void Sys_toogle(int delay){
        SysHandler.postDelayed(sys_run,delay);
    }
}
