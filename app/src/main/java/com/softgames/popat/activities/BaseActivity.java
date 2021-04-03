package com.softgames.popat.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.softgames.popat.R;
import com.softgames.popat.fragments.Loading;
import com.softgames.popat.fragments.MessageFragment;
import com.softgames.popat.service.FireStoreService;
import com.softgames.popat.utils.LocalFileUtils;
import com.softgames.popat.utils.Routing;
import com.softgames.popat.utils.UIUtils;

public class BaseActivity extends AppCompatActivity {

    protected Routing routing;
    protected String TAG;
    protected UIUtils uiUtils;
    protected LocalFileUtils localFileUtils;
    protected FireStoreService fireStoreService;
    protected MessageFragment messageFragment;
    private FragmentManager fragmentManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        routing = new Routing(this);
        uiUtils = new UIUtils(this);
        localFileUtils = new LocalFileUtils(this);

        uiUtils.setParentView(android.R.id.content);
        TAG = this.getClass().getName();

        fireStoreService = new FireStoreService();

        fragmentManager = getSupportFragmentManager();

    }

    protected void showImage(int imageId, ImageView imageView){
        Glide.with(this).load(imageId).into(imageView);
    }
    protected void showRemoteImage(String picUrl, ImageView imageView){
        Glide.with(this).load(picUrl)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        //                holder.progress.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, com.bumptech.glide.load.DataSource dataSource, boolean isFirstResource) {
                        return false;
                    }
                })
                .error(R.drawable.ic_user).fallback(R.drawable.ic_user)
                .into(imageView);
    }
    public void showMsgFragment(){

        Fragment fragment = getSupportFragmentManager().findFragmentByTag(getString(R.string.msg_frg_tag));

        if(fragment == null){
           messageFragment = new MessageFragment();
            fragmentManager.beginTransaction()
                    .replace(android.R.id.content, messageFragment,getString(R.string.msg_frg_tag))
                    .commit();
        }

    }
    public void hideMsgFragment(){

        Fragment fragment = getSupportFragmentManager().findFragmentByTag(getString(R.string.msg_frg_tag));

        if(fragment != null){
            try{
                fragmentManager.beginTransaction().remove(fragment).commit();
            }catch (Exception e){
                e.printStackTrace();
            }

        }

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        try {
            View view = getCurrentFocus();
            if (view != null && (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_MOVE) && view instanceof EditText && !view.getClass().getName().startsWith("android.webkit.")) {
                int scrcoords[] = new int[2];
                view.getLocationOnScreen(scrcoords);
                float x = ev.getRawX() + view.getLeft() - scrcoords[0];
                float y = ev.getRawY() + view.getTop() - scrcoords[1];
                if (x < view.getLeft() || x > view.getRight() || y < view.getTop() || y > view.getBottom())
                    ((InputMethodManager)this.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow((this.getWindow().getDecorView().getApplicationWindowToken()), 0);
            }
            return super.dispatchTouchEvent(ev);
        }catch (Exception e){
            e.printStackTrace();
        }
        return true;
    }

    public void showLoading(){

        Fragment fragment = getSupportFragmentManager().findFragmentByTag("global_loader");
        if(fragment == null){
            Loading loading = new Loading();
            fragmentManager.beginTransaction()
                    .replace(android.R.id.content, loading,"global_loader")
                    .commit();
        }

        //context.findViewById(R.id.loadingIndicator).setVisibility(View.VISIBLE);
    }
    public void hideLoading(){

        try{
            Fragment fragment = getSupportFragmentManager().findFragmentByTag("global_loader");

            if(fragment != null){
                fragmentManager.beginTransaction().remove(fragment).commit();
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        //context.findViewById(R.id.loadingIndicator).setVisibility(View.GONE);
    }
}