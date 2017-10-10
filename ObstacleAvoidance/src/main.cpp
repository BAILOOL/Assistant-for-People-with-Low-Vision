#include <boost/python.hpp>
#include <iostream>
#include <stdio.h>
#include <fstream>
#include <iomanip>
#include <sstream> //Just to record frames
#include <opencv2/opencv.hpp>
#include "stereocam.h"
#include <string>
#include <climits>
#include <deque>

#include "opencv2/imgproc/imgproc.hpp"
#include "opencv2/highgui/highgui.hpp"
using namespace cv;
using namespace std;

namespace bp = boost::python;

struct point
{
    int x;
    int y;
    point(int x, int y): x(x), y(y){}
};


boost::python::list mainRoutine(const std::string & left_path, 
                const std::string & right_path,
                const std::string & pathParam)
{
        const int N = 100;      

        //Prepare the class stereocam and read the parameters
        StereoCam StereoRig;
        StereoRig.OpenParams(pathParam);

        // Load images
        Mat ImageL = imread(left_path,CV_LOAD_IMAGE_COLOR);
        Mat ImageR = imread(right_path,CV_LOAD_IMAGE_COLOR);
        ImageL.copyTo(StereoRig.ImageL);
        ImageR.copyTo(StereoRig.ImageR);
            
        // Rectify
        StereoRig.RectifyStereo();
        // Compute disparity
        Mat DispMap = StereoRig.ComputeDisparity(1,true);

        // Depth from disparity
        StereoRig.ComputeDepthMap();
        StereoRig.DepthMap.convertTo(StereoRig.DepthMap, CV_32F);
        Mat DepthMat(StereoRig.DepthMap.cols,StereoRig.DepthMap.rows,CV_32FC1, 0);

        medianBlur ( StereoRig.DepthMap, DepthMat, 3 );

        double minVal = (double) INT_MAX;
        int xL = 0;
        int yL = 0;
        deque<point> q;
        // Average N closest points
        for (int i = 0; i < DepthMat.size().height; i++)
            for (int j = 0; j < DepthMat.size().width; j++)
            {
                double val = DepthMat.at<float>(i, j);
                if (minVal < 0 || (val > 0 && minVal > val))
                {
                    if (minVal - val > 0.75) q.clear();
                    minVal = val;
                    yL = i;
                    xL = j;
                    q.push_back(point(xL, yL));
                    if (q.size() > N)
                        q.pop_front();
                }
            }
        int num = q.size();
        int avg_x = 0;
        int avg_y = 0;
        for (int i = 0; i < num; ++i)
        {
            point tmp = q.front();
            q.pop_front();
            avg_y += tmp.y;
            avg_x += tmp.x;
        }
        xL = avg_x / num;
        yL = avg_y / num;

        int xR = xL - (int)(StereoRig.Disparity.at<double>(yL, xL));
        int wL = ImageL.size().width;
        int wR = ImageR.size().width;

        int res = 0;

        // Find on which size object is located
        if (xL >= wL/3)
            res += 1;
        if (xL >= 2*wL/3)
            res += 2;
            
        res += 10;
        if (xR >= wR/3)
            res += 20;
        if (xR >= 2*wR/3)
            res += 30;

        int side = 0;

        if (res <= 20)
            side = 0;
        else if (res == 31)
            side = 1;
        else side = 2;

        boost::python::list ret;
        ret.append(minVal);
        ret.append(side);
        return ret;
}

BOOST_PYTHON_MODULE(Depth_extraction)
{
          using namespace boost::python;
            def("mainRoutine", mainRoutine);
}
