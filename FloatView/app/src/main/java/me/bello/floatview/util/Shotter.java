package me.bello.floatview.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
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

import me.bello.floatview.R;

/**
 * @Info 截屏与录屏
 * @Auth Bello
 * @Time 18-9-30 下午4:26
 * @Ver
 */

public class Shotter {

    public Context context;

    @SuppressLint("NewApi")
    public Shotter(final Context context, int resultCode, Intent data, final OnShotListener listener) {
        this.context = context;
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
                    if (!isLandscape(context)) {
                        bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height);
                    } else {
                        bitmap = getNewBitmap(bitmap);

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
                                    bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
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
                }

            }, 300);


        }
    }

    /**
     * 开始比较图片，并生成新图
     *
     * @param bitmap
     * @return
     */
    private Bitmap getNewBitmap(Bitmap bitmap) {
        //先取出原图里要比较的大区域
        bitmap = Bitmap.createBitmap(bitmap, 615, 50, 1090, 981);
        //要比较的图片
        Bitmap b2 = BitmapFactory.decodeResource(context.getResources(), R.drawable.screen_19);
        Bitmap resultBitmap = Bitmap.createBitmap(1090, 981, Bitmap.Config.ARGB_8888);

        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 9; j++) {
                int x = i * 109;
                int y = j * 109;
                Bitmap b1 = Bitmap.createBitmap(bitmap, x, y, 109, 109);

                if (ComparePicUtil.compareTwoBitmap(b1, b2)) {
                    //高相似度
                    resultBitmap = createNewBitmap(resultBitmap, x, y);
                } else {
                    //低相似度
                }
            }
        }
        return resultBitmap;
    }


    /**
     * 往bitmap里写入一张小图
     *
     * @param resultBitmap
     * @param x
     * @param y
     * @return
     */
    private Bitmap createNewBitmap(Bitmap resultBitmap, int x, int y) {

        Bitmap litterBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.screen_19);
        Canvas canvas = new Canvas(resultBitmap);
        canvas.drawColor(Color.TRANSPARENT);
        canvas.drawBitmap(litterBitmap, x, y, null);

        return resultBitmap;
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

    /**
     * 回调截图成功的接口
     */
    public interface OnShotListener {
        void onFinish();
    }
}

