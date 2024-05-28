// GroupInvitesAdapter.java
package com.labprog.closer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.labprog.closer.R;

import java.util.List;

public class GroupInvitesAdapter extends RecyclerView.Adapter<GroupInvitesAdapter.GroupInviteViewHolder> {

    private Context context;
    private List<GroupInvite> invites;
    private OnInviteActionClickListener listener;

    public interface OnInviteActionClickListener {
        void onAcceptClick(int inviteId);
        void onRejectClick(int inviteId);
    }

    public GroupInvitesAdapter(Context context, List<GroupInvite> invites, OnInviteActionClickListener listener) {
        this.context = context;
        this.invites = invites;
        this.listener = listener;
    }

    @NonNull
    @Override
    public GroupInviteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.group_invite_item, parent, false);
        return new GroupInviteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupInviteViewHolder holder, int position) {
        GroupInvite invite = invites.get(position);
        holder.groupName.setText(invite.groupName);
        holder.invitedBy.setText("Invited by: " + invite.invitedBy);

        holder.acceptButton.setOnClickListener(v -> listener.onAcceptClick(invite.inviteId));
        holder.rejectButton.setOnClickListener(v -> listener.onRejectClick(invite.inviteId));
    }

    @Override
    public int getItemCount() {
        return invites.size();
    }

    static class GroupInviteViewHolder extends RecyclerView.ViewHolder {
        TextView groupName;
        TextView invitedBy;
        Button acceptButton;
        Button rejectButton;

        GroupInviteViewHolder(@NonNull View itemView) {
            super(itemView);
            groupName = itemView.findViewById(R.id.group_name);
            invitedBy = itemView.findViewById(R.id.invited_by);
            acceptButton = itemView.findViewById(R.id.accept_button);
            rejectButton = itemView.findViewById(R.id.reject_button);
        }
    }
}

class GroupInvite {
    int inviteId;
    String groupName;
    String invitedBy;

    public GroupInvite(int inviteId, String groupName, String invitedBy) {
        this.inviteId = inviteId;
        this.groupName = groupName;
        this.invitedBy = invitedBy;
    }

    public int getInviteId() {
        return inviteId;
    }

    public String getGroupName() {
        return groupName;
    }

    public String getInvitedBy() {
        return invitedBy;
    }
}