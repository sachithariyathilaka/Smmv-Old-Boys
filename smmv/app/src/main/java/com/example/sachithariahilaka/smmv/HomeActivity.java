package com.example.sachithariahilaka.smmv;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.example.sachithariahilaka.smmv.viewHolder.postViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeActivity extends AppCompatActivity {

    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private RecyclerView recyclerView;
    private Toolbar toolbar;
    private CircleImageView profile_pic;
    private TextView profile_uname;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference mDatabase,postref,likeRef,database;
    String user_id,postKey;
    private ImageButton post;
    Boolean likeChecker=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        firebaseAuth=FirebaseAuth.getInstance();
        user_id=firebaseAuth.getCurrentUser().getUid();
        mDatabase= FirebaseDatabase.getInstance().getReference().child("Users");
        postref= FirebaseDatabase.getInstance().getReference().child("Posts");
        likeRef=FirebaseDatabase.getInstance().getReference().child("Likes");
        database=FirebaseDatabase.getInstance().getReference();

        toolbar=(Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Home");

        post=(ImageButton) findViewById(R.id.post_btn);

        drawerLayout=(DrawerLayout) findViewById(R.id.drawer);
        actionBarDrawerToggle=new ActionBarDrawerToggle(HomeActivity.this, drawerLayout,R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navigationView=(NavigationView) findViewById(R.id.navigation);

        recyclerView=(RecyclerView) findViewById(R.id.recycle);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        View navView=navigationView.inflateHeaderView(R.layout.header);
        profile_pic=(CircleImageView) navView.findViewById(R.id.profile);
        profile_uname=(TextView) navView.findViewById(R.id.username);






        mDatabase.child(user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
               if(dataSnapshot.exists())
                {
                    String image=dataSnapshot.child("Profile").getValue().toString();
                    String Uname=dataSnapshot.child("Username").getValue().toString();
                    Picasso.get().load(image).placeholder(R.drawable.profile).into(profile_pic);
                    profile_uname.setText(Uname);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
       });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                userMenuSelector(item);
                return false;
            }
        });

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                sendusertoPost();

            }
        });
        displayAllUserPost();
    }

    private void displayAllUserPost()
    {


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

        mDatabase.child(user_id)
                .updateChildren(post);
    }

    private void sendusertoPost()
    {
        Intent postIntent=new Intent(HomeActivity.this, post.class);
        startActivity(postIntent);
    }

    @Override
    protected void onStart() {
        super.onStart();

        final FirebaseUser currunt_user=firebaseAuth.getCurrentUser();
        if(currunt_user==null)
        {
            sendUserToLoginActivity();
        }
        else
        {
            checkUserExistance();
        }

        Query sortposts=postref.orderByChild("CountPosts");

        FirebaseRecyclerOptions<posts> options=new FirebaseRecyclerOptions.Builder<posts>()
                .setQuery(sortposts,  posts.class)
                .build();

        FirebaseRecyclerAdapter<posts,postViewHolder> adapter=new FirebaseRecyclerAdapter<posts, postViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull postViewHolder holder, int position, @NonNull posts model) {

                postKey=getRef(position).getKey();

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
                        Intent ClickPost=new Intent(HomeActivity.this, com.example.sachithariahilaka.smmv.ClickPost.class);
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
                        Intent commentIntent=new Intent(HomeActivity.this, comment.class);
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

        recyclerView.setAdapter(adapter);
        adapter.startListening();

        curruntstate("Online");
    }


    private void checkUserExistance()
    {
        final String currunt_uid=firebaseAuth.getCurrentUser().getUid();

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(!dataSnapshot.hasChild(currunt_uid))
                {
                    sendUserToSetup();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        });
    }

    private void sendUserToSetup()
    {
        Intent setupIntent=new Intent(HomeActivity.this, setup.class);
        setupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(setupIntent);
        finish();
    }


    private void sendUserToLoginActivity() {

        Intent loginIntent=new Intent(HomeActivity.this, Login.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(actionBarDrawerToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void userMenuSelector(MenuItem item) {

        switch (item.getItemId()){
            case R.id.home:
                Toast.makeText(this,"Home",Toast.LENGTH_SHORT).show();
                break;

            case R.id.post:
                Toast.makeText(this,"Add New Post",Toast.LENGTH_SHORT).show();
                sendusertoPost();
                break;

            case R.id.profile:
                Toast.makeText(this,"Profile",Toast.LENGTH_SHORT).show();
                sendUserToProfile();
                break;

            case R.id.message:
                Toast.makeText(this,"Messages",Toast.LENGTH_SHORT).show();
                senduserTomessages();
                break;

            case R.id.friends:
                Toast.makeText(this,"Friends",Toast.LENGTH_SHORT).show();
                sendUserToFriends();
                break;

            case R.id.find:
                Toast.makeText(this,"Find Friends",Toast.LENGTH_SHORT).show();
                sendUserToFindFriends();
                break;


            case R.id.settings:
                Toast.makeText(this,"Settings",Toast.LENGTH_SHORT).show();
                sendUserToSettings();
                break;

            case R.id.logout:
                Toast.makeText(this,"Logging Out...",Toast.LENGTH_SHORT).show();
                curruntstate("Offline");
                firebaseAuth.signOut();
                finish();
                sendUserToLoginActivity();
                break;
        }
    }


    private void senduserTomessages()
    {
        Intent messageIntent=new Intent(HomeActivity.this, messagesActivity.class);
        startActivity(messageIntent);
    }


    private void sendUserToFriends()
    {
        Intent friendsIntent=new Intent(HomeActivity.this, friends.class);
        startActivity(friendsIntent);
    }

    private void sendUserToFindFriends()
    {
        Intent findIntent=new Intent(HomeActivity.this, find_Friends.class);
        startActivity(findIntent);

    }

    private void sendUserToProfile() {
        Intent profileIntent=new Intent(HomeActivity.this, profile.class);
        startActivity(profileIntent);
    }

    private void sendUserToSettings()
    {
        Intent settingsIntent=new Intent(HomeActivity.this, Settings.class);
        startActivity(settingsIntent);
    }
}
