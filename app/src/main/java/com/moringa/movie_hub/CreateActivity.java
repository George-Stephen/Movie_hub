package com.moringa.movie_hub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
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

import butterknife.BindView;
import butterknife.ButterKnife;

public class CreateActivity extends AppCompatActivity implements View.OnClickListener {
    @BindView(R.id.login_text) TextView mLoginText;
    @BindView(R.id.new_name) EditText mNewName;
    @BindView(R.id.new_email)EditText mNewEmail;
    @BindView(R.id.new_password)EditText mNewPassword;
    @BindView(R.id.confirm_password)EditText mConfirmPassword;
    @BindView(R.id.create_button) Button mCreateButton;

    private static final String TAG = CreateActivity.class.getSimpleName();
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);
        ButterKnife.bind(this);

        mLoginText.setOnClickListener(this);
        mCreateButton.setOnClickListener(this);

        mAuth  = FirebaseAuth.getInstance();
        createAuthStateListener();
        createAuthProgressDialog();
    }
    @Override
    public void onClick(View v) {
        if (v == mLoginText){
            Intent intent = new Intent(CreateActivity.this,LoginActivity.class);
            startActivity(intent);
            finish();
        }
        if(v == mCreateButton){
            createUser();
        }
    }
    // adding progress dialog
    private  void  createAuthProgressDialog(){
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("Creating account");
        mProgressDialog.setMessage("LOADING....");
        mProgressDialog.setCancelable(false);
    }
    // create function
    private void createUser(){
        String name = mNewName.getText().toString().trim();
        String email =  mNewEmail.getText().toString().trim();
        String password = mNewPassword.getText().toString().trim();
        String confirmPassword = mConfirmPassword.getText().toString().trim();

        boolean validEmail = isValidEmail(email);
        boolean validName = isValidName(name);
        boolean validPassword = isValidPassword(password,confirmPassword);

        if( !validEmail || !validName || !validPassword)
            return;

        mProgressDialog.show();

        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                mProgressDialog.dismiss();
                if(task.isSuccessful()){
                    Toast.makeText(CreateActivity.this,"Your account is complete",Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(CreateActivity.this,"Sorry, this account is Unavailable", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    // authentication listener
    private void createAuthStateListener(){
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user =  firebaseAuth.getCurrentUser();
                if(user != null){
                    Intent intent  = new Intent(CreateActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            }
        };
    }
    // on start and stop methods
    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null){
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
    // validation
    private boolean isValidEmail(String email){
        boolean isGoodEmail = (email != null && Patterns.EMAIL_ADDRESS.matcher(email).matches());
        if(!isGoodEmail){
            mNewEmail.setError("Please enter a valid email address");
            return false ;
        }
        return true;
    }
    private boolean isValidName(String name){
        if(name.equals("")){
            mNewName.setError("Please enter your name");
            return false;
        }
        return true;
    }
    private boolean isValidPassword(String password,String confirm){
        if (password.length() < 6 ){
            mNewPassword.setError("Please create a password with more than 6 letters");
            return false;
        } else if(!(password.equals(confirm))){
            mNewPassword.setError("These passwords don't match");
            return false;
        }
        return true;
    }
}