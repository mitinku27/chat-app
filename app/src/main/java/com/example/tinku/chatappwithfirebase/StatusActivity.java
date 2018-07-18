package com.example.tinku.chatappwithfirebase;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class StatusActivity extends AppCompatActivity {
    Toolbar toolbar;
    Button button;
    TextInputEditText editText;
    ProgressDialog progressBar;
    FirebaseUser user;
    DatabaseReference drf;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);
        mAuth = FirebaseAuth.getInstance();
        toolbar=findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Change Status");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        editText= findViewById(R.id.statuschangtexte);
        button=findViewById(R.id.statuschangeButton);
        user= mAuth.getCurrentUser();
        progressBar= new ProgressDialog(this);
        final String a= user.getUid();
        drf= FirebaseDatabase.getInstance().getReference().child("Users").child(a);
        String text= getIntent().getStringExtra("status");
        editText.setText(text);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressBar.setTitle("Updating Status");
                progressBar.setMessage("Please wait");
                progressBar.show();
                if (!TextUtils.isEmpty(editText.getText().toString()))
                {
                    drf.child("status").setValue(editText.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful())
                            {
                                progressBar.dismiss();
                                startActivity(new Intent(StatusActivity.this,SettingsActivity.class));
                                finish();
                            }
                            else
                            {
                                progressBar.hide();
                                Toast.makeText(StatusActivity.this,"Failed to update status",Toast.LENGTH_LONG).show();
                            }
                        }
                    }) ;
                }

            }
        });
    }
}
