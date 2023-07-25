package com.store.mychat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.usage.NetworkStats;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.store.mychat.databinding.ActivityChatBinding;
import com.vanniktech.emoji.EmojiPopup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class ChatActivity extends AppCompatActivity {
    ActivityChatBinding binding;
    String reciverimg,recemail;
    String reciverName;
    String receiverId , reciverStatus , Status;
    DatabaseReference senderReference, receiverReference ,reference,statusReff;
    String senderRoom, receiverRoom;
    String senderId;
    MessageAdapter messageAdapter;
    FirebaseAuth auth;
    FirebaseUser crrUser;
    DatabaseReference databaseReference , ref;
    ValueEventListener seenListener;
    Intent intent;
    List<MessageModel> mchat;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();



        auth = FirebaseAuth.getInstance();
        crrUser = auth.getCurrentUser();

        recemail = getIntent().getStringExtra("Email");
        receiverId = getIntent().getStringExtra("id");
        reciverName = getIntent().getStringExtra("nameeee");
        reciverimg = getIntent().getStringExtra("reciverImg");
        reciverStatus = getIntent().getStringExtra("userStatus");

        DatabaseReference statusRef = FirebaseDatabase.getInstance().getReference().child("users");


        statusRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String uid = dataSnapshot.getKey();
                    if (uid.equals(receiverId)) {
                        UserModel userModel = dataSnapshot.getValue(UserModel.class);
                        binding.chatUserStatus.setText(userModel.getStatus());
                    }
                }
                readMessage(crrUser.getUid(),receiverId);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ChatActivity.this, "Failed to load Status: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        senderId = FirebaseAuth.getInstance().getUid();
//        senderRoom = senderId + receiverId;
//        receiverRoom = receiverId + senderId;

        Picasso.get().load(reciverimg).into(binding.UserProfileChat);
        binding.chatUserName.setText(reciverName);

//        messageAdapter = new MessageAdapter(this);

        binding.recycler.setAdapter(messageAdapter);
        binding.recycler.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setSmoothScrollbarEnabled(true);
        linearLayoutManager.setStackFromEnd(true);

        binding.recycler.setLayoutManager(linearLayoutManager);




        senderReference = FirebaseDatabase.getInstance().getReference("chats");

        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ChatActivity.this,Home_Activity.class));
                finish();
            }
        });

        binding.sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = binding.messageEd.getText().toString().trim();
                if (!message.equals("")) {
                    sendMessage(message , senderId , receiverId);
                }else {
                    Toast.makeText(ChatActivity.this, "You can't send empty message",Toast.LENGTH_SHORT).show();
                }
                binding.messageEd.setText("");

            }
        });



        ref = FirebaseDatabase.getInstance().getReference("users").child(receiverId);





        binding.UserProfileChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder alert = new AlertDialog.Builder(ChatActivity.this);
                View view1 = getLayoutInflater().inflate(R.layout.profile_dialog,null);
                ImageView profilePic = (ImageView) view1.findViewById(R.id.ProfileImage);
                TextView name = (TextView) view1.findViewById(R.id.UserNameClick);

                alert.setView(view1);

                databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(receiverId).child("userProfile");
                Picasso.get().load(reciverimg).into(profilePic);
                name.setText(reciverName);

                final AlertDialog alertDialog = alert.create();
                alertDialog.setCanceledOnTouchOutside(false);

                alertDialog.show();

            }
        });

        binding.chatUserAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChatActivity.this, personAbout.class);
                intent.putExtra("id", receiverId);
                intent.putExtra("nameeee", reciverName);
                intent.putExtra("reciverImg", reciverimg);
                intent.putExtra("userStatus",reciverStatus);
                intent.putExtra("Email",recemail);
                startActivity(intent);
                finish();


            }
        });

//        seenMessage(receiverId);


    }


