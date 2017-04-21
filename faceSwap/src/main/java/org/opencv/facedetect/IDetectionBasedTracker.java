package org.opencv.facedetect;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;

import android.content.Context;

public class IDetectionBasedTracker
{
	static
	{
		System.loadLibrary("detectionbasedtracker");
	}
	
	private long mNativeObj = 0;

	public IDetectionBasedTracker(String cascadeName, int minFaceSize)
	{
		mNativeObj = nativeCreateObject(cascadeName, minFaceSize);
	}

	public void start()
	{
		nativeStart(mNativeObj);
	}

	public void stop()
	{
		nativeStop(mNativeObj);
	}

	public void setMinFaceSize(int size)
	{
		nativeSetFaceSize(mNativeObj, size);
	}

	public void detect(Mat image, MatOfRect faces)
	{
		nativeDetect(mNativeObj, image.getNativeObjAddr(), faces.getNativeObjAddr());
	}

	public void release()
	{
		nativeDestroyObject(mNativeObj);
		mNativeObj = 0;
	}
	
	public static String copyCascade(Context context, int cascadeResId, String cascadeName)
	{
		try
		{
			File cascadeDir = context.getDir("cascade", Context.MODE_PRIVATE);
			InputStream inputStream = context.getResources().openRawResource(cascadeResId);

			File cascadeFile = new File(cascadeDir, cascadeName);

			if (cascadeFile.exists()) { return cascadeFile.getAbsolutePath(); }

			FileOutputStream outputStream = new FileOutputStream(cascadeFile);

			byte[] buffer = new byte[4096];
			int bytesRead;

			while ((bytesRead = inputStream.read(buffer)) != -1)
			{
				outputStream.write(buffer, 0, bytesRead);
			}

			inputStream.close();
			outputStream.close();

			return cascadeFile.getAbsolutePath();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}

	private static native long nativeCreateObject(String cascadeName, int minFaceSize);

	private static native void nativeDestroyObject(long thiz);

	private static native void nativeStart(long thiz);

	private static native void nativeStop(long thiz);

	private static native void nativeSetFaceSize(long thiz, int size);

	private static native void nativeDetect(long thiz, long inputImage, long faces);
}