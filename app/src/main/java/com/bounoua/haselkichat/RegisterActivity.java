package com.bounoua.haselkichat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegisterActivity extends AppCompatActivity {

    private EditText emailEdit,passwordEdit, userNameEdit;
    private CircleImageView imageView;
    private Button registerBtn;
    private Uri imageUri;
    private boolean imageChoosing;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase database;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initViews();
        progressBar.setVisibility(View.INVISIBLE);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageChooser();
            }
        });
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userName = userNameEdit.getText().toString();
                String email = emailEdit.getText().toString();
                String password = passwordEdit.getText().toString();
                createNewAccount(userName,email,password);
            }
        });
        TextView haveAccount = findViewById(R.id.haveAccount);
        haveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
            }
        });
    }

    private void createNewAccount(String name,String email,String password) {
        if (TextUtils.isEmpty(name)) {
            userNameEdit.setError("Enter your user name please");
            return;
        }else if (TextUtils.isEmpty(email)) {
            emailEdit.setError("Enter your email please");
            return;
        }else if (TextUtils.isEmpty(password)) {
            passwordEdit.setError("Enter your password please");
            return;
        }else {
            progressBar.setVisibility(View.VISIBLE);
            firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    progressBar.setVisibility(View.INVISIBLE);
                    if (task.isSuccessful()) {
                        database.getReference().child("Users").child(firebaseAuth.getUid()).child("userName")
                                .setValue(name);
                        sendImageStorage();
                        Toast.makeText(RegisterActivity.this, "Account created successfully", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
                    }else {
                        Toast.makeText(RegisterActivity.this,task.getResult().toString(),Toast.LENGTH_SHORT);
                    }
                }
            });
        }
    }
/**
 * store the image in the storageDB with unique name(UUID)
 * get the image path to store it in the real db like image user
 * **/
    private void sendImageStorage() {
        UUID uniqueID = UUID.randomUUID();
        String imageName = "Images/" +uniqueID + ".jpg";
        firebaseStorage.getReference().child(imageName).putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                StorageReference myReference = firebaseStorage.getReference().child(imageName);
                myReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String filePath = uri.toString();
                        database.getReference().child("Users").child(firebaseAuth.getUid())
                                .child("image").setValue(filePath);
                    }
                });
            }
        });
    }
/**
 * chose the image from the phone
 * **/
    private void imageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,1);
    }
/**
 * control the chosen image by the user
 * the user can chose image or not
 * **/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            Picasso.get().load(imageUri).into(imageView);
            imageChoosing = true;
        }else {
            imageChoosing = false;
        }
    }

    private void initViews() {
        imageView = findViewById(R.id.profileImage);
        passwordEdit = findViewById(R.id.passwordRegister);
        userNameEdit = findViewById(R.id.userNameRegister);
        emailEdit = findViewById(R.id.emailRegister);
        registerBtn = findViewById(R.id.buttonRegister);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        progressBar = findViewById(R.id.progressBarRegister);
        database = FirebaseDatabase.getInstance();
    }
}