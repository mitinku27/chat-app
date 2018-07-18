package com.example.tinku.chatappwithfirebase;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by TINKU on 2/21/2018.
 */

public class ChatReAdapter extends RecyclerView.Adapter<ChatReAdapter.ViewHolder> {


    Context context;
    List<Conversation> conversationList;

    String name2;
    public ChatReAdapter(Context context, List<Conversation> conversationList) {
        this.context = context;
        this.conversationList = conversationList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.chatbuddy,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Conversation conversation= conversationList.get(position);

        final String user= FirebaseAuth.getInstance().getCurrentUser().getUid().toString();
       // key2= conversation.getKey();
        DatabaseReference drf= FirebaseDatabase.getInstance().getReference().child("Users").child(conversation.getKey());
        drf.keepSynced(true);
        drf.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                holder.name.setText(dataSnapshot.child("name").getValue().toString());
                name2=dataSnapshot.child("name").getValue().toString();
                Picasso.with(context).load(dataSnapshot.child("thumnill").getValue().toString())
                .placeholder(R.drawable.empty_profile).into(holder.imageView);

                if (dataSnapshot.child("online").getValue().toString().equals("true"))
                {
                    holder.onoff.setVisibility(View.VISIBLE);
                }

                DatabaseReference mses=FirebaseDatabase.getInstance().getReference()
                        .child("Messages").child(user).child(conversation.getKey());
                Query newquery=mses.limitToLast(1);
                newquery.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        final Message message=dataSnapshot.getValue(Message.class);
                        if (message.getType().equals("text"))
                        {
                            if (conversation.getSeen())
                                holder.msg.setText(message.getMessage());
                            else
                            {
                                holder.msg.setTypeface(Typeface.DEFAULT_BOLD);
                                holder.msg.setText(message.getMessage());
                            }
                        }else {

                            if (conversation.getSeen())
                                holder.msg.setText("image....");
                            else
                            {
                                holder.msg.setTypeface(Typeface.DEFAULT_BOLD);
                                holder.msg.setText("image....");
                            }

                        }

                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return conversationList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView msg;
        ImageView imageView;
        ImageView onoff;
        String key2;
        public ViewHolder(View itemView) {
            super(itemView);
            name= itemView.findViewById(R.id.chatuddyname);
            msg=itemView.findViewById(R.id.chatbuddymsg);
            imageView=itemView.findViewById(R.id.chatbuddyimage);
            onoff=itemView.findViewById(R.id.onoff);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Conversation c= conversationList.get(getAdapterPosition());
                    key2=c.getKey();
                    Intent intent= new Intent(context,ChatBoxActivity.class);
                    intent.putExtra("key",key2);
                    intent.putExtra("name",name2);
                    context.startActivity(intent);
                }
            });
        }
    }
}
