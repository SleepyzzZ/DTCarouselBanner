package com.sleepyzzz.dtcarouselbanner;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * User: SleepyzzZ(SleepyzzZ19911002@126.com)
 * Date: 2016-07-30
 * Time: 10:04
 * FIXME
 */
public class DTCarouselBanner extends RelativeLayout {

    private static final String TAG = "CarouselBanner";

    //指示器是否可见
    private boolean mIndicatorVisibility;

    //指示器位置，默认在底部中间位置
    private int mIndicatorPos;
    private static final int CENTER = 0;
    private static final int LEFT   = 1;
    private static final int RIGHT  = 2;

    //指示器背景
    private Drawable mIndicatorBackground;

    //自动轮播时间间隔
    private int mAutoPlayInternal;

    //是否支持自动播放
    private boolean mAutoPlayable;

    //viewpager
    private ViewPager mViewPager;

    //指示器容器
    private LinearLayout mIndicatorContanierLl;
    private LayoutParams mIndicatorContainerLp;

    //是否只有一张轮播图片
    private boolean mJustOneImage = false;

    //轮播定时器
    private ScheduledExecutorService mCarouselTimer;

    //当前轮播器位置
    private int mCurrentPos;

    //本地图片
    private List<Integer> mLocalImages;

    //网络图片
    private List<String> mNetImages;

    //是否加载的是网络图片
    private boolean mIsNetImages = false;

    public DTCarouselBanner(Context context) {
        this(context, null);
    }

    public DTCarouselBanner(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DTCarouselBanner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray ta = context.obtainStyledAttributes(
                attrs, R.styleable.DTCarouselBanner);
        mIndicatorVisibility = ta.getBoolean(
                R.styleable.DTCarouselBanner_indicator_visibility, true);
        mIndicatorPos = ta.getInt(
                R.styleable.DTCarouselBanner_indicator_position, CENTER);
        mIndicatorBackground = ta.getDrawable(
                R.styleable.DTCarouselBanner_indicator_background);
        mAutoPlayInternal = ta.getInt(
                R.styleable.DTCarouselBanner_autoplay_internal, 3000);
        mAutoPlayable = ta.getBoolean(
                R.styleable.DTCarouselBanner_autoplayable, true);
        ta.recycle();

        //关闭over_scroll效果
        setOverScrollMode(OVER_SCROLL_NEVER);

        if (mIndicatorBackground == null) {
            mIndicatorBackground = new ColorDrawable(Color.parseColor("#00B5B5B5"));
        }

        //add viewpager
        mViewPager = new ViewPager(context);
        addView(mViewPager, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        //圆点指示器背景容器relativelayout
        RelativeLayout indicatorContainerRl = new RelativeLayout(context);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            indicatorContainerRl.setBackground(mIndicatorBackground);
        } else {
            indicatorContainerRl.setBackgroundDrawable(mIndicatorBackground);
        }

        //边距
        indicatorContainerRl.setPadding(0, 10, 0, 10);
        //指示器容器layout
        LayoutParams indicatorContainerLp = new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT);
        indicatorContainerLp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        addView(indicatorContainerRl, indicatorContainerLp);

