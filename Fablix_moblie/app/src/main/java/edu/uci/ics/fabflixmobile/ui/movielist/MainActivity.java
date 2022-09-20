package edu.uci.ics.fabflixmobile.ui.movielist;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.util.Log;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;

import edu.uci.ics.fabflixmobile.data.NetworkManager;
import edu.uci.ics.fabflixmobile.databinding.ActivityLoginBinding;
import edu.uci.ics.fabflixmobile.databinding.SearchMainBinding;
import edu.uci.ics.fabflixmobile.ui.login.LoginActivity;
import edu.uci.ics.fabflixmobile.ui.movielist.MovieListActivity;
import org.json.*;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private EditText searchTitle;
    private TextView message;

    /*
      In Android, localhost is the address of the device or the emulator.
      To connect to your machine, you need to use the below IP address
     */
    private final String host = "ec2-54-151-116-40.us-west-1.compute.amazonaws.com";
    private final String port = "8443";
    private final String domain = "fablix";
    private final String baseURL = "https://" + host + ":" + port + "/" + domain;

    private final String numRecords = "20";
    private final String startIndex = "0";
    private final String searchURL = "movies?name=&director=&stars=&year=&genre=null&AZ=null&sortBy1=null&order1=null&sortBy2=null&order2=null&numRecords=" + numRecords + "&startIndex=" + startIndex + "&fullSearch=";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SearchMainBinding binding = SearchMainBinding.inflate(getLayoutInflater());
        // upon creation, inflate and initialize the layout
        setContentView(binding.getRoot());

        searchTitle = binding.searchTitle;
        final Button searchButton = binding.searchButton;
        message = binding.message;

        //assign a listener to call a function to handle the user request when clicking a button
        searchButton.setOnClickListener(view -> search());
    }

    @SuppressLint("SetTextI18n")
    public void search() {
        String url = searchURL + searchTitle.getText().toString();
//        message.setText("Searching");
        // use the same network queue across our application
        final RequestQueue queue = NetworkManager.sharedManager(this).queue;
        // request type is POST
        final StringRequest loginRequest = new StringRequest(
                Request.Method.GET,
                baseURL + "/api/" + url,
                response -> {
                    // TODO: should parse the json response to redirect to appropriate functions
                    //  upon different response value.
                    Log.d("search.success", response);
                    try{
                        JSONArray re = new JSONArray(response);
//                        re.getJSONObject(0);
                        if(re.length() > 1){
                            Log.d("search.success", response);
//                            message.setText("Login Success");
                            //Complete and destroy login activity once successful
                            finish();
                            // initialize the activity(page)/destination
                            Intent MovieListPage = new Intent(MainActivity.this, MovieListActivity.class);
                            MovieListPage.putExtra("resultData", response);
                            MovieListPage.putExtra("searchTitle", searchTitle.getText().toString());
                            // activate the list page.
                            startActivity(MovieListPage);
                        }
                        else {
//                            Log.d("search.fail", response);
                            message.setText("No Result Found");
                        }
                    }catch (JSONException err){
                        Log.d("search.error", err.toString());
                    }
                },
                error -> {
                    // error
                    Log.d("search.error", error.toString());
                }) {
//            @Override
//            protected Map<String, String> getParams() {
//                // POST request form data
//                final Map<String, String> params = new HashMap<>();
//                params.put("username", username.getText().toString());
//                params.put("password", password.getText().toString());
//                params.put("deviceTag", "Android");
//                return params;
//            }
        };
        // important: queue.add is where the login request is actually sent
        queue.add(loginRequest);
    }
}
