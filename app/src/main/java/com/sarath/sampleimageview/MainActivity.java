package com.sarath.sampleimageview;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;

import com.sarath.widget.AlignedImageView;

public class MainActivity extends AppCompatActivity  implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.image_view).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        ((AlignedImageView) v).setImageDrawable(getResources().getDrawable(R.drawable.img_share_popup));
    }
}
