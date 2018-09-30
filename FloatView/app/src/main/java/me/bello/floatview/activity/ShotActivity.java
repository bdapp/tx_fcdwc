package me.bello.floatview.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Window;
import android.widget.Toast;

import me.bello.floatview.service.SelectViewService;
import me.bello.floatview.util.Shotter;

/**
 * @Info
 * @Auth Bello
 * @Time 18-9-30 下午4:54
 * @Ver
 */
public class ShotActivity extends Activity {
    public  static int REQUEST_MEDIA_PROJECTION = 109;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //启动一个透明的Activity
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        getWindow().setDimAmount(0f);

        if (Build.VERSION.SDK_INT >= 21) {
            MediaProjectionManager manager = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);
            startActivityForResult(manager.createScreenCaptureIntent(), REQUEST_MEDIA_PROJECTION);
        } else {
            Toast.makeText(ShotActivity.this, "版本过低,无法截屏", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_MEDIA_PROJECTION && data != null){
            new Shotter(ShotActivity.this, resultCode, data, new Shotter.OnShotListener() {
                @Override
                public void onFinish() {
                    Intent intent = new Intent(ShotActivity.this, SelectViewService.class);
                    startService(intent);
                    finish();
                }
            });
        } else {
            finish();
        }
    }
}
