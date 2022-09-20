package edu.uci.ics.fabflixmobile.ui.login;

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
import edu.uci.ics.fabflixmobile.ui.movielist.MainActivity;
import edu.uci.ics.fabflixmobile.ui.movielist.MovieListActivity;
import org.json.*;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private EditText username;
    private EditText password;
    private TextView message;

    /*
      In Android, localhost is the address of the device or the emulator.
      To connect to your machine, you need to use the below IP address
     */
    private final String host = "ec2-54-151-116-40.us-west-1.compute.amazonaws.com";
    private final String port = "8443";
    private final String domain = "fablix";
    private final String baseURL = "https://" + host + ":" + port + "/" + domain;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityLoginBinding binding = ActivityLoginBinding.inflate(getLayoutInflater());
        // upon creation, inflate and initialize the layout
        setContentView(binding.getRoot());

        username = binding.username;
        password = binding.password;
        message = binding.message;
        final Button loginButton = binding.login;

        //assign a listener to call a function to handle the user request when clicking a button
        loginButton.setOnClickListener(view -> login());
    }

    @SuppressLint("SetTextI18n")
    public void login() {
        message.setText("Trying to login");
        // use the same network queue across our application
        final RequestQueue queue = NetworkManager.sharedManager(this).queue;
        // request type is POST
        final StringRequest loginRequest = new StringRequest(
                Request.Method.POST,
                baseURL + "/api/login",
                response -> {
                    // TODO: should parse the json response to redirect to appropriate functions
                    //  upon different response value.
//                    Log.d("login.success", response);
                    try{
                        JSONObject re = new JSONObject(response);
                        if(re.getString("status").equals("success")){
                            message.setText("Login Success");
                            Log.d("login.success", response);
                            //Complete and destroy login activity once successful
                            finish();
                            // initialize the activity(page)/destination
                            Intent SearchMainPage = new Intent(LoginActivity.this, MainActivity.class);
                            // activate the list page.
                            startActivity(SearchMainPage);
                        }
                        else {
                            Log.d("login.fail", response);
                            message.setText(re.getString("message"));
                        }
                    }catch (JSONException err){
                        Log.d("login.error", err.toString());
                    }
                },
                error -> {
                    // error
                    Log.d("login.error", error.toString());
                }) {
            @Override
            protected Map<String, String> getParams() {
                // POST request form data
                final Map<String, String> params = new HashMap<>();
                params.put("username", username.getText().toString());
                params.put("password", password.getText().toString());
                params.put("deviceTag", "Android");
                return params;
            }
        };
        // important: queue.add is where the login request is actually sent
        queue.add(loginRequest);
    }
}