package com.example.tinku.chatappwithfirebase;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by TINKU on 2/5/2018.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
    Context context;
    List<User> userList;
    String key;

    public RecyclerAdapter(Context context, List<User> userList) {

        this.context = context;
        this.userList = userList;
      //  User firebaseUser=userList.get(0);
       // Log.d("ddd",firebaseUser.getName());
    }

    @Override
    public RecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.user_list,parent,false);
        return new ViewHolder(view,context);
    }

    @Override
    public void onBindViewHolder(final RecyclerAdapter.ViewHolder holder, int position) {
            final User user= userList.get(position);
            holder.name.setText(user.getName());

            holder.status.setText(user.getStatus());
        Picasso.with(context).load(user.getThumnill()).networkPolicy(NetworkPolicy.OFFLINE)
                .placeholder(R.drawable.empty_profile)
                .into(holder.imageView, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        Picasso.with(context).load(user.getThumnill()).placeholder(R.drawable.empty_profile)
                                .into(holder.imageView);
                    }
                });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView status;
        CircleImageView imageView;
        public ViewHolder(View itemView, Context cxt) {
            super(itemView);
            cxt=context;
            name= itemView.findViewById(R.id.userusername);
            status=itemView.findViewById(R.id.userstatus);
            imageView=itemView.findViewById(R.id.userimage);
            final Context finalCxt = cxt;
            final Context finalCxt1 = cxt;
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   int position= getAdapterPosition();
                   User user=userList.get(position);
                    Intent intent = new Intent(finalCxt1,ProfileActivity.class);


                   DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference().child("Users");
                    //databaseReference.getRef();//
                    Log.d("key", user.getUserid());
                    intent.putExtra("key", user.getUserid());
                    finalCxt.startActivity(intent);

                }
            });
        }
    }
}
