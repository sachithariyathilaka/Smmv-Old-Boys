package com.example.sachithariahilaka.smmv;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class messageAdapter extends RecyclerView.Adapter<messageAdapter.messageViewHolder>
{
    private List<messages> messagesList;
    private FirebaseAuth mAuth;


    public messageAdapter(List<messages> messagesList)

    {
        this.messagesList=messagesList;
    }

    public class messageViewHolder extends RecyclerView.ViewHolder
    {
        public TextView senderMessage,receiverMessage,senderDate,senderTime,receiverDate,receiverTime;
        public messageViewHolder(View itemView)
        {
            super(itemView);

            senderMessage=itemView.findViewById(R.id.sender_message);
            receiverMessage=itemView.findViewById(R.id.receive_message);

        }


    }

    @NonNull
    @Override
    public messageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.all_messages,parent,false);

        mAuth=FirebaseAuth.getInstance();

        return new messageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull messageViewHolder holder, int position)
    {
        String semderUid=mAuth.getCurrentUser().getUid();
        messages messages=messagesList.get(position);

        String fromUid=messages.getFrom();

        holder.receiverMessage.setVisibility(View.INVISIBLE);

        if(fromUid.equals(semderUid))
        {
            holder.senderMessage.setBackgroundResource(R.drawable.sender);
            holder.senderMessage.setText(messages.getMessage());
            holder.senderMessage.setGravity(Gravity.LEFT);

        }
        else
        {
            holder.senderMessage.setVisibility(View.INVISIBLE);
            holder.receiverMessage.setVisibility(View.VISIBLE);

            holder.receiverMessage.setBackgroundResource(R.drawable.receiver);
            holder.receiverMessage.setText(messages.getMessage());
            holder.receiverMessage.setGravity(Gravity.RIGHT);



        }

    }

    @Override
    public int getItemCount()
    {
        return messagesList.size();
    }
}
