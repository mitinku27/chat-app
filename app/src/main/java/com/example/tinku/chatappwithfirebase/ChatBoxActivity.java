package com.example.tinku.chatappwithfirebase;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatBoxActivity extends AppCompatActivity {
    String key;
    StorageReference mImageStorage;
    String name;
    Toolbar toolbar;
    EditText writemsg;
    ImageButton sendmsg;
    TextView nametext;
    TextView onlinetext;
    CircleImageView imageView;
    DatabaseReference root;
    String currentuser;
    ChatboxAdapter adapter;
    RecyclerView recyclerView;
    ImageButton more;
    static final int GAL=1;
    List<Message> messageList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_box);
        key= getIntent().getStringExtra("key");
        name=getIntent().getStringExtra("name");
        toolbar= findViewById(R.id.include2);
        more=findViewById(R.id.moreoption);
        writemsg= findViewById(R.id.writemsg);
        sendmsg= findViewById(R.id.sendmsg);
        recyclerView= findViewById(R.id.chatrecycler);
        currentuser= FirebaseAuth.getInstance().getCurrentUser().getUid().toString();
        root= FirebaseDatabase.getInstance().getReference();
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        ActionBar actionBar=getSupportActionBar();
        LayoutInflater inflater= (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        View view= inflater.inflate(R.layout.custom_chat_toolbar,null);
        actionBar.setCustomView(view);
        messageList= new ArrayList<>();
        mImageStorage = FirebaseStorage.getInstance().getReference();

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadMessage();

        nametext=(TextView) view.findViewById(R.id.displaynameText);


        onlinetext= (TextView) view.findViewById(R.id.lastseen);
        imageView= (CircleImageView) view.findViewById(R.id.displaycircular);
        root.child("Chat").child(currentuser).child(key).child("seen").setValue(true);
        root.child("Users").child(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                name=dataSnapshot.child("name").getValue().toString();
                nametext.setText(name);
                String s=dataSnapshot.child("online").getValue().toString();
                if (s.equals("true"))
                {
                    onlinetext.setText(dataSnapshot.child("online").getValue().toString());
                }
                else {
                    Long time=Long.parseLong(s);
                    TimeAgo timeAgo= new TimeAgo();
                    String last=timeAgo.getTimeAgo(time,getApplicationContext());
                    onlinetext.setText(last);
                }
                Picasso.with(getApplicationContext()).load(dataSnapshot.child("thumnill").getValue().toString())
                        .placeholder(R.drawable.empty_profile).into(imageView);

                root.child("Chat").child(currentuser).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (!dataSnapshot.hasChild(key))
                        {
                            Map chatmap= new HashMap();
                            chatmap.put("seen",false);
                            chatmap.put("timestamp", ServerValue.TIMESTAMP);
                            Map mapuser= new HashMap();
                            mapuser.put("Chat/"+currentuser+"/"+key,chatmap);
                            mapuser.put("Chat/"+key+"/"+currentuser,chatmap);
                            root.updateChildren(mapuser, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                                }
                            });
                        }

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
        sendmsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message= writemsg.getText().toString();
                sendMessage(message);
            }
        });
        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"select image"),GAL);

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==GAL&&resultCode==RESULT_OK)
        {
            Uri imageUri= data.getData();
            final String current_user_ref = "Messages/" + currentuser + "/" + key;
            final String chat_user_ref = "Messages/" + key + "/" + currentuser;

            DatabaseReference user_message_push = root.child("Messages")
                    .child(currentuser).child(key).push();

            final String push_id = user_message_push.getKey();


            StorageReference filepath = mImageStorage.child("message_images").child( push_id + ".jpg");

            filepath.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                    if(task.isSuccessful()){

                        String download_url = task.getResult().getDownloadUrl().toString();


                        Map massages = new HashMap();
                        massages.put("message",download_url);
                        massages.put("seen",false);
                        massages.put("time",ServerValue.TIMESTAMP);
                        massages.put("type","image");
                        massages.put("from",currentuser);

                        Map messageUserMap = new HashMap();
                        messageUserMap.put(current_user_ref + "/" + push_id, massages);
                        messageUserMap.put(chat_user_ref + "/" + push_id, massages);

                        writemsg.setText("");

                        root.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                                if(databaseError != null){

                                    Log.d("CHAT_LOG", databaseError.getMessage().toString());

                                }

                            }
                        });
                        Map chatmap= new HashMap();
                        chatmap.put("seen",true);
                        chatmap.put("timestamp", ServerValue.TIMESTAMP);
                        Map mapuser= new HashMap();
                        mapuser.put("Chat/"+currentuser+"/"+key,chatmap);

                        root.updateChildren(mapuser, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                            }
                        });


                    }

                }
            });



        }
    }

    private void loadMessage() {
        DatabaseReference msgquery=root.child("Messages").child(currentuser).child(key);

        root.child("Messages").child(currentuser).child(key).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                Message message= dataSnapshot.getValue(Message.class);

                messageList.add(message);
                adapter= new ChatboxAdapter(getApplicationContext(),messageList);
                adapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(messageList.size()-1);
                recyclerView.setAdapter(adapter);
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

    private void sendMessage(String message) {
        if (!TextUtils.isEmpty(message))
        {
            Log.d("test",message);
            DatabaseReference drf= FirebaseDatabase.getInstance().getReference().child("Messages").child(currentuser)
                    .child(key).push();
            String push=drf.getKey();
            Map massages = new HashMap();
            massages.put("message",message);
            massages.put("seen",false);
            massages.put("time",ServerValue.TIMESTAMP);
            massages.put("type","text");
            massages.put("from",currentuser);
            Map massageuser= new HashMap();
            massageuser.put("Messages/"+currentuser+"/"+key+"/"+push,massages);
            massageuser.put("Messages/"+key+"/"+currentuser+"/"+push,massages);
            writemsg.setText("");
            root.updateChildren(massageuser, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                }
            });
            Map chatmap= new HashMap();
            chatmap.put("seen",true);
            Map other=new HashMap();
            other.put("seen",false);
            other.put("timestamp", ServerValue.TIMESTAMP);
            chatmap.put("timestamp", ServerValue.TIMESTAMP);
            Map mapuser= new HashMap();
            mapuser.put("Chat/"+currentuser+"/"+key,chatmap);
            mapuser.put("Chat/"+key+"/"+currentuser,other);

            root.updateChildren(mapuser, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                }
            });

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this,MainActivity.class));
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }
}
