package nano.udacity.ishan.popularmovies;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import nano.udacity.ishan.popularmovies.common.Const;
import nano.udacity.ishan.popularmovies.data.Movie;

/**
 * Fragment for MovieDetailActivity - displays movie information and posters etc selected
 * from the main activity.
 */
public class MovieDetailActivityFragment extends Fragment implements View.OnClickListener {

    private static final String LOG_TAG = "MovieDetail";

    public MovieDetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);

        Button favoriteButton = (Button) rootView.findViewById(R.id.favorite_button);
        favoriteButton.setOnClickListener(this);

        Intent movieDetailIntent = getActivity().getIntent();
        Movie thisMovie = (Movie) movieDetailIntent.getParcelableExtra("MOVIE_PARCEL");
        //Log.v(LOG_TAG, "Movie from parcel: " + thisMovie.toString());

        final ImageView posterLargeImageView = (ImageView) rootView.findViewById(R.id.movie_poster_large_imageview);

        /* Code borrowed from http://stackoverflow.com/questions/21889735/resize-image-to-full-width-and-variable-height-with-picasso
        *  Initial design was to show the movie poster full-size in the background. Not using it for the current version, hence commenting
        *  it out
        *
        Transformation fullsizeTransformation = new Transformation() {

            @Override
            public Bitmap transform(Bitmap source) {
                int targetWidth = getActivity().getWindowManager().getDefaultDisplay().getWidth();
                double aspectRatio = (double) source.getHeight() / (double) source.getWidth();
                int targetHeight = (int) (targetWidth * aspectRatio);
                Bitmap result = Bitmap.createScaledBitmap(source, targetWidth, targetHeight, false);
                if (result != source) {
                    // Same bitmap is returned if sizes are the same
                    source.recycle();
                }
                return result;
            }

            @Override
            public String key() {
                return "transformation" + " desiredWidth";
            }
        };
        */

        TextView movieNameTextView = (TextView) rootView.findViewById(R.id.movie_name_textview);
        movieNameTextView.setText(thisMovie.getTitle());

        //getActivity().setTitle(thisMovie.getTitle());

        TextView movieOverviewTextView = (TextView) rootView.findViewById(R.id.movie_overview_textview);
        movieOverviewTextView.setText(thisMovie.getOverview());

        TextView movieUserRatingTextView = (TextView) rootView.findViewById(R.id.movie_userrating_textview);
        movieUserRatingTextView.setText(thisMovie.getVoteAverage()+" / 10");

        TextView movieReleaseDateTextView = (TextView) rootView.findViewById(R.id.movie_release_date_textview);
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date releaseDate = simpleDateFormat.parse(thisMovie.getReleaseDate());
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(releaseDate);
            movieReleaseDateTextView.setText(String.valueOf(calendar.get(Calendar.YEAR)));
        } catch (ParseException e) {
            e.printStackTrace();
        }


        /* Constructing the movie poster url
         * TODO: make the poster resolution configurable via a setting
         */
        String moviePosterUrl = Const.URL_MOVIE_POSTER + "/" + Const.SETTING_POSTER_SIZE_THUMBNAIL + thisMovie.getPosterPath();

/*        Picasso.with(getActivity())
                .load(moviePosterUrl)
                .placeholder(R.drawable.placeholder)
                .transform(fullsizeTransformation)
                .into(posterLargeImageView);
*/
        Picasso.Builder builder = new Picasso.Builder(getActivity());
        //builder.indicatorsEnabled(true);
        builder.build()
                .load(moviePosterUrl)
                .fit()
                .placeholder(R.drawable.placeholder_w185)
                .error(R.drawable.error)
                .into(posterLargeImageView);

        return rootView;
    }

    @Override
    public void onClick(View v) {
        Snackbar.make(v, R.string.message_favorite_added, Snackbar.LENGTH_SHORT).show();
    }
}
