package com.example.loginapp;

import android.content.Intent;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.loginapp.util.SSLutils;

import java.io.IOException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private EditText editTextUsername;
    private EditText editTextPassword;
    private Button buttonLogin;
    private CookieManager cookieManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SSLutils.disableCertificateValidation();

        cookieManager = new CookieManager(null, CookiePolicy.ACCEPT_ALL);
        CookieHandler.setDefault(cookieManager);

        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.loginButton);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = editTextUsername.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();
                new HttpRequestTask().execute(username, password);
            }
        });
    }

    private class HttpRequestTask extends AsyncTask<String, Void, List<HttpCookie>> {

        @Override
        protected List<HttpCookie> doInBackground(String... params) {
            String username = params[0];
            String password = params[1];
            List<HttpCookie> cookies = null;

            try {
                // Construct URL with username and password
                String urlString = "https://eam.slo-zeleznice.si/maximo/j_security_check/?j_username=" +
                        username + "&j_password=" + password;
                URL url = new URL(urlString);

                // Open connection
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                // Set request method
                connection.setRequestMethod("GET");

                // Get response code
                int responseCode = connection.getResponseCode();

                // Check if request was successful
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // Get cookies from the cookie store
                    URI uri = url.toURI();
                    cookies = cookieManager.getCookieStore().get(uri);
                }
            } catch (IOException | URISyntaxException e) {
                Log.e("HTTP Request", "Error: " + e.getMessage());
            }
            return cookies;
        }


        @Override
        protected void onPostExecute(List<HttpCookie> cookies) {
            // Display and log cookies
            if (cookies != null && !cookies.isEmpty()) {
                String jSessionId = null;
                String ltpaToken = null;

                for (HttpCookie cookie : cookies) {
                    Log.d("HttpCookies", "Cookie: " + cookie.toString());
                    if ("JSESSIONID".equals(cookie.getName())) {
                        jSessionId = cookie.getValue();
                    } else if ("LtpaToken2".equals(cookie.getName())) {
                        ltpaToken = cookie.getValue();
                    }
                }

                if (jSessionId != null && ltpaToken != null) {
                    Log.d("HttpCookies", "JSESSIONID: " + jSessionId);
                    Log.d("HttpCookies", "LtpaToken2: " + ltpaToken);
                    Toast.makeText(MainActivity.this, "JSESSIONID: " + jSessionId, Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(MainActivity.this, WelcomeActivity.class);
                    intent.putExtra("JSESSIONID", jSessionId);
                    intent.putExtra("LtpaToken2", ltpaToken);
                    startActivity(intent);
                    finish();
                } else {
                    Log.e("HTTP Request", "JSESSIONID or LtpaToken2 not found in cookies");
                }
            } else {
                Log.e("HTTP Request", "No cookies received");
            }
        }
    }
}