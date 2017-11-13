package com.example.meetingsdkdemo;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

public class MyViewpager extends ViewPager{
	private boolean isCanScroll = false;

	public MyViewpager(Context context, AttributeSet attrs) {  
		super(context, attrs);  
	}  

	public boolean isCanScroll() {
		return isCanScroll;
	}

	public void setCanScroll(boolean isCanScroll) {
		this.isCanScroll = isCanScroll;
	}
	public MyViewpager(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	@Override  
	public void scrollTo(int x, int y){  
		if (isCanScroll){  
			super.scrollTo(x, y);  
		}  
	} 
}
