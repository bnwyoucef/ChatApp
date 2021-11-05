package com.bounoua.haselkichat;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class MsgAdapter extends RecyclerView.Adapter<MsgAdapter.MsgViewHolder> {
    ArrayList<MessageModel> msgList = new ArrayList<>();
    Context context;
    boolean status;
    public interface RemoveMessageCallBack {
        void deleteMsg(String key);
    }
    RemoveMessageCallBack removeMessageCallBack;

    public MsgAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public MsgViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == 1) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.show_send_msg,parent,false);
        }else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.show_receive_msg, parent, false);
        }
        return new MsgViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MsgViewHolder holder, int position) {
        holder.textView.setText(msgList.get(position).getMessage());
        holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("are you sure to delete this message?")
                        .setTitle("Delete message")
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        removeMessageCallBack = (RemoveMessageCallBack) context;
                        try{
                            removeMessageCallBack.deleteMsg(msgList.get(position).getKeyUnique());
                        }catch (ClassCastException ex) {
                            ex.printStackTrace();
                        }
                    }
                });
                builder.create().show();
                return false;
            }
        });
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
        MaterialCardView cardView;
        public MsgViewHolder(@NonNull View itemView) {
            super(itemView);
            if (status) {
                textView = itemView.findViewById(R.id.userSentMessage);
                cardView = itemView.findViewById(R.id.senderCardView);
            }else {
                textView = itemView.findViewById(R.id.userReceivedMessage);
                cardView = itemView.findViewById(R.id.receiverCardView);
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (msgList.get(position).getFrom().equals(FirebaseAuth.getInstance().getUid())) {
            status = true;
            return 1;
        } else {
            status = false;
            return 2;
        }
    }
}
