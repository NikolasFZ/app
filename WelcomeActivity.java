package com.example.loginapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.loginapp.util.SSLutils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.HttpCookie;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WelcomeActivity extends AppCompatActivity {

    private TextView textViewResponseBody;
    private Button sendRequestButton;
    private List<HttpCookie> cookies;
    private LinearLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        SSLutils.disableCertificateValidation();

        layout = findViewById(R.id.buttonContainer);

        // Retrieve cookies from Intent extras
        ArrayList<String> cookieStrings = getIntent().getStringArrayListExtra("COOKIES");

        cookies = new ArrayList<>();

        if (cookieStrings != null) {
            for (String cookieString : cookieStrings) {
                try {
                    List<HttpCookie> parsedCookies = HttpCookie.parse(cookieString);
                    if (!parsedCookies.isEmpty()) {
                        cookies.add(parsedCookies.get(0));
                    }
                } catch (IllegalArgumentException e) {
                    Log.e("WelcomeActivity", "Failed to parse cookie: " + cookieString, e);
                }
            }
        } else {
            Log.e("WelcomeActivity", "No cookies received");
        }

        textViewResponseBody = findViewById(R.id.textViewResponseBody);

        sendRequestButton = findViewById(R.id.btn_refresh);
        sendRequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new HttpRequestTask().execute();
            }
        });
    }

    private class HttpRequestTask extends AsyncTask<Void, Void, String> {

        private static final int MAX_RETRY_COUNT = 3;

        @Override
        protected String doInBackground(Void... params) {
            String responseBody = null;
            int retryCount = 0;

            while (retryCount < MAX_RETRY_COUNT) {
                try {
                    String urlString = "https://eam.slo-zeleznice.si/maximo/api/os/mxzjobplan?oslc.select=*&oslc.where=jobplanspec.CLASSSTRUCTUREID=2030&oslc.pageSize=10";
                    URL url = new URL(urlString);

                    Log.d("HTTP Request", "Request URL: " + url);

                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");

                    StringBuilder cookieHeaderValue = new StringBuilder();
                    if (cookies != null && !cookies.isEmpty()) {
                        for (HttpCookie cookie : cookies) {
                            cookieHeaderValue.append(cookie.toString()).append("; ");
                        }
                    }

                    if (cookieHeaderValue.length() > 0) {
                        connection.setRequestProperty("Cookie", cookieHeaderValue.toString());
                    }

                    Map<String, List<String>> requestHeaders = connection.getRequestProperties();
                    for (Map.Entry<String, List<String>> entry : requestHeaders.entrySet()) {
                        Log.d("HTTP Request", entry.getKey() + ": " + entry.getValue());
                    }

                    int responseCode = connection.getResponseCode();
                    Log.d("HTTP Request", "Response code: " + responseCode);

                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        StringBuilder response = new StringBuilder();
                        String inputLine;
                        while ((inputLine = in.readLine()) != null) {
                            response.append(inputLine);
                        }
                        in.close();
                        responseBody = response.toString();
                        break;
                    } else {
                        Log.e("HTTP Request", "Response code: " + responseCode);
                    }
                } catch (Exception e) {
                    Log.e("HTTP Request", "Error: " + e.getMessage());
                }
                retryCount++;
            }
            return responseBody;
        }

        @Override
        protected void onPostExecute(String responseBody) {
            if (responseBody != null) {
                try {
                    JSONObject jsonResponse = new JSONObject(responseBody);
                    JSONArray jobPlans = jsonResponse.getJSONArray("rdfs:member");

                    layout.removeAllViews();

                    for (int i = 0; i < jobPlans.length(); i++) {
                        JSONObject jobPlan = jobPlans.getJSONObject(i);
                        final String jpNum = jobPlan.getString("spi:jpnum");

                        Button button = new Button(WelcomeActivity.this);
                        button.setText("Job Plan " + jpNum);

                        button.setBackgroundResource(R.drawable.button_background);
                        button.setTextColor(getResources().getColor(android.R.color.white));
                        button.setPadding(12, 12, 12, 12);
                        button.setElevation(4);

                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(WelcomeActivity.this, JobPlanDetailsActivity.class);
                                intent.putExtra("jobPlanDetails", jobPlan.toString());
                                startActivity(intent);
                            }
                        });

                        layout.addView(button);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    textViewResponseBody.setText("Failed to parse JSON");
                }
            } else {
                textViewResponseBody.setText("Failed to retrieve data");
            }
        }
    }
}
