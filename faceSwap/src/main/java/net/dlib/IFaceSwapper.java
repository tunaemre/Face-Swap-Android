package net.dlib;

public class IFaceSwapper extends IDlib
{
	static
	{
		System.loadLibrary("dlib");
		System.loadLibrary("faceswapper");
	}

	public static native boolean loadPoseModel(String modelPath);

	public static native void swapFaces(long frame, long faces);
}
