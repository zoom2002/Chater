package com.example.chater;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chater.adapter.ChatAdapter;
import com.example.chater.db.MessageDao;
import com.example.chater.model.Message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {
    public static final String EXTRA_TARGET_ID = "extra_target_id";
    public static final String EXTRA_TARGET_NAME = "extra_target_name";

    private int targetUserId;
    private String targetUserName;
    
    private RecyclerView recyclerView;
    private ChatAdapter adapter;
    private EditText etMessage;
    private MessageDao messageDao;
    private LinearLayout emojiPanel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        targetUserId = getIntent().getIntExtra(EXTRA_TARGET_ID, 1);
        targetUserName = getIntent().getStringExtra(EXTRA_TARGET_NAME);
        if (targetUserName == null) targetUserName = "User " + targetUserId;

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(targetUserName);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        messageDao = new MessageDao(this);
        // Clear unread count
        messageDao.clearUnreadCount(targetUserId);

        initViews();
        loadMessages();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerViewChat);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ChatAdapter();
        recyclerView.setAdapter(adapter);

        etMessage = findViewById(R.id.etMessage);
        Button btnSend = findViewById(R.id.btnSend);
        btnSend.setOnClickListener(v -> sendMessage());

        ImageButton btnEmoji = findViewById(R.id.btnEmoji);
        emojiPanel = findViewById(R.id.emojiPanel);
        btnEmoji.setOnClickListener(v -> {
            if (emojiPanel.getVisibility() == View.VISIBLE) {
                emojiPanel.setVisibility(View.GONE);
            } else {
                emojiPanel.setVisibility(View.VISIBLE);
            }
        });
        
        setupEmojiGrid();
    }
    
    private void setupEmojiGrid() {
        GridView gridView = findViewById(R.id.emojiGrid);
        // Simple emoji list
        String[] emojis = new String[]{"😀", "😂", "😍", "👍", "🎉", "❤️", "😎", "🤔", "😭", "👋"};
        List<Map<String, String>> data = new ArrayList<>();
        for (String e : emojis) {
            Map<String, String> map = new HashMap<>();
            map.put("emoji", e);
            data.add(map);
        }
        
        SimpleAdapter simpleAdapter = new SimpleAdapter(this, data, 
                android.R.layout.simple_list_item_1, 
                new String[]{"emoji"}, new int[]{android.R.id.text1});
        
        gridView.setAdapter(simpleAdapter);
        gridView.setOnItemClickListener((parent, view, position, id) -> {
             String emoji = emojis[position];
             etMessage.append(emoji);
        });
    }

    private void sendMessage() {
        String content = etMessage.getText().toString().trim();
        if (content.isEmpty()) return;

        Message msg = new Message(content, System.currentTimeMillis(), Message.SENDER_SELF, targetUserId);
        messageDao.insertMessage(msg);
        adapter.addMessage(msg);
        recyclerView.smoothScrollToPosition(adapter.getItemCount() - 1);
        etMessage.setText("");

        // Simulate reply
        simulateReply();
    }

    private void simulateReply() {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            String replyContent = "Reply to: " + System.currentTimeMillis(); // Simple reply
            Message reply = new Message(replyContent, System.currentTimeMillis(), targetUserId, targetUserId); // senderId is targetUserId
            messageDao.insertMessage(reply);
            
            // Since we are in the chat, clear unread count immediately
            messageDao.clearUnreadCount(targetUserId);
            
            // Only update UI if activity is still valid (simplified check)
            if (!isFinishing()) {
                adapter.addMessage(reply);
                recyclerView.smoothScrollToPosition(adapter.getItemCount() - 1);
            }
        }, 1500);
    }

    private void loadMessages() {
        List<Message> messages = messageDao.getMessages(targetUserId);
        adapter.setMessages(messages);
        if (!messages.isEmpty()) {
            recyclerView.scrollToPosition(messages.size() - 1);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
