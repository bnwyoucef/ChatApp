package com.bounoua.haselkichat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import static com.bounoua.haselkichat.UserAdapter.RECEIVER_ID;
import static com.bounoua.haselkichat.UserAdapter.RECEIVER_NAME;
import static com.bounoua.haselkichat.UserAdapter.SENDER_ID;

public class ChatActivity extends AppCompatActivity {

    private TextView otherUserTextView;
    private ImageView backArrow;
    private RecyclerView recyclerView;
    private EditText messageEditText;
    private FloatingActionButton sendBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        initViews();
        Intent intent = getIntent();
        if (null != intent) {
            String currentUserID = intent.getStringExtra(SENDER_ID);
            String otherUserID = intent.getStringExtra(RECEIVER_ID);
            String otherUserName = intent.getStringExtra(RECEIVER_NAME);
            otherUserTextView.setText(otherUserName);
            sendBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendMessage(currentUserID,otherUserID,otherUserName);
                    messageEditText.setText("");
                }
            });
        }
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ChatActivity.this,MainActivity.class));
                finish();
            }
        });
    }

    /**
     * to send message first we need to register it to the database with the userId of the sender
     * and the receiver and a key we will use it when modified a message that message need to
     * update in the two users messages
     * **/
    private void sendMessage(String currentUserID, String otherUserID, String otherUserName) {
        String msg = messageEditText.getText().toString();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        /**
         * we need this key when we remove a message from a user the message will
         * be deleted from the other user too
         * **/
        String key = reference.child("Messages").child(currentUserID).child(otherUserID).push().getKey();
        HashMap<String,String> msgInformation = new HashMap<>();
        msgInformation.put("message",msg);
        msgInformation.put("from",currentUserID);
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
    }
}