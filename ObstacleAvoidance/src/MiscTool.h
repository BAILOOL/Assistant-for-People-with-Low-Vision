#ifndef MISCTOOL_H
#define MISCTOOL_H

#endif // MISCTOOL_H

#include <iostream>
#include <stdio.h>
#include <fstream>
#include <iomanip>
#include <opencv2/opencv.hpp>

using namespace std;
using namespace cv;

vector<cv::KeyPoint> ANMS(vector<cv::KeyPoint> keypoints, double MaxRad, double crobust, int MaxPoints);
void quat2mat(float *q, float *R);
void mat2quat(float *R, float *q);
Mat correctGamma( Mat& img, double gamma );
