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

import com.example.sachithariahilaka.smmv.viewHolder.postViewHolder;
import com.example.sachithariahilaka.smmv.viewHolder.userViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class Mypost extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView myposts;
    private DatabaseReference postRef,likeRef;
    private FirebaseAuth mAuth;
    String user_id;
    Boolean likeChecker=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypost);

        toolbar=findViewById(R.id.Myposts);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("My Posts");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        myposts=findViewById(R.id.myposts);
        myposts.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        myposts.setLayoutManager(linearLayoutManager);

        mAuth=FirebaseAuth.getInstance();
        user_id=mAuth.getCurrentUser().getUid();
        postRef= FirebaseDatabase.getInstance().getReference().child("Posts");
        likeRef=FirebaseDatabase.getInstance().getReference().child("Likes");

        DisplayMyallPosts();
    }

    private void DisplayMyallPosts()
    {
        Query myQuery=postRef.orderByChild("UId").startAt(user_id).endAt(user_id + "\uf8ff");

        FirebaseRecyclerOptions<posts> options=new FirebaseRecyclerOptions.Builder<posts>()
                .setQuery(myQuery,  posts.class)
                .build();

        FirebaseRecyclerAdapter<posts,postViewHolder> adapter=new FirebaseRecyclerAdapter<posts, postViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull postViewHolder holder, int position, @NonNull posts model) {

                final String postKey=getRef(position).getKey();

                holder.post_user.setText(model.getUsername());
                holder.post_date.setText(model.getDate());
                holder.post_time.setText(model.getTime());
                holder.post_description.setText(model.getDescription());
                Picasso.get().load(model.getPostImage()).into(holder.post_img);
                Picasso.get().load(model.getProfilePic()).into(holder.pp);

                holder.setLikeButtonStatus(postKey);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent ClickPost=new Intent(Mypost.this, ClickPost.class);
                        ClickPost.putExtra("PostKey",postKey);
                        startActivity(ClickPost);
                    }
                });

                holder.like.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view)
                    {
                        likeChecker=true;
                        likeRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                if(likeChecker.equals(true))
                                {
                                    if(dataSnapshot.child(postKey).hasChild(user_id))
                                    {
                                        likeRef.child(postKey).child(user_id).removeValue();
                                        likeChecker=false;

                                    }
                                    else
                                    {
                                        likeRef.child(postKey).child(user_id).setValue(true);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    }
                });

                holder.comment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view)
                    {
                        Intent commentIntent=new Intent(Mypost.this, comment.class);
                        commentIntent.putExtra("PostKey",postKey);
                        startActivity(commentIntent);

                    }
                });

            }

            @NonNull
            @Override
            public postViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.all_post,parent,false);
                postViewHolder holder=new postViewHolder(view);
                return holder;
            }
        };

        myposts.setAdapter(adapter);
        adapter.startListening();
    }
}
