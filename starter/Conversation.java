package com.parse.starter;

import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Conversation extends AppCompatActivity implements View.OnKeyListener,
         TextView.OnEditorActionListener {

    public static ArrayList<Map<String,String>> mapArray = new ArrayList<>();
    EditText editText;
    public static String objectId = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);

        Intent intent = getIntent();
        objectId = intent.getStringExtra("objectId");
        ListView listView = (ListView)findViewById(R.id.listView);
        editText = (EditText)findViewById(R.id.editText);
        ConstraintLayout layout = (ConstraintLayout)findViewById(R.id.layout);
        editText.requestFocus();
        editText.setOnEditorActionListener(this);

    }




    public void showKeyboard(View view){


        InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == event.ACTION_DOWN ){
            //sendMessage();
        }
        return false;
    }


    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId==EditorInfo.IME_ACTION_SEND){
            final Map<String,String> map = new HashMap<>();

            map.put("message",editText.getText().toString());
            map.put("user",ParseUser.getCurrentUser().getUsername());




            ParseQuery<ParseObject> query = ParseQuery.getQuery("Messaging");
            query.whereEqualTo("Id", objectId);
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public String done(List<ParseObject> objects, ParseException e) {
                    if (e==null){
                        if (objects != null){
                          if (!objects.isEmpty()){
                            if (objects.get(0).getList("messageArray") != null){
                                objects.get(0).add("messageArray",map);
                                objects.get(0).saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if (e==null){
                                            Toast.makeText(Conversation.this, "Message delivered first", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(Conversation.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            } else {
                                Toast.makeText(Conversation.this, "array not added", Toast.LENGTH_SHORT).show();
                                objects.get(0).put("messageArray",mapArray);
                                objects.get(0).add("messageArray",map);
                                objects.get(0).saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if (e==null){
                                            Toast.makeText(Conversation.this, "Message delivered", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(Conversation.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        }else{
                              Toast.makeText(Conversation.this, "Object is empty", Toast.LENGTH_SHORT).show();
                          }
                        } else {
                            Toast.makeText(Conversation.this, "Object is null", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(Conversation.this, "first error  "+ e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    return null;
                }
            });




            return true;
        }
        return false;
    }
}
