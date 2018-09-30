package me.bello.comparepic;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cv4j.core.datamodel.CV4JImage;
import com.cv4j.core.datamodel.ImageProcessor;
import com.cv4j.core.hist.CalcHistogram;
import com.cv4j.core.hist.CompareHist;

import java.util.ArrayList;

/**
 * @Info
 * @Auth Bello
 * @Time 2018-09-29 18:58:19
 * @Ver
 */
public class PicAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<PicData> mListItems;

    public PicAdapter(Context mContext, ArrayList<PicData> mListItems) {
        this.mContext = mContext;
        this.mListItems = mListItems;
    }

    @Override
    public int getCount() {
        return mListItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mListItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_pic, null);
            holder = new ViewHolder();
            holder._image1 = (ImageView) convertView.findViewById(R.id.image_1);
            holder._image2 = (ImageView) convertView.findViewById(R.id.image_2);
            holder._resultText = (TextView) convertView.findViewById(R.id.result_text);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        PicData data = mListItems.get(position);

        holder._image1.setImageBitmap(data.getBitmap1());
        holder._image2.setImageBitmap(data.getBitmap2());


        int[][] source1 = getSource(data.getBitmap1());
        int[][] source2 = getSource(data.getBitmap2());

        CompareHist compareHist = new CompareHist();
        StringBuilder sb = new StringBuilder();
        sb.append("巴氏距离:").append(compareHist.bhattacharyya(source1[0],source2[0])).append("\r\n")
                .append("协方差:").append(compareHist.covariance(source1[0],source2[0])).append("\r\n")
                .append("相关性因子:").append(compareHist.ncc(source1[0],source2[0]));

        holder._resultText.setText(sb.toString());


        return convertView;
    }

    class ViewHolder {
        ImageView _image1;
        ImageView _image2;
        TextView _resultText;
    }


    private int[][] getSource(Bitmap bitmap){

        CV4JImage cv4jImage = new CV4JImage(bitmap);
        ImageProcessor imageProcessor = cv4jImage.getProcessor();

        int[][] source = null;

        CalcHistogram calcHistogram = new CalcHistogram();
        int bins = 180;
        source = new int[imageProcessor.getChannels()][bins];
        calcHistogram.calcHSVHist(imageProcessor,bins,source,true);

        return source;
    }


}
