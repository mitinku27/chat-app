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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
    TextInputEditText display;
    TextInputEditText email;
    TextInputEditText password;
    Button create;
    private FirebaseAuth mAuth;
    Toolbar toolbar;
    ProgressDialog progressBar;
    FirebaseUser user;
    DatabaseReference drf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();
        toolbar=findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Create Account");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        display= findViewById(R.id.regdisplayname);
        email=findViewById(R.id.regemailname);
        password=findViewById(R.id.regpasswordname);
        create=findViewById(R.id.regcreate);

        progressBar = new ProgressDialog(this);

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailit=email.getText().toString();
                String passwordit=password.getText().toString();
                String displayit=display.getText().toString();
                if (!TextUtils.isEmpty(emailit)&&!TextUtils.isEmpty(passwordit)&&!TextUtils.isEmpty(displayit))
                {
                    progressBar.setTitle("Creating Account");
                    progressBar.setMessage("please wait....");
                    progressBar.setCanceledOnTouchOutside(false);
                    progressBar.show();
                    createAccount(emailit,passwordit,displayit);
                }

            }
        });

    }

    private void createAccount(String emailit, String passwordit, final String d) {

        mAuth.createUserWithEmailAndPassword(emailit,passwordit).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful())
                {
                    user=mAuth.getCurrentUser();
                    String uid= user.getUid();
                    drf= FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
                    HashMap<String,String> userinfo= new HashMap<>();
                    userinfo.put("name",d);
                    userinfo.put("dp","default");
                    userinfo.put("status","Hello World");
                    userinfo.put("thumnill","default");
                    drf.setValue(userinfo);
                    String token= FirebaseInstanceId.getInstance().getToken();
                    FirebaseDatabase.getInstance().getReference().child("Users")
                            .child(mAuth.getCurrentUser().getUid().toString())
                            .child("tokenId").setValue(token).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            progressBar.dismiss();
                            Intent intent= new Intent(RegisterActivity.this,MainActivity.class);

                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        }
                    });

                }else
                {
                    progressBar.hide();
                    Toast.makeText(RegisterActivity.this,"Error Occured",Toast.LENGTH_LONG);
                }
            }
        });
    }
}
