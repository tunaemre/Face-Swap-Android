#include "FaceSwapper.h"

#define LOG_TAG "FaceSwapper-JNI"
#define LOGD(...) ((void)__android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__))

using namespace dlib;
using namespace cv;

//inline void vector_Rect_to_Mat(std::vector<Rect>& v_rect, Mat& mat)
//{
//	mat = Mat(v_rect, true);
//}

JNIEXPORT jboolean JNICALL Java_net_dlib_IFaceSwapper_loadPoseModel(JNIEnv * jenv, jclass, jstring path)
{
	const char* jpathstr = jenv->GetStringUTFChars(path, NULL);
	std::string pathstr(jpathstr);

	return nativeLoadPoseModel(pathstr);
}

JNIEXPORT void JNICALL Java_net_dlib_IFaceSwapper_swapFaces(JNIEnv * jenv, jclass, jlong frame, jlong faces)
{
	cv::Mat facesmat = (*((Mat*)faces));
	std::vector<Rect> facesvector = (std::vector<Rect>) facesmat;

	if (facesvector.size() != 2)
		return;

	cv::Mat framemat = (*((Mat*)frame));

	//*((Mat*)faces) = Mat(rect_faces, true);

	nativeSwapFaces(framemat, facesvector);
}

bool nativeLoadPoseModel(const std::string model_path)
{
	try
	{
		dlib::deserialize(model_path) >> face_predictor_model;
		LOGD("nativeloadPoseModel: true");
		return true;
	}
	catch (std::exception& e)
	{
		LOGD("nativeloadPoseModel: false");
		return false;
	}
}

void nativeSwapFaces(cv::Mat &frame, std::vector<cv::Rect> faces)
{
	//LOGD("nativeSwapFaces: started");

	cropped_frame = getCroppedFrame(frame, faces[0], faces[1]);

	LOGD("nativeSwapFaces: cropped_frame=%i,%i", cropped_frame.cols, cropped_frame.rows);

	cropped_frame_size = cv::Size(cropped_frame.cols, cropped_frame.rows);

	getFaceGeometry(cropped_frame);

	getTransformationMatrices();

	createMasks();

	getWarpedMasks();

	refined_masks = getRefinedMasks();

	extractFaces();

	warped_faces = getWarpedFaces();

	colorCorrectFaces();

	cv::Mat refined_mask_1 = cv::Mat(refined_masks, big_rect_face1);
	cv::Mat refined_mask_2 = cv::Mat(refined_masks, big_rect_face2);

	featherMask(refined_mask_1);
	featherMask(refined_mask_2);

	pasteFacesOnFrame();

	cv::rectangle(frame, (cv::Rect)faces[0], cv::Scalar(0, 255, 0, 255));
	cv::rectangle(frame, (cv::Rect)faces[1], cv::Scalar(0, 255, 0, 255));

	cv::rectangle(cropped_frame, big_rect_face1, cv::Scalar(255, 0, 0, 255));
	cv::rectangle(cropped_frame, big_rect_face2, cv::Scalar(255, 0, 0, 255));
}

cv::Mat getCroppedFrame(const cv::Mat &frame, cv::Rect &rect1, cv::Rect &rect2)
{
	//LOGD("getCroppedFrame");
	cv::Rect bounding_rect = rect1 | rect2;

	bounding_rect -= cv::Point(50, 50);
	bounding_rect += cv::Size(100, 100);

	bounding_rect &= cv::Rect(0, 0, frame.cols, frame.rows);

	rect_face1 = rect1 - bounding_rect.tl();
	rect_face2 = rect2 - bounding_rect.tl();

	big_rect_face1 = ((rect_face1 - cv::Point(rect1.width / 4, rect1.height / 4)) + cv::Size(rect1.width / 2, rect1.height / 2)) & cv::Rect(0, 0, bounding_rect.width, bounding_rect.height);
	big_rect_face2 = ((rect_face2 - cv::Point(rect2.width / 4, rect2.height / 4)) + cv::Size(rect2.width / 2, rect2.height / 2)) & cv::Rect(0, 0, bounding_rect.width, bounding_rect.height);

	return frame(bounding_rect);
}

