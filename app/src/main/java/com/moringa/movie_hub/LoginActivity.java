package com.moringa.movie_hub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{
    @BindView(R.id.login_email) EditText mLoginEmail;
    @BindView(R.id.login_password)EditText mLoginPassword;
    @BindView(R.id.login_button) Button mLoginButton;
    @BindView(R.id.create_button)Button mSignUpButton;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        mAuth = FirebaseAuth.getInstance();
        createAuthStateListener();
        createAuthProgressDialog();

        mSignUpButton.setOnClickListener(this);
        mLoginButton.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        if(v == mSignUpButton){

            Intent intent = new Intent(LoginActivity.this,CreateActivity.class);
            startActivity(intent);
            finish();
        }
        if(v == mLoginButton){
            loginWithPassword();
        }
    }

    private void createAuthProgressDialog(){
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("LOADING ...");
        mProgressDialog.setMessage("Linking with accounts");
        mProgressDialog.setCancelable(false);
    }

    private void loginWithPassword(){
        String email = mLoginEmail.getText().toString();
        String password = mLoginPassword.getText().toString();

        if(email.equals("")){
            mLoginEmail.setError("Please enter your email address");
            return;
        }

        if (password.equals("")){
            mLoginPassword.setError("Please enter your password");
            return;
        }
        mProgressDialog.show();
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                mProgressDialog.dismiss();
                if(task.isSuccessful()){
                    Toast.makeText(LoginActivity.this,"Login complete",Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(LoginActivity.this,"This account does not exist",Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    // auth listener
    private void createAuthStateListener(){
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null){
                    Intent intent = new Intent(LoginActivity.this, CreateActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mAuthListener != null){
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}