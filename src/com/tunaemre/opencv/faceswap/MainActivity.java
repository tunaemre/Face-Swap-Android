package com.tunaemre.opencv.faceswap;

import java.io.File;

import com.tunaemre.opencv.faceswap.app.ExtendedActivity;
import com.tunaemre.opencv.faceswap.constant.Constant;
import com.tunaemre.opencv.faceswap.util.CacheOperator;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class MainActivity extends ExtendedActivity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		if (!checkFaceLandmarkFile())
		{
			startActivity(new Intent(this, DownloaderActivity.class));
			finish();
			
			return;
		}
		
		setContentView(R.layout.activity_main);
	}

	@Override
	protected void prepareActivity()
	{
		findViewById(R.id.btnMainFaceSwap).setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				startActivity(new Intent(MainActivity.this, FaceSwapActivity.class));
			}
		});
		
		findViewById(R.id.btnMainPhotoMask).setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Toast.makeText(MainActivity.this, "Under developing.", Toast.LENGTH_SHORT).show();
			}
		});
	}
	
	private boolean checkFaceLandmarkFile()
	{
		File localFile = new File(MainActivity.this.getDir(Constant.FaceLandmarksDownloadPath, Context.MODE_PRIVATE), Constant.FaceLandmarksFileName);
		return localFile.exists() && CacheOperator.getInstance(MainActivity.this).getFaceLandmarksDownloaded();
	}
}