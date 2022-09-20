package edu.uci.ics.fabflixmobile.ui.movielist;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import edu.uci.ics.fabflixmobile.R;
import edu.uci.ics.fabflixmobile.data.NetworkManager;
import edu.uci.ics.fabflixmobile.data.model.Movie;
import edu.uci.ics.fabflixmobile.ui.inforpage.SingleMovieActivity;
import edu.uci.ics.fabflixmobile.ui.login.LoginActivity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MovieListActivity extends AppCompatActivity {
    private final String host = "ec2-54-151-116-40.us-west-1.compute.amazonaws.com";
    private final String port = "8443";
    private final String domain = "fablix";
    private final String baseURL = "https://" + host + ":" + port + "/" + domain;
    private String searchTitle;

    ConstraintLayout movielist_canvas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movielist);

        LinearLayout pagination = findViewById(R.id.pagination);

        final ArrayList<Movie> movies = new ArrayList<>();
        Bundle extras = getIntent().getExtras();
        searchTitle = extras.getString("searchTitle");
        try {
            JSONArray re = new JSONArray(extras.getString("resultData"));
            for (int i = 0; i < re.length() - 1; i++) {
                String movie_title = re.getJSONObject(i).getString("movie_title");
                short movie_year = (short) re.getJSONObject(i).getInt("movie_year");
                String movie_director = re.getJSONObject(i).getString("movie_director");
                String movie_id = re.getJSONObject(i).getString("movie_id");
                String movie_gnames = re.getJSONObject(i).getString("movie_gnames");
                if (movie_gnames.equals("null")) {
                    movie_gnames = "N/A";
                }
                String movie_snames = re.getJSONObject(i).getString("movie_snames");
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
                String movie_rating = re.getJSONObject(i).getString("movie_rating");
                if (movie_rating.equals("null")) {
                    movie_rating = "N/A";
                }
                movies.add(new Movie(movie_title, movie_year, movie_director, movie_id, movie_gnames, movie_snames, movie_rating));
            }

            Integer t = re.getJSONObject(re.length() - 1).getInt("totalResults");
            Integer a = re.getJSONObject(re.length() - 1).getInt("startIndex");
            Integer b = 20;

            if (a > 0) {
                Integer i = a - b;
                if (i < 0) {
                    i = 0;
                }
                final Integer prev_index = i;
                Button prev = findViewById(R.id.prev_button);
                prev.setOnClickListener(v -> search(String.valueOf(b), String.valueOf(prev_index)));
            }
            else {
                Button prev = findViewById(R.id.prev_button);
                prev.setEnabled(false);
            }

            final Integer next_index = a + b;
            if (next_index < t) {
                Button next = findViewById(R.id.next_button);
                next.setOnClickListener(v -> search(String.valueOf(b), String.valueOf(next_index)));
            }
            else {
                Button next = findViewById(R.id.next_button);
                next.setEnabled(false);
            }


            for (int i = 0; i < Math.ceil(t / 20.0); i++) {
                final int num = i;
                if (Math.abs(a - (20 * num)) < 3 * 20) {
                    Button pagination_button = new Button(this);
                    LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
                    p.weight = 1;
                    pagination_button.setLayoutParams(p);
                    pagination_button.setText(String.valueOf( num + 1));
//                    pagination_button.setLayoutParams(new ConstraintLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT));
                    pagination_button.setOnClickListener(v -> search(String.valueOf(20), String.valueOf(20 * num)));
                    if(Math.abs(a - (20 * num)) == 0) {
                        pagination_button.setEnabled(false);
                    }
                    if (pagination != null) {
                        pagination.addView(pagination_button);
                    }
                }
            }
        }
        catch (JSONException err) {
            Log.d("movies.error", err.toString());
        }
        // TODO: this should be retrieved from the backend server
        MovieListViewAdapter adapter = new MovieListViewAdapter(this, movies);
        ListView listView = findViewById(R.id.list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            Movie movie = movies.get(position);
//            @SuppressLint("DefaultLocale") String message = String.format("Clicked on position: %d, name: %s, %d", position, movie.getName(), movie.getYear());
//            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            get_single_movie(movie.getId());
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        // initialize the activity(page)/destination
        Intent SearchMainPage = new Intent(MovieListActivity.this, MainActivity.class);
        // activate the list page.
        startActivity(SearchMainPage);
    }

    @SuppressLint("SetTextI18n")
    public void search(String numRecords, String startIndex){
        String searchURL = "movies?name=&director=&stars=&year=&genre=null&AZ=null&sortBy1=null&order1=null&sortBy2=null&order2=null&numRecords=" + numRecords + "&startIndex=" + startIndex + "&fullSearch=";
        String url = searchURL + searchTitle;
        // use the same network queue across our application
        final RequestQueue queue = NetworkManager.sharedManager(this).queue;
        // request type is POST
        final StringRequest loginRequest = new StringRequest(
                Request.Method.GET,
                baseURL + "/api/" + url,
                response -> {
                    // TODO: should parse the json response to redirect to appropriate functions
                    //  upon different response value.
                    Log.d("page.success", response);
                    try{
                        JSONArray re = new JSONArray(response);
//                        re.getJSONObject(0);
                        if(re.length() > 1){
                            Log.d("page.success", re.getString(re.length() - 1));
//                            message.setText("Login Success");
                            //Complete and destroy login activity once successful
                            finish();
                            // initialize the activity(page)/destination
                            Intent MovieListPage = new Intent(MovieListActivity.this, MovieListActivity.class);
                            MovieListPage.putExtra("resultData", response);
                            MovieListPage.putExtra("searchTitle", searchTitle);
                            // activate the list page.
                            startActivity(MovieListPage);
                        }
                        else {
                            Log.d("page.fail", response);
                        }
                    }catch (JSONException err){
                        Log.d("page.error", err.toString());
                    }
                },
                error -> {
                    // error
                    Log.d("page.error", error.toString());
                }) {
        };
        // important: queue.add is where the login request is actually sent
        queue.add(loginRequest);
    }

    @SuppressLint("SetTextI18n")
    public void get_single_movie(String id){
        String url = "single-movie?id=" + id;
        // use the same network queue across our application
        final RequestQueue queue = NetworkManager.sharedManager(this).queue;
        // request type is GET
        final StringRequest loginRequest = new StringRequest(
                Request.Method.GET,
                baseURL + "/api/" + url,
                response -> {
//                    Log.d("signle_movie.success", response);
                    try{
                        JSONArray re = new JSONArray(response);
                        if(re.length() > 1){
                            Log.d("signle_movie.success", response);
                            //Complete and destroy login activity once successful
                            finish();
                            // initialize the activity(page)/destination
                            Intent SingleMoviePage = new Intent(MovieListActivity.this, SingleMovieActivity.class);
                            SingleMoviePage.putExtra("resultData", response);
                            SingleMoviePage.putExtra("searchTitle", searchTitle);
                            // activate the list page.
                            startActivity(SingleMoviePage);
                        }
                    }
                    catch (JSONException err){
                        Log.d("signle_movie.error", err.toString());
                    }
                },
                error -> {
                    // error
                    Log.d("signle_movie.error", error.toString());
                }) {
        };
        // important: queue.add is where the login request is actually sent
        queue.add(loginRequest);
    }
}

