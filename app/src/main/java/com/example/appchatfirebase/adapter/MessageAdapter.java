package com.example.appchatfirebase.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appchatfirebase.R;
import com.example.appchatfirebase.model.Message;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    public static final int MSG_RIGHT = 0;
    public static final int MSG_LEFT = 1;

    private Context context;
    private ArrayList<Message> mData;

    private FirebaseUser firebaseUser;
    private String img;
    private Boolean flag = false;

    public MessageAdapter(Context context, ArrayList<Message> mData, String img) {
        this.context = context;
        this.mData = mData;
        this.img = img;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view;

        if (viewType == MSG_RIGHT) {
            view = layoutInflater.inflate(R.layout.item_layout_mess_right, parent, false);
            return new ViewHolder(view);
        }else {
            view = layoutInflater.inflate(R.layout.item_layout_mess_left, parent, false);
            return new ViewHolder(view);
        }


    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tvMess.setText(mData.get(position).getMessage());

        if (flag) {
            Picasso.get().load(img).into(holder.imgAvt);
        }

        if (position == mData.size() - 1) {
            if (mData.get(position).getStatus().equals("seen")) {
                holder.tvSeen.setText("đã xem");
                holder.tvSeen.setVisibility(View.VISIBLE);
            }else {
                holder.tvSeen.setText("đã gửi");
                holder.tvSeen.setVisibility(View.VISIBLE);
            }
        }else {
            holder.tvSeen.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvMess, tvSeen;
        private CircleImageView imgAvt;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvMess = (TextView) itemView.findViewById(R.id.content_mess_id);
            tvSeen = (TextView) itemView.findViewById(R.id.seen);

            if (flag) {
                imgAvt = (CircleImageView) itemView.findViewById(R.id.img_avt);
            }

        }
    }

    @Override
    public int getItemViewType(int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (mData.get(position).getSender().equals(firebaseUser.getUid())) {
            flag = false;
            return MSG_RIGHT;
        }else {
            flag = true;
            return MSG_LEFT;
        }
    }
}
