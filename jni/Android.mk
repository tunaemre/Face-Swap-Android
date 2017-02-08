LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE := dlib
LOCAL_SRC_FILES := ../$(LOCAL_PATH)/dlib/all/source.cpp

include $(BUILD_SHARED_LIBRARY)

include $(CLEAR_VARS)

ifdef OPENCV_ANDROID_SDK
  include ${OPENCV_ANDROID_SDK}/sdk/native/jni/OpenCV.mk
else
  include C:/Users/t-emre/Desktop/OpenCV-android-sdk/sdk/native/jni/OpenCV.mk
endif

LOCAL_C_INCLUDES += $(LOCAL_PATH)
LOCAL_LDLIBS     += -llog -ldl

LOCAL_MODULE     := detectionbasedtracker
LOCAL_SRC_FILES  := DetectionBasedTracker.cpp

include $(BUILD_SHARED_LIBRARY)

include $(CLEAR_VARS)

ifdef OPENCV_ANDROID_SDK
  include ${OPENCV_ANDROID_SDK}/sdk/native/jni/OpenCV.mk
else
  include C:/Users/t-emre/Desktop/OpenCV-android-sdk/sdk/native/jni/OpenCV.mk
endif

OPENCV_CAMERA_MODULES:=on
OPENCV_INSTALL_MODULES:=on

LOCAL_LDLIBS += -llog -ldl

LOCAL_MODULE := faceswapper
LOCAL_SRC_FILES := FaceSwapper.cpp

include $(BUILD_SHARED_LIBRARY)