void getFaceGeometry(const cv::Mat &frame)
{
	//LOGD("getFacePoints");
	//LOGD("getFacePoints: frame.size()=%ix%i", frame.size().width, frame.size().height);

	dlib_rects[0] = dlib::rectangle(rect_face1.x, rect_face1.y, rect_face1.x + rect_face1.width, rect_face1.y + rect_face1.height);
	dlib_rects[1] = dlib::rectangle(rect_face2.x, rect_face2.y, rect_face2.x + rect_face2.width, rect_face2.y + rect_face2.height);

	dlib_frame = frame;

	face_geometry_objects[0] = face_predictor_model(dlib_frame, dlib_rects[0]);
	face_geometry_objects[1] = face_predictor_model(dlib_frame, dlib_rects[1]);

	auto getPoint = [&](int shape_index, int part_index) -> const cv::Point2i
			{
		const auto &p = face_geometry_objects[shape_index].part(part_index);
		return cv::Point2i(p.x(), p.y());
			};

	points_face1[0] = getPoint(0, 0);
	points_face1[1] = getPoint(0, 3);
	points_face1[2] = getPoint(0, 5);
	points_face1[3] = getPoint(0, 8);
	points_face1[4] = getPoint(0, 11);
	points_face1[5] = getPoint(0, 13);
	points_face1[6] = getPoint(0, 16);

	cv::Point2i nose_length = getPoint(0, 27) - getPoint(0, 30);
	points_face1[7] = getPoint(0, 26) + nose_length;
	points_face1[8] = getPoint(0, 17) + nose_length;

	points_face2[0] = getPoint(1, 0);
	points_face2[1] = getPoint(1, 3);
	points_face2[2] = getPoint(1, 5);
	points_face2[3] = getPoint(1, 8);
	points_face2[4] = getPoint(1, 11);
	points_face2[5] = getPoint(1, 13);
	points_face2[6] = getPoint(1, 16);

	nose_length = getPoint(1, 27) - getPoint(1, 30);
	points_face2[7] = getPoint(1, 26) + nose_length;
	points_face2[8] = getPoint(1, 17) + nose_length;

//	for (int i = 0; i < 8; i++)
//	{
//		cv::line(frame, points_face1[i], points_face1[i+1], cv::Scalar(255,255,0,255));
//		cv::line(frame, points_face2[i], points_face2[i+1], cv::Scalar(255,255,0,255));
//	}

	affine_transform_keypoints_face1[0] = getPoint(0, 8);
	affine_transform_keypoints_face1[1] = getPoint(0, 36);
	affine_transform_keypoints_face1[2] = getPoint(0, 45);

	affine_transform_keypoints_face2[0] = getPoint(1, 8);
	affine_transform_keypoints_face2[1] = getPoint(1, 36);
	affine_transform_keypoints_face2[2] = getPoint(1, 45);

	feather_amount.width = feather_amount.height = (int)cv::norm(points_face1[0] - points_face1[6]) / 8;
}

void getTransformationMatrices()
{
	//LOGD("getTransformationMatrices");
	trans_face1_to_face2 = cv::getAffineTransform(affine_transform_keypoints_face1, affine_transform_keypoints_face2);
	trans_face2_to_face1 = cv::getAffineTransform(affine_transform_keypoints_face2, affine_transform_keypoints_face1);
}

void createMasks()
{
	//LOGD("createMasks");

	mask_face1.create(cropped_frame_size, CV_8UC1);
	mask_face2.create(cropped_frame_size, CV_8UC1);

	mask_face1.setTo(cv::Scalar::all(0));
	mask_face2.setTo(cv::Scalar::all(0));

	cv::fillConvexPoly(mask_face1, points_face1, 9, cv::Scalar(255));
	cv::fillConvexPoly(mask_face2, points_face2, 9, cv::Scalar(255));
}

