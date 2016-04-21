package com.gpl.qqslidingmenu;

import java.util.Random;

import com.gpl.qqslidingmenu.view.SlidingMenu;
import com.gpl.qqslidingmenu.view.SlidingMenu.OnDragStateChangeListener;

import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.CycleInterpolator;
import android.view.animation.Interpolator;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final ListView menu_listview = (ListView) findViewById(R.id.menu_listview);
        ListView main_listview = (ListView) findViewById(R.id.main_listview);
        SlidingMenu slidingMenu = (SlidingMenu) findViewById(R.id.slideMenu);
    	final ImageView iv_head = (ImageView) findViewById(R.id.iv_head);
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
  //填充数据
        menu_listview.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,Constant.sCheeseStrings){
  			@Override
  			public View getView(int position, View convertView, ViewGroup parent) {
  				TextView view = (TextView) super.getView(position, convertView, parent);
  				view.setTextColor(Color.WHITE);
  				return view;
  			}
  			
  		});
  		main_listview.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,Constant.NAMES));
  
    }
      
}
