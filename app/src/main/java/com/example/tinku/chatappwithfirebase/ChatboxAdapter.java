package com.example.tinku.chatappwithfirebase;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.zip.Inflater;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by TINKU on 2/20/2018.
 */

public class ChatboxAdapter extends RecyclerView.Adapter<ChatboxAdapter.ViewHolder> {

    Context context;
    List<Message> messageList;

    public ChatboxAdapter(Context context, List<Message> messageList) {
        this.context = context;
        this.messageList = messageList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.chatboxdesign,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
           Message message= messageList.get(position);
           String test= message.getType();
           String other=FirebaseAuth.getInstance().getCurrentUser().getUid().toString();
        FirebaseDatabase.getInstance().getReference().child("Users").child(message.getFrom()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Picasso.with(context).load(dataSnapshot.child("thumnill").getValue().toString())
                        .placeholder(R.drawable.empty_profile)
                        .into(holder.imageView);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
           if (message.getFrom().equals(other))
           {
               if(test.equals("text")) {

                   holder.textView.setText(message.getMessage());
                   holder.messageImage.setVisibility(View.INVISIBLE);


               } else {

                   holder.textView.setVisibility(View.INVISIBLE);
                   holder.messageImage.setVisibility(View.VISIBLE);
                   Picasso.with(context).load(message.getMessage())
                           .placeholder(R.drawable.empty_profile).into(holder.messageImage);

               }

               holder.textView.setBackgroundColor(Color.WHITE);
               holder.textView.setTextColor(Color.BLACK);
           }
           else
           {
               if(test.equals("text")) {

                   holder.textView.setText(message.getMessage());
                   holder.messageImage.setVisibility(View.INVISIBLE);
                   holder.textView.setBackgroundColor(Color.CYAN);
                   holder.textView.setTextColor(Color.WHITE);

               } else {
                   holder.messageImage.setVisibility(View.VISIBLE);
                   holder.textView.setVisibility(View.INVISIBLE);
                   Picasso.with(context).load(message.getMessage())
                           .placeholder(R.drawable.empty_profile).into(holder.messageImage);

               }
           }

    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        CircleImageView imageView;
        ImageView messageImage;
        public ViewHolder(View itemView) {
            super(itemView);
            textView= itemView.findViewById(R.id.showMessageinbox);
            imageView=itemView.findViewById(R.id.showpicinbox);
            messageImage=itemView.findViewById(R.id.imageView);

        }
    }
}
