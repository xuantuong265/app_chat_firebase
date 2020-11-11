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
import com.example.appchatfirebase.model.Person;
import com.example.appchatfirebase.screen.MessengeActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class UsersOnlineAdapter extends RecyclerView.Adapter<UsersOnlineAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Person> mData;

    public UsersOnlineAdapter(Context context, ArrayList<Person> mData) {
        this.context = context;
        this.mData = mData;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_layout_user_online, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        Picasso.get().load(mData.get(position).getImg()).into(holder.imgAvt);
        holder.tvUsername.setText(mData.get(position).getUsername());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MessengeActivity.class);
                intent.putExtra("userID", mData.get(position).getId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView imgAvt;
        private TextView tvUsername;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgAvt = (CircleImageView) itemView.findViewById(R.id.img_avt_online);
            tvUsername = (TextView) itemView.findViewById(R.id.user_online_id);

        }
    }
}
