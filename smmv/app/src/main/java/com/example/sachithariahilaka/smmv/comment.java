package com.example.sachithariahilaka.smmv;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.sachithariahilaka.smmv.viewHolder.commentViewHolder;
import com.example.sachithariahilaka.smmv.viewHolder.userViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
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

public class comment extends AppCompatActivity {
    private EditText commentBox;
    private ImageButton SendButton;
    private RecyclerView comments;
    private String post_key,currunt_user;
    private DatabaseReference usersRef,postRef;
    String saveCurruntDate,saveCurruntTime,postName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        commentBox=findViewById(R.id.comment_box);
        SendButton=findViewById(R.id.send);

        comments=findViewById(R.id.commentList);
        comments.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        comments.setLayoutManager(linearLayoutManager);
        post_key=getIntent().getExtras().get("PostKey").toString();

        usersRef= FirebaseDatabase.getInstance().getReference().child("Users");
        postRef=FirebaseDatabase.getInstance().getReference().child("Posts").child(post_key).child("Comments");
        currunt_user= FirebaseAuth.getInstance().getCurrentUser().getUid();



        SendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                usersRef.child(currunt_user).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                    {
                        if(dataSnapshot.exists())
                        {
                            String username=dataSnapshot.child("Username").getValue().toString();
                            String profile=dataSnapshot.child("Profile").getValue().toString();

                            validateComment(username,profile);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError)
                    {

                    }
                });

            }
        });
    }

    private void validateComment(String username,String profile)
    {
        String comment=commentBox.getText().toString();

        if(TextUtils.isEmpty(comment))
        {
            Toast.makeText(this,"Please Enter Your Comment!!!",Toast.LENGTH_SHORT).show();
        }

        else
        {
            Calendar calDate=Calendar.getInstance();
            SimpleDateFormat Curruntdate=new SimpleDateFormat("dd-MMMM-yyyy");
            saveCurruntDate=Curruntdate.format(calDate.getTime());

            Calendar calTime=Calendar.getInstance();
            SimpleDateFormat Currunttime=new SimpleDateFormat("HH:mm");
            saveCurruntTime=Currunttime.format(calTime.getTime());


            postName=saveCurruntDate + saveCurruntTime;

            HashMap commentPost=new HashMap();

            commentPost.put("UId", currunt_user);
            commentPost.put("Comment", comment);
            commentPost.put("Date", saveCurruntDate);
            commentPost.put("Time", saveCurruntTime);
            commentPost.put("Username", username);
            commentPost.put("Profile", profile);

            postRef.child(postName).updateChildren(commentPost).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task)
                {
                    if(task.isSuccessful())
                    {
                        Toast.makeText(comment.this,"You Commented!!!",Toast.LENGTH_SHORT).show();
                        commentBox.setText(" ");
                    }
                    else
                    {
                        Toast.makeText(comment.this,"Error Occured!!!",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Comments> options=new FirebaseRecyclerOptions.Builder<Comments>()
                .setQuery(postRef, Comments.class)
                .build();

        FirebaseRecyclerAdapter<Comments, commentViewHolder> adapter=new FirebaseRecyclerAdapter<Comments, commentViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull commentViewHolder holder, int position, @NonNull Comments model)
            {
                holder.usrname.setText(model.getUsername());
                holder.Date.setText(model.getDate());
                holder.Time.setText(model.getTime());
                holder.Comment.setText(model.getComment());
                Picasso.get().load(model.getProfile()).placeholder(R.drawable.profile).into(holder.ppimg);

            }

            @NonNull
            @Override
            public commentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
            {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.all_comments, parent, false);

                commentViewHolder holder=new commentViewHolder(view);
                return holder;
            }
        };

        comments.setAdapter(adapter);
        adapter.startListening();
    }
}
