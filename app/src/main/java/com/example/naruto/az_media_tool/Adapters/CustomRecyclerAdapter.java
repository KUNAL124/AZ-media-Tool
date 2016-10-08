package com.example.naruto.az_media_tool.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.naruto.az_media_tool.Extras.Movie;
import com.example.naruto.az_media_tool.Main.Movie_detail;
import com.example.naruto.az_media_tool.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


/**
 * Created by Naruto on 7/6/2016.
 */
public class CustomRecyclerAdapter extends RecyclerView.Adapter<CustomRecyclerAdapter.ViewHolderMovieList> {
    private LayoutInflater layoutInflater;
    Context context;
    private ArrayList<Movie> Movie_list = new ArrayList<Movie>();

    public CustomRecyclerAdapter(Context context) {
        layoutInflater = LayoutInflater.from(context);
       this.context = context;
    }

    public void setMovieList(ArrayList<Movie> movie_list) {
        Movie_list = movie_list;
        notifyItemRangeChanged(0, Movie_list.size());
    }

    @Override
    public void onBindViewHolder(final ViewHolderMovieList holder, int position) {
        Movie currentmovie = Movie_list.get(position);
        //setting the poster image in the grid view
        //check if poster exists
        Picasso.with(context).load(currentmovie.getPoster_Path()).error(R.drawable.noposter).into(holder.poster_image);
    }

    @Override
    public ViewHolderMovieList onCreateViewHolder(final ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.movie_list_row, parent, false);
        ViewHolderMovieList viewHolderWeatherForcast = new ViewHolderMovieList(view, new ViewHolderMovieList.ViewHolderInterface() {
            @Override
            public void showDetails(View v, int position) {
                Intent i = new Intent(context,Movie_detail.class);
                i.putExtra("Movie_Details", Movie_list.get(position));
                context.startActivity(i);
            }
        });
        return viewHolderWeatherForcast;
    }

    @Override
    public int getItemCount() {
        return Movie_list.size();
    }

    static class ViewHolderMovieList extends RecyclerView.ViewHolder implements View.OnClickListener {
        //define all the widgets ids
        ImageView poster_image;
        private ViewHolderInterface myclickhandler;

        public ViewHolderMovieList(View itemView, ViewHolderInterface m) {
            super(itemView);
            poster_image=(ImageView)itemView.findViewById(R.id.movie_poter);
            myclickhandler = m;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            myclickhandler.showDetails(v, getAdapterPosition());
        }

        interface ViewHolderInterface {
            public void showDetails(View v, int position);
        }
    }

}

