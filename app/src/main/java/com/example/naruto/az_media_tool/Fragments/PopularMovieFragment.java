package com.example.naruto.az_media_tool.Fragments;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.naruto.az_media_tool.Adapters.CustomRecyclerAdapter;
import com.example.naruto.az_media_tool.Extras.Keys;
import com.example.naruto.az_media_tool.Extras.Movie;
import com.example.naruto.az_media_tool.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


/**
 * Created by Naruto on 6/23/2016.
 */
public class PopularMovieFragment extends Fragment {
    private static final String API_KEY="3b5a1bde121b8046aecd7c44c57adf90";
    private RecyclerView movierecyclerview;
    private CustomRecyclerAdapter movielistadapter;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //set that the menu has options
        setHasOptionsMenu(true);
        UpdateMovieList("http://api.themoviedb.org/3/movie/popular?");
    }
    public void UpdateMovieList(String string){
        FetchMovieList fetchMovieList=new FetchMovieList();
        fetchMovieList.execute(string);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.popular_menu,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            //going back to app list using homebutton
            case R.id.popular:
                menuItem.setCheckable(true);
                UpdateMovieList("http://api.themoviedb.org/3/movie/popular?");
                break;
            case R.id.top_rated:
                menuItem.setCheckable(true);
                UpdateMovieList("http://api.themoviedb.org/3/movie/top_rated?");
                break;
            case R.id.upcoming:
                menuItem.setCheckable(true);
                UpdateMovieList("http://api.themoviedb.org/3/movie/upcoming?");
        }
        return (super.onOptionsItemSelected(menuItem));
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View view=inflater.inflate(R.layout.popular_movie_fragment,container,false);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Movie Box");
        movierecyclerview = (RecyclerView) view.findViewById(R.id.recyler_view123);
        movierecyclerview.setLayoutManager(new GridLayoutManager(this.getActivity(),2));
        movielistadapter = new CustomRecyclerAdapter(getActivity());
        movierecyclerview.setAdapter(movielistadapter);
        return view;
    }

    //movie fetching
    public class FetchMovieList extends AsyncTask<String,Void,ArrayList<Movie>> {

        @Override
        protected ArrayList<Movie> doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            ArrayList<Movie> movie_array_list=new ArrayList<>();
            // Will contain the raw JSON response as a string.
            String MoviejsonRequest = null;
            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are avaiable at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
                //Defining different parts of the url and building uri
                //by uri builder class
                //after uri creation it is converted to url
                final String URL_BASE =params[0];
                final String appid = "api_key";
                Uri built_uri = Uri.parse(URL_BASE).buildUpon()
                        .appendQueryParameter(appid, API_KEY).build();
                Log.v("lat123:", "" + built_uri.toString());
                URL url = new URL(built_uri.toString());
                // Create the request to OpenWeatherMap, and open the connection

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                MoviejsonRequest = buffer.toString();
            } catch (
                    IOException e
                    )

            {
                Log.e("Log_Tag", "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                MoviejsonRequest = null;
            } finally

            {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("Log_tag", "Error closing stream", e);
                    }
                }
            }
            //define the data types needed to store the data of the Movie object
            String overview;
            String release_date;
            String title;
            long vote_count;
            float vote_average;
            try {
                JSONObject movierlist = new JSONObject(MoviejsonRequest);
                JSONArray movieresult=movierlist.getJSONArray(Keys.Movie_keys.RESULTS);
                for(int i=0;i<movieresult.length();i++){
                    String backdrop_path="http://image.tmdb.org/t/p/w500";
                    String poster_path="http://image.tmdb.org/t/p/w342";
                    JSONObject movielistobject=movieresult.getJSONObject(i);
                    poster_path=poster_path+movielistobject.getString(Keys.Movie_keys.POSTER_PATH);
                    overview=movielistobject.getString(Keys.Movie_keys.OVERVIEW);
                    release_date=movielistobject.getString(Keys.Movie_keys.RELEASE_DATE);
                    title=movielistobject.getString(Keys.Movie_keys.TITLE);
                    backdrop_path=backdrop_path+movielistobject.getString(Keys.Movie_keys.POSTER_PATH);
                    vote_count=movielistobject.getInt(Keys.Movie_keys.VOTE_COUNT);
                    vote_average=movielistobject.getLong(Keys.Movie_keys.VOTE_AVERAGE);

                    //store the data in the movie object
                    com.example.naruto.az_media_tool.Extras.Movie movie=new com.example.naruto.az_media_tool.Extras.Movie();
                    movie.setPoster_Path(poster_path);
                    movie.setOverview(overview);
                    movie.setRelease_date(ReleaseDate(release_date));
                    movie.setTitle(title);
                    movie.setBackdrop_path(backdrop_path);
                    movie.setVotes(vote_count);
                    movie.setRating(vote_average);

                    movie_array_list.add(movie);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return movie_array_list;
        }

        @Override
        protected void onPostExecute(ArrayList<Movie> movies) {
            movielistadapter.setMovieList(movies);
        }
    }


    public static BigDecimal round(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd;
    }
    public static String ReleaseDate(String string) throws ParseException {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date=format.parse(string);
        SimpleDateFormat format2=new SimpleDateFormat("d M yyyy");
        String formetteddate=format2.format(date);
        return formetteddate;
    }
}
