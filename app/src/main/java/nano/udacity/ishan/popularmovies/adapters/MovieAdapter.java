package nano.udacity.ishan.popularmovies.adapters;

import android.app.Activity;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import nano.udacity.ishan.popularmovies.R;
import nano.udacity.ishan.popularmovies.common.Const;
import nano.udacity.ishan.popularmovies.data.Movie;

/**
 * Created by Ishan on 06-02-2016.
 */
public class MovieAdapter extends ArrayAdapter<Movie> {
    ArrayList<Movie> mMovies = new ArrayList<Movie>();

    public void setmMovies(ArrayList<Movie> mMovies) {
        this.mMovies = mMovies;
    }

    public MovieAdapter(Activity context, List<Movie> movies) {
        super(context, 0, movies);

        mMovies = (ArrayList<Movie>) movies;
    }

    @Override
    public int getCount() {
        return mMovies == null ? 0 : mMovies.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Movie getItem(int position) {
        return mMovies == null ? null : mMovies.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Movie viewMovie = getItem(position);

        if (convertView == null) {
            convertView = new ImageView(getContext());
        }
        ImageView view = (ImageView) convertView;

        /* Construct the poster download url - Hardcoded the poster resolution for now.
           TODO: Make the poster resolution i.e. poster size configurable via a setting
         */
        String url = Const.URL_MOVIE_POSTER + "/" + Const.SETTING_POSTER_SIZE_THUMBNAIL + getItem(position).getPosterPath();

        Picasso.Builder builder = new Picasso.Builder(getContext());

        /*
        DEBUG purposes
        builder.indicatorsEnabled(true);
         */

        builder.build()
                .load(url)
                .memoryPolicy(MemoryPolicy.NO_STORE) //Picasso OOM Bug Fix - Not sure if this is the correct fix, but without this memory policy in place app FC's after some scroll events on the gridview
                .fit()
                .placeholder(R.drawable.placeholder_w185)
                .error(R.drawable.error)
                .tag(getContext())
                .into(view);

        return view;
    }
}
