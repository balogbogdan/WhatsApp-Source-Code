package com.parse.starter;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.text.Layout;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseSession;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.starter.utility.CustomAdapter;
import com.parse.starter.utility.Map3d;
import com.parse.starter.utility.Serializer;
import com.parse.starter.utility.SessionIdentifierGenerator;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserListActivity extends AppCompatActivity {

    ArrayList<String> friendsSelected;
    Bitmap image;
    String title = "Your new conversation\n";
    String image_placeholder = "Add an image";
    String message = "Start talking";
    ArrayList<Map3d> map3dArray;
    ListView listView;
    List<String> stringIDs;
    Gson gson = new Gson();
    CustomAdapter customAdapter;
    FloatingActionButton add;

   private void idQuery(final String username, final String id, final boolean setAddConvo){
       stringIDs.clear();
       ParseQuery<ParseObject> query = ParseQuery.getQuery("UserID");
       query.whereEqualTo("objectId", "SvqTloJHMf");

       query.findInBackground(new FindCallback<ParseObject>() {
           @Override
           public String done(List<ParseObject> objects, ParseException e) {
               if (e==null){
                   Log.i("rat", username);
                   if (objects.size()>0){
                       Log.i("fox", username);
                       stringIDs= objects.get(0).getList("IDs");

                       if ( stringIDs == null){
                           objects.get(0).put("IDs", new ArrayList<>());
                           stringIDs = new ArrayList<String>();
                           objects.get(0).saveEventually();
                       }
                       if (id != null) {
                           stringIDs.add(id);
                           objects.get(0).put("IDs", stringIDs);
                           objects.get(0).saveEventually();
                       }
                       if (!setAddConvo && !stringIDs.isEmpty()){
                           conversationQuery();
                       }
                   }
               }
               return null;
           }
       });
   }

    public void createConvo(View view){
        SessionIdentifierGenerator randomId = new SessionIdentifierGenerator();
        final String objectId = randomId.nextSessionId();
        addFriendsID(objectId);

        map3dArray.add(new Map3d(title,image_placeholder,message));
        if (!friendsSelected.contains(ParseUser.getCurrentUser().getUsername())) {
            friendsSelected.add(ParseUser.getCurrentUser().getUsername());
        }
        final List<String> users = new ArrayList<>();
        idQuery(ParseUser.getCurrentUser().getUsername(),objectId,true);


        ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e==null){

                    final ParseObject messaging = new ParseObject("Messaging");
                    Map3d map3d = new Map3d(title,image_placeholder,message);
                    String jsonString = gson.toJson(map3d);
                    messaging.put("map3d", jsonString);
                    messaging.put("usernames", users);
                    messaging.put("usernames", friendsSelected);
                    messaging.put("Id", objectId);





                    messaging.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e==null){

                                Toast.makeText(UserListActivity.this, "Success", Toast.LENGTH_SHORT).show();
                                implementAdapter(map3dArray);
                            } else {
                                Toast.makeText(UserListActivity.this, "Unsuccessful, please try again " + e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });


    }

    private void addFriendsID(String id) {

        for ( String friend : friendsSelected){
            Log.i("friendsselected", friend);
            idQuery(friend,id,true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);
       
        stringIDs = new ArrayList<>();
        map3dArray = new ArrayList<>();
        listView = (ListView)findViewById(R.id.listView);
        final ArrayList<String> friends = new ArrayList<>();
        friendsSelected = new ArrayList<>();
        add = (FloatingActionButton) findViewById(R.id.addConvo);
        idQuery(ParseUser.getCurrentUser().getUsername(),null,false);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        final Dialog dialog = new Dialog(UserListActivity.this);
        dialog.setContentView(R.layout.popup);
        final ListView addFriendsListView = (ListView) dialog.findViewById(R.id.listView);
        addFriendsListView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);

        TextView textView = (TextView) dialog.findViewById(R.id.textView);
        textView.setText("Add friends to a conversation");
        friends.clear();
        friendsSelected.clear();



        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ParseQuery<ParseUser> query = ParseUser.getQuery();
                query.whereNotEqualTo("username", ParseUser.getCurrentUser().getUsername());
                query.findInBackground(new FindCallback<ParseUser>() {
                    @Override
                    public String done(List<ParseUser> objects, ParseException e) {
                        if (e == null) {

                            if (objects.size() > 0) {
                                friends.clear();

                                for (ParseUser user : objects) {
                                    friends.add(user.getUsername());
                                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(UserListActivity.this, android.R.layout.
                                            simple_list_item_checked, friends);
                                    addFriendsListView.setAdapter(arrayAdapter);
                                    arrayAdapter.notifyDataSetChanged();

                                }
                            } else {
                                Toast.makeText(UserListActivity.this, "You have no friends", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(UserListActivity.this, "Unable to add friends " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                        return null;
                    }
                });
                dialog.show();
                addFriendsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        CheckedTextView checkedTextView = (CheckedTextView) view;

                        if (checkedTextView.isChecked()) {
                            if (friendsSelected != null) {
                                if (!friendsSelected.contains(friends.get(position))) {
                                    friendsSelected.add(friends.get(position));
                                }
                            }
                        } else {
                            friendsSelected.remove(friendsSelected.indexOf(friends.get(position)));
                        }
                    }
                });
            }
        });
    }

    private void conversationQuery() {

            for(int i=0;i<stringIDs.size();i++){
                Log.i("mole", stringIDs.get(i));
                ParseQuery<ParseObject> query = ParseQuery.getQuery("Messaging");
                final String finalI = stringIDs.get(i);
                query.whereEqualTo("Id", finalI);

                query.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public String done(List<ParseObject> objects, ParseException e) {
                        if (e==null){

                            if(objects != null){
                                Log.i("red", "works");
                                if (!objects.isEmpty()){
                                    Log.i("blue", "works");
                                    if (objects.get(0).getString("map3d") != null) {

                                        Log.i("liam", "works");
                                            String object = objects.get(0).getString("map3d");

                                            extractMap3d(object);
                                    }
                                    if (objects.get(0).getParseFile("image") != null) {
                                        String object = objects.get(0).getString("map3d");
                                        extractImage(objects);
                                    }
                                }}
                        }
                        return null;
                    }
                });

            }
        }


    private void extractMap3d(String object) {
        Map3d map3d = gson.fromJson(object, Map3d.class);
        map3dArray.add(map3d);
        implementAdapter(map3dArray);
    }

    private void extractImage(List<ParseObject> objects) {


            final Bitmap[] image = new Bitmap[1];
            ParseFile parseFile = objects.get(0).getParseFile("image");
            parseFile.getDataInBackground(new GetDataCallback() {
                @Override
                public String done(byte[] data, ParseException e) {
                    if (e == null) {
                        if (data != null) {
                            image[0] = BitmapFactory.decodeByteArray(data, 0, 0);
                        }
                    }
                    return null;
                }
            });

        implementAdapter(map3dArray);
    }

    private void implementAdapter(final ArrayList<Map3d> map3dArray) {
        customAdapter = new CustomAdapter(getApplicationContext(),R.layout.simple_adapter,
                map3dArray);
        listView.setAdapter(customAdapter);
        customAdapter.notifyDataSetChanged();
        Log.i("retry","works");
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(UserListActivity.this,Conversation.class);
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

              add.hide();

              final Button button = (Button)view.findViewById(R.id.button);
              button.setVisibility(View.VISIBLE);
              button.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View v) {

                      Log.i("position", Integer.toString(position));
                      removeFromParse(position);
                      map3dArray.remove(position);
                      customAdapter.notifyDataSetChanged();
                      add.show();
                      button.setVisibility(View.INVISIBLE);
                      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                          add.setAlpha(0.5f);
                      }
                  }
              });

                return false;
            }
        });
    }

    private void removeFromParse(final int position) {
        Log.i("matchID count",Integer.toString(stringIDs.size()));
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Messaging");
        query.whereEqualTo("Id", stringIDs.get(position));
        Log.i("dan",stringIDs.get(position));
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public String done(List<ParseObject> objects, ParseException e) {
                if (e==null){
                    if (objects != null){
                        if (objects.size()>0){
                            Log.i("jerry","1");
                        List<String> users = objects.get(0).getList("usernames");
                        Log.i("bill", Integer.toString(users.indexOf(ParseUser.getCurrentUser().getUsername())));
                        users.remove(users.indexOf(ParseUser.getCurrentUser().getUsername()));
                        objects.get(0).put("usernames",users);


                       objects.get(0).saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e==null){
                                   removeID(stringIDs.get(position));
                                    stringIDs.remove(position);
                                    Toast.makeText(UserListActivity.this, "Conversation deleted", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(UserListActivity.this, "Unsuccessfull " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }}
                }
                return null;
            }
        });
    }

    private void removeID(final String id) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("UserID");
        query.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public String done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    if (objects.size() > 0) {
                        List<String> userIDs = objects.get(0).getList("IDs");
                        userIDs.remove(userIDs.indexOf(id));
                        objects.get(0).put("IDs",userIDs);
                        objects.get(0).saveEventually();

                    } else {
                        Log.i("removeID", e.getMessage());
                    }

                }
                return null;
            }
        });

        }



}

