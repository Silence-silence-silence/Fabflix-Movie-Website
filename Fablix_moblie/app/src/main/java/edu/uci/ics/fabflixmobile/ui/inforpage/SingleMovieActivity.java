package edu.uci.ics.fabflixmobile.ui.inforpage;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import edu.uci.ics.fabflixmobile.R;
import edu.uci.ics.fabflixmobile.data.NetworkManager;
import edu.uci.ics.fabflixmobile.data.model.Movie;
import edu.uci.ics.fabflixmobile.ui.movielist.MainActivity;
import edu.uci.ics.fabflixmobile.ui.movielist.MovieListActivity;
import edu.uci.ics.fabflixmobile.ui.movielist.MovieListViewAdapter;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class SingleMovieActivity extends AppCompatActivity {
    private final String host = "ec2-54-151-116-40.us-west-1.compute.amazonaws.com";
    private final String port = "8443";
    private final String domain = "fablix";
    private final String baseURL = "https://" + host + ":" + port + "/" + domain;

    private String searchTitle;
    private String prev_url_string;

    ConstraintLayout movielist_canvas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single_movie);

        Bundle extras = getIntent().getExtras();
        searchTitle = extras.getString("searchTitle");
        try {
            JSONArray re = new JSONArray(extras.getString("resultData"));
            String movie_title = re.getJSONObject(0).getString("movie_title");
            String movie_year = re.getJSONObject(0).getString("movie_year");
            String movie_director = re.getJSONObject(0).getString("movie_director");
            String movie_gnames = re.getJSONObject(0).getString("genres_name");
            if (movie_gnames.equals("null")) {
                movie_gnames = "N/A";
            }
            String movie_snames = re.getJSONObject(0).getString("star_name");
            if (movie_snames.equals("null")) {
                movie_snames = "N/A";
            }
            else {
                String[] name_array = movie_snames.split(", ");
                movie_snames = "";
                for (int j = 0; j < name_array.length; j++) {
                    String star_name = name_array[j].split("-")[1];
                    movie_snames += star_name + ", ";
                }
                movie_snames = movie_snames.substring(0, movie_snames.length() - 2);
            }
            String movie_rating = re.getJSONObject(0).getString("movie_rating");
            if (movie_rating.equals("null")) {
                movie_rating = "N/A";
            }

            TextView title = findViewById(R.id.title);
            title.setText(movie_title);
            TextView year_value = findViewById(R.id.year_value);
            year_value.setText(movie_year);
            TextView director_value = findViewById(R.id.director_value);
            director_value.setText(movie_director);
            TextView gnames_value = findViewById(R.id.genre_value);
            gnames_value.setText(movie_gnames);
            TextView snames_value = findViewById(R.id.stars_value);
            snames_value.setText(movie_snames);
            TextView rating_value = findViewById(R.id.rating_value);
            rating_value.setText(movie_rating);

            final String prev_url = re.getJSONObject(1).getString("prev_url");
            prev_url_string = prev_url;
            Button back_button = findViewById(R.id.back_button);
            back_button.setOnClickListener(v -> { back(prev_url); });

        }
        catch (JSONException err) {
            Log.d("single-movie-data.error", err.toString());
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        back(prev_url_string);
    }

    @SuppressLint("SetTextI18n")
    public void back(String url){
        String searchURL = url.replace(".html", "s");
        // use the same network queue across our application
        final RequestQueue queue = NetworkManager.sharedManager(this).queue;
        // request type is POST
        final StringRequest loginRequest = new StringRequest(
                Request.Method.GET,
                baseURL + "/api/" + searchURL,
                response -> {
                    // TODO: should parse the json response to redirect to appropriate functions
                    //  upon different response value.
                    Log.d("search.success", response);
                    try{
                        JSONArray re = new JSONArray(response);
//                        re.getJSONObject(0);
                        if(re.length() > 1){
                            Log.d("search.success", re.getString(re.length() - 1));
//                            message.setText("Login Success");
                            //Complete and destroy login activity once successful
                            finish();
                            // initialize the activity(page)/destination
                            Intent MovieListPage = new Intent(SingleMovieActivity.this, MovieListActivity.class);
                            MovieListPage.putExtra("resultData", response);
                            MovieListPage.putExtra("searchTitle", searchTitle);
                            // activate the list page.
                            startActivity(MovieListPage);
                        }
                        else {
                            Log.d("search.fail", response);
                        }
                    }catch (JSONException err){
                        Log.d("search.error", err.toString());
                    }
                },
                error -> {
                    // error
                    Log.d("search.error", error.toString());
                }) {
        };
        // important: queue.add is where the login request is actually sent
        queue.add(loginRequest);
    }
}
