package com.example.sachithariahilaka.smmv;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ClickPost extends AppCompatActivity {

    private ImageView postImage;
    private TextView content;
    private Button edit,delete;
    private String postKey;
    private FirebaseAuth mAuth;
    String userId,DBuser,image,description;
    private DatabaseReference mdatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_click_post);

        mAuth=FirebaseAuth.getInstance();
        userId=mAuth.getCurrentUser().getUid();

        postKey=getIntent().getExtras().get("PostKey").toString();
        mdatabase=FirebaseDatabase.getInstance().getReference().child("Posts").child(postKey);

        postImage=findViewById(R.id.postImage);
        content=findViewById(R.id.post_description);
        edit=findViewById(R.id.Edit);
        delete=findViewById(R.id.Delete);

        delete.setVisibility(View.INVISIBLE);
        edit.setVisibility(View.INVISIBLE);

        mdatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists())
                {
                    description=dataSnapshot.child("Description").getValue().toString();
                    image=dataSnapshot.child("PostImage").getValue().toString();
                    DBuser=dataSnapshot.child("UId").getValue().toString();

                    content.setText(description);
                    Picasso.get().load(image).placeholder(R.drawable.select_image).into(postImage);

                    if(userId.equals(DBuser))
                    {
                        delete.setVisibility(View.VISIBLE);
                        edit.setVisibility(View.VISIBLE);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)

            {
                deleteCurruntPost();

            }
        });

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                editCurruntPost(description);

            }
        });
    }

    private void editCurruntPost(String description)
    {

        AlertDialog.Builder builder=new AlertDialog.Builder(ClickPost.this);
        builder.setTitle("Edit Post:");

        final EditText editText=new EditText(ClickPost.this);
        editText.setText(description);
        builder.setView(editText);

        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                mdatabase.child("Description").setValue(editText.getText().toString());
                Toast.makeText(ClickPost.this,"Post Edited Succusfully!!",Toast.LENGTH_SHORT).show();

            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                dialogInterface.cancel();

            }
        });

        Dialog dialog=builder.create();
        dialog.show();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.holo_green_dark);

    }

    private void deleteCurruntPost()
    {
        mdatabase.removeValue();
        sendUserToMain();
        Toast.makeText(this,"Post Has Been Deleted!!", Toast.LENGTH_SHORT).show();
    }

    private void sendUserToMain()
    {
        Intent homeIntent=new Intent(ClickPost.this, HomeActivity.class);
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(homeIntent);
        finish();
    }
}
