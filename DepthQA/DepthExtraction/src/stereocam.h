#ifndef STEREOCAM_H
#define STEREOCAM_H

#include <iostream>
#include <opencv2/opencv.hpp>
#include "opencv2/core/core.hpp"
#include "opencv2/features2d/features2d.hpp"
#include "opencv2/nonfree/features2d.hpp"
#include "opencv2/highgui/highgui.hpp"
#include "opencv2/nonfree/nonfree.hpp"
#include "MiscTool.h"
#include "elasFiles/elas.h"

using namespace std;
using namespace cv;

class StereoCam
{
    public:
    // Images
    Mat ImageL, ImageR, ImageL_Gray, ImageR_Gray;
    Mat ImageL_Rec, ImageR_Rec, ImageL_Gray_Rec, ImageR_Gray_Rec;
    Mat Disparity;
    Mat DepthMap;
    Size SizeAfterRec;

    // Camera Parameters
    Mat IntrinsicsL, DistorsionL, IntrinsicsR, DistorsionR, Stereo_Rotation, Stereo_Translation;
    Size Stereo_imgSize;

    // Rectified Parameters
    Mat Intrinsics_Rec;
    Mat rmap[2][2]; // Rectif LUT
    double baseline;

    // Stereo Matching
    vector<KeyPoint> keypointsL, keypointsR;
    Mat descriptorsL, descriptorsR;
    Mat Points3D; Mat PointsId;

    StereoCam(Mat ImageL, Mat ImageR); // Declare with images
    StereoCam(Mat ImageL, Mat ImageR, StereoCam Prev_Stereo); // Copy the previous informations
    StereoCam();

    // Functions:
    void OpenParams (string ParametersFile); // Open cameras' parameters
    void DisplayStereo(string Which); // Display images
    void RectifyStereo(); // Rectify stereo images
    Mat ComputeDisparity(double scale, bool display);
    void ComputeDepthMap();
};

#endif // STEREOCAM_H
