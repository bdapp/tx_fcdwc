package me.bello.floatview.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;

import me.bello.floatview.activity.ShotActivity;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

/**
 * @Info 悬浮选中块的服务
 * @Auth Bello
 * @Time 18-9-30 上午11:04
 * @Ver
 */
public class SelectViewService extends Service {

    private WindowManager wm;
    private ImageView img;

    @Override
    public void onCreate() {
        super.onCreate();
        createFloatView();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * 创建悬浮按钮
     */
    private void createFloatView() {
        try {
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            wm = (WindowManager) getApplication().getSystemService(getApplication().WINDOW_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                //8.0新特性
                lp.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
            } else {
                lp.type = WindowManager.LayoutParams.TYPE_TOAST;
            }
            lp.format = PixelFormat.RGBA_8888;
            lp.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
//            lp.gravity = Gravity.CENTER;
            lp.width = 1160;
            lp.height = 1028;
            lp.x = 155;
            lp.y = 0;

            WindowManager.LayoutParams wl = new WindowManager.LayoutParams();
            wl.width = 1160;
            wl.height = 1028;
            img = new ImageView(getApplicationContext());
            img.setBackgroundColor(Color.parseColor("#cc000000"));
            img.setImageBitmap(BitmapFactory.decodeFile("/storage/emulated/0/screen.png"));
            img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    stopSelf();
                    onDestroy();
                }
            });
            img.setScaleType(ImageView.ScaleType.FIT_XY);
            img.setLayoutParams(wl);

            wm.addView(img, lp);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onDestroy() {
        if (null!= wm) {
            wm.removeView(img);
            wm = null;
        }
        super.onDestroy();
    }


}
