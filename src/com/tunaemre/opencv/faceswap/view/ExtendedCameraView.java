package com.tunaemre.opencv.faceswap.view;

import org.opencv.android.JavaCameraView;

import android.content.Context;
import android.util.AttributeSet;

public class ExtendedCameraView extends JavaCameraView
{
	
	public ExtendedCameraView(Context context, int cameraId)
	{
        super(context, cameraId);
    }
	
	public ExtendedCameraView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}
	
	public int switchCamera()
	{
		if (mCameraIndex != CAMERA_ID_FRONT)
		{
			disableView();
			mCameraIndex = CAMERA_ID_FRONT;
			enableView();
			
			return mCameraIndex;
		}
		else
		{
			disableView();
			mCameraIndex = CAMERA_ID_BACK;
			enableView();
			
			return mCameraIndex;
			
		}
	}

}
