package com.example.sachithariahilaka.smmv;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class Settings extends AppCompatActivity {
    private EditText Username,Fullname,phone,status;
    private Button update;
    private CircleImageView profileimg;
    private DatabaseReference settingsUserRef;
    private FirebaseAuth mAuth;
    private String curruntUId,downloadUrl;
    private ProgressDialog progressDialog;
    private int gallery_pick=1;
    private Uri imageUri;
    private StorageReference profilepic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar=(Toolbar) findViewById(R.id.settings_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(" Account Settings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        progressDialog=new ProgressDialog(this);

        mAuth=FirebaseAuth.getInstance();
        curruntUId=mAuth.getCurrentUser().getUid();
        settingsUserRef= FirebaseDatabase.getInstance().getReference().child("Users").child(curruntUId);
        profilepic= FirebaseStorage.getInstance().getReference("Profile Pictures");

        Username=findViewById(R.id.username);
        Fullname=findViewById(R.id.fullname);
        phone=findViewById(R.id.number);
        status=findViewById(R.id.status);
        update=findViewById(R.id.update);
        profileimg=findViewById(R.id.pp);

        settingsUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists())
                {
                    String Profileimg=dataSnapshot.child("Profile").getValue().toString();
                    String username=dataSnapshot.child("Username").getValue().toString();
                    String fullname=dataSnapshot.child("Fullname").getValue().toString();
                    String Phone=dataSnapshot.child("Phone Number").getValue().toString();
                    String Status=dataSnapshot.child("Status").getValue().toString();

                    Picasso.get().load(Profileimg).placeholder(R.drawable.profile).into(profileimg);

                    Username.setText(username);
                    Fullname.setText(fullname);
                    phone.setText(Phone);
                    status.setText(Status);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                ValidateAccountDetails();

            }
        });

        profileimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gallery=new Intent();
                gallery.setAction(Intent.ACTION_GET_CONTENT);
                gallery.setType("image/*");
                startActivityForResult(gallery,gallery_pick);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==gallery_pick && resultCode==RESULT_OK && data!=null)
        {
            imageUri=data.getData();
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);
        }

        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        {
            CropImage.ActivityResult result=CropImage.getActivityResult(data);
            if(resultCode==RESULT_OK)
            {
                imageUri=result.getUri();

                progressDialog.setMessage("Profile Picture Updating!!");
                progressDialog.show();
                progressDialog.setCanceledOnTouchOutside(false);

                final StorageReference filepath=profilepic.child(curruntUId + ".jpg");
                final UploadTask uploadTask=filepath.putFile(imageUri);

                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(Settings.this,"Error Occured!!", Toast.LENGTH_SHORT).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        progressDialog.dismiss();
                        Toast.makeText(Settings.this,"Profile Image Updated Succusfully!!", Toast.LENGTH_SHORT).show();

                        Task<Uri> uriTask=uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                            @Override
                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception
                            {
                                if(!task.isSuccessful())
                                {
                                    throw task.getException();
                                }

                                downloadUrl=filepath.getDownloadUrl().toString();
                                return filepath.getDownloadUrl();
                            }
                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if(task.isSuccessful())
                                {
                                    downloadUrl=task.getResult().toString();
                                    saveImageinfo();
                                }
                                else
                                {
                                    Toast.makeText(Settings.this,"Error Occured!!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });

            }
            else
            {
                Toast.makeText(this,"Error Occured!!", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void saveImageinfo()
    {
        HashMap pp=new HashMap();
        pp.put("Profile", downloadUrl);
        settingsUserRef.updateChildren(pp);
    }

    private void ValidateAccountDetails()
    {
        String username=Username.getText().toString();
        String fullname=Fullname.getText().toString();
        String Status=status.getText().toString();
        String Number=phone.getText().toString();

        if(TextUtils.isEmpty(username))
        {
            Toast.makeText(Settings.this,"Please Enter Your Username", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(fullname))
        {
            Toast.makeText(Settings.this,"Please Enter Your Fullname", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(Status))
        {
            Toast.makeText(Settings.this,"Please Enter Your Status", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(Number))
        {
            Toast.makeText(Settings.this,"Please Enter Your Phone Number", Toast.LENGTH_SHORT).show();
        }

        else
        {
            updateAccountDetails(username,fullname,Status,Number);
        }

    }

    private void updateAccountDetails(String username, String fullname, String status, String number)
    {
        progressDialog.setMessage("Updating Account Details....");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        HashMap usermap=new HashMap();
        usermap.put("Username", username);
        usermap.put("Phone Number", number);
        usermap.put("Fullname", fullname);
        usermap.put("Status", status);

        settingsUserRef.updateChildren(usermap).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task)
            {
                if(task.isSuccessful())
                {
                    progressDialog.dismiss();
                    sendUserToHome();
                    Toast.makeText(Settings.this,"Profile Updated!!", Toast.LENGTH_SHORT).show();

                }
                else
                {
                    progressDialog.dismiss();
                    Toast.makeText(Settings.this,"Error Occured!!", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    private void sendUserToHome()
    {
        Intent homeIntent=new Intent(Settings.this, HomeActivity.class);
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(homeIntent);
        finish();
    }




}