        //设置指示器容器
        mIndicatorContanierLl = new LinearLayout(context);
        mIndicatorContanierLl.setOrientation(LinearLayout.HORIZONTAL);
        mIndicatorContainerLp = new LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);
        indicatorContainerRl.addView(mIndicatorContanierLl, mIndicatorContainerLp);

        //设置指示器是否可见
        if (mIndicatorContanierLl != null) {
            if(mIndicatorVisibility) {
                mIndicatorContanierLl.setVisibility(View.VISIBLE);
            } else {
                mIndicatorContanierLl.setVisibility(View.GONE);
            }
        }

        //设置指示器布局位置
        if (mIndicatorPos == CENTER) {
            mIndicatorContainerLp.addRule(RelativeLayout.CENTER_HORIZONTAL);
        } else if (mIndicatorPos == LEFT) {
            mIndicatorContainerLp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        } else if (mIndicatorPos == RIGHT) {
            mIndicatorContainerLp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        }
    }

    /**
     * 设置指示器是否可见
     * @param isVisible
     */
    public void setIndicatorVisibility(boolean isVisible) {
        if (mIndicatorContanierLl != null) {
            if (isVisible) {
                mIndicatorContanierLl.setVisibility(View.VISIBLE);
            } else {
                mIndicatorContanierLl.setVisibility(View.GONE);
            }
        }
    }

    /**
     * 设置指示器显示位置
     * @param pos
     */
    public void setIndicatorPos(int pos) {
        if (pos == CENTER) {
            mIndicatorContainerLp.addRule(RelativeLayout.CENTER_HORIZONTAL);
        } else if (pos == LEFT) {
            mIndicatorContainerLp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        } else if (pos == RIGHT) {
            mIndicatorContainerLp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (mAutoPlayable && !mJustOneImage) {
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    stopAutoPlay();
                    break;

                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_OUTSIDE:
                    startAutoPlay();
                    break;
            }
        }

        return super.dispatchTouchEvent(ev);
    }

    /**
     * 关闭轮播
     */
    private void stopAutoPlay() {

        if (mAutoPlayable) {
            shutdownTimer();
        }
    }

    /**
     * 开启轮播
     */
    private void startAutoPlay() {

        if (mCarouselTimer != null) {
            shutdownTimer();
        }
        if (mAutoPlayable) {
            mCarouselTimer = Executors.newSingleThreadScheduledExecutor();
            mCarouselTimer.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    post(new Runnable() {
                        @Override
                        public void run() {
                            mCurrentPos++;
                            mViewPager.setCurrentItem(mCurrentPos);
                        }
                    });
                }
            }, mAutoPlayInternal, mAutoPlayInternal, TimeUnit.MILLISECONDS);
        }
    }

    /**
     * 关闭定时器
     */
    private void shutdownTimer() {
        if (!mCarouselTimer.isShutdown()) {
            mCarouselTimer.shutdown();
        }
    }

    /**
     * 加载本地图片
     * @param images
     */
    public void setLocalImages(List<Integer> images) {

        mIsNetImages = false;
        mLocalImages = images;
        if (mLocalImages.size() < 1) {
            mJustOneImage = true;
        }

        initPlayer();
    }

    /**
     * 加载网络图片
     * @param urls
     */
    public void setNetImages(List<String> urls) {

        mIsNetImages = true;
        mNetImages = urls;
        if (mNetImages.size() < 1) {
            mJustOneImage = true;
        }

        initPlayer();
    }

    private void initPlayer() {
        if (!mJustOneImage) {
            addIndicatorPoints();
        }

        mViewPager.setAdapter(new CarouselPagerAdapter());
        mViewPager.setCurrentItem(1, false);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (mIsNetImages) {
                    mCurrentPos = position % (mNetImages.size() + 2);
                } else {
                    mCurrentPos = position % (mLocalImages.size() + 2);
                }
                //切换指示器
                switchIndicator(getRealPosition(position));
            }

            /**
             * 为实现无限循环，这里做一个处理：
             * 当滑动到最后一页时再向右滑，将item转换到第一个页面
             * 同理，当滑动到第一页向左滑，将item转换到最后一页
             *
             * 即假设实际有5张轮播图，其实加上末尾右滑和头部左滑有7张轮播图
             * @param state
             */
            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == ViewPager.SCROLL_STATE_IDLE) {
                    int current = mViewPager.getCurrentItem();
                    int lastReal = mViewPager.getAdapter().getCount() - 2;
                    if (current == 0) {
                        mViewPager.setCurrentItem(lastReal, false);
                    } else if (current == lastReal + 1) {
                        mViewPager.setCurrentItem(1, false);
                    }
                }
            }
        });

        if (!mJustOneImage) {
            startAutoPlay();
        }
    }

    /**
     * 切换指示器
     * @param currentIndex
     */
    private void switchIndicator(int currentIndex) {

        for (int i = 0;i < mIndicatorContanierLl.getChildCount();i++) {
            mIndicatorContanierLl.getChildAt(i).setEnabled(false);
        }
        mIndicatorContanierLl.getChildAt(currentIndex).setEnabled(true);
    }

    /**
     * 根据加载的额图片数量添加指示点
     */
    private void addIndicatorPoints() {

        mIndicatorContanierLl.removeAllViews();
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(5, 5, 5, 5);
        ImageView imageView;
        int length = mIsNetImages ? mNetImages.size() : mLocalImages.size();
        for (int i = 0;i < length;i++) {
            imageView = new ImageView(getContext());
            imageView.setLayoutParams(lp);
            imageView.setImageResource(R.drawable.selector_indicator);
            mIndicatorContanierLl.addView(imageView);
        }

        switchIndicator(0);
    }

    private class CarouselPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            if (mJustOneImage) {
                return 1;
            }

            if (mIsNetImages) {
                return mNetImages.size() + 2;
            }

            return mLocalImages.size() + 2;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            ImageView imageView = new ImageView(getContext());
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);

            final int realPos = getRealPosition(position);
            imageView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(realPos);
                    }
                }
            });
            if (mIsNetImages) {

                Glide.with(getContext())
                        .load(mNetImages.get(realPos))
                        .thumbnail(0.5f)
                        .placeholder(R.drawable.ic_photo_black)
                        .error(R.drawable.ic_broken_image_black)
                        .dontTransform()
                        .dontAnimate()
                        .into(imageView);
            } else {
                imageView.setImageResource(mLocalImages.get(realPos));
            }
            container.addView(imageView);

            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
            if (object != null) {
                object = null;
            }
        }
    }

    /**
     * 获取真实页面的位置
     * @param position
     * @return
     */
    private int getRealPosition(int position) {

        int realPos;
        if (mIsNetImages) {
            realPos = (position -1) % mNetImages.size();
            if (realPos < 0) {
                realPos += mNetImages.size();
            }
        } else {
            realPos = (position - 1) % mLocalImages.size();
            if (realPos < 0) {
                realPos += mLocalImages.size();
            }
        }
        return realPos;
    }

    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener listener) {

        this.mOnItemClickListener = listener;
    }

    //接口回调监听点击事件
    public interface OnItemClickListener {

        void onItemClick(int pos);
    }
}