void getWarpedMasks()
{
	//LOGD("getWarpedMasks");

	warped_mask_face1.create(cropped_frame_size, CV_8UC1);
	warped_mask_face2.create(cropped_frame_size, CV_8UC1);

	warped_mask_face1.setTo(cv::Scalar::all(0));
	warped_mask_face2.setTo(cv::Scalar::all(0));

	cv::warpAffine(mask_face1, warped_mask_face1, trans_face1_to_face2, cropped_frame_size, cv::INTER_NEAREST, cv::BORDER_CONSTANT, cv::Scalar(0));
	cv::warpAffine(mask_face2, warped_mask_face2, trans_face2_to_face1, cropped_frame_size, cv::INTER_NEAREST, cv::BORDER_CONSTANT, cv::Scalar(0));
}

cv::Mat getRefinedMasks()
{
	//LOGD("getRefinedMasks");

	cv::bitwise_and(mask_face1, warped_mask_face2, refined_mask_face1_and_face2_warped);
	cv::bitwise_and(mask_face2, warped_mask_face1, refined_mask_face2_and_face1_warped);

	cv::Mat temp_refined_masks(cropped_frame_size, CV_8UC1, cv::Scalar::all(0));
	cv::bitwise_or(refined_mask_face1_and_face2_warped, refined_mask_face2_and_face1_warped, temp_refined_masks);

	return temp_refined_masks;
}

void extractFaces()
{
	//LOGD("extractFaces");

	cropped_frame.copyTo(extracted_face1, mask_face1);
	cropped_frame.copyTo(extracted_face2, mask_face2);
}

cv::Mat getWarpedFaces()
{
	//LOGD("getWarpedFaces");

	cv::Mat warped_face1(cropped_frame_size, CV_8UC4, cv::Scalar::all(0));
	cv::Mat warped_face2(cropped_frame_size, CV_8UC4, cv::Scalar::all(0));

	cv::Mat temp_warped_faces(cropped_frame_size, CV_8UC4, cv::Scalar::all(0));

	cv::warpAffine(extracted_face1, warped_face1, trans_face1_to_face2, cropped_frame_size, cv::INTER_NEAREST, cv::BORDER_CONSTANT, cv::Scalar::all(0));
	cv::warpAffine(extracted_face2, warped_face2, trans_face2_to_face1, cropped_frame_size, cv::INTER_NEAREST, cv::BORDER_CONSTANT, cv::Scalar::all(0));

	cv::bitwise_or(warped_face1, warped_face2, temp_warped_faces, refined_masks);

	return temp_warped_faces;
}

void colorCorrectFaces()
{
	//LOGD("colorCorrectFaces");

	specifiyHistogram(cropped_frame(big_rect_face1), warped_faces(big_rect_face1), warped_mask_face2(big_rect_face1));
	specifiyHistogram(cropped_frame(big_rect_face2), warped_faces(big_rect_face2), warped_mask_face1(big_rect_face2));
}

void featherMask(cv::Mat &refined_masks)
{
	cv::erode(refined_masks, refined_masks, getStructuringElement(cv::MORPH_RECT, feather_amount), cv::Point(-1, -1), 1, cv::BORDER_CONSTANT, cv::Scalar(0));

	cv::blur(refined_masks, refined_masks, feather_amount, cv::Point(-1, -1), cv::BORDER_CONSTANT);
}

inline void pasteFacesOnFrame()
{
	//LOGD("pasteFacesOnFrame");
	for (size_t i = 0; i < cropped_frame.rows; i++)
	{
		auto frame_pixel = cropped_frame.row(i).data;
		auto faces_pixel = warped_faces.row(i).data;
		auto masks_pixel = refined_masks.row(i).data;

		for (size_t j = 0; j < cropped_frame.cols; j++)
		{
			if (*masks_pixel != 0)
			{
				*frame_pixel = ((255 - *masks_pixel) * (*frame_pixel) + (*masks_pixel) * (*faces_pixel)) >> 8; // divide by 256
				*(frame_pixel + 1) = ((255 - *(masks_pixel + 1)) * (*(frame_pixel + 1)) + (*(masks_pixel + 1)) * (*(faces_pixel + 1))) >> 8;
				*(frame_pixel + 2) = ((255 - *(masks_pixel + 2)) * (*(frame_pixel + 2)) + (*(masks_pixel + 2)) * (*(faces_pixel + 2))) >> 8;
			}

			frame_pixel += 4;
			faces_pixel += 4;
			masks_pixel++;
		}
	}
}

