package com.example.sachithariahilaka.smmv;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class profile extends AppCompatActivity {

    private TextView Username,Fullname,Status,Phone;
    private CircleImageView ppimage;
    private FirebaseAuth mAuth;
    private DatabaseReference profileRef,friendRef,postRef;
    String UId;
    private StorageReference profilepic;
    private Button post,friend;
    private int friendsCount=0, postCount=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth=FirebaseAuth.getInstance();
        UId=mAuth.getCurrentUser().getUid();
        profileRef= FirebaseDatabase.getInstance().getReference().child("Users").child(UId);
        profilepic= FirebaseStorage.getInstance().getReference("Profile Pictures");
        friendRef=FirebaseDatabase.getInstance().getReference().child("Friends");
        postRef=FirebaseDatabase.getInstance().getReference().child("Posts");

        Username=findViewById(R.id.username);
        Fullname=findViewById(R.id.fullname);
        Status=findViewById(R.id.status);
        Phone=findViewById(R.id.mobile);
        ppimage=findViewById(R.id.pp);
        post=findViewById(R.id.posts);
        friend=findViewById(R.id.friends);

        profileRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists())
                {
                    String Profileimg=dataSnapshot.child("Profile").getValue().toString();
                    String username=dataSnapshot.child("Username").getValue().toString();
                    String fullname=dataSnapshot.child("Fullname").getValue().toString();
                    String phone=dataSnapshot.child("Phone Number").getValue().toString();
                    String status=dataSnapshot.child("Status").getValue().toString();

                    Picasso.get().load(Profileimg).placeholder(R.drawable.profile).into(ppimage);

                    Username.setText(username);
                    Fullname.setText("Name: " + fullname);
                    Phone.setText("Mobile Number:" + phone);
                    Status.setText(status);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                sendUserToMyposts();
            }
        });

        friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                sendUserToFriends();
            }
        });

        postRef.orderByChild("UId").startAt(UId).endAt(UId + "\uf8ff").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists())
                {
                    postCount=(int) dataSnapshot.getChildrenCount();
                    post.setText(Integer.toString(postCount) + " Posts");
                }
                else
                {
                    post.setText("0 Posts");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        friendRef.child(UId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists())
                {
                    friendsCount=(int) dataSnapshot.getChildrenCount();
                    friend.setText(Integer.toString(friendsCount) + " Friends");
                }
                else
                {
                    friend.setText("0 Friends");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void sendUserToMyposts()
    {
        Intent postIntent=new Intent(profile.this, Mypost.class);
        startActivity(postIntent);
    }

    private void sendUserToFriends()
    {
        Intent friendsIntent=new Intent(profile.this, friends.class);
        startActivity(friendsIntent);
    }
}
