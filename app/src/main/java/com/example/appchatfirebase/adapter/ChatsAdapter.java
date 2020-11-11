package com.example.appchatfirebase.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appchatfirebase.R;
import com.example.appchatfirebase.model.Message;
import com.example.appchatfirebase.model.Person;
import com.example.appchatfirebase.screen.MessengeActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Field;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatsAdapter extends RecyclerView.Adapter<ChatsAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Person> mData;
    private String messLast;

    public ChatsAdapter(Context context, ArrayList<Person> mData) {
        this.context = context;
        this.mData = mData;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_layout_chats, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        Picasso.get().load(mData.get(position).getImg()).into(holder.imgAvt);
        holder.tvUsername.setText(mData.get(position).getUsername());

        lastMess (mData.get(position).getId(), holder.tvMessLast, holder.imgSeen);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MessengeActivity.class);
                intent.putExtra("userID", mData.get(position).getId());
                context.startActivity(intent);
            }
        });

        if (mData.get(position).getStatus().equals("offline")) {
            holder.imgStatus.setVisibility(View.GONE);
        }else {
            holder.imgStatus.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView imgAvt, imgStatus, imgSeen;
        private TextView tvUsername, tvMessLast;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgAvt = (CircleImageView) itemView.findViewById(R.id.img_avt);
            tvUsername = (TextView) itemView.findViewById(R.id.users_id);
            tvMessLast = (TextView) itemView.findViewById(R.id.mess_last_id);
            imgStatus = (CircleImageView) itemView.findViewById(R.id.icon_status);
            imgSeen = (CircleImageView) itemView.findViewById(R.id.imgSeen);

        }
    }

    public void lastMess (final String userID, final TextView lastMess, final CircleImageView imgSeen) {

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Message");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Message message = snapshot.getValue(Message.class);

                    if (message.getSender().equals(userID) && message.getReceiver().equals(user.getUid()) || message.getSender().equals(user.getUid()) && message.getReceiver().equals(userID)) {
                        messLast = message.getMessage();

                        if (message.getStatus().equals("seen")) {
                            lastMess.setText(messLast);
                            setAvt(userID, imgSeen);
                        }else {
                            imgSeen.setVisibility(View.VISIBLE);
                            imgSeen.setImageResource(R.drawable.ic_check_black_24dp);
                        }

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    public void setAvt(String receiver, final CircleImageView imgSeen) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(receiver);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Person person = dataSnapshot.getValue(Person.class);

                Picasso.get().load(person.getImg()).into(imgSeen);
                imgSeen.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
