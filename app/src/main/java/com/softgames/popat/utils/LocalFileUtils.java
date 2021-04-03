package com.softgames.popat.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.util.Base64;
import android.util.Log;

import androidx.core.content.FileProvider;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class LocalFileUtils {

    private Context context;;
    private static final int  MEGABYTE = 1024 * 1024;

    public LocalFileUtils(Context context)
    {
        this.context=context;

    }

    public File getFolder(String folderName){

        File folder = new File(context.getExternalFilesDir(null).toString(), folderName);
        folder.mkdir();

        return folder;
    }

    public File getFile(File folder, String fileName){

        File file = new File(folder, fileName);

        try{
            file.createNewFile();
        }catch (IOException e){
            e.printStackTrace();

        }

        return file;
    }

    public Uri getUri(String authority, File file){


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

            return FileProvider.getUriForFile(context, authority, file);
        } else {
            return Uri.fromFile(file);
        }
    }





    public  boolean isNetworkConnected() {
        boolean result = false;
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (cm != null) {
                NetworkCapabilities capabilities = cm.getNetworkCapabilities(cm.getActiveNetwork());
                if (capabilities != null) {
                    if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                        result = true;
                    } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                        result = true;
                    }
                }
            }
        } else {
            if (cm != null) {
                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                if (activeNetwork != null) {
                    // connected to the internet
                    if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                        result = true;
                    } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                        result = true;
                    }
                }
            }
        }
        return result;
    }


    public String capitalize(String str)
    {
        if(str == null) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
    public String capitalizeWord(String str)
    {
        if(str == null) return str;
        String result="";
        String[] arrOfStr = str.split(" ");

        for(int i = 0; i < arrOfStr.length; ++i){
            if(i==0){
                result = capitalize(arrOfStr[i]);
            }else{
                result = result + " "+capitalize(arrOfStr[i]);
            }
        }


        return result;
    }

    public String getCurrentDate(){

        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        return simpleDateFormat.format(c);
    }
    public String getCurrentTime(){

        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm a");
        String formattedDate = simpleDateFormat.format(c);
        return formattedDate;
    }

    public String initCaps(String text){
        String result;
        if(text == null) return text;
        return text.substring(0, 1).toUpperCase() + text.substring(1);
    }

    public byte[] bitmapToBytesArray(Bitmap bitmap){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        return byteArray;

    }
    public Bitmap bytesArrayToBitmap(byte[] bytes ){
        Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

        return bmp;
    }

//    public boolean saveUserDataInSharedPre(UserData userData){
//
//        SharedPreferences sharedPreferences = context.getSharedPreferences("user_data_pre", MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        Gson gson = new Gson();
//        String json = gson.toJson(userData);
//        editor.putString("user_data", json);
//        return editor.commit();
//    }
//    public UserData getUserDataFromSharedPre(){
//
//        UserData userData;
//
//        SharedPreferences sharedPreferences = context.getSharedPreferences("user_data_pre", MODE_PRIVATE);
//
//        Gson gson = new Gson();
//        String json = sharedPreferences.getString("user_data", null);
//        Type type = new TypeToken<UserData>() {}.getType();
//
//        userData = gson.fromJson(json, type);
//
//        return userData;
//    }
//    public void setImage(ImageView imageView, String imageUrl){
//
//        Glide.with(context).load(imageUrl)
//                .listener(new RequestListener<Drawable>() {
//                    @Override
//                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
//                        //                holder.progress.setVisibility(View.GONE);
//                        return false;
//                    }
//
//                    @Override
//                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, com.bumptech.glide.load.DataSource dataSource, boolean isFirstResource) {
//                        return false;
//                    }
//                })
//                .error(R.drawable.ic_baseline_sports_handball_24).fallback(R.drawable.ic_baseline_sports_handball_24)
//                .into(imageView);
//    }

    public Bitmap convert(String base64Str) throws IllegalArgumentException
    {
        byte[] decodedBytes = Base64.decode(
                base64Str.substring(base64Str.indexOf(",")  + 1),
                Base64.DEFAULT
        );

        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    public String convert(Bitmap bitmap)
    {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);

        return Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT);
    }

    public String getBase64FromImageURL(String url) {

        try {
            URL imageUrl = new URL(url);
            URLConnection ucon = imageUrl.openConnection();
            InputStream is = ucon.getInputStream();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int read = 0;
            while ((read = is.read(buffer, 0, buffer.length)) != -1) {
                baos.write(buffer, 0, read);
            }
            baos.flush();
            return Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
        } catch (Exception e) {
            Log.d("Error", e.toString());
        }
        return null;
    }

    public String getBase64FromFile(File file){
        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
        return convert(bitmap);
    }
    public Bitmap fileToBitmap(File file){

        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());

        return bitmap;

    }

}
