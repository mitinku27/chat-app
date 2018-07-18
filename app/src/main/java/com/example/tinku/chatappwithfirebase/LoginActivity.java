package com.example.tinku.chatappwithfirebase;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class LoginActivity extends AppCompatActivity {

    TextInputEditText email;
    TextInputEditText password;
    Button login;
    private FirebaseAuth mAuth;
    ProgressDialog pd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        email= findViewById(R.id.logemailname);
        password= findViewById(R.id.logpasswordname);
        login= findViewById(R.id.logcreate);
        mAuth = FirebaseAuth.getInstance();
        pd= new ProgressDialog(this);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String emailit=email.getText().toString();
                String passit= password.getText().toString();
                if (!TextUtils.isEmpty(emailit)&&!TextUtils.isEmpty(passit))
                {
                    logintToAccount(emailit,passit);
                }

            }
        });

    }

    private void logintToAccount(String emailit, String passit) {
        pd.setTitle("Logging In");
        pd.setMessage("Please wait");
        pd.show();
        mAuth.signInWithEmailAndPassword(emailit,passit).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful())
                    {
                        pd.dismiss();
                        String token= FirebaseInstanceId.getInstance().getToken();
                        FirebaseDatabase.getInstance().getReference().child("Users")
                                .child(mAuth.getCurrentUser().getUid().toString())
                                .child("tokenId").setValue(token).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Intent intent= new Intent(LoginActivity.this,MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            }
                        });

                    }
                    else {
                        pd.hide();
                        Toast.makeText(LoginActivity.this,"Error in Log in",Toast.LENGTH_LONG).show();
                    }
            }
        });
    }
}
