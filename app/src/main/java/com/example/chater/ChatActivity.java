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

import android.view.MotionEvent;
import android.view.inputmethod.InputMethodManager;
import android.content.Context;

import android.view.ViewTreeObserver;
import android.graphics.Rect;

public class ChatActivity extends AppCompatActivity {
    public static final String EXTRA_TARGET_ID = "extra_target_id";
    public static final String EXTRA_TARGET_NAME = "extra_target_name";

    private int targetUserId;
    private String targetUserName;
    
    private View rootLayout;
    private RecyclerView recyclerView;
    private ChatAdapter adapter;
    private EditText etMessage;
    private MessageDao messageDao;
    private LinearLayout emojiPanel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        
        rootLayout = findViewById(R.id.rootLayout);

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
        
        // Scroll to bottom when keyboard opens
        recyclerView.addOnLayoutChangeListener((v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {
            if (bottom < oldBottom) {
                recyclerView.postDelayed(() -> {
                    if (adapter.getItemCount() > 0) {
                        recyclerView.scrollToPosition(adapter.getItemCount() - 1);
                    }
                }, 100);
            }
        });

        Button btnSend = findViewById(R.id.btnSend);
        btnSend.setOnClickListener(v -> sendMessage());

        ImageButton btnEmoji = findViewById(R.id.btnEmoji);
        emojiPanel = findViewById(R.id.emojiPanel);
        btnEmoji.setOnClickListener(v -> {
            if (emojiPanel.getVisibility() == View.VISIBLE) {
                // Close emoji, open keyboard
                emojiPanel.setVisibility(View.GONE);
                showKeyboard();
            } else {
                // Open emoji, close keyboard
                hideKeyboard();
                // Delay showing emoji panel slightly to allow keyboard to close smoothly
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    emojiPanel.setVisibility(View.VISIBLE);
                    // Scroll to bottom after panel opens
                    if (adapter.getItemCount() > 0) {
                        recyclerView.scrollToPosition(adapter.getItemCount() - 1);
                    }
                }, 100);
            }
        });
        
        etMessage.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                 if (emojiPanel.getVisibility() == View.VISIBLE) {
                     emojiPanel.setVisibility(View.GONE);
                 }
            }
            return false; // Let system handle focus and keyboard
        });
        
        setupEmojiGrid();
        
        // Hide keyboard when clicking on recycler view
        recyclerView.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                hideKeyboard();
                if (emojiPanel.getVisibility() == View.VISIBLE) {
                    emojiPanel.setVisibility(View.GONE);
                }
            }
            return false;
        });
        
        controlKeyboardLayout();
    }
    
    private void controlKeyboardLayout() {
        rootLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                rootLayout.getWindowVisibleDisplayFrame(r);
                
                int screenHeight = rootLayout.getRootView().getHeight();
                int keypadHeight = screenHeight - r.bottom;
                
                // 0.15 ratio is enough to detect keyboard
                if (keypadHeight > screenHeight * 0.15) {
                    // Keyboard is open
                    // Set padding to bottom to push content up
                    // Note: If adjustResize is working properly, keypadHeight might be small (system handled)
                    // But since we set windowSoftInputMode="stateHidden", system might not resize.
                    // Actually, with stateHidden (or nothing), system might do nothing or adjustPan.
                    // We want to FORCE resize by padding.
                    
                    // Check if we already have padding
                    if (rootLayout.getPaddingBottom() != keypadHeight) {
                        rootLayout.setPadding(0, 0, 0, keypadHeight);
                        // Also scroll to bottom
                         if (adapter.getItemCount() > 0) {
                             recyclerView.scrollToPosition(adapter.getItemCount() - 1);
                         }
                    }
                } else {
                    // Keyboard is closed
                    if (rootLayout.getPaddingBottom() != 0) {
                        rootLayout.setPadding(0, 0, 0, 0);
                    }
                }
            }
        });
    }
    
    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        etMessage.clearFocus();
    }
    
    private void showKeyboard() {
        etMessage.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(etMessage, InputMethodManager.SHOW_IMPLICIT);
    }
    
    private void setupEmojiGrid() {
        GridView gridView = findViewById(R.id.emojiGrid);
        // Expanded emoji list
        String[] emojis = new String[]{
            "😀", "😂", "😍", "👍", "🎉", "❤️", "😎", "🤔", "😭", "👋",
            "😊", "🤣", "😘", "👏", "🎊", "💔", "🤓", "😐", "😢", "👌",
            "😁", "😅", "🥰", "🙌", "🎈", "💕", "🧐", "😑", "😤", "🤝",
            "😆", "😉", "🤩", "🙏", "🎁", "💘", "🤠", "😶", "😡", "✌️",
            "😅", "😋", "🤗", "💪", "🎂", "💖", "😈", "🙄", "🤬", "🤞"
        };
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
