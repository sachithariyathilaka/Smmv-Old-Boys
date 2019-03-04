package com.example.sachithariahilaka.smmv;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class personalProfile extends AppCompatActivity {
    private TextView Username,Fullname,Status,Phone;
    private CircleImageView ppimage;
    private Button add,cancel;
    private DatabaseReference friendrequestRef,userRef, friendRef;
    private FirebaseAuth mAuth;
    String sender_userId,receiver_userId,Currunt_state,saveCurruntDate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_profile);

        Username=findViewById(R.id.username);
        Fullname=findViewById(R.id.fullname);
        Status=findViewById(R.id.status);
        Phone=findViewById(R.id.mobile);
        ppimage=findViewById(R.id.pp);
        add=findViewById(R.id.send);
        cancel=findViewById(R.id.cancel);
        Currunt_state="Not Friends";

        mAuth=FirebaseAuth.getInstance();
        sender_userId=mAuth.getCurrentUser().getUid();
        receiver_userId=getIntent().getExtras().get("visit_userId").toString();
        userRef= FirebaseDatabase.getInstance().getReference().child("Users");
        friendrequestRef=FirebaseDatabase.getInstance().getReference().child("FriendRequest");
        friendRef=FirebaseDatabase.getInstance().getReference().child("Friends");

        userRef.child(receiver_userId).addValueEventListener(new ValueEventListener() {
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

                    maintainButtons();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        cancel.setVisibility(View.INVISIBLE);
        cancel.setEnabled(false);

        if(!sender_userId.equals(receiver_userId))
        {
            add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view)
                {
                    add.setEnabled(false);
                    if(Currunt_state.equals("Not Friends"))
                    {
                        sendFriendRequest();
                    }
                    if(Currunt_state.equals("Request_Sent"))
                    {
                        Cancelequest();
                    }
                    if(Currunt_state.equals("Request_Received"))
                    {
                        AcceptFriendRequst();
                    }
                    if(Currunt_state.equals("Friends"))
                    {
                        unfriend();
                    }

                }
            });

        }
        else {
            cancel.setVisibility(View.INVISIBLE);
            add.setVisibility(View.INVISIBLE);
        }




    }

    private void unfriend()
    {
        friendRef.child(sender_userId).child(receiver_userId)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if(task.isSuccessful())
                        {
                            friendRef.child(receiver_userId).child(sender_userId)
                                    .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task)
                                {
                                    if(task.isSuccessful())
                                    {
                                        add.setEnabled(true);
                                        Currunt_state="Not Friends";
                                        add.setText("Send Friend Request");

                                        cancel.setVisibility(View.INVISIBLE);
                                        cancel.setEnabled(false);
                                    }

                                }
                            });
                        }

                    }
                });
    }

    private void Cancelequest()
    {
        friendrequestRef.child(sender_userId).child(receiver_userId)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if(task.isSuccessful())
                        {
                            friendrequestRef.child(receiver_userId).child(sender_userId)
                                    .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task)
                                {
                                    if(task.isSuccessful())
                                    {
                                        add.setEnabled(true);
                                        Currunt_state="Not Friends";
                                        add.setText("Send Friend Request");

                                        cancel.setVisibility(View.INVISIBLE);
                                        cancel.setEnabled(false);
                                    }

                                }
                            });
                        }

                    }
                });
    }

    private void maintainButtons()
    {
        friendrequestRef.child(sender_userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild(receiver_userId))
                        {
                            String requestType=dataSnapshot.child(receiver_userId).child("Request_Type").getValue().toString();

                            if(requestType.equals("Sent"))
                            {
                                Currunt_state="Request_Sent";
                                add.setText("Cancel Friend Request");

                                cancel.setVisibility(View.INVISIBLE);
                                cancel.setEnabled(false);
                            }
                            else if(requestType.equals("Received"))
                            {
                                Currunt_state= "Request_Received";
                                add.setText("Accept Friend Request");

                                cancel.setVisibility(View.VISIBLE);
                                cancel.setEnabled(true);

                                cancel.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view)
                                    {
                                        Cancelequest();

                                    }
                                });
                            }

                        }
                        else
                        {
                            friendRef.child(sender_userId).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                                {
                                    if(dataSnapshot.hasChild(receiver_userId))
                                    {
                                        Currunt_state= "Friends";
                                        add.setText("Unfriend");

                                        cancel.setVisibility(View.INVISIBLE);
                                        cancel.setEnabled(false);
                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void AcceptFriendRequst()
    {
        Calendar calDate=Calendar.getInstance();
        SimpleDateFormat Curruntdate=new SimpleDateFormat("dd-MMMM-yyyy");
        saveCurruntDate=Curruntdate.format(calDate.getTime());

        friendRef.child(sender_userId).child(receiver_userId).child("Date").setValue(saveCurruntDate).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                if(task.isSuccessful())
                {
                    friendRef.child(receiver_userId).child(sender_userId).child("Date").setValue(saveCurruntDate).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {
                            if(task.isSuccessful())
                            {
                                friendrequestRef.child(sender_userId).child(receiver_userId)
                                        .removeValue()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task)
                                            {
                                                if(task.isSuccessful())
                                                {
                                                    friendrequestRef.child(receiver_userId).child(sender_userId)
                                                            .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task)
                                                        {
                                                            if(task.isSuccessful())
                                                            {
                                                                add.setEnabled(true);
                                                                Currunt_state="Friends";
                                                                add.setText("Unfriend");

                                                                cancel.setVisibility(View.INVISIBLE);
                                                                cancel.setEnabled(false);

                                                            }

                                                        }
                                                    });
                                                }

                                            }
                                        });
                            }

                        }
                    });
                }

            }
        });
    }

    private void sendFriendRequest()
    {
        friendrequestRef.child(sender_userId).child(receiver_userId)
                        .child("Request_Type").setValue("Sent")
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task)
                            {
                                if(task.isSuccessful())
                                {
                                    friendrequestRef.child(receiver_userId).child(sender_userId)
                                            .child("Request_Type").setValue("Received").addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            if(task.isSuccessful())
                                            {
                                                add.setEnabled(true);
                                                Currunt_state="Request_Sent";
                                                add.setText("Cancel Friend Request");

                                                cancel.setVisibility(View.INVISIBLE);
                                                cancel.setEnabled(false);
                                            }

                                        }
                                    });
                                }

                            }
                        });
    }
}
