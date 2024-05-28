package com.labprog.closer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.labprog.closer.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class GroupListActivity extends AppCompatActivity {

    private static final String TAG = "GroupListActivity";
    private static final int REQUEST_CODE_INVITE_TO_GROUP = 1;
    private static final int REQUEST_CODE_PENDING_GROUP_INVITES = 2;

    private RecyclerView recyclerView;
    private GroupAdapter groupAdapter;
    private List<Group> groupList;
    private TextView userNameDisplay;
    private Button createGroupButton;
    private Button friendsListButton;
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_list);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        groupList = new ArrayList<>();
        userEmail = getIntent().getStringExtra("USER_EMAIL");
        groupAdapter = new GroupAdapter(this, groupList, userEmail);
        recyclerView.setAdapter(groupAdapter);

        userNameDisplay = findViewById(R.id.user_name_display);
        createGroupButton = findViewById(R.id.create_group_button);
        friendsListButton = findViewById(R.id.friends_list_button);

        fetchUserName(userEmail);

        createGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GroupListActivity.this, CreateGroupActivity.class);
                intent.putExtra("USER_EMAIL", userEmail);
                startActivity(intent);
            }
        });

        friendsListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GroupListActivity.this, FriendsListActivity.class);
                intent.putExtra("USER_EMAIL", userEmail);
                startActivity(intent);
            }
        });

        fetchGroups(userEmail);
    }

    @Override
    protected void onResume() {
        super.onResume();
        groupList.clear(); // Clear the current list to avoid duplicates
        fetchGroups(userEmail); // Fetch the updated list of groups
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == REQUEST_CODE_INVITE_TO_GROUP || requestCode == REQUEST_CODE_PENDING_GROUP_INVITES) && resultCode == RESULT_OK) {
            fetchGroups(userEmail); // Refresh the group list
            if (data != null) {
                String message = data.getStringExtra("result_message");
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void fetchUserName(String email) {
        String url = "http://10.0.2.2:8080/closer_war_exploded/get-username?email=" + email;
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String userName = response.getString("username");
                            userNameDisplay.setText(userName);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });

        requestQueue.add(jsonObjectRequest);
    }

    private void fetchGroups(String email) {
        String url = "http://10.0.2.2:8080/closer_war_exploded/group-list?email=" + email;
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            groupList.clear(); // Clear the current list to avoid duplicates
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject groupObject = response.getJSONObject(i);
                                int groupId = groupObject.getInt("groupId");
                                String groupName = groupObject.getString("groupName");
                                String theme = groupObject.getString("theme");
                                String imageUrl = groupObject.getString("imageUrl");

                                // Map themes to Portuguese equivalents
                                switch (theme) {
                                    case "family":
                                        theme = "Família";
                                        break;
                                    case "friends":
                                        theme = "Amigos";
                                        break;
                                    case "romantic":
                                        theme = "Romântico";
                                        break;
                                }

                                groupList.add(new Group(groupId, groupName, theme, imageUrl));
                            }
                            groupAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        // Handle the error response here
                    }
                });

        requestQueue.add(jsonArrayRequest);
    }
}

