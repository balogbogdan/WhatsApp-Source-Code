package com.parse.starter.utility;


import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.starter.R;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class CustomAdapter extends ArrayAdapter<Map3d> {

    private ArrayList<Map3d> dataSet;
    Context mContext;
    String ID = "";


    public CustomAdapter(@NonNull Context context, @LayoutRes int resource,ArrayList<Map3d> data) {
        super(context, com.parse.starter.R.layout.simple_adapter,data);
        this.dataSet = data;
        mContext = context;
    }


    public class ViewHolder{
        TextView title;
        TextView message;
        ImageView imageView;
        TextView image_placeholder;

    }



    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Map3d map3d = (Map3d) getItem(position);

        ViewHolder viewHolder;
        if (convertView == null){
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(com.parse.starter.R.layout.simple_adapter,parent,false);
            viewHolder.title = (TextView)convertView.findViewById(R.id.title);
            viewHolder.imageView = (ImageView)convertView.findViewById(R.id.imageView);
            viewHolder.message = (TextView)convertView.findViewById(R.id.message);
            viewHolder.image_placeholder = (TextView)convertView.findViewById(R.id.placeHolder);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
            viewHolder.title.setText(map3d.getTitle());
            viewHolder.message.setText(query(position));
        if (map3d.getImage() != null) {
            viewHolder.image_placeholder.setVisibility(View.INVISIBLE);
            viewHolder.imageView.setImageBitmap(map3d.getImage());
        }

        return convertView;
    }

    private String query(int position) {
        ID = IDquery(position);
        final String[] lastMessage = {""};
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Messaging");
        query.whereEqualTo("Id",ID);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public String done(List<ParseObject> objects, ParseException e) {
                if (e==null){
                   if (objects != null){
                    if (!objects.isEmpty()){
                        lastMessage[0] = objects.get(0).getList("messageArray").get(objects.get(0).
                                getList("messageArray").size()-1).toString();
                    }}
                }
                return null;
            }
        });
        return lastMessage[0];
    }

    private String IDquery(final int position) {
        final String[] ID = {""};
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("username",ParseUser.getCurrentUser().getUsername());
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public String done(List<ParseUser> objects, ParseException e) {
                if (e==null){
                    if (objects != null){
                        if (!objects.isEmpty()) {
                            if (objects.get(0).getList("IDs") != null){
                            ID[0] = (String) objects.get(0).getList("IDs").get(position);
                        }
                        }
                    }
                        }
                return null;
            }
        });
        return ID[0];
    }
}
