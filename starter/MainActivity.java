/*
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.parse.starter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.util.List;


public class MainActivity extends AppCompatActivity implements View.OnKeyListener,View.OnClickListener {

  EditText username;
  EditText password;
  Button button;
  TextView textView;
  ImageView imageView;
  RelativeLayout relativeLayout;



  public void showUserList(){



    Intent intent = new Intent(getApplicationContext(), UserListActivity.class);
    startActivity(intent);


  }


  @Override
  public void onClick(View view) {


    if (view.getId() == R.id.relativeLayout ) {

      InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
      inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),0);



    }
  }


  @Override
  public boolean onKey(View view, int i, KeyEvent keyEvent) {

    if (i == KeyEvent.KEYCODE_ENTER && keyEvent.getAction() == keyEvent.ACTION_DOWN) {

      ParseQuery<ParseUser> query = ParseUser.getQuery();
      query.whereEqualTo("username", username.getText().toString());
      query.findInBackground(new FindCallback<ParseUser>() {
        @Override
        public String done(List<ParseUser> objects, ParseException e) {



          if (objects == null) {

            signUp();

          } else {

            logIn();

          }


          return null;
        }
      });



    }

    return false;
  }

  public void onSwitch(View view){

    if (button.getText().equals("Sign up")) {


      button.setText("Log in");
      textView.setText("or Sign up");

    } else {

      button.setText("Sign up");
      textView.setText("or Log in");

    }

  }

  public void onPress(View view) {


    if (username.getText().toString().matches("") || password.getText().toString().matches("") ){

      Toast.makeText(this, "Please Enter a valid Username / Password", Toast.LENGTH_SHORT).show();



    } else {

      if (button.getText().equals("Sign up")){

        signUp();
          Log.i("unique", "test");

      } else {

        logIn();

      }


    }



  }

  public void logIn(){

    ParseUser.logInInBackground(username.getText().toString(),password.getText().toString(), new LogInCallback() {
      @Override
      public String done(ParseUser user, ParseException e) {




        if (user != null){


          Toast.makeText(MainActivity.this, "Log in Successful", Toast.LENGTH_SHORT).show();
          showUserList();

        } else {

          Toast.makeText(MainActivity.this, "Log in unsuccessful: " + e.getMessage(), Toast.LENGTH_SHORT).show();

        }

        return null;
      }
    });



  }

  public void signUp(){

    final ParseUser user = new ParseUser();
    user.setUsername(username.getText().toString());
    user.setPassword(password.getText().toString());

    user.signUpInBackground(new SignUpCallback() {
      @Override
      public void done(ParseException e) {

        if (e == null){

          Toast.makeText(MainActivity.this, "Sign up Successful", Toast.LENGTH_SHORT).show();


          ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {

              if (e==null){

                Toast.makeText(MainActivity.this, " sign up successful", Toast.LENGTH_SHORT).show();
                ParseObject parseObject = new ParseObject("UserID");
                parseObject.put("username",ParseUser.getCurrentUser().getUsername());
                parseObject.saveEventually();

              } else {

                Toast.makeText(MainActivity.this,  "Unsuccessful " + e.getMessage(), Toast.LENGTH_SHORT).show();
              }



            }
          });

          showUserList();

        } else {

          Toast.makeText(MainActivity.this, "Sign up Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

      }
    });


  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);



    setTitle("Twitter Clone");
    username = (EditText)findViewById(R.id.username);
    password = (EditText)findViewById(R.id.password);
    button = (Button)findViewById(R.id.button);
    textView = (TextView)findViewById(R.id.textView);

    relativeLayout = (RelativeLayout)findViewById(R.id.relativeLayout);

    relativeLayout.setOnClickListener(this);

    password.setOnKeyListener(this);
      ParseUser.getCurrentUser().logOut();
   /* if (ParseUser.getCurrentUser() != null){

      showUserList();

    }*/




    ParseAnalytics.trackAppOpenedInBackground(getIntent());
  }



}
