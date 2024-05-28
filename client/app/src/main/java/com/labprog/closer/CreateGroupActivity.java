package com.labprog.closer;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.labprog.closer.R;

import java.util.HashMap;
import java.util.Map;

public class CreateGroupActivity extends AppCompatActivity {

    private static final String TAG = "CreateGroupActivity";

    private EditText groupNameEditText;
    private Spinner groupThemeSpinner;
    private Button createGroupButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        groupNameEditText = findViewById(R.id.group_name);
        groupThemeSpinner = findViewById(R.id.group_theme_spinner);
        createGroupButton = findViewById(R.id.create_group_button);

        // Set up the spinner with options
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.group_themes, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        groupThemeSpinner.setAdapter(adapter);

        String userEmail = getIntent().getStringExtra("USER_EMAIL");

        createGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String groupName = groupNameEditText.getText().toString();
                String groupTheme = groupThemeSpinner.getSelectedItem().toString();
                String translatedTheme = translateTheme(groupTheme);
                createGroup(userEmail, groupName, translatedTheme);
            }
        });
    }

    private String translateTheme(String theme) {
        switch (theme.toLowerCase()) {
            case "família":
                return "family";
            case "amigos":
                return "friends";
            case "romântico":
                return "romantic";
            default:
                return theme;
        }
    }

    private void createGroup(String email, String groupName, String groupTheme) {
        String url = "http://10.0.2.2:8080/closer_war_exploded/create-group";
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "Response from server: " + response);
                        setResult(RESULT_OK);
                        finish();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Error response from server", error);
                        error.printStackTrace();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                params.put("groupName", groupName);
                params.put("groupTheme", groupTheme);
                Log.d(TAG, "Parameters: " + params.toString());
                return params;
            }
        };

        requestQueue.add(stringRequest);
    }
}
