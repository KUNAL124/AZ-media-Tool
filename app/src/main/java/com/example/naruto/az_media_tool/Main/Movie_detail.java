package com.example.naruto.az_media_tool.Main;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.naruto.az_media_tool.Extras.Movie;
import com.example.naruto.az_media_tool.R;
import com.squareup.picasso.Picasso;

public class Movie_detail extends AppCompatActivity {

    //Movie object
    Movie movie;
    ImageView backdrop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        //set the custom toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.popular_movie_toolbar);
        setSupportActionBar(toolbar);
        //set the home button enabled
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //set the tile to "Popula Movies"
        getSupportActionBar().setTitle("Movie Details");
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        //get the data from the bundle
        Bundle bundle = getIntent().getExtras();
        movie = bundle.getParcelable("Movie_Details");
        //get the movie data to be set in the movie details
        String overview = movie.getOverview();
        String releasedate = movie.getRelease_date();
        String title = movie.getTitle();
        String backdrop_path = movie.getPoster_Path();
        int vote_count = (int) movie.getVotes();
        float rating = movie.getRating();
        //setting the widgets
        TextView heading = (TextView) findViewById(R.id.heading);
        TextView votes = (TextView) findViewById(R.id.votes);
        backdrop= (ImageView) findViewById(R.id.backdrop);
        RatingBar ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        TextView overview_details = (TextView) findViewById(R.id.overview);
        TextView releasedate_text = (TextView) findViewById(R.id.releasedate);
        //set the data in the widgets
        heading.setText(title);
        votes.setText("Votes: " + vote_count);
        if(movie.getPoster_Path()==null){
            backdrop.setImageResource(R.mipmap.no_poster_image);
        }else{
        Picasso.with(this).load(backdrop_path).into(backdrop);
        }
        ratingBar.setRating(rating / 2.0F);
        releasedate_text.setText("Release Date: " + releasedate);
        overview_details.setText(overview);
        //item click listener on image view
        backdrop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                display_dialogue();
            }
        });
    }

    private void display_dialogue() {
        Dialog settingsDialog = new Dialog(this);
        settingsDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        View view=getLayoutInflater().inflate(R.layout.poster_image_onclick, null);
        settingsDialog.setContentView(view);
        ImageView imageView=(ImageView)view.findViewById(R.id.SET_IMAGE);
        Picasso.with(this).load(movie.getBackdrop_path()).into(imageView);
        settingsDialog.show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //going back to app list using homebutton
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
