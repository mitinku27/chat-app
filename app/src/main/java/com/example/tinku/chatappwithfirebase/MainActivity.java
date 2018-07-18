package com.example.tinku.chatappwithfirebase;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    android.support.v7.widget.Toolbar toolbar;
    DatabaseReference online;
    String user;
    ViewPager viewPager;
    TabLayout tabLayout;
    MainViewpagerAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        toolbar= findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("We Chat");
        if (mAuth.getCurrentUser()!=null)
        {
            user=FirebaseAuth.getInstance().getCurrentUser().getUid().toString();
            online=FirebaseDatabase.getInstance().getReference().child("Users").child(user);
        }

        viewPager= findViewById(R.id.mainviewpager);
        tabLayout= findViewById(R.id.maintab);

        adapter= new MainViewpagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);

        tabLayout.setupWithViewPager(viewPager);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
         getMenuInflater().inflate(R.menu.app_bar,menu);
         return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

         super.onOptionsItemSelected(item);
         int id= item.getItemId();
         if (id==R.id.menulogout)
         {
             mAuth.signOut();
             Intent startIntent= new Intent(this,StartActivity.class);
             startActivity(startIntent);
             finish();
         }
          if(id==R.id.menusetting)
         {
             Intent startIntent= new Intent(this,SettingsActivity.class);
             startActivity(startIntent);

         }
         if (id==R.id.menuusers)
         {
             Intent startIntent= new Intent(this,UsersActivity.class);
             startActivity(startIntent);

         }
         return true;
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser==null)
        {
            Intent startIntent= new Intent(this,StartActivity.class);
            startActivity(startIntent);
            finish();
        }else {
            if (online!=null)
            online.child("online").setValue("true");

        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (online!=null)
        online.child("online").onDisconnect().setValue(ServerValue.TIMESTAMP);

    }
}
