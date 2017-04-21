package net.dlib;
 
public class IPhotoMaskMaker extends IDlib
{
	static
	{
		System.loadLibrary("dlib");
		System.loadLibrary("photomaskmaker");
	}

	public static native boolean loadPoseModel(String modelPath);

	public static native void maskPhoto(long sourceFrame, long sourceFace, long targetFrame, long targetFaces);
}
