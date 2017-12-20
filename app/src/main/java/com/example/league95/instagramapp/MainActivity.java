package com.example.league95.instagramapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnKeyListener {


    boolean signUpState = true;
    EditText user, pass;
    Button btn;
    TextView textView;
    RelativeLayout relativeLayout;
    ImageView imageView;
    //When app is terminated, log user out!

    //Make an Onclick select our text view only
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.changeSignUp) {
            if (signUpState) {
                btn.setText("Login");
                textView.setText("Or, Sign up");
                signUpState = false;
            } else {
                signUpState = true;
                btn.setText("Sign Up");
                textView.setText("Or, Login");
            }
        } else if (v.getId() == R.id.relativeLayout || v.getId() == R.id.logoImage) {
            //Hide keyboard!
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

        }
    }

    public void signUp(View v) {


        if (user.getText().toString().isEmpty() || pass.getText().toString().isEmpty()) {
            Toast.makeText(this, "User/Pass required", Toast.LENGTH_SHORT).show();
        } else {
            if (signUpState) {
                //Create a new user name and pass!!
                ParseUser user1 = new ParseUser();
                //Create them here
                user1.setUsername(user.getText().toString());
                user1.setPassword(pass.getText().toString());
                //then sign up the user!
                user1.signUpInBackground(new SignUpCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            Log.i("Sign Up", "Successful");
                            populateList();
                        } else {
                            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            } else {
                ParseUser.logInInBackground(user.getText().toString(), pass.getText().toString(), new LogInCallback() {
                    @Override
                    public void done(ParseUser user, ParseException e) {
                        if (user != null) {
                            Log.i("Login", "Successful");
                            populateList();
                        } else {
                            if (e != null) {
                                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(MainActivity.this, "Login Fail", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
            }
        }
        clear();
    }

    public void clear() {
        user.setText("");
        pass.setText("");

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        relativeLayout = findViewById(R.id.relativeLayout);
        imageView = findViewById(R.id.logoImage);

        relativeLayout.setOnClickListener(this);
        imageView.setOnClickListener(this);

        user = findViewById(R.id.user);
        pass = findViewById(R.id.pass);
        clear();
        btn = findViewById(R.id.signUp);
        pass.setOnKeyListener(this);

        textView = findViewById(R.id.changeSignUp);

        textView.setOnClickListener(this);

        //if the user is already logged in
        if (ParseUser.getCurrentUser() != null) {
            populateList();
        }

        ParseAnalytics.trackAppOpenedInBackground(getIntent());
    }


    @Override
    public boolean onKey(View view, int i, KeyEvent keyEvent) {
        if (i == KeyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
            signUp(view);
        }
        return false;
    }

    public void populateList() {
        Intent intent = new Intent(MainActivity.this, ListActivity.class);
        startActivity(intent);
    }
}
