package com.example.tinku.chatappwithfirebase;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by TINKU on 2/14/2018.
 */

public class FriendrecyclerAdapter extends RecyclerView.Adapter<FriendrecyclerAdapter.ViewHolder> {
    Context context;
    List<Friends> friendsList;

    String dispalyname;
    public FriendrecyclerAdapter(Context context, List<Friends> friendsList) {
        this.context = context;
        this.friendsList = friendsList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.user_list,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {


        final Friends friends= friendsList.get(position);


       // Log.d("rekey",key+"   "+position);

        FirebaseDatabase.getInstance().getReference().child("Users").child(friends.getKey()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                dispalyname=dataSnapshot.child("name").getValue().toString();
                holder.name.setText(dataSnapshot.child("name").getValue().toString());
                //Log.d("testonline",dataSnapshot.child("online").getValue().toString());
               if (dataSnapshot.hasChild("online"))
                {
                    if ((boolean)dataSnapshot.child("online").getValue().equals("true"))
                    holder.img.setVisibility(View.VISIBLE);
                }
                else
               {
                   holder.img.setVisibility(View.VISIBLE);
               }

                Picasso.with(context).load(dataSnapshot.child("thumnill").getValue().toString()).networkPolicy(NetworkPolicy.OFFLINE)
                        .placeholder(R.drawable.empty_profile)
                        .into(holder.imageView, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError() {
                                Picasso.with(context).load(String.valueOf(dataSnapshot.child("thumnill").getValue())).placeholder(R.drawable.empty_profile)
                                        .into(holder.imageView);
                            }
                        });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        holder.status.setText(friends.getTime());
    }

    @Override
    public int getItemCount() {
        return friendsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView status;
        CircleImageView imageView;
        ImageView img;

        public ViewHolder(View itemView) {
            super(itemView);
            name= itemView.findViewById(R.id.userusername);
            status=itemView.findViewById(R.id.userstatus);
            imageView=itemView.findViewById(R.id.userimage);
            img= itemView.findViewById(R.id.onlinebutton);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Friends f=friendsList.get(getAdapterPosition());
                    final String key=f.getKey();
                    final CharSequence charSequence[]={"open profile","send message"};
                    AlertDialog.Builder builder= new AlertDialog.Builder(context);
                    builder.setTitle("select option");
                    builder.setItems(charSequence, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (which==0)
                            {
                                Intent intent= new Intent(context,ProfileActivity.class);
                                intent.putExtra("key",key);
                                context.startActivity(intent);
                            }
                            if (which==1)
                            {
                                Intent intent= new Intent(context,ChatBoxActivity.class);
                                intent.putExtra("name",dispalyname);
                                intent.putExtra("key",key);
                                Log.d("one",key);
                                context.startActivity(intent);
                            }
                        }
                    });
                    builder.show();
                }
            });
        }
    }
}
