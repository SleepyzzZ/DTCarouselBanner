#DTCarouselBanner-Android
--------------------------
##主要功能
*	支持无限循环轮播，手指按下暂停轮播，抬起手指开始轮播
*	支持自定义的指示器位置
*	支持监听Item点击事件
*	支持本地图片和网络图片的动态加载
*	支持圆点指示器和数字指示器

##效果图
![](https://github.com/SleepyzzZ/DTCarouselBanner/raw/master/result.gif)

##使用方法
1.	import module 'DTCarouselBanner' and add gradle
	```
		dependencies {
		    
		    compile project(':dtcarouselbanner')
		}
	```
2.	布局文件中添加DTCarouselBanner
	```
		<com.sleepyzzz.dtcarouselbanner.DTCarouselBanner
	        android:id="@+id/carousel1"
	        android:layout_width="match_parent"
	        android:layout_height="200dp"
	        custom:autoplay_internal="5000"
	        custom:autoplayable="true"
	        custom:indicator_position="CENTER"
	        custom:indicator_visibility="true"
	        custom:indicator_type="num"/>

		<com.sleepyzzz.dtcarouselbanner.DTCarouselBanner
	        android:id="@+id/carousel2"
	        android:layout_width="match_parent"
	        android:layout_height="200dp"
	        custom:autoplay_internal="3000"
	        custom:indicator_position="RIGHT"
	        custom:indicator_visibility="true"/>
	```
3.	配置	DTCarouselBanner
	```
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
	```
4.	监听页面Item点击事件
	```
		mCarouselBanner1.setOnItemClickListener(new DTCarouselBanner.OnItemClickListener() {
            @Override
            public void onItemClick(int pos) {
                Toast.makeText(getApplicationContext(),
                        "click item " + (pos+1), Toast.LENGTH_SHORT).show();
            }
        });
	```

##自定义属性
```
	<declare-styleable name="DTCarouselBanner">
        <!--指示器是否可见 true or false-->
        <attr name="indicator_visibility" format="boolean"/>
        <!--底部指示器显示位置-->
        <attr name="indicator_position" format="enum">
            <enum name="CENTER" value="0"/>
            <enum name="LEFT" value="1"/>
            <enum name="RIGHT" value="2"/>
        </attr>
        <!--指示器容器背景-->
        <attr name="indicator_background" format="reference|color"/>
        <!--轮播时间间隔 ms-->
        <attr name="autoplay_internal" format="integer"/>
        <!--是否进行自动轮播-->
        <attr name="autoplayable" format="boolean"/>
        <!--指示器类型-->
        <attr name="indicator_type" format="enum">
            <enum name="pot" value="0"/>
            <enum name="num" value="1"/>
        </attr>
        <!--指示点左右外间距-->
        <attr name="indicatorPoint_horizontalMargin" format="dimension"/>
        <!--指示器容器上下内间距-->
        <attr name="indicatorContainer_verticalPadding" format="dimension"/>
        <!--数字指示器字体大小-->
        <attr name="indicatorNum_textSize" format="dimension"/>
        <!--数字指示器字体颜色-->
        <attr name="indicatorNUm_textColor" format="color"/>
    </declare-styleable>
```