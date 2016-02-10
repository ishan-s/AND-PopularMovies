package nano.udacity.ishan.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;

import nano.udacity.ishan.popularmovies.adapters.MovieAdapter;
import nano.udacity.ishan.popularmovies.common.Const;
import nano.udacity.ishan.popularmovies.data.Movie;

/**
 * Fragment holding the GridView of movie poster images.
 */
public class MainActivityFragment extends Fragment {



    GridView imageGridView;
    MovieAdapter mMovieAdapter;
    ArrayList<Movie> mMovies = new ArrayList<Movie>();
    String currentSortOrder = Const.SETTING_SORT_ORDER_POPULARITY;

    private static final String LOG_TAG = "MainActivityFragment";

    public MainActivityFragment() {
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("SAVED_MOVIES", mMovies);
        //TODO: Save the scroll position ?

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main_fragment, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    /* Utility method to refetch images from TMD and refresh the image grid */
    private void reloadImageGrid() {
        FetchMoviePostersTask fetchMoviePostersTask = new FetchMoviePostersTask();
        fetchMoviePostersTask.execute(currentSortOrder);
        mMovieAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor = sharedPreferences.edit();

        switch (item.getItemId()) {
            case R.id.sortorder_popularity:
                editor.putString(getString(R.string.key_sortorder), Const.SETTING_SORT_ORDER_POPULARITY);
                editor.commit();

                currentSortOrder = Const.SETTING_SORT_ORDER_POPULARITY;
                reloadImageGrid();
                break;
            case R.id.sortorder_rating:
                editor.putString(getString(R.string.key_sortorder), Const.SETTING_SORT_ORDER_RATING);
                editor.commit();

                currentSortOrder = Const.SETTING_SORT_ORDER_RATING;
                reloadImageGrid();
                break;
            case R.id.action_refresh:
                reloadImageGrid();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        /*
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        String prevSortOrder = currentSortOrder;
        currentSortOrder = sharedPreferences.getString(getString(R.string.key_sortorder), Const.SETTING_SORT_ORDER_POPULARITY);
        if (!prevSortOrder.equals(currentSortOrder)) {

            FetchMoviePostersTask fetchMoviePostersTask = new FetchMoviePostersTask();
            fetchMoviePostersTask.execute(currentSortOrder);

            mMovieAdapter.notifyDataSetChanged();
        }
        */

        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        setHasOptionsMenu(true);

        imageGridView = (GridView) rootView.findViewById(R.id.main_image_gridview);
        mMovieAdapter = new MovieAdapter(getActivity(), mMovies);
        imageGridView.setAdapter(mMovieAdapter);
        imageGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Movie thisMovie = mMovieAdapter.getItem(position);
                Intent movieDetailIntent = new Intent(getActivity(), MovieDetailActivity.class);

                movieDetailIntent.putExtra("MOVIE_PARCEL", thisMovie);
                startActivity(movieDetailIntent);
                // Just Experimenting with transition animations :)
                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

            }
        });

        if(savedInstanceState == null || !savedInstanceState.containsKey("SAVED_MOVIES")) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            FetchMoviePostersTask fetchMoviePostersTask = new FetchMoviePostersTask();
            fetchMoviePostersTask.execute(sharedPreferences.getString(getString(R.string.key_sortorder), Const.SETTING_SORT_ORDER_POPULARITY));
        }
        else{
            mMovies.clear();
            mMovies.addAll((ArrayList)savedInstanceState.getParcelableArrayList("SAVED_MOVIES"));
            mMovieAdapter.notifyDataSetChanged();
        }
        return rootView;
    }

    public class FetchMoviePostersTask extends AsyncTask<String, Void, ArrayList<Movie>> {

        @Override
        protected void onPostExecute(ArrayList<Movie> movies) {
            mMovieAdapter.notifyDataSetChanged();
        }

        @Override
        protected ArrayList doInBackground(String... params) {
            mMovies.clear();
            mMovies.addAll(fetchMovieInfo(params[0]));
            return mMovies;
        }

        private ArrayList<Movie> fetchMovieInfo(String sortOrder) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String movieInfoJsonStr = null;
            JSONArray movieInfoJsonArray = null;
            ArrayList<Movie> movieArrayList = new ArrayList<Movie>();

            Uri.Builder uriBuilder = Uri.parse(Const.URL_INIT_MOVIE_INFO)
                    .buildUpon()
                    .appendQueryParameter(Const.URL_PARAM_SORT_ORDER, sortOrder)
                    .appendQueryParameter(Const.URL_PARAM_API_KEY, Const.TMD_API_KEY);

            try {
                URL url = new URL(uriBuilder.build().toString());
                Log.v(LOG_TAG, "Connecting to :" + url.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    Log.e(LOG_TAG, "FetchMoviePostersTask: inputStream from the request is null...");
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    Log.e(LOG_TAG, "FetchMoviePostersTask: null response body..");
                    return null;
                }

                movieInfoJsonStr = buffer.toString();
                Log.v(LOG_TAG, "JSON response: " + movieInfoJsonStr);

                JSONObject movieInfoJson = new JSONObject(movieInfoJsonStr);
                movieInfoJsonArray = movieInfoJson.getJSONArray("results");

                for (int i = 0; i < movieInfoJsonArray.length(); i++) {
                    JSONObject movieJSON = movieInfoJsonArray.getJSONObject(i);
                    Movie movie = new Movie(
                            movieJSON.getString("poster_path"),
                            movieJSON.getString("overview"),
                            movieJSON.getString("release_date"),
                            movieJSON.getString("original_title"),
                            movieJSON.getString("vote_average")
                    );
                    //Log.v(LOG_TAG, "Adding movie: " + movie.toString());
                    movieArrayList.add(movie);
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("MainActivityFragment", "Error closing stream", e);
                    }
                }
            }

            return movieArrayList;
        }
    }

}
