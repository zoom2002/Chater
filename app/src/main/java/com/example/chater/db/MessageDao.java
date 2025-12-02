package com.example.chater.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.chater.model.Conversation;
import com.example.chater.model.Message;

import java.util.ArrayList;
import java.util.List;

public class MessageDao {
    private ChatDatabaseHelper dbHelper;

    public MessageDao(Context context) {
        dbHelper = new ChatDatabaseHelper(context);
    }

    public void insertMessage(Message message, String userName) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ChatDatabaseHelper.COL_CONTENT, message.getContent());
        values.put(ChatDatabaseHelper.COL_TIMESTAMP, message.getTimestamp());
        values.put(ChatDatabaseHelper.COL_SENDER_ID, message.getSenderId());
        values.put(ChatDatabaseHelper.COL_CHAT_ID, message.getChatId());
        db.insert(ChatDatabaseHelper.TABLE_MESSAGES, null, values);
        
        updateConversation(message, userName);
    }

    // Overload for compatibility or internal use
    public void insertMessage(Message message) {
        insertMessage(message, "User " + message.getChatId());
    }

    private void updateConversation(Message message, String userName) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int targetUserId = message.getChatId();
        
        Cursor cursor = db.query(ChatDatabaseHelper.TABLE_CONVERSATIONS, null,
                ChatDatabaseHelper.COL_TARGET_USER_ID + "=?", new String[]{String.valueOf(targetUserId)},
                null, null, null);
        
        ContentValues values = new ContentValues();
        values.put(ChatDatabaseHelper.COL_LAST_MESSAGE, message.getContent());
        values.put(ChatDatabaseHelper.COL_LAST_TIMESTAMP, message.getTimestamp());
        
        if (cursor.moveToFirst()) {
            if (message.getSenderId() != Message.SENDER_SELF) {
                int currentUnread = cursor.getInt(cursor.getColumnIndexOrThrow(ChatDatabaseHelper.COL_UNREAD_COUNT));
                values.put(ChatDatabaseHelper.COL_UNREAD_COUNT, currentUnread + 1);
            }
            // Update name if provided and not default
            if (userName != null && !userName.startsWith("User ")) {
                 values.put(ChatDatabaseHelper.COL_TARGET_USER_NAME, userName);
            }
            db.update(ChatDatabaseHelper.TABLE_CONVERSATIONS, values,
                    ChatDatabaseHelper.COL_TARGET_USER_ID + "=?", new String[]{String.valueOf(targetUserId)});
        } else {
            values.put(ChatDatabaseHelper.COL_TARGET_USER_ID, targetUserId);
            values.put(ChatDatabaseHelper.COL_TARGET_USER_NAME, userName != null ? userName : "User " + targetUserId);
            values.put(ChatDatabaseHelper.COL_UNREAD_COUNT, message.getSenderId() != Message.SENDER_SELF ? 1 : 0);
            db.insert(ChatDatabaseHelper.TABLE_CONVERSATIONS, null, values);
        }
        cursor.close();
    }

    public List<Message> getMessages(int chatId) {
        List<Message> messages = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(ChatDatabaseHelper.TABLE_MESSAGES, null,
                ChatDatabaseHelper.COL_CHAT_ID + "=?", new String[]{String.valueOf(chatId)},
                null, null, ChatDatabaseHelper.COL_TIMESTAMP + " ASC");

        if (cursor.moveToFirst()) {
            do {
                Message msg = new Message();
                msg.setId(cursor.getLong(cursor.getColumnIndexOrThrow(ChatDatabaseHelper.COL_ID)));
                msg.setContent(cursor.getString(cursor.getColumnIndexOrThrow(ChatDatabaseHelper.COL_CONTENT)));
                msg.setTimestamp(cursor.getLong(cursor.getColumnIndexOrThrow(ChatDatabaseHelper.COL_TIMESTAMP)));
                msg.setSenderId(cursor.getInt(cursor.getColumnIndexOrThrow(ChatDatabaseHelper.COL_SENDER_ID)));
                msg.setChatId(cursor.getInt(cursor.getColumnIndexOrThrow(ChatDatabaseHelper.COL_CHAT_ID)));
                messages.add(msg);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return messages;
    }

    public List<Conversation> getConversations() {
        List<Conversation> conversations = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        // Sort by unread (unread > 0) DESC, then timestamp DESC
        String orderBy = "(CASE WHEN " + ChatDatabaseHelper.COL_UNREAD_COUNT + " > 0 THEN 1 ELSE 0 END) DESC, " + 
                         ChatDatabaseHelper.COL_LAST_TIMESTAMP + " DESC";
        
        Cursor cursor = db.query(ChatDatabaseHelper.TABLE_CONVERSATIONS, null, null, null, null, null, orderBy);

        if (cursor.moveToFirst()) {
            do {
                Conversation conv = new Conversation();
                conv.setTargetUserId(cursor.getInt(cursor.getColumnIndexOrThrow(ChatDatabaseHelper.COL_TARGET_USER_ID)));
                conv.setTargetUserName(cursor.getString(cursor.getColumnIndexOrThrow(ChatDatabaseHelper.COL_TARGET_USER_NAME)));
                conv.setLastMessage(cursor.getString(cursor.getColumnIndexOrThrow(ChatDatabaseHelper.COL_LAST_MESSAGE)));
                conv.setLastTimestamp(cursor.getLong(cursor.getColumnIndexOrThrow(ChatDatabaseHelper.COL_LAST_TIMESTAMP)));
                conv.setUnreadCount(cursor.getInt(cursor.getColumnIndexOrThrow(ChatDatabaseHelper.COL_UNREAD_COUNT)));
                conversations.add(conv);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return conversations;
    }

    public void clearUnreadCount(int targetUserId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ChatDatabaseHelper.COL_UNREAD_COUNT, 0);
        db.update(ChatDatabaseHelper.TABLE_CONVERSATIONS, values,
                ChatDatabaseHelper.COL_TARGET_USER_ID + "=?", new String[]{String.valueOf(targetUserId)});
    }
}
