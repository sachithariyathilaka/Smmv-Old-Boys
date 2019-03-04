package com.example.sachithariahilaka.smmv;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class chat extends AppCompatActivity {

    private Toolbar toolbar;
    private ImageButton select, send;
    private EditText message;
    private RecyclerView messagesList;
    String receiverId, Profile, receiverUsername, senderid,saveCurruntDate,saveCurruntTime,messageId;
    private DatabaseReference receiverRef,sendkey,receivekey,UsersRef,database;
    private TextView Username,lastseen;
    private CircleImageView ppimage;
    private FirebaseAuth mAuth;
    private final List<messages> messageslist=new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private messageAdapter messageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        toolbar = findViewById(R.id.chat);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View actionbar_view = layoutInflater.inflate(R.layout.chat_toolbar, null);
        actionBar.setCustomView(actionbar_view);

        select = findViewById(R.id.select_image);
        send = findViewById(R.id.send);
        message = findViewById(R.id.new_message);
        messagesList = findViewById(R.id.message_list);
        Username = findViewById(R.id.username);
        ppimage = findViewById(R.id.profile_pic);
        mAuth = FirebaseAuth.getInstance();
        senderid = mAuth.getCurrentUser().getUid();
        receiverId = getIntent().getExtras().get("visit_userId").toString();
        receiverRef = FirebaseDatabase.getInstance().getReference();
        lastseen=findViewById(R.id.last_seen);
        UsersRef=FirebaseDatabase.getInstance().getReference().child("Users");
        database=FirebaseDatabase.getInstance().getReference();

        receiverRef.child("Users").child(receiverId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {


                    String type=dataSnapshot.child("State").getValue().toString();
                    String date=dataSnapshot.child("LastDate").getValue().toString();
                    String time=dataSnapshot.child("LastTime").getValue().toString();
                    receiverUsername = dataSnapshot.child("Username").getValue().toString();
                    Profile = dataSnapshot.child("Profile").getValue().toString();

                    Picasso.get().load(Profile).placeholder(R.drawable.profile).into(ppimage);
                    Username.setText(receiverUsername);

                    if(type.equals("Online"))
                    {
                        lastseen.setText("online");
                    }
                    else if(type.equals("Offline"))
                    {
                        lastseen.setText("last seen: " + date + " " + time);
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sendMessage();
            }
        });

        messageAdapter=new messageAdapter(messageslist);
        linearLayoutManager=new LinearLayoutManager(this);
        messagesList.setLayoutManager(linearLayoutManager);
        messagesList.setAdapter(messageAdapter);

        DisplayMessages();



    }

    private void DisplayMessages()
    {
        receiverRef.child("Messages").child(senderid).child(receiverId).
                addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
                    {
                        if(dataSnapshot.exists())
                        {
                            messages messages=dataSnapshot.getValue(messages.class);
                            messageslist.add(messages);
                            messageAdapter.notifyDataSetChanged();
                        }

                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void curruntstate(String state)
    {
        String saveCurruntDate,saveCurruntTime;

        Calendar calDate=Calendar.getInstance();
        SimpleDateFormat Curruntdate=new SimpleDateFormat("dd-MMMM-yyyy");
        saveCurruntDate=Curruntdate.format(calDate.getTime());

        Calendar calTime=Calendar.getInstance();
        SimpleDateFormat Currunttime=new SimpleDateFormat("HH:mm");
        saveCurruntTime=Currunttime.format(calTime.getTime());

        final HashMap post=new HashMap();
        post.put("State",state);
        post.put("LastDate",saveCurruntDate);
        post.put("LastTime",saveCurruntTime);

        UsersRef.child(senderid)
                .updateChildren(post);
    }

    private void sendMessage() {

        curruntstate("Online");

        String newMessage = message.getText().toString();

        if (TextUtils.isEmpty(newMessage)) {
            Toast.makeText(this, "Please Enter Your Message", Toast.LENGTH_SHORT).show();
        }

        else
            {
                Calendar calDate=Calendar.getInstance();
                SimpleDateFormat Curruntdate=new SimpleDateFormat("dd-MMMM-yyyy");
                saveCurruntDate=Curruntdate.format(calDate.getTime());

                Calendar calTime=Calendar.getInstance();
                SimpleDateFormat Currunttime=new SimpleDateFormat("HH:mm");
                saveCurruntTime=Currunttime.format(calTime.getTime());

                sendkey=receiverRef.child("Messages").child(senderid).child(receiverId).push();
                messageId=sendkey.getKey();

                HashMap sendermap=new HashMap();
                sendermap.put("Date", saveCurruntDate);
                sendermap.put("Time", saveCurruntTime);
                sendermap.put("Message", newMessage);
                sendermap.put("From", senderid);
                sendermap.put("To", receiverId);

                receiverRef.child("Messages").child(senderid).child(receiverId).child(messageId).updateChildren(sendermap).addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task)
                    {
                        if(task.isSuccessful())
                        {
                            Toast.makeText(chat.this,"Message Sent Succusfully!!", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Toast.makeText(chat.this,"Error Occured!!", Toast.LENGTH_SHORT).show();
                        }

                    }
                });


                HashMap receivermap=new HashMap();
                receivermap.put("Date", saveCurruntDate);
                receivermap.put("Time", saveCurruntTime);
                receivermap.put("Message", newMessage);
                receivermap.put("From", senderid);
                receivermap.put("To", receiverId);

                receiverRef.child("Messages").child(receiverId).child(senderid).child(messageId).updateChildren(sendermap).addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful())
                        {
                            Toast.makeText(chat.this,"Message Sent Succusfully!!", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Toast.makeText(chat.this,"Error Occured!!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                message.setText("");
            }

    }


}
