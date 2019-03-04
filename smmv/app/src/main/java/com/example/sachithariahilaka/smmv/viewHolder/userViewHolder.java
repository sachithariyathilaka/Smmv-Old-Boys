package com.example.sachithariahilaka.smmv.viewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sachithariahilaka.smmv.Interface.itemClickListner;
import com.example.sachithariahilaka.smmv.R;

public class userViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView usrname,Status;
    public ImageView ppimg;
    public itemClickListner listner;
    public ImageView onlineIcon;

    public userViewHolder(View itemView) {
        super(itemView);

        ppimg=itemView.findViewById(R.id.ppimage);
        usrname=itemView.findViewById(R.id.name);
        Status=itemView.findViewById(R.id.status);
        onlineIcon=itemView.findViewById(R.id.onlineIcon);

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
