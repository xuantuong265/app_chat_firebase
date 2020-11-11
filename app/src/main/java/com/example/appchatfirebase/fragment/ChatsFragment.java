package com.example.appchatfirebase.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appchatfirebase.R;
import com.example.appchatfirebase.adapter.ChatsAdapter;
import com.example.appchatfirebase.adapter.UsersAdapter;
import com.example.appchatfirebase.adapter.UsersOnlineAdapter;
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

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatsFragment extends Fragment {

    private View view;
    private SearchView searchView;

    private RecyclerView userOnline, userHadMess;
    private UsersOnlineAdapter usersOnlineAdapter;
    private ChatsAdapter chatsAdapter;
    private ArrayList<Person> userOnlineList = new ArrayList<>();
    private ArrayList<Person> userChatList = new ArrayList<>();
    private List<String> lastMess = new ArrayList<>();

    private FirebaseUser user;
    private DatabaseReference reference;

    private Boolean isCheck = false;
    private int count = 0;

    public ChatsFragment () {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate( R.layout.chats_layout, container, false);

        initView();
        readChats();
        readUsersOnline();

//        DatabaseReference userMess = FirebaseDatabase.getInstance().getReference();
//        userMess.child("Chats").removeValue();

        return view;
    }

    private void initView () {

        user = FirebaseAuth.getInstance().getCurrentUser();

        userOnline = (RecyclerView) view.findViewById(R.id.recyclerview_horizontal_id);
        userOnline.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager
                ( getContext(), RecyclerView.HORIZONTAL, false );
        userOnline.setLayoutManager(layoutManager);

        userHadMess = (RecyclerView) view.findViewById(R.id.recyclerview_vertical_id);
        userHadMess.setHasFixedSize(true);
        LinearLayoutManager manager = new LinearLayoutManager
                ( getContext(), RecyclerView.VERTICAL, false );
        userHadMess.setLayoutManager(manager);
    }

    private void readUsersOnline () {

        reference = FirebaseDatabase.getInstance().getReference("Users");

        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Person person = dataSnapshot.getValue(Person.class);

                if (!person.getId().equals(user.getUid())) {
                   if (person.getStatus().equals("online")) {
                       userOnlineList.add(person);
                       usersOnlineAdapter.notifyDataSetChanged();
                   }
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

            // set recyclerview
            usersOnlineAdapter = new UsersOnlineAdapter(getContext(), userOnlineList);
            userOnline.setAdapter(usersOnlineAdapter);
    }

    private void readChats () {

        reference = FirebaseDatabase.getInstance().getReference("Chats");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chats chats = snapshot.getValue(Chats.class);

                    if (chats.getSender().equals(user.getUid())) {
                        lastMess.add(chats.getReceiver());
                    }

                    if (chats.getReceiver().equals(user.getUid())) {
                        lastMess.add(chats.getSender());
                    }
                }
                readUserChats ();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }

    private void readUserChats () {

        reference = FirebaseDatabase.getInstance().getReference("Users");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Person person = snapshot.getValue(Person.class);

                    for (int i = 0; i < lastMess.size(); i++) {
                        if (person.getId().equals(lastMess.get(i))) {
                            userChatList.add(person);
                            chatsAdapter.notifyDataSetChanged();
                        }
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // set recyclerview
        chatsAdapter = new ChatsAdapter(getContext(), userChatList);
        userHadMess.setAdapter(chatsAdapter);
    }


}
