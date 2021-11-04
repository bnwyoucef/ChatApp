package com.bounoua.haselkichat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class UpdateProfileActivity extends AppCompatActivity {

    private CircleImageView circleImageView;
    private EditText userNameEditTxt;
    private Button updateBtn;
    private Uri imageUri;
    private FirebaseStorage storage;
    private FirebaseDatabase database;
    private FirebaseAuth firebaseAuth;
    private boolean imageIsChanged;
    private String nameUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);
        initViews();
        database.getReference().child("Users").child(firebaseAuth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                nameUser = snapshot.child("userName").getValue().toString();
                userNameEditTxt.setText(nameUser);
                String pathImg = snapshot.child("image").getValue().toString();
                if (pathImg.equals("null")) {
                    circleImageView.setImageResource(R.drawable.account);
                } else {
                    Picasso.get().load(pathImg).into(circleImageView);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UpdateProfileActivity.this, error.getDetails(), Toast.LENGTH_SHORT).show();
            }
        });
        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageChooser();
            }
        });
        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile();
                Toast.makeText(UpdateProfileActivity.this, "Updated", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(UpdateProfileActivity.this, ProfileActivity.class));
            }
        });
    }

    /**
     * we update the profile if the user change the image
     * or change the user name
     **/
    private void updateProfile() {
        String name = userNameEditTxt.getText().toString();
        if (TextUtils.isEmpty(name)) {
            userNameEditTxt.setError("Enter your user name please");
            return;
        } else if (!name.equals(nameUser)) {
            database.getReference().child("Users").child(firebaseAuth.getUid()).child("userName").setValue(name);
        }
        if (imageIsChanged) {
            UUID unique = UUID.randomUUID();
            String imageName = "Images/" + unique + ".jpg";
            storage.getReference().child(imageName).putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    storage.getReference().child(imageName).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String imgPath = uri.toString();
                            database.getReference().child("Users").child(firebaseAuth.getUid()).child("image").setValue(imgPath);
                        }
                    });
                }
            });
        }
    }

    private void ImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            Picasso.get().load(imageUri).into(circleImageView);
            imageIsChanged = true;
        } else {
            imageIsChanged = false;
        }
    }

    private void initViews() {
        circleImageView = findViewById(R.id.updateProfileImage);
        userNameEditTxt = findViewById(R.id.updateProfileUserName);
        updateBtn = findViewById(R.id.updateProfileButton);
        firebaseAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        database = FirebaseDatabase.getInstance();
    }
}