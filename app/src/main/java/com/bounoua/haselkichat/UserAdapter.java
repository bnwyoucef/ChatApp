package com.bounoua.haselkichat;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolderUser> {
    private Context context;
    private ArrayList<User> usersList;
    public static final String SENDER_ID = "sender";
    public static final String RECEIVER_ID = "receiver";
    public static final String RECEIVER_NAME = "rName";
    public UserAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolderUser onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.show_users, parent, false);
        return new ViewHolderUser(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderUser holder, int position) {
        holder.userName.setText(usersList.get(position).getUserName());
        if (usersList.get(position).getImagePath().equals("null")) {
            holder.imageView.setImageResource(R.drawable.account);
        } else {
            Picasso.get().load(usersList.get(position).getImagePath()).into(holder.imageView);
        }
        holder.parentCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentUserID = FirebaseAuth.getInstance().getUid();
                String otherUserID = usersList.get(position).getUserID();
                String otherUserName = usersList.get(position).getUserName();
                Intent intent = new Intent(context,ChatActivity.class);
                intent.putExtra(SENDER_ID,currentUserID);
                intent.putExtra(RECEIVER_ID,otherUserID);
                intent.putExtra(RECEIVER_NAME,otherUserName);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    public void setUsersList(ArrayList<User> usersList) {
        this.usersList = usersList;
        notifyDataSetChanged();
    }

    class ViewHolderUser extends RecyclerView.ViewHolder {
        private CircleImageView imageView;
        private TextView userName;
        private MaterialCardView parentCard;

        public ViewHolderUser(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.recyclerImageView);
            userName = itemView.findViewById(R.id.recyclerUserName);
            parentCard = itemView.findViewById(R.id.parentCard);
        }
    }
}
