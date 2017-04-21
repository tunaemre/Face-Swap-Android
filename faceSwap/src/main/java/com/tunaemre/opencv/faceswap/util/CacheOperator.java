package com.tunaemre.opencv.faceswap.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class CacheOperator
{
	private static CacheOperator instance = null;
	
	String PREF_NAME = "PREF_FACESWAP_CACHE";
	
	String IS_FACELANDMARKS_DOWNLOADED= "ISFACELANDMARKSDOWNLOADED";
	
	SharedPreferences mPreference;
	Editor mEditor;
	
	public static CacheOperator getInstance(Context context)
	{
		if (instance != null)
			return instance;
		
		instance = new CacheOperator(context);
		return instance;
	}
	
	private CacheOperator(Context context)
	{
		mPreference = context.getApplicationContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
	}

	public void setFaceLandmarksDownloaded(boolean value)
	{
		mEditor = mPreference.edit();
		mEditor.putBoolean(IS_FACELANDMARKS_DOWNLOADED, value);
		mEditor.commit();
	}

	public boolean getFaceLandmarksDownloaded()
	{
		return mPreference.getBoolean(IS_FACELANDMARKS_DOWNLOADED, false);
	}
}
