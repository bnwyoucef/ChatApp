package com.bounoua.haselkichat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText passwordEditTxt, emailEditTxt;
    private TextView forgetPassword, createAccount;
    private Button loginBtn;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog loader;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initViews();
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleLoginOperation();
            }
        });
        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
            }
        });
        forgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ResetPassword.class));
            }
        });
    }

    private void initViews() {
        passwordEditTxt = findViewById(R.id.userNameLogin);
        emailEditTxt = findViewById(R.id.emailLogin);
        forgetPassword = findViewById(R.id.forgetPasswordLogin);
        createAccount = findViewById(R.id.createAccountLogin);
        loginBtn = findViewById(R.id.loginButton);
        firebaseAuth = FirebaseAuth.getInstance();
        loader = new ProgressDialog(this);
    }

    private void handleLoginOperation() {
        String email = emailEditTxt.getText().toString();
        String password = passwordEditTxt.getText().toString();
        if (TextUtils.isEmpty(email)) {
            emailEditTxt.setError("Enter your email");
            return;
        }else if (TextUtils.isEmpty(password)){
            passwordEditTxt.setError("Enter your password");
            return;
        }else {
            loader.setTitle("Log in");
            loader.setMessage("Log in ...");
            loader.setCanceledOnTouchOutside(false);
            loader.show();
            firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        startActivity(new Intent(LoginActivity.this,MainActivity.class));
                        finish();
                    }else {
                        Toast.makeText(LoginActivity.this, "Email or password was wrong", Toast.LENGTH_SHORT).show();
                    }
                    loader.dismiss();
                }
            });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            startActivity(new Intent(LoginActivity.this,MainActivity.class));
        }
    }
}