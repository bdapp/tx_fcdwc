package me.bello.floatview.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;

/**
 * @Info 截屏与录屏
 * @Auth Bello
 * @Time 18-9-30 下午4:26
 * @Ver
 */

public class Shotter {


    @SuppressLint("NewApi")
    public Shotter(final Context context, int resultCode, Intent data, final OnShotListener listener) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            MediaProjectionManager mMediaProjectionManager = (MediaProjectionManager) context.getSystemService(Context
                    .MEDIA_PROJECTION_SERVICE);
            MediaProjection mediaProjection = mMediaProjectionManager.getMediaProjection(resultCode, data);

            final ImageReader mImageReader = ImageReader.newInstance(Resources.getSystem().getDisplayMetrics().widthPixels, Resources
                            .getSystem().getDisplayMetrics().heightPixels, PixelFormat.RGBA_8888,//此处必须和下面 buffer处理一致的格式
                    // ，RGB_565在一些机器上出现兼容问题。
                    1);

            final VirtualDisplay virtualDisplay = mediaProjection.createVirtualDisplay("screen-mirror", Resources.getSystem()
                    .getDisplayMetrics().widthPixels, Resources.getSystem().getDisplayMetrics().heightPixels, Resources.getSystem()
                    .getDisplayMetrics().densityDpi, DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR, mImageReader.getSurface(), null,
                    null);


            //必须在子线程处理
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    virtualDisplay.release();
                    Image image = mImageReader.acquireLatestImage();
                    if (null == image)
                        return;
                    int width = image.getWidth();
                    int height = image.getHeight();
                    final Image.Plane[] planes = image.getPlanes();
                    final ByteBuffer buffer = planes[0].getBuffer();
                    //每个像素的间距
                    int pixelStride = planes[0].getPixelStride();
                    //总的间距
                    int rowStride = planes[0].getRowStride();
                    int rowPadding = rowStride - pixelStride * width;
                    Bitmap bitmap = Bitmap.createBitmap(width + rowPadding / pixelStride, height, Bitmap.Config.ARGB_8888);
                    //虽然这个色彩比较费内存但是 兼容性更好
                    bitmap.copyPixelsFromBuffer(buffer);
                    //
                    Log.d("bitmap", width + ", " + height);
                    if (isLandscape(context)) {
                        bitmap = Bitmap.createBitmap(bitmap, 615, 50, 1090, 980);
                    } else {
                        bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height);
                    }
                    image.close();
                    File fileImage = null;
                    try {
                        if (bitmap != null) {
                            String mLocalUrl = "";
                            if (TextUtils.isEmpty(mLocalUrl)) {
                                mLocalUrl = Environment.getExternalStorageDirectory().toString() + "/screen.png";
                            }
                            Log.i("picUrl", mLocalUrl);
                            fileImage = new File(mLocalUrl);

                            if (!fileImage.exists()) {
                                fileImage.createNewFile();
                            }
                            FileOutputStream out = new FileOutputStream(fileImage);
                            if (out != null) {
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                                out.flush();
                                out.close();
                            }

                            listener.onFinish();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        bitmap.recycle();
                        bitmap = null;
                    }
                }

            }, 300);


        }
    }

    public interface OnShotListener {
        void onFinish();
    }

    /**
     * 判断当前屏幕竖横屏状态
     * 
     * @param context
     * @return
     */
    private boolean isLandscape(Context context) {
        Configuration mConfiguration = context.getResources().getConfiguration(); //获取设置的配置信息
        int ori = mConfiguration.orientation; //获取屏幕方向
        if (ori == mConfiguration.ORIENTATION_LANDSCAPE) {
            //横屏
            return true;
        } else if (ori == mConfiguration.ORIENTATION_PORTRAIT) {
            //竖屏
            return false;
        }

        return false;
    }
}
