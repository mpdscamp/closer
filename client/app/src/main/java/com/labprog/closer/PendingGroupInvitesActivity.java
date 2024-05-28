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

public class PendingGroupInvitesActivity extends AppCompatActivity {

    private static final String TAG = "PendingGroupInvitesAct";

    private RecyclerView recyclerView;
    private GroupInvitesAdapter invitesAdapter;
    private List<GroupInvite> invitesList;
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_group_invites);

        recyclerView = findViewById(R.id.invites_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        invitesList = new ArrayList<>();
        invitesAdapter = new GroupInvitesAdapter(this, invitesList, new GroupInvitesAdapter.OnInviteActionClickListener() {
            @Override
            public void onAcceptClick(int inviteId) {
                handleGroupInviteAction(inviteId, true);
            }

            @Override
            public void onRejectClick(int inviteId) {
                handleGroupInviteAction(inviteId, false);
            }
        });
        recyclerView.setAdapter(invitesAdapter);

        userEmail = getIntent().getStringExtra("USER_EMAIL");

        fetchPendingInvites();
    }

    private void fetchPendingInvites() {
        String url = "http://10.0.2.2:8080/closer_war_exploded/get-pending-group-invites?email=" + userEmail;
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            invitesList.clear();
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject inviteObject = response.getJSONObject(i);
                                int inviteId = inviteObject.getInt("inviteId");
                                String groupName = inviteObject.getString("groupName");
                                String invitedBy = inviteObject.getString("invitedBy");
                                invitesList.add(new GroupInvite(inviteId, groupName, invitedBy));
                            }
                            invitesAdapter.notifyDataSetChanged();
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

    private void handleGroupInviteAction(int inviteId, boolean accept) {
        String url = "http://10.0.2.2:8080/closer_war_exploded/" + (accept ? "accept-group-invite" : "refuse-group-invite");
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "Response from server: " + response);
                        Toast.makeText(PendingGroupInvitesActivity.this, response, Toast.LENGTH_SHORT).show();
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("result_message", response);
                        setResult(RESULT_OK, resultIntent); // Set result to OK with message
                        fetchPendingInvites(); // Refresh invites list after action
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Error response from server", error);
                        error.printStackTrace();
                        Toast.makeText(PendingGroupInvitesActivity.this, "Error handling invite", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("inviteId", String.valueOf(inviteId));
                Log.d(TAG, "Parameters: " + params.toString());
                return params;
            }
        };

        requestQueue.add(stringRequest);
    }
}
