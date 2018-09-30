package me.bello.comparepic;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ListView mListView;
    private int[] pics = new int[]{R.drawable.ic_0, R.drawable.ic_00, R.drawable.ic_1,
            R.drawable.ic_2, R.drawable.ic_3, R.drawable.ic_4, R.drawable.ic_5};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mListView = findViewById(R.id.list_view);

        ArrayList<PicData> mListItems = new ArrayList<>();
//        mListItems.add(getPicData(R.drawable.ic_0, R.drawable.ic_1));
        for(int i=0; i<pics.length; i++){
            for(int j=0; j<pics.length; j++){
                mListItems.add(getPicData(pics[i], pics[j]));
            }
        }

        PicAdapter adapter = new PicAdapter(this, mListItems);
        mListView.setAdapter(adapter);


    }

    private PicData getPicData(int pic1, int pic2){
        PicData data = new PicData();
        data.setBitmap1(BitmapFactory.decodeResource(getResources(), pic1));
        data.setBitmap2(BitmapFactory.decodeResource(getResources(), pic2));
        return  data;
    }


}
