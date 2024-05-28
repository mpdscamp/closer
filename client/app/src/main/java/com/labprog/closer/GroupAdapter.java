package com.labprog.closer;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.labprog.closer.R;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.List;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.GroupViewHolder> {
    private Context context;
    private List<Group> groups;
    private String userEmail;

    public GroupAdapter(Context context, List<Group> groups, String userEmail) {
        this.context = context;
        this.groups = groups;
        this.userEmail = userEmail;
    }

    @NonNull
    @Override
    public GroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.group_item, parent, false);
        return new GroupViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupViewHolder holder, int position) {
        Group group = groups.get(position);
        holder.groupName.setText(group.groupName);
        holder.groupTheme.setText(group.theme);
        Picasso.get().load(group.imageUrl).into(holder.groupImage);

        // Fetch and display group members
        fetchGroupMembers(group.groupId, holder.groupMembers);

        holder.openButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, GroupActivity.class);
            intent.putExtra("GROUP_ID", group.groupId);
            intent.putExtra("GROUP_NAME", group.groupName);
            intent.putExtra("USER_EMAIL", userEmail);
            context.startActivity(intent);
        });
    }

    private void fetchGroupMembers(int groupId, TextView groupMembersTextView) {
        String url = "http://10.0.2.2:8080/closer_war_exploded/get-group-members?groupId=" + groupId;
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        StringBuilder members = new StringBuilder();
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                members.append(response.getString(i));
                                if (i < response.length() - 1) {
                                    members.append(", ");
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        groupMembersTextView.setText(members.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });

        requestQueue.add(jsonArrayRequest);
    }

    @Override
    public int getItemCount() {
        return groups.size();
    }

    static class GroupViewHolder extends RecyclerView.ViewHolder {
        ImageView groupImage;
        TextView groupName;
        TextView groupTheme;
        TextView groupMembers;
        Button openButton;

        GroupViewHolder(@NonNull View itemView) {
            super(itemView);
            groupImage = itemView.findViewById(R.id.group_image);
            groupName = itemView.findViewById(R.id.group_name);
            groupTheme = itemView.findViewById(R.id.group_theme);
            groupMembers = itemView.findViewById(R.id.group_members);
            openButton = itemView.findViewById(R.id.open_button);
        }
    }
}


class Group {
    int groupId;
    String groupName;
    String theme;
    String imageUrl;

    Group(int groupId, String groupName, String theme, String imageUrl) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.theme = theme;
        this.imageUrl = imageUrl;
    }
}
