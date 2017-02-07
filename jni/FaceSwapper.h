#include <jni.h>
#include <android/log.h>

#include <opencv2/imgproc.hpp>
#include <opencv2/core/mat.hpp>
#include <opencv2/core/types.hpp>

#include "dlib/geometry/rectangle.h"
#include "dlib/image_processing/full_object_detection.h"
#include "dlib/image_processing/shape_predictor.h"
#include "dlib/opencv/cv_image.h"
#include "dlib/pixel.h"
#include "dlib/serialize.h"

cv::Mat cropped_frame;
cv::Size cropped_frame_size;

cv::Rect rect_face1, rect_face2;
cv::Rect big_rect_face1, big_rect_face2;

dlib::shape_predictor face_predictor_model;
dlib::full_object_detection face_geometry_objects[2];

dlib::rectangle dlib_rects[2];
dlib::cv_image<dlib::rgb_alpha_pixel> dlib_frame;

cv::Point2f affine_transform_keypoints_face1[3], affine_transform_keypoints_face2[3];

cv::Mat refined_mask_face1_and_face2_warped, refined_mask_face2_and_face1_warped;

cv::Point2i points_face1[9], points_face2[9];

cv::Mat trans_face1_to_face2, trans_face2_to_face1;

cv::Mat mask_face1, mask_face2;
cv::Mat warped_mask_face1, warped_mask_face2;

cv::Mat refined_masks;

cv::Mat extracted_face1, extracted_face2;
cv::Mat warped_faces;

cv::Size feather_amount;

uint8_t look_up[3][256];
int source_hist_int[3][256];
int target_hist_int[3][256];
float source_histogram[3][256];
float target_histogram[3][256];

bool nativeloadPoseModel(const std::string model_path);

void nativeSwapFaces(cv::Mat &frame, std::vector<cv::Rect> faces);

cv::Mat getCroppedFrame(const cv::Mat &frame, cv::Rect &rect1, cv::Rect &rect2);

void getFaceGeometry(const cv::Mat &frame);

void getTransformationMatrices();

void createMasks();

void getWarpedMasks();

cv::Mat getRefinedMasks();

void extractFaces();

cv::Mat getWarpedFaces();

void colorCorrectFaces();

void featherMask(cv::Mat &refined_masks);

void pasteFacesOnFrame();

void specifiyHistogram(const cv::Mat source_image, cv::Mat target_image, cv::Mat mask);

#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT jboolean JNICALL Java_net_dlib_IFaceSwapper_loadPoseModel(JNIEnv *, jclass, jstring);

JNIEXPORT void JNICALL Java_net_dlib_IFaceSwapper_swapFaces(JNIEnv *, jclass, jlong, jlong);

#ifdef __cplusplus
}
#endif

