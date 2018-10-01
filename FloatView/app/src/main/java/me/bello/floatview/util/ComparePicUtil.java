package me.bello.floatview.util;

import android.graphics.Bitmap;

import com.cv4j.core.datamodel.CV4JImage;
import com.cv4j.core.datamodel.ImageProcessor;
import com.cv4j.core.hist.CalcHistogram;
import com.cv4j.core.hist.CompareHist;

/**
 * @Info 图片比较相似性
 * @Auth Bello
 * @Time 2018/9/30 23:59
 * @Ver
 */
public class ComparePicUtil {

    public static boolean compareTwoBitmap(Bitmap b1, Bitmap b2) {
        int[][] source1 = getSource(b1);
        int[][] source2 = getSource(b2);
        CompareHist compareHist = new CompareHist();
        //计算相关性因子（0.0~1.0，值越大越相似）
        double d = compareHist.ncc(source1[0], source2[0]);
        if (d >= 0.8) {
            return true;
        } else {
            return false;
        }
    }

    private static int[][] getSource(Bitmap bitmap) {
        CV4JImage cv4jImage = new CV4JImage(bitmap);
        ImageProcessor imageProcessor = cv4jImage.getProcessor();

        int[][] source = null;

        CalcHistogram calcHistogram = new CalcHistogram();
        int bins = 180;
        source = new int[imageProcessor.getChannels()][bins];
        calcHistogram.calcHSVHist(imageProcessor, bins, source, true);

        return source;
    }
}
