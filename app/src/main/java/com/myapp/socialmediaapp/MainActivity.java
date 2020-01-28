package com.myapp.socialmediaapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethod;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity implements  View.OnClickListener,View.OnKeyListener{

    boolean loginActionActive = true;
    TextView switchTextView;
    EditText usernameEditText;
    EditText passwordEditText;
    FirebaseAuth mAuth;

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {

        //Login when enter key pressed
        if(keyCode == KeyEvent.KEYCODE_ENTER && KeyEvent.ACTION_DOWN == event.getAction()) {
            loginClick(v);
        }

        return false;
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.switchTextView) {
            Button button = findViewById(R.id.button);
            TextView actionTextView = findViewById(R.id.actionTextView);

            if(loginActionActive) {
                //Switch to sign up mode
                loginActionActive = false;
                button.setText("SIGN-UP");
                switchTextView.setText("Login here");
                actionTextView.setText("SIGN-UP");
            }else{
                //Switch to login mode
                loginActionActive = true;
                button.setText("LOGIN");
                switchTextView.setText("Sign-up here");
                actionTextView.setText("LOGIN");
            }
        } else if(v.getId() == R.id.constraintLayout) {
            //Hide Keyboard if user clicks on background
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),0);
        }
    }

    public  void loginClick(View view) {

        //Check if EditText Empty
        if(usernameEditText.getText().toString().equals("") || passwordEditText.getText().toString().equals("")) {
            Toast.makeText(this, "Please enter Username and Password", Toast.LENGTH_SHORT).show();
        }else{

            final String username = usernameEditText.getText().toString();
            String password = passwordEditText.getText().toString();

            if(loginActionActive) {
                //LOGIN
                mAuth.signInWithEmailAndPassword(username,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            login();
                        }else {
                            Toast.makeText(MainActivity.this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }else{
                //SIGN-UP
                mAuth.createUserWithEmailAndPassword(username,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            //Add user to Database
                            FirebaseDatabase.getInstance().getReference().child("Users").child(task.getResult().getUser().getUid().toString()).child("email").setValue(username);
                            //Login
                            login();
                        }else{
                            Toast.makeText(MainActivity.this, "Signup Fail", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
    }

    public void login() {
        //Move to next Activity
        Intent intent = new Intent(getApplicationContext(),FeedActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

         usernameEditText = findViewById(R.id.usernameEditText);
         passwordEditText = findViewById(R.id.passwordEditText);

         //Check if user already logged in
        if(mAuth.getCurrentUser() != null){
            login();
        }

        //onClick and onKey Listners
        ConstraintLayout constraintLayout = findViewById(R.id.constraintLayout);
        constraintLayout.setOnClickListener(this);

        passwordEditText = findViewById(R.id.passwordEditText);
        passwordEditText.setOnKeyListener(this);

        switchTextView = findViewById(R.id.switchTextView);
        switchTextView.setOnClickListener(this);
    }
}
