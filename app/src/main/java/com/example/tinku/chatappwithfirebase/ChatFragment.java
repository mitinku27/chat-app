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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatFragment extends Fragment {

    RecyclerView rcv;
    List<Conversation> conversationList;
    ChatReAdapter adapter;
    DatabaseReference chat;
    String currentuser;
    public ChatFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
       View view =  inflater.inflate(R.layout.fragment_chat, container, false);
        currentuser= FirebaseAuth.getInstance().getCurrentUser().getUid().toString();
       rcv=view.findViewById(R.id.chatlist);
       conversationList= new ArrayList<>();
        chat= FirebaseDatabase.getInstance().getReference().child("Chat");
        chat.keepSynced(true);
        LinearLayoutManager lin= new LinearLayoutManager(getContext());
        lin.setReverseLayout(true);
        lin.setStackFromEnd(true);
        rcv.setHasFixedSize(true);
        rcv.setLayoutManager(lin);
        return  view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Query query= chat.child(currentuser).orderByChild("timestamp");
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Conversation conversation= dataSnapshot.getValue(Conversation.class);
                conversation.key= dataSnapshot.getKey();
               // Log.d("keyit",conversation.key);
                conversationList.add(conversation);
                adapter= new ChatReAdapter(getContext(),conversationList);
                rcv.setAdapter(adapter);
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