//    private void seenMessage(String userId){
//        reference = FirebaseDatabase.getInstance().getReference("chats");
//        seenListener = reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
//                for(DataSnapshot snapshot : datasnapshot.getChildren()){
//                    MessageModel chat = snapshot.getValue(MessageModel.class);
//                    if(chat.getReceiverId().equals(crrUser.getUid()) && chat.getSenderId().equals(receiverId) ){
//                        HashMap<String , Object> hashMap = new HashMap<>();
//                        hashMap.put("isSeen",true);
//                        snapshot.getRef().updateChildren(hashMap);
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }

    private void sendMessage(String message , String senderId , String receiverId) {

        String messageId = UUID.randomUUID().toString();
        long timestamp = System.currentTimeMillis();
        reference = FirebaseDatabase.getInstance().getReference();

        HashMap<String , Object> hashMap = new HashMap<>();

        hashMap.put("message" , message);
        hashMap.put("receiverId",receiverId );
        hashMap.put("senderId",senderId );
        hashMap.put("timestamp", timestamp);
        hashMap.put("messageId" , messageId);
//        hashMap.put("isSeen" , false);

        reference.child("chats").child(messageId).setValue(hashMap);





//        String messageId = UUID.randomUUID().toString();
//        String senderId = FirebaseAuth.getInstance().getUid();
//        long timestamp = System.currentTimeMillis();
//
//
//        MessageModel messageModel = new MessageModel(messageId, senderId, message, timestamp ,receiverId );
//        binding.messageEd.setText("");
//        messageAdapter.add(messageModel);
//
//
//
//        senderReference.child(messageId).setValue(messageModel).addOnSuccessListener(new OnSuccessListener<Void>() {
//            @Override
//            public void onSuccess(Void aVoid) {
//                // Clear the EditText after sending the message
//                binding.messageEd.setText("");
//            }
//        });

    }


    public void readMessage(String myId , String userId){

        mchat = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference("chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {

                mchat.clear();
                for(DataSnapshot snapshot : datasnapshot.getChildren()){
                    MessageModel chat = snapshot.getValue(MessageModel.class);
                    if(chat.getReceiverId().equals(myId)  &&  chat.getSenderId().equals(userId) ||
                            chat.getReceiverId().equals(userId) && chat.getSenderId().equals(myId)){
                            mchat.add(chat);
                    }
                    messageAdapter = new MessageAdapter(ChatActivity.this,mchat);
                    binding.recycler.setAdapter(messageAdapter);
                    messageAdapter.sortMessages();

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }




//    public void showRecMenu(View view){
//
//        PopupMenu popupMenu = new PopupMenu(this , view);
//        MenuInflater inflater = new MenuInflater(this);
//        inflater.inflate(R.menu.receiver_detail_menu,popupMenu.getMenu());
//        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem menuItem) {
//                return onPopupMenuClick(menuItem);
//            }
//        });
//        popupMenu.show();
//
//    }
//    private boolean onPopupMenuClick(MenuItem item){
//
//        if(item.getItemId() == R.id.del_chat){
//
//            DatabaseReference Del_Chat_reff = FirebaseDatabase.getInstance().getReference().child("chats");
//
//            final AlertDialog.Builder alert = new AlertDialog.Builder(ChatActivity.this);
//            View view = getLayoutInflater().inflate(R.layout.del_chat_dialog , null);
//            TextView cancel = (TextView) view.findViewById(R.id.cancel_button);
//            TextView deleteForMe = (TextView) view.findViewById(R.id.delete_for_me);
//            TextView deleteForEveryone = (TextView) view.findViewById(R.id.delete_for_everyone);
//            alert.setView(view);
//            final AlertDialog alertDialog = alert.create();
//            alertDialog.setCanceledOnTouchOutside(true);
//
//            cancel.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    alertDialog.dismiss();
//                }
//            });
//
//            deleteForMe.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//
//
//                    senderReference.addValueEventListener(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot snapshot) {
//                            messageAdapter.clear();
//
//                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
//                                MessageModel messageModel = dataSnapshot.getValue(MessageModel.class);
//                                if(messageModel.getReceiverId().equals(receiverId) && messageModel.getSenderId().equals(senderId)){
//
//                                    senderReference.child(messageModel.getMessageId()).removeValue();
//
//                                }
//                                if(messageModel.getReceiverId().equals(senderId) && messageModel.getSenderId().equals(receiverId)){
//                                    senderReference.child(messageModel.getMessageId()).removeValue();
//                                }
//
//                            }
//
//                            // Sort the messages based on timestamps
//                            messageAdapter.sortMessages();
//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError error) {
//                            // Handle onCancelled event
//                        }
//                    });
//
//                    alertDialog.dismiss();
//
//                }
//            });
//
//
//            deleteForEveryone.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    senderReference.addValueEventListener(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot snapshot) {
//                            messageAdapter.clear();
//
//                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
//                                MessageModel messageModel = dataSnapshot.getValue(MessageModel.class);
//                                if(messageModel.getReceiverId().equals(receiverId) && messageModel.getSenderId().equals(senderId)){
//
//                                    senderReference.child(messageModel.getMessageId()).removeValue();
//
//                                }
//                                if(messageModel.getReceiverId().equals(senderId) && messageModel.getSenderId().equals(receiverId)){
//                                    senderReference.child(messageModel.getMessageId()).removeValue();
//                                }
//
//                            }
//
//                            // Sort the messages based on timestamps
//                            messageAdapter.sortMessages();
//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError error) {
//                            // Handle onCancelled event
//                        }
//                    });
//
//                    alertDialog.dismiss();
//
//                }
//            });
//            alertDialog.show();
//
////            Toast.makeText(getApplicationContext(),"Chat deteted from you", Toast.LENGTH_LONG).show();
//        } else if (item.getItemId() == R.id.block_user) {
//
//            Toast.makeText(getApplicationContext(),"Block User option is not Available", Toast.LENGTH_LONG).show();
//        } else if (item.getItemId() == R.id.view_user) {
//            Toast.makeText(getApplicationContext(),"UserView option is not Available", Toast.LENGTH_LONG).show();
//
//        }
//        return true;
//    }

    //    **************** Status *******************************
    public void status(String status ,long lastSeen ) {


        statusReff = FirebaseDatabase.getInstance().getReference().child("users").child(crrUser.getUid());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("Status", status);
        hashMap.put("lastSeen" ,lastSeen);
        statusReff.updateChildren(hashMap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        long lastSeen = 0;
        status("Online" , lastSeen);
    }

    @Override
    protected void onPause() {
        super.onPause();
        long lastSeen = System.currentTimeMillis();;
//        statusReff.removeEventListener(seenListener);
        status("Offline" , lastSeen);
    }
//****************************************************************

}
