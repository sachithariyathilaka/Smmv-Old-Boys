package com.example.sachithariahilaka.smmv;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
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

import java.net.URI;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class setup extends AppCompatActivity {

    private Button picture;
    private EditText fullname,username,number,status;
    private CircleImageView circleImageView;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference mDatabase;
    String userid,image,downloadUrl;
    Uri imageUri;
    private ProgressDialog progressDialog;
    final static int gallery_pick=1;
    private StorageReference profilepic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        progressDialog=new ProgressDialog(this);

        firebaseAuth=FirebaseAuth.getInstance();
        userid=firebaseAuth.getCurrentUser().getUid();
        mDatabase= FirebaseDatabase.getInstance().getReference("Users").child(userid);
        profilepic=FirebaseStorage.getInstance().getReference("Profile Pictures");

        circleImageView=(CircleImageView) findViewById(R.id.profile);
        picture=(Button) findViewById(R.id.save);
        fullname=(EditText) findViewById(R.id.name);
        username=(EditText) findViewById(R.id.username);
        number=(EditText) findViewById(R.id.phone);
        status=findViewById(R.id.status);

        picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                saveAccountInfo();

            }
        });

        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent gallery=new Intent();
                gallery.setAction(Intent.ACTION_GET_CONTENT);
                gallery.setType("image/*");
                startActivityForResult(gallery,gallery_pick);

            }
        });

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists())
                {
                    String image=dataSnapshot.child("Profile").getValue().toString();
                    Picasso.get().load(image).placeholder(R.drawable.profile).into(circleImageView);



                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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

                final StorageReference filepath=profilepic.child(userid + ".jpg");
                final UploadTask uploadTask=filepath.putFile(imageUri);

                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(setup.this,"Error Occured!!", Toast.LENGTH_SHORT).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        progressDialog.dismiss();
                        Toast.makeText(setup.this,"Profile Image Updated Succusfully!!", Toast.LENGTH_SHORT).show();

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
                                    Toast.makeText(setup.this,"Error Occured!!", Toast.LENGTH_SHORT).show();
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
        mDatabase.updateChildren(pp);
    }

    private void saveAccountInfo()
    {
        String name=fullname.getText().toString();
        String phone=number.getText().toString();
        String user=username.getText().toString();
        String Status=status.getText().toString();

        if(TextUtils.isEmpty(name))
        {
            Toast.makeText(this,"Please Enter Your Full Name", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(phone))
        {
            Toast.makeText(this,"Please Enter Your Phone Number", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(user))
        {
            Toast.makeText(this,"Please Enter Your User Name", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(Status))
        {
            Toast.makeText(this,"Please Enter Your Status", Toast.LENGTH_SHORT).show();
        }
        else
        {
            progressDialog.setMessage("Updating Profile...");
            progressDialog.show();
            progressDialog.setCanceledOnTouchOutside(false);
            HashMap usermap=new HashMap();
            usermap.put("Username", user);
            usermap.put("Phone Number", phone);
            usermap.put("Fullname", name);
            usermap.put("Status", Status);

            mDatabase.updateChildren(usermap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task)
                {
                    if(task.isSuccessful())
                    {
                        progressDialog.dismiss();
                        Toast.makeText(setup.this,"Profile Updated!!", Toast.LENGTH_SHORT).show();
                        sendUserToLogin();

                    }
                    else
                    {
                        progressDialog.dismiss();
                        Toast.makeText(setup.this,"Error!! Please Try Again!!", Toast.LENGTH_SHORT).show();
                    }


                }

            });
        }
    }

    private void sendUserToLogin()
    {
        Intent loginIntent=new Intent(setup.this, Login.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }


}
