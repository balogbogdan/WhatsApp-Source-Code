package com.parse.starter.utility;


import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Serializer {

    public static Object serialize(Map3d map3d, Context context) throws IOException, ClassNotFoundException {
        String path =  context.getFilesDir().getPath() + "/newtarget.ser";
        Log.i("unique", "works here1");

        try {
            FileOutputStream fileOutputStream = new FileOutputStream(path);
            Log.i("unique", "works her2");
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(map3d);
            objectOutputStream.close();
            fileOutputStream.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return deserialize(path);
    }
    private static Object deserialize(String path) throws IOException, ClassNotFoundException {
        FileInputStream fileInputStream = new FileInputStream(path);
        ObjectInputStream objectInputStream = new  ObjectInputStream(fileInputStream);
        Object map3d = objectInputStream.readObject();
        objectInputStream.close();
        fileInputStream.close();
        Log.i("unique", "works her2");

        return map3d;
    }
}

