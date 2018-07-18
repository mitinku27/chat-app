package com.example.tinku.chatappwithfirebase;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by TINKU on 2/21/2018.
 */

public class RequestHandleRecycleAdapter extends RecyclerView.Adapter<RequestHandleRecycleAdapter.ViewHolder> {
    Context context;
    List<Request> requestList;
    String user;
    String key;
    public RequestHandleRecycleAdapter(Context context, List<Request> requestList) {
        this.context = context;
        this.requestList = requestList;
    }

    @Override
    public RequestHandleRecycleAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.requestsegment,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RequestHandleRecycleAdapter.ViewHolder holder, int position) {
        Request request= requestList.get(position);
        holder.accept.setVisibility(View.INVISIBLE);
        holder.delete.setVisibility(View.INVISIBLE);
        key=request.getKey();
        user= FirebaseAuth.getInstance().getCurrentUser().getUid().toString();
        //Log.d("next",request.getKey());
        FirebaseDatabase.getInstance().getReference().child("Users").child(request.getKey()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                holder.name.setText(dataSnapshot.child("name").getValue().toString());
                holder.status.setText(dataSnapshot.child("status").getValue().toString());
                Picasso.with(context).load(dataSnapshot.child("thumnill").getValue().toString())
                        .placeholder(R.drawable.empty_profile).into(holder.imageView);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    @Override
    public int getItemCount() {
        return requestList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView name;
        TextView status;
        Button accept;
        Button delete;
        public ViewHolder(View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.requestuname);
            status=itemView.findViewById(R.id.requestustatus);
            imageView= itemView.findViewById(R.id.requestuimage);
            accept= itemView.findViewById(R.id.requestuaccept);
            delete=itemView.findViewById(R.id.requestudecline);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context,ProfileActivity.class);

                    intent.putExtra("key",key);
                    context.startActivity(intent);
                }
            });
        }
    }
}
