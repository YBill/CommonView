package com.bill.commonview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.bill.commonwidget.ExpandableTextView;

public class MainActivity extends AppCompatActivity {

    private ExpandableTextView expandableTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        expandableTextView = findViewById(R.id.expand_text_view);
        expandableTextView.setText("This is the larger TextView of the test line. Click to expand");
    }
}
