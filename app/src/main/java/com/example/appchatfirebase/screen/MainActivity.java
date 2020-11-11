package com.example.appchatfirebase.screen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.example.appchatfirebase.R;
import com.example.appchatfirebase.fragment.ChatsFragment;
import com.example.appchatfirebase.fragment.UsersFragment;
import com.example.appchatfirebase.model.Person;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private CircleImageView imgAvt;

    public static SharedPreferences loginSharedPreferences;
    public static SharedPreferences.Editor loginEditor;

    private Person person;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // check user
        checkInfo ();

        // anh xa
        imgAvt = (CircleImageView) findViewById(R.id.img_avt_id);

        // call fun
        readInfoCurrent();


        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);

        if ( savedInstanceState == null ){
            getSupportFragmentManager().beginTransaction().replace(R.id.framContainer, new ChatsFragment()).commit();
        }

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                Fragment fragment = null;
                switch (menuItem.getItemId()) {
                    case R.id.chats_id:
                        fragment = new ChatsFragment();
                        break;

                    case R.id.users_id:
                        fragment = new UsersFragment();
                        break;

                }
                fragmentTransaction.replace(R.id.framContainer, fragment);
                fragmentTransaction.commit();
                return true;

            }
        });

        // imgAvt click

        imgAvt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                user = FirebaseAuth.getInstance().getCurrentUser();

                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                intent.putExtra("img", person.getImg());
                intent.putExtra("name", person.getUsername());
                intent.putExtra("address", person.getAddress());
                intent.putExtra("phone", person.getPhone());
                intent.putExtra("email", user.getEmail());

                startActivity(intent);
            }
        });



    }


    private void readInfoCurrent () {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference userCurrent = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());

        userCurrent.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                person = dataSnapshot.getValue(Person.class);

                Picasso.get().load(person.getImg()).into(imgAvt);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void checkInfo () {

        loginSharedPreferences = getSharedPreferences("Account", Context.MODE_PRIVATE);

        String email = loginSharedPreferences.getString("email", "");

        if (email.length() == 0) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        }

    }

    private void status (String status) {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);

        reference.updateChildren(hashMap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        status("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        status("offline");
    }
}
