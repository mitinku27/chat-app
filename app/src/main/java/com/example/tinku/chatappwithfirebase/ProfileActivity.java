package com.example.tinku.chatappwithfirebase;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {
    TextView name;
    TextView status;
    ImageView image;
    Button sendreq;
    Button decline;
    String currentState;
    DatabaseReference online;
    String user;
    FirebaseUser firebaseUser;
    DatabaseReference drf;
    int switching;
    DatabaseReference root;
    String type;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        name= findViewById(R.id.prodisplayname);
        status=findViewById(R.id.prostatus);
        image= findViewById(R.id.proimageview);
        sendreq=findViewById(R.id.prosendreq);
        decline= findViewById(R.id.prodecline);
        root= FirebaseDatabase.getInstance().getReference();
        user=FirebaseAuth.getInstance().getCurrentUser().getUid().toString();
        online=FirebaseDatabase.getInstance().getReference().child("Users").child(user);
        decline.setEnabled(false);
        decline.setVisibility(View.INVISIBLE);
        Intent intent= getIntent();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final String key=intent.getStringExtra("key");
        switching=intent.getIntExtra("req",0);

        currentState="not_friend";
        drf= FirebaseDatabase.getInstance().getReference().child("Users").child(key);
        drf.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                FirebaseDatabase.getInstance().getReference().child("Friend_request")
                        .child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(key))
                        {
                             type=dataSnapshot.child(key).child("type").getValue().toString();

                            if (type.equals("receive"))
                            {
                                currentState="receive";
                                sendreq.setText("accept request");
                                decline.setEnabled(true);
                                decline.setVisibility(View.VISIBLE);
                            }
                            if (type.equals("sent"))
                            {
                                currentState="sent";
                                sendreq.setText("cancel request");
                            }

                        }
                        else {
                            FirebaseDatabase.getInstance().getReference().child("Friends")
                                    .child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.hasChild(key))
                                    {
                                        currentState="friend";
                                        sendreq.setEnabled(true);
                                        sendreq.setText("unfriend");
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                name.setText(dataSnapshot.child("name").getValue().toString());
                status.setText(dataSnapshot.child("status").getValue().toString());
                Picasso.with(ProfileActivity.this).load(dataSnapshot.child("dp").getValue().toString())
                        .placeholder(R.drawable.empty_profile).into(image);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    //------------------------send friend request -------------------------------------------------------
        final String user= FirebaseAuth.getInstance().getCurrentUser().getUid().toString();
       // textView.setText(s);
        sendreq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendreq.setEnabled(false);
                if (currentState.equals("not_friend"))
                {
                    DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference().child("Notification").child(key).push();
                    String notificationId= databaseReference.getKey();
                    HashMap<String,String> notificationdata=new HashMap<>();
                    notificationdata.put("from",user);
                    notificationdata.put("type","request");
                    Map friendsmap= new HashMap();
                    friendsmap.put("Friend_request/"+user+"/"+key+"/"+"type","sent");
                    friendsmap.put("Friend_request/"+key+"/"+user+"/"+"type","receive");
                    friendsmap.put("Notification/"+key+"/"+notificationId,notificationdata);
                    root.updateChildren(friendsmap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                        }
                    });
                    currentState="sent";
                    sendreq.setEnabled(true);
                    sendreq.setText("cancel request");
                }

                //----------------------cancel friend request--------------------------
               else  if (currentState.equals("sent"))
                {

                    FirebaseDatabase.getInstance().getReference().child("Friend_request").child(user)
                            .child(key).removeValue();
                    FirebaseDatabase.getInstance().getReference().child("Friend_request").child(key)
                            .child(user).removeValue();
                   // sendreq.setText("send request");
                    currentState="not_friend";
                    sendreq.setEnabled(true);
                    sendreq.setText("send request");

                }
                //-----------------------accept---------------
                else if (currentState.equals("receive"))
                {
                    String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                    FirebaseDatabase.getInstance().getReference().child("Friends").child(user)
                            .child(key).child("time").setValue(date);
                    FirebaseDatabase.getInstance().getReference().child("Friends").child(key)
                            .child(user).child("time").setValue(date);
                    currentState="friend";
                    sendreq.setEnabled(true);
                    sendreq.setText("unfriend");
                    FirebaseDatabase.getInstance().getReference().child("Friend_request").child(user)
                            .child(key).removeValue();
                    FirebaseDatabase.getInstance().getReference().child("Friend_request").child(key)
                            .child(user).removeValue();
                }

                //----------------------------unfriend-----------------------------------------
                else if (currentState.equals("friend"))
                {
                    FirebaseDatabase.getInstance().getReference().child("Friends").child(user)
                            .child(key).removeValue();
                    FirebaseDatabase.getInstance().getReference().child("Friends").child(key)
                            .child(user).removeValue();
                    currentState="not_friend";
                    sendreq.setText("send request");
                    sendreq.setEnabled(true);
                }

                    sendreq.setEnabled(true);
            }
        });

        decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase.getInstance().getReference().child("Friend_request").child(user)
                        .child(key).removeValue();
                FirebaseDatabase.getInstance().getReference().child("Friend_request").child(key)
                        .child(user).removeValue();
                // sendreq.setText("send request");
                currentState="not_friend";
                sendreq.setEnabled(true);
                sendreq.setText("send request");
                decline.setEnabled(false);
                decline.setVisibility(View.INVISIBLE);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        online.child("online").setValue(true);

    }

    @Override
    protected void onStop() {
        super.onStop();
        online.child("online").onDisconnect().setValue(false);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        startActivity(new Intent(ProfileActivity.this,MainActivity.class));
    }
}
