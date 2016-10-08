package com.example.naruto.az_media_tool.Main;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.example.naruto.az_media_tool.DatabaseHelper.DbBitmapUtility;
import com.example.naruto.az_media_tool.Fragments.Audio_List;
import com.example.naruto.az_media_tool.Fragments.Net_Error_Frag;
import com.example.naruto.az_media_tool.Fragments.PopularMovieFragment;
import com.example.naruto.az_media_tool.Fragments.Video_list;
import com.example.naruto.az_media_tool.NetState.Network_state;
import com.example.naruto.az_media_tool.R;
import com.snappydb.DB;
import com.snappydb.DBFactory;
import com.snappydb.SnappydbException;

import java.io.File;
import java.util.concurrent.TimeUnit;

public class MAIN_PAGE extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private boolean Movie_present = false;
    private boolean Audio = false;
    private boolean Video = false;
    private Context context = getBaseContext();
    DrawerLayout drawer;
    private DB snappydb=null;
    View loading;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main__page);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Home");
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        toolbar.setNavigationIcon(R.drawable.navigation_icon);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        loading=(View)findViewById(R.id.loading_wait);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };

        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        //if the app is running first time than navigation drawer will open  otherwise not
        SharedPreferences preferences = getSharedPreferences(getString(R.string.Pref_file), MODE_PRIVATE);
        if (preferences.getBoolean(getString(R.string.first_run), true)) {
            drawer.openDrawer(GravityCompat.START);
            preferences.edit().putBoolean(getString(R.string.first_run), false).apply();
        } else {
            drawer.closeDrawer(GravityCompat.START);
        }
        try {
            snappydb = DBFactory.open(getApplicationContext());
        } catch (SnappydbException e) {
            e.printStackTrace();
        }
        InitalizeList initalize=new InitalizeList();
        initalize.execute();
        SetContent();
    }

    private void SetContent() {

        //checking for the network connectivity
        if (Network_state.IsNetworkAvailable(this)) {
            SetMovieView();
        } else {
            SetAlertView();
        }
    }


    //creating fragment view of the movie app
    private void SetMovieView() {
        //creating and begning transaction of the popular movie app
        Movie_present = true;
        getSupportFragmentManager().beginTransaction().replace(R.id.popular_movie_main_page, new PopularMovieFragment()).commit();
    }

    //inflating the network alert dialogue
    private void SetAlertView() {
        //set the view to alert view
        Movie_present = false;
        getSupportFragmentManager().beginTransaction().replace(R.id.popular_movie_main_page, new Net_Error_Frag()).commit();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            if (Movie_present == false)
                Movie_present = true;
            Audio = false;
            Video = false;
            SetContent();
        } else if (id == R.id.Audios) {
            if (Audio == false) {
                Movie_present = false;
                Audio = true;
                Video = false;
                getSupportFragmentManager().beginTransaction().replace(R.id.popular_movie_main_page, new Audio_List()).commit();
            }
        } else if (id == R.id.Videos) {
            if (Video == false) {
                Movie_present = false;
                Audio = false;
                Video = true;
                getSupportFragmentManager().beginTransaction().replace(R.id.popular_movie_main_page, new Video_list()).commit();
            }
        } else if (id == R.id.settings) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public class InitalizeList extends AsyncTask<Void, Void, Void> {

        MediaMetadataRetriever retriver = new MediaMetadataRetriever();

        @Override
        protected Void doInBackground(Void... params) {
            File f = new File("/storage");
            find_files(f);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            loading.setVisibility(View.GONE);
        }

        public void find_files(File root) {
            MediaMetadataRetriever retriever=new MediaMetadataRetriever();
            File f = root;
            File[] files = f.listFiles();
            for (File singleFile : files) {
                if (singleFile.isDirectory()) {
                    find_files(singleFile);
                } else {
                    String filename = singleFile.getName();
                    if (filename.endsWith(".mp4")||filename.endsWith(".mkv")||filename.endsWith(".mkv")||filename.endsWith(".avi")||filename.endsWith(".3gp")||filename.endsWith(".webm")) {
                        byte[] image;
                        String duration;
                        try {
                            if (snappydb.exists(singleFile.getName() + " image")) {

                            } else {
                                retriver.setDataSource(singleFile.getAbsolutePath());
                                duration = retriver.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                                long durations = Long.parseLong(duration);
                                Bitmap bm = ThumbnailUtils.createVideoThumbnail(singleFile.getAbsolutePath(), MediaStore.Images.Thumbnails.MINI_KIND);
                                String length = String.format("%02d:%02d",
                                        TimeUnit.MILLISECONDS.toMinutes(durations),
                                        TimeUnit.MILLISECONDS.toSeconds(durations) -
                                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(durations))
                                );
                                snappydb.put(singleFile.getName() + " duration", length);

                                if (bm == null)
                                    snappydb.put(singleFile.getName() + " image", "");
                                else
                                    snappydb.put(singleFile.getName() + " image", DbBitmapUtility.getBytes(bm));
                            }
                        } catch (SnappydbException ex) {

                        }
                    }
                }
            }
        }

    }
}