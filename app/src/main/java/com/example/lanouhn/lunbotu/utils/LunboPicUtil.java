package com.example.lanouhn.lunbotu.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.lanouhn.lunbotu.R;
import com.example.lanouhn.lunbotu.contans.lunboImage;
import com.example.lanouhn.lunbotu.interfaces.Circle_ItemClick;
import com.example.lanouhn.lunbotu.interfaces.ViewPagerItemListenter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * ViewPager实现的轮播图广告自定义视图，如京东首页的广告轮播图效果；
 * 既支持自动轮播页面也支持手势滑动切换页面
 * Created by lanouhn on 16/7/21.
 */
public class LunboPicUtil extends FrameLayout {

    private boolean isAutoPlay = true;    //自动轮播启用开关

    private Context context;
    private List<lunboImage> lunbotuList;    //实体类
    private List<ImageView> imageViewsList;    //放轮播图片的ImageView 的list
    private List<View> dotViewsList;    //放圆点的View的list

    private TextView guide_slide_tv;    //标题
    private ViewPager viewPager;
    private int currentItem = 0;    //当前轮播页

    private ScheduledExecutorService scheduledExecutorService;    //定时任务

    private ViewPagerItemListenter mListenter;    //轮播图的监听接口
    private View viewLL;

    public  void setViewPagerItemListenter (ViewPagerItemListenter mListenter){
        this.mListenter=mListenter;
    }
    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            viewPager.setCurrentItem(currentItem);
        }
    };

    public LunboPicUtil(Context context) {
        this(context, null, 0);
    }

    public LunboPicUtil(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LunboPicUtil(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }

    /**
     * 初始化相关Data
     */
    public void initData(List<lunboImage> list) {
        this.lunbotuList = list;
        imageViewsList = new ArrayList<ImageView>();
        dotViewsList = new ArrayList<View>();

        initUI(context);

        if (isAutoPlay == true) {
            startPlay();
        } else
            stopPlay();

        viewPager.setCurrentItem(currentItem);    //下拉刷新后设置为当前页面
        for (int i = 0; i < dotViewsList.size(); i++) {
            if (i == currentItem) {
                ((View) dotViewsList.get(currentItem)).setBackgroundResource(R.mipmap.w);
            } else {
                ((View) dotViewsList.get(i)).setBackgroundResource(R.mipmap.haha);
            }
        }
    }

    /**
     * 初始化Views等UI
     */
    private void initUI(Context context) {
        if (lunbotuList == null || lunbotuList.size() == 0)
            return;

        if (viewLL == null)
            viewLL = LayoutInflater.from(context).inflate(R.layout.layout_slideshow, this, true);

        LinearLayout dotLayout = (LinearLayout) findViewById(R.id.guide_slide_ll);
        //图片标题
       // guide_slide_tv = (TextView) findViewById(R.id.guide_slide_tv);
        dotLayout.removeAllViews();

        // 热点个数与图片特殊相等
        for (int i = 0; i < lunbotuList.size(); i++) {
            ImageView view = new ImageView(context);
            view.setTag(lunbotuList.get(i).getCover());

            view.setScaleType(ImageView.ScaleType.FIT_XY);
            imageViewsList.add(view);

            ImageView dotView = new ImageView(context);
            //设置点
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(20, 20);    //点的大小
            params.leftMargin = 5;
            params.rightMargin = 5;
            dotLayout.addView(dotView, params);
            dotViewsList.add(dotView);
        }

        viewPager = (ViewPager) findViewById(R.id.guide_slide_vp);

        MyPagerAdapter mAdapter =new MyPagerAdapter();
        viewPager.setFocusable(true);
        viewPager.setAdapter(mAdapter);
        viewPager.addOnPageChangeListener(new MyPageChangeListener());

        //通过接口把pos传递给fragment,找到轮播图位置
        mAdapter.setItemClick(new Circle_ItemClick() {
            @Override
            public void myCircleItemClick(int position) {
                mListenter.myItemListenr(position);
            }
        });
    }


    /**
     * 填充ViewPager的页面适配器
     */
    private class MyPagerAdapter extends PagerAdapter {

        private Circle_ItemClick itemClick;    //点击事件的接口

        public void setItemClick(Circle_ItemClick itemClick){
            this.itemClick = itemClick;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {

           // guide_slide_tv.setText(lunbotuList.get(position).getCover());    //标题赋值

            ImageView imageView = imageViewsList.get(position);    //获取集合中的图片

            imageView.setScaleType(ImageView.ScaleType.FIT_XY);    //设置图片的宽高

            Picasso.with(context).load((String) imageView.getTag()).into(imageView);    //图片赋值

            //通过点击图片来个给自定义的轮播图传递pos
            imageView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClick.myCircleItemClick(position);
                }
            });
            container.addView(imageView);

            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            //object要销毁的对象
            container.removeView((View) object);
        }

        @Override
        public int getCount() {
            return imageViewsList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

    }

    /**
     * ViewPager的监听器
     * 当ViewPager中页面的状态发生改变时调用
     */
    private class MyPageChangeListener implements ViewPager.OnPageChangeListener {

        boolean isAutoPlay = false;

        @Override
        public void onPageScrollStateChanged(int arg0) {
            switch (arg0) {
                case 1:    // 手势滑动，空闲中
                    isAutoPlay = false;
                    break;
                case 2:    // 界面切换中
                    isAutoPlay = true;
                    break;
                case 0:    // 滑动结束，即切换完毕或者加载完毕
                    // 当前为最后一张，此时从右向左滑，则切换到第一张
                    if (viewPager.getCurrentItem() == viewPager.getAdapter().getCount() - 1 && !isAutoPlay) {
                        viewPager.setCurrentItem(0);
                    }
                    // 当前为第一张，此时从左向右滑，则切换到最后一张
                    else if (viewPager.getCurrentItem() == 0 && !isAutoPlay) {
                        viewPager.setCurrentItem(viewPager.getAdapter().getCount() - 1);
                    }
                    break;
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageSelected(int pos) {

            currentItem = pos;
            for (int i = 0; i < dotViewsList.size(); i++) {
                if (i == pos) {
                    ((View) dotViewsList.get(pos)).setBackgroundResource(R.mipmap.hui_dian);
                } else {
                    ((View) dotViewsList.get(i)).setBackgroundResource(R.mipmap.bai_dian);
                }
            }
        }
    }

    /**
     * 开始轮播图切换
     */

    private void startPlay() {
        if (scheduledExecutorService == null) {
            scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
            scheduledExecutorService.scheduleAtFixedRate(new SlideShowTask(), 1, 4, TimeUnit.SECONDS);
        }
    }

    /**
     * 停止轮播图切换
     */
    private void stopPlay() {
        scheduledExecutorService.shutdown();
    }


    /**
     * 执行轮播图切换任务
     */
    private class SlideShowTask implements Runnable {

        @Override
        public void run() {
            synchronized (viewPager) {
                currentItem = (currentItem + 1) % imageViewsList.size();
                handler.obtainMessage().sendToTarget();
            }
        }
    }

}
