package com.tunaemre.opencv.faceswap.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

public class PermissionOperator
{
	public static int REQUEST_STORAGE_PERMISSION = 11;
	public static int REQUEST_CAMERA_PERMISSION = 12;

	
	public boolean isStoragePermissionGranded(Context context)
	{
		if (Build.VERSION.SDK_INT < 23) return true;
		
		if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) return true;
		
		return false;
	}
	
	public boolean requestStoragePermission(Activity context)
	{
		if (isStoragePermissionGranded(context)) return true;
		
		ActivityCompat.requestPermissions(context, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_STORAGE_PERMISSION);
		
		return true;
	}
	
	public boolean isCameraPermissionGranded(Context context)
	{
		if (Build.VERSION.SDK_INT < 23) return true;
		
		if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) return true;
		
		return false;
	}
	
	public boolean requestCameraPermission(Activity context)
	{
		if (isCameraPermissionGranded(context)) return true;
		
		ActivityCompat.requestPermissions(context, new String[] {Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
		
		return true;
	}
}
