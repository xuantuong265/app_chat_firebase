package com.example.appchatfirebase.screen;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.appchatfirebase.R;
import com.example.appchatfirebase.adapter.MessageAdapter;
import com.example.appchatfirebase.adapter.UsersAdapter;
import com.example.appchatfirebase.model.Chats;
import com.example.appchatfirebase.model.Message;
import com.example.appchatfirebase.model.Person;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessengeActivity extends AppCompatActivity {

    private CircleImageView imgAvt;
    private ImageButton imgSend;
    private TextView tvUsername;
    private EditText edtMess;

    private FirebaseUser firebaseUser;
    private DatabaseReference reference;

    private RecyclerView recyclerView;
    private MessageAdapter messageAdapter;
    private ArrayList<Message> mData = new ArrayList<>();
    private ArrayList<Chats> listChat = new ArrayList<>();

    private String userID;
    private Boolean isCheck = false;

    private ValueEventListener seenListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messenge);

        initView();

        // nhan id
        Intent intent = getIntent();
        userID = intent.getStringExtra("userID");

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        // call
        readInfoUser(userID);
        setOnClick();


    }

    private void initView() {
        imgAvt = (CircleImageView) findViewById(R.id.img_avt_id);
        tvUsername = (TextView) findViewById(R.id.username_id);
        edtMess = (EditText) findViewById(R.id.edt_mess_id);
        imgSend = (ImageButton) findViewById(R.id.img_send_id);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
    }

    private void readInfoUser (final String userID) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(userID);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Person person = dataSnapshot.getValue(Person.class);
                tvUsername.setText(person.getUsername());
                Picasso.get().load(person.getImg()).into(imgAvt);

                // doc data
                readMessage(firebaseUser.getUid(), userID, person.getImg());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setOnClick () {
        imgSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mess = edtMess.getText().toString().trim();

                if (mess.isEmpty()) {
                    return;
                }

                sendMessage(firebaseUser.getUid(), userID, mess);

            }
        });
    }

    private void sendMessage (String sender, String receiver, String mess) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message", mess);
        hashMap.put("status", "notSee");

        reference.child("Message").push().setValue(hashMap);

       addUserChast(sender, receiver); // call



        edtMess.setText("");
    }

    private void readMessage (final String myID, final String userID, final String img) {
        reference = FirebaseDatabase.getInstance().getReference("Message");

        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Message message = dataSnapshot.getValue(Message.class);

                if (message.getReceiver().equals(myID) && message.getSender().equals(userID) || message.getReceiver().equals(userID) && message.getSender().equals(myID)) {
                    mData.add(message);
                    messageAdapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // call method
        seenMessage(userID);

        // set recyclerview
        messageAdapter = new MessageAdapter(getApplicationContext(), mData, img);
        recyclerView.setAdapter(messageAdapter);


    }

    private void addUserChast (final String sender, final String receiver) {
        // doc user
        final DatabaseReference userMess = FirebaseDatabase.getInstance().getReference("Chats");

//        HashMap<String, String> hashMap = new HashMap<>();
//        hashMap.put("sender", sender);
//        hashMap.put("receiver", receiver);
//
//        userMess.push().setValue(hashMap);

        userMess.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chats chats = snapshot.getValue(Chats.class);

                    if (chats.getSender().equals(firebaseUser.getUid()) && chats.getReceiver().equals(userID) || chats.getSender().equals(userID) && chats.getReceiver().equals(firebaseUser.getUid())) {
                        isCheck = true;
                    }

                }

                if (isCheck == false) {

                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("sender", sender);
                    hashMap.put("receiver", receiver);

                    userMess.push().setValue(hashMap);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




    }

    private void seenMessage (final String userID) {
        reference = FirebaseDatabase.getInstance().getReference("Message");

        seenListener = reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Message message = snapshot.getValue(Message.class);

                    if (message.getReceiver().equals(firebaseUser.getUid()) && message.getSender().equals(userID)) {
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("status", "seen");
                        snapshot.getRef().updateChildren(hashMap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void status (String status) {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);

        reference.updateChildren(hashMap);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }

    @Override
    protected void onResume() {
        super.onResume();
        status("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        reference.removeEventListener(seenListener);
        status("offline");
    }
}
