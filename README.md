# SlidingMenu
##简介
&emsp;&emsp;这个工程是自己模仿QQ5.0侧滑菜单效果的实现,属于自定义的ViewGroup,主要是使用ViewDragHelper来实现ViewGroup中对子View的拖拽,并实现子View的伴随移动,需要考虑的细节较多
##使用帮助
&emsp;&emsp;&nbsp;
1.在布局使用自定义的SlidingMenu作为容器,添加两个子view(只能是2个不能多也不能少)

	<com.gpl.qqslidingmenu.view.SlidingMenu xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    android:id="@+id/slideMenu"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="@drawable/bg"
    tools:context=".MainActivity" >

    <include layout="@layout/layout_menu"/>
    <include layout="@layout/layout_main"/>
	</com.gpl.qqslidingmenu.view.SlidingMenu>

2.在Activity中填充View初始化相关控件

	...

3.为slidingMenu添加监听器

	slidingMenu.setOnDragStateChangeListener(new OnDragStateChangeListener() {
			
			@Override
			public void onOpen() {
				
				menu_listview.smoothScrollToPosition(new Random().nextInt(menu_listview.getCount()));
			}
			
			@Override
			public void onDrag(float fraction) {
				ViewHelper.setAlpha(iv_head, 1-fraction);
				
			}
			
			@Override
			public void onClose() {
				 ViewPropertyAnimator.animate(iv_head).translationX(15)
				.setInterpolator(new CycleInterpolator(4)).setDuration(300).start();
				
			}
		});
##实现效果
![Alt text](https://github.com/gplcn/SlidingMenu/raw/master/Screenshots/Screenshot01.gif)