# ViewPager-LayoutManager [![Download](https://api.bintray.com/packages/leochuan/maven/viewpager-layout-manager/images/download.svg) ](https://bintray.com/leochuan/maven/viewpager-layout-manager/_latestVersion) ![build](https://travis-ci.org/leochuan/ViewPagerLayoutManager.svg?branch=master)

[English](README.md) | **中文**

![logo](static/logo.png)

VPLM 实现了一些常见的动画效果，如果你有什么别的想要的效果欢迎给我提ISSUE以及PR

![circle](static/circle.jpg) ![circle_scale](static/circle_scale.jpg) ![carousel](static/carousel.jpg) ![gallery](static/gallery.jpg) ![rotate](static/rotate.jpg) ![scale](static/scale.jpg)

## 自定义属性
![customize](static/customize.gif)

各个`layoutmanager`都有各自的一些属性可以设置
比如：
* 半径
* 滚动速度
* 间隔
* 排列方向

可以运行下demo看下具体有哪些属性可以设置

## 循环列表

![infinite](static/infinite.gif)

## 自动滚动到中心

在每次拖动或者快速滑动的时候，你可以通过设置 `CenterSnapHelper` 让目标 view 自动停在屏幕中央
```java
// work exactly same as LinearSnapHelper.
new CenterSnapHelper().attachToRecyclerView(recyclerView);
```

## 设置可见个数
```java
layoutmanager.setMaxVisibleItemCount(count);
```

## 获取中间item的位置
```java
layoutmanager.getCurrentPosition()
```

## 滚动到特定位置
一般情况下，直接使用`RecyclerView`自带的`smoothScrollToPosition`就可以了，
但是当无限滚动开启的时候，如果能获取到要滚动到的view建议使用下面的方法。
```java
ScrollHelper.smoothScrollToTargetView(recyclerView, itemViewYouWantScrollTo);
```

## 自动轮播

请使用 `AutoPlayRecyclerView`

```xml
<com.leochuan.AutoPlayRecyclerView
    android:id="@+id/recycler"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    app:direction="right"
    app:timeInterval="1500"/>
```

## 安装

Gradle:

```groovy
repositories {
  jcenter()
}

dependencies {
  compile 'com.qavan.viewpagerlayoutmanagerx:viewpagerlayoutmanagerx:1.x.y'
}
```

Maven:

```xml
<dependency>
  <groupId>com.qavan.viewpagerlayoutmanagerx</groupId>
  <artifactId>viewpagerlayoutmanagerx</artifactId>
  <version>1.x.y</version>
  <type>pom</type>
</dependency>
```

## 快速开始

使用前请确保每一个`view`的大小都相同，不然可能会发生不可预料的错误。



你可以通过新建一个`Builder`来设置各种属性:

```java
new CircleLayoutManager.Builder(context)
                .setAngleInterval(mAngle)
                .setMaxRemoveAngle(mMaxRemoveAngle)
                .setMinRemoveAngle(mMinRemoveAngle)
                .setMoveSpeed(mSpeed)
                .setRadius(mRadius)
                .setReverseLayout(true)
                .build();
```

或者只是简单的调用一下预设的构造方法:

```java
new CircleLayoutManager(context);
```

## License

Apache-2.0. 详情见 [LICENSE](LICENSE)