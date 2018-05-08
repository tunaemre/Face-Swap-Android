#include <jni.h>

#include <opencv2/core/core.hpp>
#include <opencv2/contrib/detection_based_tracker.hpp>

#include <string>
#include <vector>

#include <android/log.h>

#ifndef _Included_org_opencv_facedetect_IDetectionBasedTracker
#define _Included_org_opencv_facedetect_IDetectionBasedTracker

#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT jlong JNICALL Java_org_opencv_facedetect_IDetectionBasedTracker_nativeCreateObject(JNIEnv *, jclass, jstring, jint);

JNIEXPORT void JNICALL Java_org_opencv_facedetect_IDetectionBasedTracker_nativeDestroyObject(JNIEnv *, jclass, jlong);

JNIEXPORT void JNICALL Java_org_opencv_facedetect_IDetectionBasedTracker_nativeStart(JNIEnv *, jclass, jlong);

JNIEXPORT void JNICALL Java_org_opencv_facedetect_IDetectionBasedTracker_nativeStop(JNIEnv *, jclass, jlong);

JNIEXPORT void JNICALL Java_org_opencv_facedetect_IDetectionBasedTracker_nativeSetFaceSize(JNIEnv *, jclass, jlong, jint);

JNIEXPORT void JNICALL Java_org_opencv_facedetect_IDetectionBasedTracker_nativeDetect(JNIEnv *, jclass, jlong, jlong, jlong);

#ifdef __cplusplus
}
#endif

#endif
