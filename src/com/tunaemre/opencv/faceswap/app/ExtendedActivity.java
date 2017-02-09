package com.tunaemre.opencv.faceswap.app;

import android.annotation.SuppressLint;
import android.support.v7.app.AppCompatActivity;

@SuppressLint("InlinedApi")
public abstract class ExtendedActivity extends AppCompatActivity
{	
	@Override
	public void setContentView(int layoutResID)
	{
		super.setContentView(layoutResID);
		
		prepareActivity();
	}
	
	protected abstract void prepareActivity();

}
