package com.example.tinku.chatappwithfirebase;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class SettingsActivity extends AppCompatActivity {
    CircleImageView imageView;
    DatabaseReference drf;
    FirebaseUser user;
    TextView status;
    DatabaseReference online;
    String user12;
    public static final int GAL=1;
    TextView displayname;
    Button changeimage;
    Button changestatus;
    String thumb;
    private StorageReference mStorageRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        status = findViewById(R.id.settingstatus);
        displayname=findViewById(R.id.settingdisplay);
        changestatus = findViewById(R.id.changestatus);
        imageView=findViewById(R.id.settingimageview);
        changeimage =findViewById(R.id.settingchangeimage);
        mStorageRef = FirebaseStorage.getInstance().getReference().child("profile_picture");
        user= FirebaseAuth.getInstance().getCurrentUser();
        String uid= user.getUid();
        user12=FirebaseAuth.getInstance().getCurrentUser().getUid().toString();
        online=FirebaseDatabase.getInstance().getReference().child("Users").child(user12);

        drf= FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
        drf.keepSynced(true);
        drf.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
              status.setText(dataSnapshot.child("status").getValue().toString());
              displayname.setText(dataSnapshot.child("name").getValue().toString());
              if (!dataSnapshot.child("dp").getValue().toString().equals("default"));
              Picasso.with(SettingsActivity.this)

                      .load(dataSnapshot.child("dp").getValue().toString())
                      .placeholder(R.drawable.empty_profile)
                      .into(imageView);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        changestatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this,StatusActivity.class);
                intent.putExtra("status",status.getText().toString());
                startActivity(intent);
            }
        });

        changeimage.setOnClickListener(new View.OnClickListener() {
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
            Toast.makeText(this,"hello"+imageUri.toString(),Toast.LENGTH_LONG);
            CropImage.activity(imageUri).setAspectRatio(1,1)
                    .setMinCropWindowSize(500,500)
                    .start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                File filepath = new File(resultUri.getPath());
                Bitmap compressedImageBitmap = null;
                try {
                    compressedImageBitmap = new Compressor(this)
                            .setMaxHeight(75)
                            .setMaxHeight(75)
                            .setQuality(50)
                            .compressToBitmap(filepath);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                compressedImageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                final byte[] my_data = baos.toByteArray();




                StorageReference stf = mStorageRef.child(resultUri.getLastPathSegment());
                StorageReference mStorageRef2 = FirebaseStorage.getInstance().getReference();
                final StorageReference mountainsRef=mStorageRef2.child("thumb_image").child(filepath.getName());
                stf.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        final String down = taskSnapshot.getDownloadUrl().toString();
                        UploadTask uploadTask = mountainsRef.putBytes(my_data);
                        uploadTask.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Handle unsuccessful uploads
                            }
                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                                DatabaseReference rf = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid()).child("dp");
                                rf.setValue(down);
                               // Picasso.with(SettingsActivity.this).load(down)
                                    //    .into(imageView);
                                Picasso.with(SettingsActivity.this).load(down).networkPolicy(NetworkPolicy.OFFLINE)
                                        .into(imageView, new Callback() {
                                            @Override
                                            public void onSuccess() {

                                            }

                                            @Override
                                            public void onError() {
                                                Picasso.with(SettingsActivity.this).load(down)
                                                    .into(imageView);
                                            }
                                        });
                                DatabaseReference rf2 = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid()).child("thumnill");
                                rf2.setValue(downloadUrl.toString());
                            }
                        });

                    }
                });

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

    }

    public SettingsActivity() {
        super();

    }

    @Override
    protected void onStart() {
        super.onStart();
        online.child("online").setValue("true");
    }

    @Override
    protected void onStop() {
        super.onStop();
        online.child("online").onDisconnect().setValue(ServerValue.TIMESTAMP);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this,MainActivity.class));
    }
}
