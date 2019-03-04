package com.example.sachithariahilaka.smmv;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.example.sachithariahilaka.smmv.viewHolder.userViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;


public class find_Friends extends AppCompatActivity {

    private Toolbar toolbar;
    private ImageButton search;
    private EditText searchBox;
    private RecyclerView recycleView;
    private DatabaseReference usersRef;
    RecyclerView.LayoutManager layoutManager;
    Query searchRef;
    String visit_userId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find__friends);

        toolbar=findViewById(R.id.find_friend);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Find Friends");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        usersRef= FirebaseDatabase.getInstance().getReference().child("Users");
        searchRef=usersRef.orderByChild("Username");

        search=findViewById(R.id.search);
        searchBox=findViewById(R.id.searchBox);
        recycleView=findViewById(R.id.recycle);
        recycleView.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        recycleView.setLayoutManager(layoutManager);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                String searchinput=searchBox.getText().toString();
                searchFriends(searchinput);
            }
        });
    }

    private void searchFriends(String searchinput)
    {
        Toast.makeText(this,"Searching...", Toast.LENGTH_SHORT).show();
        Query searchPeople=searchRef
                .startAt(searchinput).endAt(searchinput + "\uf8ff");

        FirebaseRecyclerOptions<users> options=new FirebaseRecyclerOptions.Builder<users>()
                .setQuery(searchPeople, users.class)
                .build();

        FirebaseRecyclerAdapter<users, userViewHolder> adapter=new FirebaseRecyclerAdapter<users, userViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final userViewHolder holder, final int position, @NonNull users model)
            {
                visit_userId=getRef(position).getKey();

                usersRef.child(visit_userId).addValueEventListener(new ValueEventListener() {
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
                    public void onClick(View view) {


                        Intent profileIntent=new Intent(find_Friends.this,personalProfile.class);
                        profileIntent.putExtra("visit_userId", visit_userId);
                        startActivity(profileIntent);
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

        recycleView.setAdapter(adapter);
        adapter.startListening();

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<users> options=new FirebaseRecyclerOptions.Builder<users>()
                .setQuery(usersRef, users.class)
                .build();

        FirebaseRecyclerAdapter<users, userViewHolder> adapter=new FirebaseRecyclerAdapter<users, userViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final userViewHolder holder, final int position, @NonNull users model)
            {


                holder.usrname.setText(model.getUsername());
                holder.Status.setText(model.getStatus());
                Picasso.get().load(model.getProfile()).into(holder.ppimg);

                if(model.getState().equals("Online"))
                {
                    holder.onlineIcon.setVisibility(View.VISIBLE);
                }
                else
                {
                    holder.onlineIcon.setVisibility(View.INVISIBLE);
                }
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        String visit_userId3=getRef(position).getKey();

                        Intent profileIntent=new Intent(find_Friends.this,personalProfile.class);
                        profileIntent.putExtra("visit_userId", visit_userId3);
                        startActivity(profileIntent);
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

        recycleView.setAdapter(adapter);
        adapter.startListening();


    }
}
