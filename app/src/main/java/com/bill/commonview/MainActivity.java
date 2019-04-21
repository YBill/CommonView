package com.bill.commonview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.bill.commonwidget.EllipsizeEndTextView2;
import com.bill.commonwidget.EllipsizeEndTextView;
import com.bill.commonwidget.ExpandableTextView;

public class MainActivity extends AppCompatActivity {

    private final String url = "http://rmrbtest-image.peopleapp.com/upload/image/201811/rmrb_95811542187067.gif";
    private final String url2 = "http://rmrbtest-image.peopleapp.com/upload/image/201809/201809291044352193.png?x-oss-process=style/w7";
    private ImageView imageView;
    private ImageView imageView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expandable);
//        imageView = findViewById(R.id.image);

//        Glide.with(this).load(url).into(imageView);

        ExpandableTextView expandableTextView = findViewById(R.id.expand_text_view2);
        EllipsizeEndTextView2 commentTextView = findViewById(R.id.ctv_item_pn_dynamic_content);
        EllipsizeEndTextView ellipsizeendtextview = findViewById(R.id.ellipsizeendtextview);

        String text = "荆防颗粒解放路口世纪东方两间房老师讲的饭量就是的楼房氪金大佬风景啥的雷锋精神的楼房看见了的看法见识到了看风景的解放东路开房记录看发就说雷锋精神两顿饭就算了" +
                "荆防颗粒解放路口世纪东方两间房老师讲的饭量就是的楼房氪金大佬风景啥的雷锋精神的楼房看见了的看法见识到了看风景的解放东路开房记录看发就说雷锋精神两顿饭就算了";

        expandableTextView.setText(text);
        commentTextView.setRealText(text);
        ellipsizeendtextview.setText(text);

    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
