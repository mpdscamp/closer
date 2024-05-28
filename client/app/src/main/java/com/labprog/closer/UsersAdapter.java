package com.labprog.closer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.labprog.closer.R;

import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> {

    private List<User> usersList;
    private LayoutInflater mInflater;
    private OnInviteClickListener onInviteClickListener;

    public interface OnInviteClickListener {
        void onInviteClick(String friendEmail);
    }

    // data is passed into the constructor
    UsersAdapter(Context context, List<User> data, OnInviteClickListener onInviteClickListener) {
        this.mInflater = LayoutInflater.from(context);
        this.usersList = data;
        this.onInviteClickListener = onInviteClickListener;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.user_row, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        User user = usersList.get(position);
        holder.userName.setText(user.getName());
        holder.userEmail.setText(user.getEmail());

        holder.inviteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onInviteClickListener.onInviteClick(user.getEmail());
            }
        });
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return usersList.size();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView userName;
        TextView userEmail;
        Button inviteButton;

        ViewHolder(View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.user_name);
            userEmail = itemView.findViewById(R.id.user_email);
            inviteButton = itemView.findViewById(R.id.invite_button);
        }
    }
}

class User {
    private String name;
    private String email;

    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }
}
