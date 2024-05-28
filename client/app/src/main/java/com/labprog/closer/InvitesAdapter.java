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

public class InvitesAdapter extends RecyclerView.Adapter<InvitesAdapter.ViewHolder> {

    private List<Invite> invitesList;
    private LayoutInflater mInflater;
    private OnInviteActionListener onInviteActionListener;

    public interface OnInviteActionListener {
        void onAcceptClick(String friendEmail);
        void onRefuseClick(String friendEmail);
    }

    // data is passed into the constructor
    InvitesAdapter(Context context, List<Invite> data, OnInviteActionListener onInviteActionListener) {
        this.mInflater = LayoutInflater.from(context);
        this.invitesList = data;
        this.onInviteActionListener = onInviteActionListener;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.invite_row, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Invite invite = invitesList.get(position);
        holder.userName.setText(invite.getName());
        holder.userEmail.setText(invite.getEmail());

        holder.acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onInviteActionListener.onAcceptClick(invite.getEmail());
            }
        });

        holder.refuseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onInviteActionListener.onRefuseClick(invite.getEmail());
            }
        });
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return invitesList.size();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView userName;
        TextView userEmail;
        Button acceptButton;
        Button refuseButton;

        ViewHolder(View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.user_name);
            userEmail = itemView.findViewById(R.id.user_email);
            acceptButton = itemView.findViewById(R.id.accept_button);
            refuseButton = itemView.findViewById(R.id.refuse_button);
        }
    }
}

class Invite {
    private String name;
    private String email;

    public Invite(String name, String email) {
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