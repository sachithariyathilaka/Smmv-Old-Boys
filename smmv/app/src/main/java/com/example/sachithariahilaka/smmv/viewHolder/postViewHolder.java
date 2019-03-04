package com.example.sachithariahilaka.smmv.viewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sachithariahilaka.smmv.Interface.itemClickListner;
import com.example.sachithariahilaka.smmv.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class postViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
   {
       public TextView post_date,post_time,post_description,post_user;
       public ImageView post_img,pp;
       public itemClickListner itemClickListner;
       public ImageButton like,comment;
       public TextView count;
       int likeCounter;
       String userId;
       DatabaseReference likesRef;


       public postViewHolder(View itemView) {
           super(itemView);

           post_img=(ImageView) itemView.findViewById(R.id.post_image);
           post_date=(TextView) itemView.findViewById(R.id.date);
           post_time=(TextView) itemView.findViewById(R.id.time);
           post_user=(TextView) itemView.findViewById(R.id.profile_name);
           post_description=(TextView) itemView.findViewById(R.id.post_description);
           pp=(ImageView) itemView.findViewById(R.id.post_profile);
           like=itemView.findViewById(R.id.like);
           comment=itemView.findViewById(R.id.comment);
           count=itemView.findViewById(R.id.count);
           likesRef= FirebaseDatabase.getInstance().getReference().child("Likes");
           userId= FirebaseAuth.getInstance().getCurrentUser().getUid();
       }

       public void setItemClickListner(itemClickListner listner)
       {
           this.itemClickListner=listner;
       }

       @Override
       public void onClick(View view) {

           itemClickListner.onClick(view, getAdapterPosition(),false);
       }

       public void setLikeButtonStatus(final String postKey)
       {
           likesRef.addValueEventListener(new ValueEventListener() {
               @Override
               public void onDataChange(@NonNull DataSnapshot dataSnapshot)
               {
                   if(dataSnapshot.child(postKey).hasChild(userId))
                   {
                       likeCounter=(int) dataSnapshot.child(postKey).getChildrenCount();
                       like.setImageResource(R.drawable.like);
                       count.setText(Integer.toString(likeCounter));
                   }
                   else
                   {
                       likeCounter=(int) dataSnapshot.child(postKey).getChildrenCount();
                       like.setImageResource(R.drawable.dislike);
                       count.setText(Integer.toString(likeCounter));
                   }

               }

               @Override
               public void onCancelled(@NonNull DatabaseError databaseError) {

               }
           });

       }
   }
