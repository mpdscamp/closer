package com.labprog.closer;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.labprog.closer.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InviteFriendActivity extends AppCompatActivity {

    private static final String TAG = "InviteFriendActivity";

    private RecyclerView recyclerView;
    private UsersAdapter usersAdapter;
    private List<User> userList;

    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_friend);

        recyclerView = findViewById(R.id.users_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        userList = new ArrayList<>();
        usersAdapter = new UsersAdapter(this, userList, new UsersAdapter.OnInviteClickListener() {
            @Override
            public void onInviteClick(String friendEmail) {
                inviteFriend(userEmail, friendEmail);
            }
        });
        recyclerView.setAdapter(usersAdapter);

        userEmail = getIntent().getStringExtra("USER_EMAIL");

        fetchUsers();
    }

    private void fetchUsers() {
        String url = "http://10.0.2.2:8080/closer_war_exploded/get-non-friends?email=" + userEmail;
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            userList.clear();
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject userObject = response.getJSONObject(i);
                                String userName = userObject.getString("username");
                                String email = userObject.getString("email");
                                if (!email.equals(userEmail)) { // Exclude current user
                                    userList.add(new User(userName, email));
                                }
                            }
                            usersAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Error response from server", error);
                        error.printStackTrace();
                    }
                });

        requestQueue.add(jsonArrayRequest);
    }

    private void inviteFriend(String userEmail, String friendEmail) {
        String url = "http://10.0.2.2:8080/closer_war_exploded/invite-friend";
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "Response from server: " + response);
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("result_message", response);
                        setResult(RESULT_OK, resultIntent); // Set result to OK with message
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
                params.put("email", userEmail);
                params.put("friendEmail", friendEmail);
                Log.d(TAG, "Parameters: " + params.toString());
                return params;
            }
        };

        requestQueue.add(stringRequest);
    }
}
