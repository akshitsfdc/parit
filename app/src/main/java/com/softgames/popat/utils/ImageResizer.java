package com.softgames.popat.utils;


import android.app.Activity;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import id.zelory.compressor.*;

import org.apache.commons.io.FileUtils;


public class ImageResizer {

    private Activity activity;
    //For Image Size 640*480, use MAX_SIZE =  307200 as 640*480 307200
    //private static long MAX_SIZE = 360000;
    //private static long THUMB_SIZE = 6553;

    public ImageResizer(){}

    public ImageResizer(Activity activity){
        this.activity = activity;
    }

    public static Bitmap reduceBitmapSize(Bitmap bitmap, int MAX_SIZE) {
        double ratioSquare;
        int bitmapHeight, bitmapWidth;
        bitmapHeight = bitmap.getHeight();
        bitmapWidth = bitmap.getWidth();
        ratioSquare = (bitmapHeight * bitmapWidth) / MAX_SIZE;
        if (ratioSquare <= 1)
            return bitmap;
        double ratio = Math.sqrt(ratioSquare);
        Log.d("mylog", "Ratio: " + ratio);
        int requiredHeight = (int) Math.round(bitmapHeight / ratio);
        int requiredWidth = (int) Math.round(bitmapWidth / ratio);
        return Bitmap.createScaledBitmap(bitmap, requiredWidth, requiredHeight, true);
    }

    public static Bitmap generateThumb(Bitmap bitmap, int THUMB_SIZE) {
        double ratioSquare;
        int bitmapHeight, bitmapWidth;
        bitmapHeight = bitmap.getHeight();
        bitmapWidth = bitmap.getWidth();
        ratioSquare = (bitmapHeight * bitmapWidth) / THUMB_SIZE;
        if (ratioSquare <= 1)
            return bitmap;
        double ratio = Math.sqrt(ratioSquare);
        Log.d("mylog", "Ratio: " + ratio);
        int requiredHeight = (int) Math.round(bitmapHeight / ratio);
        int requiredWidth = (int) Math.round(bitmapWidth / ratio);
        return Bitmap.createScaledBitmap(bitmap, requiredWidth, requiredHeight, true);
    }

    public byte[] compressImage(Uri uri){

        try {
            File actualFile = new File(uri.getPath());
            Log.d("5678", "Before compressImage: size >> "+actualFile.length()/1024+" KB");
            File file = new Compressor(activity).
                    setMaxHeight(200)
                    .setMaxWidth(200)
                    .setQuality(30)
                    .compressToFile(actualFile);

            Log.d("5678", "After compressImage: size >> "+file.length()/1024+" KB");
            return FileUtils.readFileToByteArray(file);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }



}