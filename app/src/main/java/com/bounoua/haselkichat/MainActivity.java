package com.bounoua.haselkichat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.recyclerView);
        UserAdapter adapter = new UserAdapter(this);
        ArrayList<User> userArrayList = new ArrayList<>();
        adapter.setUsersList(userArrayList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        /**
         * get the list of all users
         * **/
        FirebaseDatabase.getInstance().getReference().child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userArrayList.clear();
                String name ="";
                String imgPath = "";
                User user = new User(name,imgPath);
                for (DataSnapshot ds : snapshot.getChildren()) {
                    if(!FirebaseAuth.getInstance().getUid().equals(ds.getKey())) {
                        name = ds.child("userName").getValue().toString();
                        imgPath = ds.child("image").getValue().toString();
                        user = new User(name,imgPath);
                        userArrayList.add(user);
                    }
                }
                adapter.notifyDataSetChanged();
                //adapter.setUsersList(userArrayList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    /**
     * to show the menu in this activity
     * the second menu in inflate method is by default
     * **/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * to set the action when the user click in one element
     * from the menu list
     * **/
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.profileMenu:
                 startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                break;
            case R.id.signOutMenu:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}