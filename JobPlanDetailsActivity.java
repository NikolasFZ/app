package com.example.loginapp;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONException;
import org.json.JSONObject;

public class JobPlanDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_plan_details);

        // Get job plan details string from intent extras
        String jobPlanDetailsString = getIntent().getStringExtra("jobPlanDetails");

        // Find the layout where the details will be added
        LinearLayout layout = findViewById(R.id.jobPlanDetailsLayout);

        // Parse job plan details string to JSONObject
        try {
            JSONObject jobPlanDetails = new JSONObject(jobPlanDetailsString);

            // Add key-value pairs to the layout
            addKeyValueToLayout(layout, "calcmethod", jobPlanDetails.getString("spi:calcmethod"));
            addKeyValueToLayout(layout, "jpnum", jobPlanDetails.getString("spi:jpnum"));
            addKeyValueToLayout(layout, "status", jobPlanDetails.getString("spi:status"));
            addKeyValueToLayout(layout, "orgid", jobPlanDetails.getString("spi:orgid"));
            addKeyValueToLayout(layout, "templatetype", jobPlanDetails.getString("spi:templatetype"));
            addKeyValueToLayout(layout, "templatetype_description", jobPlanDetails.getString("spi:templatetype_description"));
            addKeyValueToLayout(layout, "pluscstatusdate", jobPlanDetails.getString("spi:pluscstatusdate"));
            addKeyValueToLayout(layout, "calcmethod_description", jobPlanDetails.getString("spi:calcmethod_description"));
            addKeyValueToLayout(layout, "jpduration", String.valueOf(jobPlanDetails.getInt("spi:jpduration")));
            addKeyValueToLayout(layout, "pluscrevnum", String.valueOf(jobPlanDetails.getInt("spi:pluscrevnum")));
            addKeyValueToLayout(layout, "mxzfldhide", String.valueOf(jobPlanDetails.getBoolean("spi:mxzfldhide")));
            addKeyValueToLayout(layout, "calcapplyto_description", jobPlanDetails.getString("spi:calcapplyto_description"));
            addKeyValueToLayout(layout, "calcapplyto", jobPlanDetails.getString("spi:calcapplyto"));
            addKeyValueToLayout(layout, "pluscchangedate", jobPlanDetails.getString("spi:pluscchangedate"));
            addKeyValueToLayout(layout, "plusamajrevision", String.valueOf(jobPlanDetails.getInt("spi:plusamajrevision")));
            addKeyValueToLayout(layout, "description", jobPlanDetails.getString("spi:description"));
            addKeyValueToLayout(layout, "pplynondynamiccalc", String.valueOf(jobPlanDetails.getBoolean("spi:applynondynamiccalc")));
            addKeyValueToLayout(layout, "dynamic", String.valueOf(jobPlanDetails.getBoolean("spi:dynamic")));
            addKeyValueToLayout(layout, "inctasksinsched", String.valueOf(jobPlanDetails.getBoolean("spi:inctasksinsched")));
            addKeyValueToLayout(layout, "plusamajorjpnum", jobPlanDetails.getString("spi:plusamajorjpnum"));
            addKeyValueToLayout(layout, "plusamajrevisionnum", jobPlanDetails.getString("spi:plusamajrevisionnum"));
            addKeyValueToLayout(layout, "unitsofworkmultiplier", String.valueOf(jobPlanDetails.getInt("spi:unitsofworkmultiplier")));
            addKeyValueToLayout(layout, "status_description", jobPlanDetails.getString("spi:status_description"));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Handle the update button click
        Button updateButton = findViewById(R.id.updateButton);
        updateButton.setOnClickListener(view -> {

        });
    }

    /**
     * Adds a key-value pair to the specified layout.
     *
     * @param layout The layout to which the key-value pair should be added.
     * @param key The key string.
     * @param value The value string.
     */
    private void addKeyValueToLayout(LinearLayout layout, String key, String value) {
        // Create a horizontal LinearLayout for key-value pair
        LinearLayout keyValueLayout = new LinearLayout(this);
        keyValueLayout.setOrientation(LinearLayout.HORIZONTAL);

        // Create and set the key TextView
        TextView keyTextView = new TextView(this);
        keyTextView.setText(key + ": ");
        keyTextView.setTextAppearance(this, R.style.KeyTextViewStyle);
        keyValueLayout.addView(keyTextView);

        if (key.equals("status")) {
            // Create and set the Spinner for status
            Spinner statusSpinner = new Spinner(this);
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                    R.array.status_array, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            statusSpinner.setAdapter(adapter);

            // Set the initial selection
            int spinnerPosition = adapter.getPosition(value);
            statusSpinner.setSelection(spinnerPosition);

            keyValueLayout.addView(statusSpinner);
        } else {
            // Create and set the value TextView
            TextView valueTextView = new TextView(this);
            valueTextView.setText(value);
            valueTextView.setTextAppearance(this, R.style.ValueTextViewStyle);
            keyValueLayout.addView(valueTextView);
        }

        // Add the key-value pair layout to the parent layout
        layout.addView(keyValueLayout);
    }
}
