package com.parse.starter.utility;


import android.graphics.Bitmap;
import android.widget.ImageView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Map3d implements Serializable {

    private String title;
    private Bitmap image;
    private String message;
    private String image_placeholder;

    public Map3d(String title, Bitmap image, String message){

        this.title = title;
        this.image = image;
        this.message = message;
    }

    public Map3d(String title,String image_placeholder,String message){
        this.title = title;
        this.image_placeholder = image_placeholder;
        this.message = message;
    }

    public String getTitle() {
        return title;
    }

    public Bitmap getImage() {
        return image;
    }


    public String getMessage() {
        return message;
    }

    public String getImage_placeholder() {
        return image_placeholder;
    }
}
