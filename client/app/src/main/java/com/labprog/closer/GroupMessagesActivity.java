package com.labprog.closer;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class GroupMessagesActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private MessageAdapter messageAdapter;
    private List<Image> messageList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Replace this with actual groupId
        String groupId = "exampleGroupId";

        // Fetch messages for the group
        messageList = fetchMessagesForGroup(groupId);

        messageAdapter = new MessageAdapter(messageList);
        recyclerView.setAdapter(messageAdapter);
    }

    private List<Image> fetchMessagesForGroup(String groupId) {
        // This should be replaced with actual data fetching logic
        List<Image> messages = new ArrayList<>();

        // Example data
        messages.add(new Image("user1", groupId, "Hello, this is a message."));
        messages.add(new Image("user2", groupId, "This is another message."));

        return messages;
    }
}
