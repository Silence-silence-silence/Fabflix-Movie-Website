package edu.uci.ics.fabflixmobile.data.model;

/**
 * Movie class that captures movie information for movies retrieved from MovieListActivity
 */
public class Movie {
    private final String name;
    private final short year;
    private final String director;
    private final String movieId;
    private final String genres;
    private final String stars;
    private final String rating;

    public Movie(String name, short year, String director, String movieId, String genres, String stars, String rating) {
        this.name = name;
        this.year = year;
        this.director = director;
        this.movieId = movieId;
        this.genres = genres;
        this.stars = stars;
        this.rating = rating;
    }

    public String getName() {
        return name;
    }

    public short getYear() {
        return year;
    }

    public final String getDirector() {
        return director;
    }

    public final String getId() {
        return movieId;
    }

    public final String getGenres() {
        return genres;
    }

    public final String getStars() {
        return stars;
    }

    public final String getRating() {
        return rating;
    }
}