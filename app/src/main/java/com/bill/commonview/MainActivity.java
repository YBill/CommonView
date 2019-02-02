package com.bill.commonview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    private final String url = "http://rmrbtest-image.peopleapp.com/upload/image/201811/rmrb_95811542187067.gif";
    private final String url2 = "http://rmrbtest-image.peopleapp.com/upload/image/201809/201809291044352193.png?x-oss-process=style/w7";
    private ImageView imageView;
    private ImageView imageView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        imageView = findViewById(R.id.image);

//        Glide.with(this).load(url).into(imageView);

    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
