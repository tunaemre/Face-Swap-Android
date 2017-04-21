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

cv::Rect rect_source_face;
cv::Rect big_rect_source_face;

std::vector<cv::Rect> rect_faces;
std::vector<cv::Rect> big_rect_faces;

dlib::shape_predictor face_predictor_model;
std::vector<dlib::full_object_detection> face_geometry_objects;

std::vector<dlib::rectangle> dlib_rects;

std::vector<std::array<cv::Point2f, 3>> affine_transform_keypoints_faces;

cv::Mat refined_mask_face1_and_face2_warped, refined_mask_face2_and_face1_warped;

std::vector<std::array<cv::Point2i, 9>> points_faces;

cv::Mat trans_face1_to_face2, trans_face2_to_face1;

cv::Mat mask_face1, mask_face2;
cv::Mat warped_mask_face1, warped_mask_face2;

cv::Mat refined_masks;

cv::Mat extracted_face1, extracted_face2;
cv::Mat warped_faces;

std::vector<cv::Size> feather_amounts;

uint8_t look_up[3][256];
int source_hist_int[3][256];
int target_hist_int[3][256];
float source_histogram[3][256];
float target_histogram[3][256];

bool nativeLoadPoseModel(const std::string model_path);

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

JNIEXPORT void JNICALL Java_net_dlib_IFaceSwapper_maskPhoto(JNIEnv *, jclass, jlong, jlong, jlong, jlong);

#ifdef __cplusplus
}
#endif

