package com.example.sachithariahilaka.smmv;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.sachithariahilaka.smmv.viewHolder.userViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class messagesActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView friends;
    private DatabaseReference friendRef,UserRef;
    private FirebaseAuth mAuth;
    String currunt_uid,receive_Uid,visit_userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        toolbar=findViewById(R.id.find_friend);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Messages");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        friends=findViewById(R.id.friends);
        friends.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        friends.setLayoutManager(linearLayoutManager);

        mAuth=FirebaseAuth.getInstance();
        currunt_uid=mAuth.getCurrentUser().getUid();
        friendRef= FirebaseDatabase.getInstance().getReference().child("Friends").child(currunt_uid);
        UserRef=FirebaseDatabase.getInstance().getReference().child("Users");

        displayAllFriends();
    }

    private void displayAllFriends()
    {
        FirebaseRecyclerOptions<users> options=new FirebaseRecyclerOptions.Builder<users>()
                .setQuery(friendRef, users.class)
                .build();

        FirebaseRecyclerAdapter<users, userViewHolder> adapter=new FirebaseRecyclerAdapter<users, userViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final userViewHolder holder, final int position, @NonNull final users model)
            {

                visit_userId=getRef(position).getKey();


                UserRef.child(visit_userId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                    {
                        if(dataSnapshot.exists())
                        {
                            String Username=dataSnapshot.child("Username").getValue().toString();
                            String Status=dataSnapshot.child("Status").getValue().toString();
                            String profile=dataSnapshot.child("Profile").getValue().toString();
                            String type=dataSnapshot.child("State").getValue().toString();

                            holder.usrname.setText(Username);
                            holder.Status.setText(Status);
                            Picasso.get().load(profile).into(holder.ppimg);

                            if(type.equals("Online"))
                            {
                                holder.onlineIcon.setVisibility(View.VISIBLE);
                            }

                            else
                            {
                                holder.onlineIcon.setVisibility(View.INVISIBLE);
                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view)
                    {

                        Intent chatntent=new Intent(messagesActivity.this, chat.class);
                        chatntent.putExtra("visit_userId", visit_userId);
                        startActivity(chatntent);

                    }
                });

            }

            @NonNull
            @Override
            public userViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
            {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.all_users, parent, false);

                userViewHolder holder=new userViewHolder(view);

                return holder;
            }
        };

        friends.setAdapter(adapter);
        adapter.startListening();
    }
}
