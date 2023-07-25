package com.store.mychat;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.store.mychat.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    DatabaseReference databaseReference;

    List<UserModel> mUsers;
    UserAdapter userAdapter;
    FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Objects.requireNonNull(getSupportActionBar()).hide();

        databaseReference = FirebaseDatabase.getInstance().getReference("users");


        binding.recycler.setHasFixedSize(true);
        binding.recycler.setLayoutManager(new LinearLayoutManager(this));

        mUsers = new ArrayList<>();

        readUser();
    }


    private void readUser() {

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");


        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                mUsers.clear();
                for(DataSnapshot snapshot : datasnapshot.getChildren()){
                    UserModel user = snapshot.getValue(UserModel.class);
                    assert user != null;
                    assert firebaseUser != null;
                    if(!user.getUserId().equals(firebaseUser.getUid())){
                        mUsers.add(user);
                    }
                }
                userAdapter = new UserAdapter( MainActivity.this ,mUsers,true );
                binding.recycler.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }



//     Status Update
private void status(String status  , long lastSeen) {
    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    if (firebaseUser != null) {
        DatabaseReference userRef = databaseReference.child(firebaseUser.getUid());
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("Status", status);
        hashMap.put("lastSeen" ,lastSeen);
        userRef.updateChildren(hashMap);
    }
}

    @Override
    public void onResume() {
        super.onResume();
        if (userAdapter != null) {
            userAdapter.setIsChat(true);
        }
        long lastSeen = 0;

        status("Online", lastSeen);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (userAdapter != null) {
            userAdapter.setIsChat(false);
        }
        long lastSeen = System.currentTimeMillis();;

        status("Offline",lastSeen);
    }

}
