package com.store.mychat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Set;


public class Home_Activity extends AppCompatActivity {

    TabLayout tab;
    ViewPager viewPager;
//    ImageButton imbtn;

    DatabaseReference databaseReference;
    UserAdapter userAdapter;

    TextView UserName;
    FirebaseAuth auth;
    FirebaseUser currentUser;
    ImageView AddUser , userImage,Settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        getSupportActionBar().hide();

        UserName = findViewById(R.id.UserName);
        userImage = findViewById(R.id.userImg);
        Settings = findViewById(R.id.settings);
//********************************************************************************
        tab = findViewById(R.id.TabLayout);
        viewPager = findViewById(R.id.ViewPager);

        AddUser = findViewById(R.id.AddUser);

        VPAdapter adapter = new VPAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);

        tab.setupWithViewPager(viewPager);



        AddUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Home_Activity.this, MainActivity.class) );
//                Toast.makeText(Home_Activity.this , "Add contact ", Toast.LENGTH_LONG).show();
            }
        });


//*****************************************************************************************


        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();

        if (currentUser == null) {
            // If the user is not authenticated, redirect to the login activity
            startActivity(new Intent(Home_Activity.this, Authentication.class));
            finish();
            return;
        }

        DatabaseReference ProReference = FirebaseDatabase.getInstance().getReference("users");

        ProReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    UserModel user = snapshot.getValue(UserModel.class);
                    if(user.getUserId().equals(currentUser.getUid())){
                        UserName.setText(user.getUserName());
                        Glide.with(Home_Activity.this)
                                .load(user.getUserProfile())
                                .into(userImage);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        Settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Home_Activity.this, Settings.class));

            }
        });


    }


    public void showRecMenu(View view){
        PopupMenu popupMenu = new PopupMenu(this , view);
        MenuInflater inflater = new MenuInflater(this);
        inflater.inflate(R.menu.main_menu,popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                return onPopupMenuClick(menuItem);
            }
        });
        popupMenu.show();

    }

    private boolean onPopupMenuClick(MenuItem item){

        if(item.getItemId() == R.id.settings) {
            startActivity(new Intent(Home_Activity.this, Settings.class));
        }
        return true;
    }

    public void status(String status,long lastSeen  ) {


        DatabaseReference statusReff = FirebaseDatabase.getInstance().getReference().child("users").child(currentUser.getUid());

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
        long lastSeen = System.currentTimeMillis();

        status("Offline" , lastSeen);
    }


}