package me.bello.floatview.service;

import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import me.bello.floatview.activity.ShotActivity;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

/**
 * @Info 悬浮按钮的服务
 * @Auth Bello
 * @Time 18-9-30 上午11:04
 * @Ver
 */
public class FloatViewService extends Service {

    private WindowManager wm;
    private Button btn;

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
        lp.gravity = Gravity.LEFT | Gravity.TOP;
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.x = 0;
        lp.y = 0;

        btn = new Button(getApplicationContext());
        btn.setText("悬浮");
        btn.setBackgroundColor(Color.parseColor("#66FFFFFF"));
        btn.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        btn.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FloatViewService.this, ShotActivity.class);
                intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        btn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                stopSelf();
                System.exit(0);
                return false;
            }
        });

        wm.addView(btn, lp);
    }


    @Override
    public void onDestroy() {
        wm.removeView(btn);
        wm = null;
        super.onDestroy();
    }
}
