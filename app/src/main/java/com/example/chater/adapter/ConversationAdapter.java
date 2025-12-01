package com.example.chater.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.chater.ChatActivity;
import com.example.chater.R;
import com.example.chater.model.Conversation;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.ViewHolder> {
    private List<Conversation> conversations = new ArrayList<>();
    private Context context;

    public ConversationAdapter(Context context) {
        this.context = context;
    }

    public void setConversations(List<Conversation> conversations) {
        this.conversations = conversations;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_conversation, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Conversation conv = conversations.get(position);
        holder.tvName.setText(conv.getTargetUserName());
        holder.tvLastMsg.setText(conv.getLastMessage());
        holder.tvTime.setText(formatTime(conv.getLastTimestamp()));
        
        if (conv.getUnreadCount() > 0) {
            holder.tvUnread.setVisibility(View.VISIBLE);
            holder.tvUnread.setText(String.valueOf(conv.getUnreadCount()));
        } else {
            holder.tvUnread.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ChatActivity.class);
            intent.putExtra(ChatActivity.EXTRA_TARGET_ID, conv.getTargetUserId());
            intent.putExtra(ChatActivity.EXTRA_TARGET_NAME, conv.getTargetUserName());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return conversations.size();
    }

    private String formatTime(long timestamp) {
        if (timestamp == 0) return "";
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvLastMsg, tvTime, tvUnread;

        ViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvLastMsg = itemView.findViewById(R.id.tvLastMsg);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvUnread = itemView.findViewById(R.id.tvUnread);
        }
    }
}
