package com.example.lanouhn.lunbotu;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.lanouhn.lunbotu.contans.Url;
import com.example.lanouhn.lunbotu.contans.lunboImage;
import com.example.lanouhn.lunbotu.interfaces.ViewPagerItemListenter;
import com.example.lanouhn.lunbotu.utils.HttpUtils;
import com.example.lanouhn.lunbotu.utils.LunboJson;
import com.example.lanouhn.lunbotu.utils.LunboPicUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lanouhn on 2016/9/12.
 */
public class LunbotuActivity extends Activity implements ViewPagerItemListenter{
    private LunboPicUtil  lunbotuView;
    private List<lunboImage>   list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lunbotu);
        lunbotuView= (LunboPicUtil) findViewById(R.id.lunbotuView);
        initData();
    }
    //绑定数据
    private void initData() {

        list = new ArrayList<>();

        new Thread(new Runnable() {
            @Override
            public void run() {
                String result = HttpUtils.doGet(Url.Lunbourl);
                list = LunboJson.getlunboList(result);
                if (list.size() > 0) {

                    handler.sendEmptyMessage(1);

                } else {
                    handler.sendEmptyMessage(0);
                }
            }
        }).start();
    }

    //判断网络是否连接
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    lunbotuView.initData(list);
                    //绑定轮播图
                    lunbotuView.setViewPagerItemListenter(LunbotuActivity.this);
                    break;
                case 0:
                    Toast.makeText(LunbotuActivity.this,getResources().getString(R.string.fail),Toast.LENGTH_SHORT).show();
                    break;
            }

        }
    };
//轮播图点击事件
    @Override
    public void myItemListenr(int position) {
        Intent intent = new Intent(LunbotuActivity.this,Pic.class);
        //传值
        intent.putExtra("cover",list.get(position).getCover());

       startActivity(intent);
    }
}
