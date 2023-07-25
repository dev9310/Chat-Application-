package com.store.mychat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.BasePermissionListener;
import com.karumi.dexter.listener.single.PermissionListener;
import com.squareup.picasso.Picasso;
import com.store.mychat.databinding.ActivitySettingsBinding;

import java.io.InputStream;
import java.nio.channels.Pipe;
import java.util.HashMap;

public class Settings extends AppCompatActivity {

    ActivitySettingsBinding binding;
    String changedName;
    DatabaseReference databaseReference;
    FirebaseAuth auth;

    StorageReference storageReference;
    private Uri FilePath;
    private Bitmap bitmap;
    String url;
    String name,Status;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();

//        Showing Profile on ImageView and Name
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
//        String url = firebaseUser.getPhotoUrl().toString();
//        name = firebaseUser.getDisplayName().toString();
        String Email = firebaseUser.getEmail();


        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseUser User = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(User.getUid());


        databaseReference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.isComplete()){
                    if(task.getResult().exists()){
                        DataSnapshot snapshot = task.getResult();

                        name = String.valueOf(snapshot.child("userName").getValue());
//                        Toast.makeText(getApplicationContext(), name, Toast.LENGTH_LONG).show();
                        binding.NameSetting.setText(name);


                        url = String.valueOf(snapshot.child("userProfile").getValue());
//                        Toast.makeText(getApplicationContext(), url, Toast.LENGTH_LONG).show();
                        Picasso.get().load(url).into(binding.personsImage);

                        Status = String.valueOf(snapshot.child("userStatus").getValue());
                        binding.Status.setText(Status);


                    }
                }else {
                    Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
                }
            }
        });

        binding.Email.setText(Email);


        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent (Settings.this,Home_Activity.class));
                finish();
            }
        });

        binding.DELAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    auth = FirebaseAuth.getInstance();
                    FirebaseUser user = auth.getCurrentUser();

                    String uid = auth.getUid().toString();

                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference root = database.getReference().child("users").child(uid);

                    storageReference = FirebaseStorage.getInstance().getReference().child(FirebaseAuth.getInstance().getUid());

                    assert user != null;
                    user.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {

                            root.setValue(null);
                            storageReference.delete();
                            Toast.makeText(Settings.this, "Account Deleted",Toast.LENGTH_SHORT).show();
                            auth.signOut();
                            startActivity(new Intent(Settings.this , Authentication.class));
                            finish();

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Settings.this, "Unable To Delete Account",Toast.LENGTH_SHORT).show();
                        }
                    });

            }
        });

        binding.LOGOUT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(Settings.this,Authentication.class));
                finish();
            }
        });

        binding.personsImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dexter.withActivity(Settings.this)
                        .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse response) {
                                Intent intent = new Intent(Intent.ACTION_PICK);
                                intent.setType("image/*");
                                startActivityForResult(Intent.createChooser(intent,"Select Image File"),1);

                            }

                            @Override
                            public void onPermissionDenied(PermissionDeniedResponse response) {

                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                                token.continuePermissionRequest();
                            }
                        }).check();
            }
        });

        binding.SaveDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(firebaseUser.getUid()).child("userProfile");


//                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
//                assert firebaseUser != null;
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference uploader = storage.getReference().child(FirebaseAuth.getInstance().getUid());

                uploader.putFile(FilePath)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Toast.makeText(getApplicationContext(), "Changed Profile Pic Sucessfully !!", Toast.LENGTH_LONG).show();
                                uploader.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                            databaseReference.setValue(uri.toString());

                                    }
                                });


                            }
                        });
                startActivity(new Intent(Settings.this , Home_Activity.class));



            }
        });

        binding.editPencil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                final AlertDialog.Builder alert = new AlertDialog.Builder(Settings.this);
                View view1 = getLayoutInflater().inflate(R.layout.custom_dialog , null);

                EditText EditName = (EditText) view1.findViewById(R.id.EditName);
                Button btn_cancel = (Button) view1.findViewById(R.id.cancel_button);
                Button btn_ok = (Button) view1.findViewById(R.id.Ok_btn);

                alert.setView(view1).setTitle("Change Name");

                String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(firebaseUser.getUid()).child("userName");

                final AlertDialog alertDialog = alert.create();
                alertDialog.setCanceledOnTouchOutside(false);

                btn_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                    }
                });

                btn_ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        name =  EditName.getText().toString();
                        Toast.makeText(getApplicationContext(), "Name Changed Sucessfully !!", Toast.LENGTH_LONG).show();
                        binding.NameSetting.setText(name);
                        alertDialog.dismiss();
                        databaseReference.setValue(name);
                    }
                });
                alertDialog.show();
            }
        });

        binding.editPencil2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder alert = new AlertDialog.Builder(Settings.this);
                View view1 = getLayoutInflater().inflate(R.layout.custom_dialog , null);
                EditText status = (EditText) view1.findViewById(R.id.EditName);
                Button btn_cancel = (Button) view1.findViewById(R.id.cancel_button);
                Button btn_ok = (Button) view1.findViewById(R.id.Ok_btn);

                alert.setView(view1).setTitle("Change Status");


                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(firebaseUser.getUid()).child("userStatus");

                final AlertDialog alertDialog = alert.create();
                alertDialog.setCanceledOnTouchOutside(false);

                btn_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                    }
                });

                btn_ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Status =  status.getText().toString();
                        Toast.makeText(getApplicationContext(), "Name Changed Sucessfully !!", Toast.LENGTH_LONG).show();
                        binding.Status.setText(Status);
                        alertDialog.dismiss();
                        databaseReference.setValue(Status);
                    }
                });
                alertDialog.show();

            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK && requestCode == 1 && data != null) {
            FilePath = data.getData();
            try {
                InputStream inputStream = getContentResolver().openInputStream(FilePath);
                bitmap = BitmapFactory.decodeStream(inputStream);
                binding.personsImage.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Error selecting image", Toast.LENGTH_SHORT).show();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }



    public void status(String status , long lastSeen ) {


        DatabaseReference statusReff = FirebaseDatabase.getInstance().getReference().child("users").child(firebaseUser.getUid());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("Status", status);
        hashMap.put("lastSeen" ,lastSeen);

        statusReff.updateChildren(hashMap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        long lastSeen = 0;

        status("Online", lastSeen);
    }

    @Override
    protected void onPause() {
        super.onPause();
        long lastSeen = System.currentTimeMillis();;

        status("Offline",lastSeen);
    }

}