void specifiyHistogram(const cv::Mat sourceimage, cv::Mat targetimage, cv::Mat mask)
{
	//LOGD("specifiyHistogram");

	std::memset(source_hist_int, 0, sizeof(int) * 3 * 256);
	std::memset(target_hist_int, 0, sizeof(int) * 3 * 256);

	for (size_t i = 0; i < mask.rows; i++)
	{
		auto current_mask_pixel = mask.row(i).data;
		auto current_source_pixel = sourceimage.row(i).data;
		auto current_target_pixel = targetimage.row(i).data;

		for (size_t j = 0; j < mask.cols; j++)
		{
			if (*current_mask_pixel != 0) {
				source_hist_int[0][*current_source_pixel]++;
				source_hist_int[1][*(current_source_pixel + 1)]++;
				source_hist_int[2][*(current_source_pixel + 2)]++;

				target_hist_int[0][*current_target_pixel]++;
				target_hist_int[1][*(current_target_pixel + 1)]++;
				target_hist_int[2][*(current_target_pixel + 2)]++;
			}

			current_source_pixel += 4;
			current_target_pixel += 4;
			current_mask_pixel++;
		}
	}

	for (size_t i = 1; i < 256; i++)
	{
		source_hist_int[0][i] += source_hist_int[0][i - 1];
		source_hist_int[1][i] += source_hist_int[1][i - 1];
		source_hist_int[2][i] += source_hist_int[2][i - 1];

		target_hist_int[0][i] += target_hist_int[0][i - 1];
		target_hist_int[1][i] += target_hist_int[1][i - 1];
		target_hist_int[2][i] += target_hist_int[2][i - 1];
	}

	for (size_t i = 0; i < 256; i++)
	{
		source_histogram[0][i] = (source_hist_int[0][i] ? (float)source_hist_int[0][i] / source_hist_int[0][255] : 0);
		source_histogram[1][i] = (source_hist_int[1][i] ? (float)source_hist_int[1][i] / source_hist_int[1][255] : 0);
		source_histogram[2][i] = (source_hist_int[2][i] ? (float)source_hist_int[2][i] / source_hist_int[2][255] : 0);

		target_histogram[0][i] = (target_hist_int[0][i] ? (float)target_hist_int[0][i] / target_hist_int[0][255] : 0);
		target_histogram[1][i] = (target_hist_int[1][i] ? (float)target_hist_int[1][i] / target_hist_int[1][255] : 0);
		target_histogram[2][i] = (target_hist_int[2][i] ? (float)target_hist_int[2][i] / target_hist_int[2][255] : 0);
	}

	auto binary_search = [&](const float needle, const float haystack[]) -> uint8_t
			{
		uint8_t l = 0, r = 255, m;
		while (l < r)
		{
			m = (l + r) / 2;
			if (needle > haystack[m])
				l = m + 1;
			else
				r = m - 1;
		}
		return m;
			};

	for (size_t i = 0; i < 256; i++)
	{
		look_up[0][i] = binary_search(target_histogram[0][i], source_histogram[0]);
		look_up[1][i] = binary_search(target_histogram[1][i], source_histogram[1]);
		look_up[2][i] = binary_search(target_histogram[2][i], source_histogram[2]);
	}

	for (size_t i = 0; i < mask.rows; i++)
	{
		auto current_mask_pixel = mask.row(i).data;
		auto current_target_pixel = targetimage.row(i).data;
		for (size_t j = 0; j < mask.cols; j++)
		{
			if (*current_mask_pixel != 0)
			{
				*current_target_pixel = look_up[0][*current_target_pixel];
				*(current_target_pixel + 1) = look_up[1][*(current_target_pixel + 1)];
				*(current_target_pixel + 2) = look_up[2][*(current_target_pixel + 2)];
			}

			current_target_pixel += 4;
			current_mask_pixel++;
		}
	}
}
