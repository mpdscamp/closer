package com.labprog.closer;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.labprog.closer.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FriendsListActivity extends AppCompatActivity {

    private static final String TAG = "FriendsListActivity";
    private static final int REQUEST_CODE_INVITE_FRIEND = 2;
    private static final int REQUEST_CODE_PENDING_INVITES = 1;

    private RecyclerView recyclerView;
    private FriendsAdapter friendsAdapter;
    private List<User> friendsList;
    private Set<String> friendsSet;
    private Button inviteFriendButton;
    private Button pendingInvitesButton;
    private Button pendingGroupInvitesButton;
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_list);

        recyclerView = findViewById(R.id.friends_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        friendsList = new ArrayList<>();
        friendsSet = new HashSet<>();
        friendsAdapter = new FriendsAdapter(this, friendsList);
        recyclerView.setAdapter(friendsAdapter);

        userEmail = getIntent().getStringExtra("USER_EMAIL");

        inviteFriendButton = findViewById(R.id.invite_friend_button);
        pendingInvitesButton = findViewById(R.id.pending_invites_button);
        pendingGroupInvitesButton = findViewById(R.id.pending_group_invites_button);

        inviteFriendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FriendsListActivity.this, InviteFriendActivity.class);
                intent.putExtra("USER_EMAIL", userEmail);
                startActivityForResult(intent, REQUEST_CODE_INVITE_FRIEND);
            }
        });

        pendingInvitesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FriendsListActivity.this, PendingInvitesActivity.class);
                intent.putExtra("USER_EMAIL", userEmail);
                startActivityForResult(intent, REQUEST_CODE_PENDING_INVITES);
            }
        });

        pendingGroupInvitesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FriendsListActivity.this, PendingGroupInvitesActivity.class);
                intent.putExtra("USER_EMAIL", userEmail);
                startActivity(intent);
            }
        });

        fetchFriends();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == REQUEST_CODE_PENDING_INVITES || requestCode == REQUEST_CODE_INVITE_FRIEND) && resultCode == RESULT_OK) {
            fetchFriends(); // Refresh the friends list when returning from PendingInvitesActivity or InviteFriendActivity
            if (data != null) {
                String message = data.getStringExtra("result_message");
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void fetchFriends() {
        String url = "http://10.0.2.2:8080/closer_war_exploded/get-friends?email=" + userEmail;
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            friendsSet.clear();
                            friendsList.clear(); // Clear the list to avoid duplicates
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject friendObject = response.getJSONObject(i);
                                String friendEmail = friendObject.getString("email");
                                String friendName = friendObject.getString("username");
                                friendsSet.add(friendEmail);
                                friendsList.add(new User(friendName, friendEmail));
                            }
                            friendsAdapter.notifyDataSetChanged(); // Notify the adapter about the new data
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
}
