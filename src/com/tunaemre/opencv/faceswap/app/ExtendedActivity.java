package com.tunaemre.opencv.faceswap.app;

import com.tunaemre.opencv.faceswap.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

@SuppressLint("InlinedApi")
public abstract class ExtendedActivity extends Activity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		if (Build.VERSION.SDK_INT >= 21)
		{
			Window window = getWindow();
			window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
			window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			//window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			//window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
			window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
			window.setStatusBarColor(getResources().getColor(R.color.primary_dark));
			window.setNavigationBarColor(getResources().getColor(R.color.primary_dark));
		}
	}
	
	@Override
	public void setContentView(int layoutResID)
	{
		super.setContentView(layoutResID);
		
		prepareActivity();
	}
	
	protected abstract void prepareActivity();

}
