package nano.udacity.ishan.popularmovies.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Ishan on 01-02-2016.
 */
public class Movie implements Parcelable {
    String posterPath;
    String overview;
    String releaseDate;
    String title;
    //String[] genreIds;
    //String id;
    //String originalTitle;
    //String originalLanguage;
    //String adultFlag;

    protected Movie(Parcel in) {
        posterPath = in.readString();
        overview = in.readString();
        releaseDate = in.readString();
        title = in.readString();
        voteAverage = in.readString();
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(String voteAverage) {
        this.voteAverage = voteAverage;
    }

    @Override
    public String toString() {
        return "Movie{" +
                "posterPath='" + posterPath + '\'' +
                ", overview='" + overview + '\'' +
                ", releaseDate='" + releaseDate + '\'' +
                ", title='" + title + '\'' +
                ", voteAverage='" + voteAverage + '\'' +
                '}';
    }

    public Movie(String posterPath, String overview, String releaseDate, String title, String voteAverage) {
        this.posterPath = posterPath;
        this.overview = overview;
        this.releaseDate = releaseDate;
        this.title = title;
        this.voteAverage = voteAverage;
    }

    //String backdropPath;
    //String popularity;
    //String voteCount;
    //String video;
    String voteAverage;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(posterPath);
        dest.writeString(overview);
        dest.writeString(releaseDate);
        dest.writeString(title);
        dest.writeString(voteAverage);
    }

}
