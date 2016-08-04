package com.sleepyzzz.carouselbannerdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.sleepyzzz.dtcarouselbanner.DTCarouselBanner;

import java.util.ArrayList;
import java.util.List;

public class DemoActivity extends AppCompatActivity {

    private DTCarouselBanner mCarouselBanner1;

    private DTCarouselBanner mCarouselBanner2;

    private List<Integer> mLocalImages;

    private List<String> mNetImages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);

        mCarouselBanner1 = (DTCarouselBanner) findViewById(R.id.carousel1);
        mCarouselBanner2 = (DTCarouselBanner) findViewById(R.id.carousel2);

        mLocalImages = new ArrayList<Integer>();
        mLocalImages.add(R.drawable.img1);
        mLocalImages.add(R.drawable.img2);
        mLocalImages.add(R.drawable.img3);
        mLocalImages.add(R.drawable.img4);

        mNetImages = new ArrayList<String>();
        mNetImages.add("http://img.my.csdn.net/uploads/201407/26/1406383290_9329.jpg");
        mNetImages.add("http://img.my.csdn.net/uploads/201407/26/1406383243_5120.jpg");
        mNetImages.add("http://img.my.csdn.net/uploads/201407/26/1406383219_5806.jpg");
        mNetImages.add("http://img.my.csdn.net/uploads/201407/26/1406383166_3407.jpg");
        mNetImages.add("http://img.my.csdn.net/uploads/201407/26/1406383131_3736.jpg");

        mCarouselBanner1.setLocalImages(mLocalImages);
        mCarouselBanner2.setNetImages(mNetImages);

        mCarouselBanner1.setOnItemClickListener(new DTCarouselBanner.OnItemClickListener() {
            @Override
            public void onItemClick(int pos) {
                Toast.makeText(getApplicationContext(),
                        "click item " + (pos+1), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
