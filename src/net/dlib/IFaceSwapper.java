package net.dlib;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import android.content.Context;

public class IFaceSwapper
{
	static
	{
		System.loadLibrary("dlib");
		System.loadLibrary("faceswapper");
	}

	@Deprecated
	public static String copyPoseModel(Context context, int modelResId, String modelName)
	{
		try
		{
			File modelDir = context.getDir("poseModel", Context.MODE_PRIVATE);
			InputStream inputStream = context.getResources().openRawResource(modelResId);

			File modelFile = new File(modelDir, modelName);

			if (modelFile.exists()) { return modelFile.getAbsolutePath(); }

			FileOutputStream outputStream = new FileOutputStream(modelFile);

			byte[] buffer = new byte[4096];
			int bytesRead;

			while ((bytesRead = inputStream.read(buffer)) != -1)
			{
				outputStream.write(buffer, 0, bytesRead);
			}

			inputStream.close();
			outputStream.close();

			return modelFile.getAbsolutePath();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}

	public static native boolean loadPoseModel(String modelPath);

	public static native void swapFaces(long frame, long faces);
}
