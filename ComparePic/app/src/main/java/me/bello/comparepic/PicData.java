package me.bello.comparepic;

import android.graphics.Bitmap;

/**
 * @Info
 * @Auth Bello
 * @Time 18-9-29 下午6:58
 * @Ver
 */
public class PicData {
    private Bitmap bitmap1;
    private Bitmap bitmap2;
    private String result;

    public Bitmap getBitmap1() {
        return bitmap1;
    }

    public void setBitmap1(Bitmap bitmap1) {
        this.bitmap1 = bitmap1;
    }

    public Bitmap getBitmap2() {
        return bitmap2;
    }

    public void setBitmap2(Bitmap bitmap2) {
        this.bitmap2 = bitmap2;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
