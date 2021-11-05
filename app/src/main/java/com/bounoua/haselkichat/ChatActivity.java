package com.bounoua.haselkichat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.bounoua.haselkichat.UserAdapter.RECEIVER_ID;
import static com.bounoua.haselkichat.UserAdapter.RECEIVER_NAME;
import static com.bounoua.haselkichat.UserAdapter.SENDER_ID;

public class ChatActivity extends AppCompatActivity implements MsgAdapter.RemoveMessageCallBack {

    private TextView otherUserTextView;
    private ImageView backArrow;
    private RecyclerView recyclerView;
    private EditText messageEditText;
    private FloatingActionButton sendBtn;
    private DatabaseReference reference;
    private String currentUserID;
    private String otherUserID;
    private String otherUserName;
    private ArrayList<MessageModel> messagesList;
    private MsgAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        initViews();
        adapter.setMsgList(messagesList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Intent intent = getIntent();
        if (null != intent) {
            currentUserID = intent.getStringExtra(SENDER_ID);
            otherUserID = intent.getStringExtra(RECEIVER_ID);
            otherUserName = intent.getStringExtra(RECEIVER_NAME);
            otherUserTextView.setText(otherUserName);
            sendBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!messageEditText.getText().toString().equals("")) {
                        sendMessage(currentUserID, otherUserID, otherUserName);
                        messageEditText.setText("");
                    }
                }
            });
        }
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ChatActivity.this, MainActivity.class));
                finish();
            }
        });


        /**
         * get all the user messages
         * **/
        reference.child("Messages").child(currentUserID).child(otherUserID).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                MessageModel model = snapshot.getValue(MessageModel.class);
                model.setKeyUnique(snapshot.getKey());
                messagesList.add(model);
                adapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(messagesList.size() - 1);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    /**
     * to send message first we need to register it to the database with the userId of the sender
     * and the receiver and a key we will use it when modified a message that message need to
     * update in the two users messages
     **/
    private void sendMessage(String currentUserID, String otherUserID, String otherUserName) {
        String msg = messageEditText.getText().toString();
        /**
         * we need this key when we remove a message from a user the message will
         * be deleted from the other user too
         * **/
        String key = reference.child("Messages").child(currentUserID).child(otherUserID).push().getKey();
        HashMap<String, String> msgInformation = new HashMap<>();
        msgInformation.put("message", msg);
        msgInformation.put("from", currentUserID);
        reference.child("Messages").child(currentUserID).child(otherUserID).child(key).setValue(msgInformation)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        reference.child("Messages").child(otherUserID).child(currentUserID).child(key).setValue(msgInformation);
                    }
                });
    }

    private void initViews() {
        backArrow = findViewById(R.id.chatArrowBack);
        otherUserTextView = findViewById(R.id.chatOtherUserName);
        recyclerView = findViewById(R.id.chatRecyclerView);
        messageEditText = findViewById(R.id.chatMessageEditTxt);
        sendBtn = findViewById(R.id.chatSendMessage);
        reference = FirebaseDatabase.getInstance().getReference();
        messagesList = new ArrayList<>();
        adapter = new MsgAdapter(this);
    }

    @Override
    public void deleteMsg(String key) {
        reference.child("Messages").child(currentUserID).child(otherUserID).child(key).removeValue();
        reference.child("Messages").child(otherUserID).child(currentUserID).child(key).removeValue();
        Toast.makeText(this, "removed", Toast.LENGTH_SHORT).show();
        for (int i=0; i< messagesList.size(); i++) {
            if (messagesList.get(i).getKeyUnique().equals(key)) {
                messagesList.remove(i);
            }
        }
        adapter.notifyDataSetChanged();
    }
}