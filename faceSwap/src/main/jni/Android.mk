LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_LDLIBS += -latomic
LOCAL_MODULE := dlib

# $(LOCAL_PATH) gives directory names with spaces (eg. "Folder Name")
# GNU make doesn't handle spaces, should be DOS style directory names
# LOCAL_SRC_FILES := ../$(LOCAL_PATH)/dlib/all/source.cpp
# Your dlib source.cpp path of your local copy of project in DOS style
LOCAL_SRC_FILES := C:/Face-S~1/faceSwap/src/main/jni/dlib/all/source.cpp

include $(BUILD_SHARED_LIBRARY)

include $(CLEAR_VARS)
# You can define environment variable as "OPENCV_ANDROID_SDK" that points OpenCV local repo path
# OpenCV local repo path in DOS style
ifdef OPENCV_ANDROID_SDK
  include ${OPENCV_ANDROID_SDK}/sdk/native/jni/OpenCV.mk
else
  include C:/Users/t-emre/Desktop/OpenCV~1/sdk/native/jni/OpenCV.mk
endif

LOCAL_C_INCLUDES += $(LOCAL_PATH)
LOCAL_LDLIBS += -llog -ldl

LOCAL_MODULE := detectionbasedtracker
# LOCAL_SRC_FILES := ../$(LOCAL_PATH)/DetectionBasedTracker.cpp
# Your DetectionBasedTracker.cpp path of your local copy of project in DOS style
LOCAL_SRC_FILES := C:/Face-S~1/faceSwap/src/main/jni/DetectionBasedTracker.cpp

include $(BUILD_SHARED_LIBRARY)

include $(CLEAR_VARS)

ifdef OPENCV_ANDROID_SDK
  include ${OPENCV_ANDROID_SDK}/sdk/native/jni/OpenCV.mk
else
  include C:/Users/t-emre/Desktop/OpenCV~1/sdk/native/jni/OpenCV.mk
endif

OPENCV_CAMERA_MODULES:=on
OPENCV_INSTALL_MODULES:=on

LOCAL_LDLIBS += -llog -ldl

LOCAL_MODULE := faceswapper
# LOCAL_SRC_FILES := ../$(LOCAL_PATH)/FaceSwapper.cpp
# Your FaceSwapper.cpp path of your local copy of project in DOS style
LOCAL_SRC_FILES := C:/Face-S~1/faceSwap/src/main/jni/FaceSwapper.cpp

include $(BUILD_SHARED_LIBRARY)