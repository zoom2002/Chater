package com.example.chater.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.chater.R;
import com.example.chater.model.Message;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_LEFT = 1;
    private static final int TYPE_RIGHT = 2;

    private List<Message> messages = new ArrayList<>();

    public void setMessages(List<Message> messages) {
        this.messages = messages;
        notifyDataSetChanged();
    }

    public void addMessage(Message message) {
        messages.add(message);
        notifyItemInserted(messages.size() - 1);
    }

    @Override
    public int getItemViewType(int position) {
        Message msg = messages.get(position);
        return msg.getSenderId() == Message.SENDER_SELF ? TYPE_RIGHT : TYPE_LEFT;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == TYPE_LEFT) {
            View view = inflater.inflate(R.layout.item_msg_left, parent, false);
            return new MsgViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.item_msg_right, parent, false);
            return new MsgViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MsgViewHolder vh = (MsgViewHolder) holder;
        Message msg = messages.get(position);
        vh.tvContent.setText(msg.getContent());
        vh.tvTime.setText(formatTime(msg.getTimestamp()));
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    private String formatTime(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }

    static class MsgViewHolder extends RecyclerView.ViewHolder {
        TextView tvContent;
        TextView tvTime;

        MsgViewHolder(View itemView) {
            super(itemView);
            tvContent = itemView.findViewById(R.id.tvContent);
            tvTime = itemView.findViewById(R.id.tvTime);
        }
    }
}
