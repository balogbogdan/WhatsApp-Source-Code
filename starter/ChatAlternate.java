package com.parse.starter;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import static com.parse.starter.R.id.editText;

public class ChatAlternate extends AppCompatActivity {

    String activeUser = "";
    ArrayList<String> messages = new ArrayList<>();

    // example of sending messages; (send chat will be an onclick function for
    //the "send" button

    public void sendChat(View view){
        ParseObject message = new ParseObject("Message");
        message.put("sender", ParseUser.getCurrentUser().getUsername());
        message.put("recipient", activeUser);
        message.put("message", editText.getText().toString());
        editText.setText(""); //to clear after typing message
        message.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                messages.add(editText.getText().toString());
            }
        });
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_alternate);

        activeUser = getIntent().getStringExtra("username");

        // add listview array adapter etc to show messages

        //below illustrates running an "or" parsequery, to find whether one thing
        //has occured or the other. i.e was I the sender or recipient?

        ParseQuery<ParseObject> query1 = new ParseQuery<ParseObject>("Message");
        query1.whereEqualTo("sender",ParseUser.getCurrentUser().getUsername());
        query1.whereEqualTo("recipient",activeUser);

        ParseQuery<ParseObject> query2 = new ParseQuery<ParseObject>("Message");
        query2.whereEqualTo("sender",activeUser);
        query2.whereEqualTo("recipient",ParseUser.getCurrentUser().getUsername());

        List<ParseQuery<ParseObject>> queries = new ArrayList<>();

        queries.add(query1);
        queries.add(query2);

        ParseQuery<ParseObject> query = ParseQuery.or(queries);
        query.orderByAscending("createdAt");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public String done(List<ParseObject> objects, ParseException e) {
                if (objects.size()>0){
                    for (ParseObject message : objects){
                        String messageContent = message.getString("sender");
                       if (!message.getString("sender").equals(ParseUser.getCurrentUser().getUsername()))
                       {
                           messageContent = "> " + messageContent;
                       }
                       messages.add(messageContent);
                    }
                    //notifydatasetchanged etc.
                }
                return null;
            }
        });
    }
}
