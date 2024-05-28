package com.labprog.closer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.labprog.closer.R;

public class GroupActivity extends AppCompatActivity {

    private TextView groupNameDisplay;
    private Button inviteToGroupButton;
    private int groupId;
    private String groupName;
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        groupNameDisplay = findViewById(R.id.group_name_display);
        inviteToGroupButton = findViewById(R.id.invite_to_group_button);

        groupId = getIntent().getIntExtra("GROUP_ID", -1);
        groupName = getIntent().getStringExtra("GROUP_NAME");
        userEmail = getIntent().getStringExtra("USER_EMAIL");

        groupNameDisplay.setText("Grupo " + groupName);

        inviteToGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GroupActivity.this, InviteToGroupActivity.class);
                intent.putExtra("GROUP_ID", groupId);
                intent.putExtra("USER_EMAIL", userEmail);
                startActivity(intent);
            }
        });
    }
}
