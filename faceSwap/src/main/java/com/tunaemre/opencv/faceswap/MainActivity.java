package com.tunaemre.opencv.faceswap;

import java.io.File;

import com.tunaemre.opencv.faceswap.app.ExtendedCompatActivity;
import com.tunaemre.opencv.faceswap.constant.Constant;
import com.tunaemre.opencv.faceswap.util.CacheOperator;
import com.tunaemre.opencv.faceswap.util.PermissionOperator;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class MainActivity extends ExtendedCompatActivity
{
	private PermissionOperator permissionOperator = new PermissionOperator();
	
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
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
	{
		if (requestCode == PermissionOperator.REQUEST_CAMERA_PERMISSION && grantResults[0] == PackageManager.PERMISSION_GRANTED)
			checkPermission();
	}
	
	@Override
	protected void prepareActivity()
	{
		findViewById(R.id.btnMainFaceSwap).setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if (!checkPermission())
					return;
				
				startActivity(new Intent(MainActivity.this, FaceSwapActivity.class));
			}
		});
		
		findViewById(R.id.btnMainPhotoMask).setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if (!checkPermission())
					return;
				//TODO

				Toast.makeText(MainActivity.this, "In development.", Toast.LENGTH_SHORT).show();
			}
		});
		
		checkPermission();
	}
	
	private boolean checkPermission()
	{
		if (!permissionOperator.isCameraPermissionGranded(this))
		{
			permissionOperator.requestCameraPermission(this);
			return false;
		}
		
		return true;
	}
	
	private boolean checkFaceLandmarkFile()
	{
		File localFile = new File(MainActivity.this.getDir(Constant.FaceLandmarksDownloadPath, Context.MODE_PRIVATE), Constant.FaceLandmarksFileName);
		return localFile.exists() && CacheOperator.getInstance(MainActivity.this).getFaceLandmarksDownloaded();
	}
}