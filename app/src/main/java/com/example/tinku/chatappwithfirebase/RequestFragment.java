package com.example.tinku.chatappwithfirebase;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class RequestFragment extends Fragment {
    RequestHandleRecycleAdapter adapter;
    List<Request> requestList;
    RecyclerView recyclerView;
    DatabaseReference request;
    String user;
    public RequestFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.fragment_request, container, false);
        recyclerView=view.findViewById(R.id.reqreve);
        LinearLayoutManager lin= new LinearLayoutManager(getContext());
        user= FirebaseAuth.getInstance().getCurrentUser().getUid().toString();
        request= FirebaseDatabase.getInstance().getReference().child("Friend_request").child(user);
        requestList= new ArrayList<>();
        lin.setReverseLayout(true);
        lin.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(lin);
        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d("test","test");
        request.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Request riq= dataSnapshot.getValue(Request.class);
               // Log.d("tag",riq.getType());

                riq.setKey(dataSnapshot.getKey().toString());
                requestList.add(riq);
                adapter= new RequestHandleRecycleAdapter(getContext(),requestList);
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
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
}
