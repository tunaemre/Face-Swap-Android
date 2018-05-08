LOCAL_PATH := $(call my-dir)

OPENCV_INSTALL_MODULES := on
OPENCV_CAMERA_MODULES := on
OPENCV_LIB_TYPE := SHARED

include $(CLEAR_VARS)

LOCAL_LDLIBS += -latomic

# $(LOCAL_PATH) gives directory names with spaces (eg. "Folder Name")
# GNU make doesn't handle spaces, should be DOS style directory names
# LOCAL_SRC_FILES := ../$(LOCAL_PATH)/dlib/all/source.cpp
# Your dlib source.cpp path of your local copy of project in DOS style
LOCAL_SRC_FILES := C:/Face-Swap-Android/faceSwap/src/main/jni/dlib/all/source.cpp

LOCAL_MODULE := dlib

include $(BUILD_SHARED_LIBRARY)

include $(CLEAR_VARS)

# You can define environment variable as "OPENCV_ANDROID_SDK" that points OpenCV local repo path
ifdef OPENCV_ANDROID_SDK
  include ${OPENCV_ANDROID_SDK}/sdk/native/jni/OpenCV.mk
else
  include C:/Users/TUNAE/Desktop/OpenCV-Android-SDK/sdk/native/jni/OpenCV.mk
endif

# LOCAL_SRC_FILES := ../$(LOCAL_PATH)/DetectionBasedTracker.cpp
LOCAL_SRC_FILES := C:/Face-Swap-Android/faceSwap/src/main/jni/DetectionBasedTracker.cpp

LOCAL_C_INCLUDES += $(LOCAL_PATH)
LOCAL_LDLIBS += -llog -ldl -latomic

LOCAL_MODULE := detectionbasedtracker

include $(BUILD_SHARED_LIBRARY)

include $(CLEAR_VARS)

ifdef OPENCV_ANDROID_SDK
  include ${OPENCV_ANDROID_SDK}/sdk/native/jni/OpenCV.mk
else
  include C:/Users/TUNAE/Desktop/OpenCV-Android-SDK/sdk/native/jni/OpenCV.mk
endif

# LOCAL_SRC_FILES := ../$(LOCAL_PATH)/FaceSwapper.cpp
LOCAL_SRC_FILES := C:/Face-Swap-Android/faceSwap/src/main/jni/FaceSwapper.cpp

LOCAL_LDLIBS += -llog -ldl -latomic

LOCAL_MODULE := faceswapper

include $(BUILD_SHARED_LIBRARY)