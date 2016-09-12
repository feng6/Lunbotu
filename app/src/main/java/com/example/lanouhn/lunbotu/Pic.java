package com.example.lanouhn.lunbotu;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 * Created by lanouhn on 2016/9/12.
 *
 * 轮播图跳转的页面
 */
public class Pic extends Activity {
    private ImageView  iv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pic);
        iv=(ImageView)findViewById(R.id.iv);
        Intent intent=getIntent();
        String url=  intent.getStringExtra("cover");
        //控制图片 .fit() 必须有个固定高度才能用
        Picasso.with(Pic.this).load(url).fit().into(iv);
    }

}
