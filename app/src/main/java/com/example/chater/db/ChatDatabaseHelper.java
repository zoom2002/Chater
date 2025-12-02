package com.example.chater.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ChatDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "chater.db";
    private static final int DATABASE_VERSION = 7;

    public static final String TABLE_MESSAGES = "messages";
    public static final String TABLE_CONVERSATIONS = "conversations";

    // Messages table columns
    public static final String COL_ID = "id";
    public static final String COL_CONTENT = "content";
    public static final String COL_TIMESTAMP = "timestamp";
    public static final String COL_SENDER_ID = "sender_id";
    public static final String COL_CHAT_ID = "chat_id";

    // Conversations table columns
    public static final String COL_TARGET_USER_ID = "target_user_id";
    public static final String COL_TARGET_USER_NAME = "target_user_name";
    public static final String COL_LAST_MESSAGE = "last_message";
    public static final String COL_LAST_TIMESTAMP = "last_timestamp";
    public static final String COL_UNREAD_COUNT = "unread_count";

    public ChatDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createMessagesTable = "CREATE TABLE " + TABLE_MESSAGES + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_CONTENT + " TEXT, " +
                COL_TIMESTAMP + " INTEGER, " +
                COL_SENDER_ID + " INTEGER, " +
                COL_CHAT_ID + " INTEGER)";

        String createConversationsTable = "CREATE TABLE " + TABLE_CONVERSATIONS + " (" +
                COL_TARGET_USER_ID + " INTEGER PRIMARY KEY, " +
                COL_TARGET_USER_NAME + " TEXT, " +
                COL_LAST_MESSAGE + " TEXT, " +
                COL_LAST_TIMESTAMP + " INTEGER, " +
                COL_UNREAD_COUNT + " INTEGER)";

        db.execSQL(createMessagesTable);
        db.execSQL(createConversationsTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MESSAGES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONVERSATIONS);
        onCreate(db);
    }
}
