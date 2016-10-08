package com.example.naruto.az_media_tool.Extras;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Naruto on 7/7/2016.
 */
//defining all the data to be fetched for the movie item

public class Movie implements Parcelable {
    private String Poster_Path;
    private String Overview;
    private String release_date;
    private String title;
    private String backdrop_path;
    private long votes;
    private float rating;

    public String getBackdrop_path() {
        return backdrop_path;
    }

    public void setBackdrop_path(String backdrop_path) {
        this.backdrop_path = backdrop_path;
    }

    public String getOverview() {
        return Overview;
    }

    public void setOverview(String overview) {
        Overview = overview;
    }


    public String getPoster_Path() {
        return Poster_Path;
    }

    public void setPoster_Path(String poster_Path) {
        Poster_Path = poster_Path;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getRelease_date() {
        return release_date;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getVotes() {
        return votes;
    }

    public void setVotes(long votes) {
        this.votes = votes;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.Poster_Path);
        dest.writeString(this.Overview);
        dest.writeString(this.release_date);
        dest.writeString(this.title);
        dest.writeString(this.backdrop_path);
        dest.writeLong(this.votes);
        dest.writeFloat(this.rating);
    }

    public Movie() {
    }

    protected Movie(Parcel in) {
        this.Poster_Path = in.readString();
        this.Overview = in.readString();
        this.release_date = in.readString();
        this.title = in.readString();
        this.backdrop_path = in.readString();
        this.votes = in.readLong();
        this.rating = in.readFloat();
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel source) {
            return new Movie(source);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
}
