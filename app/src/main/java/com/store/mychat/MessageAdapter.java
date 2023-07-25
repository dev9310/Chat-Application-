package com.store.mychat;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MyViewHolder> {

    private Context context;
    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;
    FirebaseUser fUser;
    private List<MessageModel> messageModelList ;

    FirebaseUser fuser;

    public MessageAdapter(Context context , List<MessageModel> messageModelList) {
        this.context = context;
        this.messageModelList = messageModelList;
    }

    void add(MessageModel messageModel) {
        messageModelList.add(messageModel);
        notifyDataSetChanged();
    }

    public void clear() {
        messageModelList.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_TYPE_RIGHT) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sender_row, parent, false);
            return new MyViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.receiver_row, parent, false);
            return new MyViewHolder(view);
        }
    }


    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        MessageModel messageModel = messageModelList.get(position);
        holder.msg.setText(messageModel.getMessage());
        long timestamp = messageModel.getTimestamp();
        holder.timestamp.setText(getFormattedTimestamp(timestamp));

        // Check the type of message and set the gravity for the layout
        int viewType = getItemViewType(position);
        if (viewType == MSG_TYPE_RIGHT) {
            holder.main.setGravity(Gravity.END);
        } else {
            holder.main.setGravity(Gravity.START);
        }



//        if(position == messageModelList.size()-1 ){
//            if(messageModel.isSeen()){
//                holder.seen_tick.setVisibility(View.VISIBLE);
//                holder.sent_tick.setVisibility(View.GONE);
//            }else {
//                holder.sent_tick.setVisibility(View.VISIBLE);
//                holder.seen_tick.setVisibility(View.GONE);
//            }
//        }else {
//            holder.sent_tick.setVisibility(View.VISIBLE);
//            holder.seen_tick.setVisibility(View.GONE);
//        }

    }

    @Override
    public int getItemCount() {
        return messageModelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView msg;
        private TextView timestamp;
//        private ImageView sent_tick , seen_tick;
        private RelativeLayout main;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            msg = itemView.findViewById(R.id.message);
            timestamp = itemView.findViewById(R.id.timestamp);
            main = itemView.findViewById(R.id.mainMessageLayout);
//            sent_tick = itemView.findViewById(R.id.sent);
//            seen_tick = itemView.findViewById(R.id.seen);


        }
    }

    // Helper method to format timestamp to a readable format
    private String getFormattedTimestamp(long timestamp) {
        try {

            SimpleDateFormat sdf = new SimpleDateFormat("h:mm a");
            return sdf.format(new Date(timestamp));
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    // Method to sort the messages based on their timestamps
    public void sortMessages() {
        messageModelList.sort(new Comparator<MessageModel>() {
            @Override
            public int compare(MessageModel message1, MessageModel message2) {
                return Long.compare(message1.getTimestamp(), message2.getTimestamp());
            }
        });
        notifyDataSetChanged();
    }
    public int getItemViewType(int position) {
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        if (messageModelList.get(position).getSenderId().equals(fUser.getUid())) {
            return MSG_TYPE_RIGHT; // Return the view type for the sender's messages
        } else {
            return MSG_TYPE_LEFT; // Return the view type for the receiver's messages
        }
    }
}
