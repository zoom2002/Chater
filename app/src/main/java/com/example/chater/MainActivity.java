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
        messageDao.insertMessage(new com.example.chater.model.Message("Hi there!", now - 24 * hour, aliceId, aliceId), "Alice Wonderland");
        messageDao.insertMessage(new com.example.chater.model.Message("Hello Alice!", now - 23 * hour, com.example.chater.model.Message.SENDER_SELF, aliceId), "Alice Wonderland");
        messageDao.insertMessage(new com.example.chater.model.Message("How are you doing?", now - 22 * hour, aliceId, aliceId), "Alice Wonderland");
        messageDao.insertMessage(new com.example.chater.model.Message("I'm good, thanks!", now - 2 * hour, com.example.chater.model.Message.SENDER_SELF, aliceId), "Alice Wonderland");
        messageDao.clearUnreadCount(aliceId); // Mark as read

        // 2. Bob (Unread)
        int bobId = 102;
        messageDao.insertMessage(new com.example.chater.model.Message("Hey!", now - 5 * hour, bobId, bobId), "Bob Builder");
        messageDao.insertMessage(new com.example.chater.model.Message("Did you see the news?", now - 4 * hour, bobId, bobId), "Bob Builder");
        messageDao.insertMessage(new com.example.chater.model.Message("Call me when you can.", now - 1 * hour, bobId, bobId), "Bob Builder");
        // Bob has 3 unread messages now

        // 3. Charlie (Mixed)
        int charlieId = 103;
        messageDao.insertMessage(new com.example.chater.model.Message("Project update?", now - 48 * hour, com.example.chater.model.Message.SENDER_SELF, charlieId), "Charlie Chaplin");
        messageDao.insertMessage(new com.example.chater.model.Message("I'm working on it.", now - 10 * hour, charlieId, charlieId), "Charlie Chaplin");
        // Charlie has 1 unread message

        // 4. David (All read)
        int davidId = 104;
        messageDao.insertMessage(new com.example.chater.model.Message("Meeting at 3 PM?", now - 5 * hour, davidId, davidId), "David Bowman");
        messageDao.insertMessage(new com.example.chater.model.Message("Sure, I'll be there.", now - 4 * hour, com.example.chater.model.Message.SENDER_SELF, davidId), "David Bowman");
        messageDao.clearUnreadCount(davidId);

        // 5. Eve (All read)
        int eveId = 105;
        messageDao.insertMessage(new com.example.chater.model.Message("Did you finish the report?", now - 20 * hour, eveId, eveId), "Eve Polastri");
        messageDao.insertMessage(new com.example.chater.model.Message("Yes, sent it via email.", now - 19 * hour, com.example.chater.model.Message.SENDER_SELF, eveId), "Eve Polastri");
        messageDao.insertMessage(new com.example.chater.model.Message("Great, thanks!", now - 18 * hour, eveId, eveId), "Eve Polastri");
        messageDao.clearUnreadCount(eveId);

        // 6. Frank (Unread)
        int frankId = 106;
        messageDao.insertMessage(new com.example.chater.model.Message("Long time no see!", now - 2 * hour, frankId, frankId), "Frank Castle");
        messageDao.insertMessage(new com.example.chater.model.Message("We should catch up.", now - 1 * hour, frankId, frankId), "Frank Castle");
        // Frank has 2 unread messages

        // 7. Grace (Unread)
        int graceId = 107;
        messageDao.insertMessage(new com.example.chater.model.Message("Check this out.", now - 30 * hour, graceId, graceId), "Grace Hopper");
        messageDao.insertMessage(new com.example.chater.model.Message("Found a bug in the compiler.", now - 30 * hour + 1000, graceId, graceId), "Grace Hopper");
        // Grace has 2 unread messages
    }
}
