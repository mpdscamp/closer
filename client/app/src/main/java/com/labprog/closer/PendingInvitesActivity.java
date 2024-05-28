package com.labprog.closer;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

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

public class PendingInvitesActivity extends AppCompatActivity {

    private static final String TAG = "PendingInvitesActivity";

    private RecyclerView recyclerView;
    private InvitesAdapter invitesAdapter;
    private List<Invite> inviteList;

    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_invites);

        recyclerView = findViewById(R.id.invites_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        inviteList = new ArrayList<>();
        invitesAdapter = new InvitesAdapter(this, inviteList, new InvitesAdapter.OnInviteActionListener() {
            @Override
            public void onAcceptClick(String friendEmail) {
                handleInviteAction(userEmail, friendEmail, true);
            }

            @Override
            public void onRefuseClick(String friendEmail) {
                handleInviteAction(userEmail, friendEmail, false);
            }
        });
        recyclerView.setAdapter(invitesAdapter);

        userEmail = getIntent().getStringExtra("USER_EMAIL");

        fetchPendingInvites();
    }

    private void fetchPendingInvites() {
        String url = "http://10.0.2.2:8080/closer_war_exploded/get-pending-invites?email=" + userEmail;
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            inviteList.clear();
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject inviteObject = response.getJSONObject(i);
                                String userName = inviteObject.getString("username");
                                String email = inviteObject.getString("email");
                                inviteList.add(new Invite(userName, email));
                            }
                            invitesAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            Log.e(TAG, "JSON parsing error", e);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Error response from server", error);
                        Toast.makeText(PendingInvitesActivity.this, "Error fetching invites", Toast.LENGTH_SHORT).show();
                    }
                });

        requestQueue.add(jsonArrayRequest);
    }

    private void handleInviteAction(String userEmail, String friendEmail, boolean accept) {
        String url = "http://10.0.2.2:8080/closer_war_exploded/" + (accept ? "accept-friend-invite" : "refuse-friend-invite");
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "Response from server: " + response);
                        Toast.makeText(PendingInvitesActivity.this, response, Toast.LENGTH_SHORT).show();
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("result_message", response);
                        setResult(RESULT_OK, resultIntent); // Set result to OK with message
                        fetchPendingInvites(); // Refresh the list after action
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Error response from server", error);
                        Toast.makeText(PendingInvitesActivity.this, "Error handling invite", Toast.LENGTH_SHORT).show();
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
