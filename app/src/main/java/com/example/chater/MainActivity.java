package com.example.chater;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.chater.adapter.ConversationAdapter;
import com.example.chater.db.MessageDao;
import com.example.chater.model.Conversation;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ConversationAdapter adapter;
    private MessageDao messageDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        messageDao = new MessageDao(this);
        checkAndInitDefaultData();

        recyclerView = findViewById(R.id.recyclerViewConversations);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ConversationAdapter(this);
        recyclerView.setAdapter(adapter);

        FloatingActionButton fab = findViewById(R.id.fabNewChat);
        fab.setOnClickListener(v -> {
            // Simulate starting a new chat with a random user
            int randomId = (int) (Math.random() * 100) + 1;
            Intent intent = new Intent(MainActivity.this, ChatActivity.class);
            intent.putExtra(ChatActivity.EXTRA_TARGET_ID, randomId);
            intent.putExtra(ChatActivity.EXTRA_TARGET_NAME, "User " + randomId);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadConversations();
    }

    private void loadConversations() {
        List<Conversation> conversations = messageDao.getConversations();
        adapter.setConversations(conversations);
    }

    private void checkAndInitDefaultData() {
        List<Conversation> conversations = messageDao.getConversations();
        if (conversations != null && !conversations.isEmpty()) {
            return;
        }

        // Initialize default data
        long now = System.currentTimeMillis();
        long hour = 3600000L;

        // 1. Alice (All read)
        int aliceId = 101;
        messageDao.insertMessage(new com.example.chater.model.Message("Hi there!", now - 24 * hour, aliceId, aliceId));
        messageDao.insertMessage(new com.example.chater.model.Message("Hello Alice!", now - 23 * hour, com.example.chater.model.Message.SENDER_SELF, aliceId));
        messageDao.insertMessage(new com.example.chater.model.Message("How are you doing?", now - 22 * hour, aliceId, aliceId));
        messageDao.insertMessage(new com.example.chater.model.Message("I'm good, thanks!", now - 2 * hour, com.example.chater.model.Message.SENDER_SELF, aliceId));
        messageDao.clearUnreadCount(aliceId); // Mark as read

        // 2. Bob (Unread)
        int bobId = 102;
        messageDao.insertMessage(new com.example.chater.model.Message("Hey!", now - 5 * hour, bobId, bobId));
        messageDao.insertMessage(new com.example.chater.model.Message("Did you see the news?", now - 4 * hour, bobId, bobId));
        messageDao.insertMessage(new com.example.chater.model.Message("Call me when you can.", now - 1 * hour, bobId, bobId));
        // Bob has 3 unread messages now

        // 3. Charlie (Mixed)
        int charlieId = 103;
        messageDao.insertMessage(new com.example.chater.model.Message("Project update?", now - 48 * hour, com.example.chater.model.Message.SENDER_SELF, charlieId));
        messageDao.insertMessage(new com.example.chater.model.Message("I'm working on it.", now - 10 * hour, charlieId, charlieId));
        // Charlie has 1 unread message
    }
}
