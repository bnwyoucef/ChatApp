package com.bounoua.haselkichat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class MsgAdapter extends RecyclerView.Adapter<MsgAdapter.MsgViewHolder> {
    ArrayList<MessageModel> msgList = new ArrayList<>();
    Context context;
    boolean status;
    int cardNumber;

    public MsgAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public MsgViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (status) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.show_send_msg,parent,false);
        }else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.show_receive_msg, parent, false);
        }
        return new MsgViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MsgViewHolder holder, int position) {
        holder.textView.setText(msgList.get(position).getMessage());
    }

    @Override
    public int getItemCount() {
        return msgList.size();
    }

    public void setMsgList(ArrayList<MessageModel> msgList) {
        this.msgList = msgList;
        notifyDataSetChanged();
    }

    class MsgViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public MsgViewHolder(@NonNull View itemView) {
            super(itemView);
            if (status) {
                textView = itemView.findViewById(R.id.userSentMessage);
            }else {
                textView = itemView.findViewById(R.id.userReceivedMessage);
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (msgList.get(position).getFrom().equals(FirebaseAuth.getInstance().getUid())) {
            status = true;
            cardNumber = 1;
        } else {
            status = false;
            cardNumber = 2;
        }
        return cardNumber;
    }
}
