package com.example.sachithariahilaka.smmv.viewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sachithariahilaka.smmv.Interface.itemClickListner;
import com.example.sachithariahilaka.smmv.R;

public class commentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView usrname,Date,Time,Comment;
    public ImageView ppimg;
    public itemClickListner listner;

    public commentViewHolder(View itemView) {
        super(itemView);

        ppimg=itemView.findViewById(R.id.post_profile);
        usrname=itemView.findViewById(R.id.profile_name);
        Date=itemView.findViewById(R.id.date);
        Time=itemView.findViewById(R.id.time);
        Comment=itemView.findViewById(R.id.comment_text);
    }

    public void setItemClickListner(itemClickListner listner)
    {
        this.listner=listner;
    }

    @Override
    public void onClick(View view) {
        listner.onClick(view, getAdapterPosition(), false);

    }
}

