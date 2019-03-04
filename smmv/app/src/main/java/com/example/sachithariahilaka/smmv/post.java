package com.example.sachithariahilaka.smmv;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class post extends AppCompatActivity {

    private Toolbar toolbar;
    private ImageButton image;
    private Button post;
    private EditText description;
    private static int gallery_pick=1;
    private Uri imageUri;
    private String Content;
    private StorageReference postReferance;
    private  String saveCurruntDate,saveCurruntTime,postName,downloadUrl;
    private ProgressDialog progressDialog;
    private DatabaseReference usersRef,postRef;
    private FirebaseAuth mAuth;
    String user_id;
    private long  countPost=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        mAuth=FirebaseAuth.getInstance();
        user_id=mAuth.getCurrentUser().getUid();

        toolbar=(Toolbar) findViewById(R.id.Post_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Add New Post");

        usersRef= FirebaseDatabase.getInstance().getReference().child("Users");
        postRef= FirebaseDatabase.getInstance().getReference().child("Posts");

        image=(ImageButton) findViewById(R.id.post_image);
        post=(Button) findViewById(R.id.update);
        description=(EditText)findViewById(R.id.post_content);
        progressDialog=new ProgressDialog(this);

        postReferance= FirebaseStorage.getInstance().getReference();

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                openGallary();

            }
        });

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                validatePost();

            }
        });
    }

    private void validatePost()
    {
         Content=description.getText().toString();

        if(imageUri==null)
        {
            Toast.makeText(this,"Please Select A Image",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(Content))
        {
            Toast.makeText(this,"Please Enter Content Of The Post",Toast.LENGTH_SHORT).show();
        }
        else{
            storeImage();
        }
    }

    private void storeImage()
    {
        progressDialog.setMessage("Uploading Your Post....");
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(false);

        Calendar calDate=Calendar.getInstance();
        SimpleDateFormat Curruntdate=new SimpleDateFormat("dd-MMMM-yyyy");
        saveCurruntDate=Curruntdate.format(calDate.getTime());

        Calendar calTime=Calendar.getInstance();
        SimpleDateFormat Currunttime=new SimpleDateFormat("HH:mm");
        saveCurruntTime=Currunttime.format(calTime.getTime());

        postName=user_id + saveCurruntDate + saveCurruntTime;


        final StorageReference filepath2=postReferance.child("Post Images").child(imageUri.getLastPathSegment()+postName+".jpg");
        final UploadTask uploadTask=filepath2.putFile(imageUri);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //progressDialog.dismiss();
                Toast.makeText(post.this,"Error Occured!!", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                //progressDialog.dismiss();
                Toast.makeText(post.this,"Image Updated Succusfully!!", Toast.LENGTH_SHORT).show();

                Task<Uri> uriTask=uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception
                    {
                        if(!task.isSuccessful())
                        {
                            throw task.getException();
                        }

                        downloadUrl=filepath2.getDownloadUrl().toString();
                        return filepath2.getDownloadUrl();

                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if(task.isSuccessful())
                        {
                            downloadUrl=task.getResult().toString();
                            saveTodatabase();
                        }
                        else
                        {
                            Toast.makeText(post.this,"Error Occured!!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    private void saveTodatabase() {

        postRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists())
                {
                    countPost=dataSnapshot.getChildrenCount();
                }
                else
                {
                    countPost=0;
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        usersRef.child(user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists())
                {
                    String Username=dataSnapshot.child("Username").getValue().toString();
                    String pp=dataSnapshot.child("Profile").getValue().toString();

                    final HashMap post=new HashMap();
                    post.put("UId",user_id);
                    post.put("Date",saveCurruntDate);
                    post.put("Time",saveCurruntTime);
                    post.put("Description",Content);
                    post.put("ProfilePic",pp);
                    post.put("PostImage",downloadUrl);
                    post.put("Username",Username);
                    post.put("CountPost", countPost);
                    postRef.child(postName).updateChildren(post).addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if(task.isSuccessful())
                            {
                                progressDialog.dismiss();
                                Toast.makeText(post.this,"Post Updated Succusfully!!",Toast.LENGTH_SHORT).show();
                                sendUserToHome();
                            }
                            else
                            {
                                progressDialog.dismiss();
                                Toast.makeText(post.this,"Error Occured!!",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




    }


    private void openGallary()
    {
        Intent gallery=new Intent();
        gallery.setAction(Intent.ACTION_GET_CONTENT);
        gallery.setType("image/*");
        startActivityForResult(gallery,gallery_pick);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==gallery_pick && resultCode==RESULT_OK && data!=null)
        {
            imageUri=data.getData();
            image.setImageURI(imageUri);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id=item.getItemId();
        if(id==android.R.id.home)
        {
            sendUserToHome();
        }
        return super.onOptionsItemSelected(item);
    }

    private void sendUserToHome()
    {
        Intent homeIntent=new Intent(post.this,HomeActivity.class);
        startActivity(homeIntent);
    }
}
