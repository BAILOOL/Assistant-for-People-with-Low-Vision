#include <iostream>
#include <stdio.h>
#include <fstream>
#include <iomanip>
#include <sstream> //Just to record frames
#include <opencv2/opencv.hpp>
#include "stereocam.h"


double computeDistance2(vector<int> box, Mat mask, Mat depth){
    cout<<"Entered computeDistance"<<endl;
    vector<double> distances;
    for (int i=box[1];i<=box[3];++i){
        for(int j=box[0];j<=box[2];++j){
            //cout<<mask.at<uchar>(i,j)<<endl;
            if (mask.at<uchar>(i,j)==255){
                //cout<<"Entered"<<endl;
                distances.push_back(depth.at<double>(i,j));
             }
        }
    }
//mean calculation :accumulate( distances.begin(), distances.end(), 0.0 )/ distances.size();
    
    double median;
    int size = distances.size();
    if (size==0) median = 0.0;
    else median = (size%2==0)? (distances[size/2-1]+distances[size/2])/2 : distances[size/2];
    return median; 
}

double computeDistance(vector<int> box, Mat mask, Mat depth){
    cout<<"Entered computeDistance"<<endl;
    vector<double> distances;
    double numarator = 0;
    double denominator = 0.0000000001;
    for (int i=box[1];i<=box[3];++i){
        for(int j=box[0];j<=box[2];++j){
            //cout<<mask.at<uchar>(i,j)<<endl;
            //&& mask.at<int>(i,j)>0.0
            if (depth.at<double>(i,j)>0.0 && depth.at<double>(i,j)<25.0 && mask.at<int>(i,j)>0.0){
                numarator += mask.at<int>(i,j)*depth.at<double>(i,j);
                denominator += mask.at<int>(i,j);
                distances.push_back(depth.at<double>(i,j));
            }
        }
    }
    double median;
    sort(distances.begin(),distances.end());
    int size = distances.size();
    if (size==0) median = 0.0;
    else median = (size%2==0)? (distances[size/2-1]+distances[size/2])/2 : distances[size/2];
    return median; 
    //return numarator/denominator; 
}

vector<double> computeHW(vector<int> box,double distance, Mat K){
    vector<double> heightWidth;

    int xL = box[0];
    int xR = box[2];
    int yT = box[1];
    int yB = box[3];
    int xM = (xR-xL)/2;
    int yM = (yB-yT)/2;

    Mat pT(3,1,CV_64F); pT.at<double>(0) = xM; pT.at<double>(1) = yT; pT.at<double>(2) = 1;
    Mat pB(3,1,CV_64F); pB.at<double>(0) = xM; pB.at<double>(1) = yB; pB.at<double>(2) = 1;
    Mat pL(3,1,CV_64F); pL.at<double>(0) = xL; pL.at<double>(1) = yM; pL.at<double>(2) = 1;
    Mat pR(3,1,CV_64F); pR.at<double>(0) = xR; pR.at<double>(1) = yM; pR.at<double>(2) = 1;

    Mat vT = K.inv()*pT;
    Mat vB = K.inv()*pB;
    Mat vL = K.inv()*pL;
    Mat vR = K.inv()*pR;

    Mat newPT = vT.mul(distance);
    Mat newPB = vB.mul(distance);
    Mat newPL = vL.mul(distance);
    Mat newPR = vR.mul(distance);

    double height = sqrt(pow(newPT.at<double>(0)-newPB.at<double>(0),2)+pow(newPT.at<double>(1)-newPB.at<double>(1),2)+pow(newPT.at<double>(2)-newPB.at<double>(2),2));

    double width = sqrt(pow(newPL.at<double>(0)-newPR.at<double>(0),2)+pow(newPL.at<double>(1)-newPR.at<double>(1),2)+pow(newPL.at<double>(2)-newPR.at<double>(2),2));

    heightWidth.push_back(height);
    heightWidth.push_back(width);

    return heightWidth;
}

int main(int argc, char *argv[])
{
    /*
    QCoreApplication a(argc, argv);

    //Record video
    stringstream ssLeft, ssRight;
    //string nameLeft = "/home/alex/Desktop/Alex stereo data/Extracted/Rectified/Left";
    //string nameRight = "/home/alex/Desktop/Alex stereo data/Extracted/Rectified/Right";
    //string nameDisparity = "/home/alex/Desktop/Alex stereo data/Extracted/Rectified/Disparity";
    //string type = ".png";
    // Read images sequence
    //string argL = "/home/francoisr/Documents/C++_Projects/Rectify Stereo/untitled/Images/Left/ImageL%05d.png";
    //VideoCapture sequenceL(argL);
    //string argR = "/home/francoisr/Documents/C++_Projects/Rectify Stereo/untitled/Images/Right/ImageR%05d.png";
    //VideoCapture sequenceR(argR);
    //string left_path = "/home/alex/Desktop/Alex stereo data/Extracted/left6.jpg";
    //string right_path = "/home/alex/Desktop/Alex stereo data/Extracted/right6.jpg";


    // input arguments
    string left_path = argv[1];
    string right_path = argv[2];
    string bboxPath = argv[3];
    string segMaskPath = argv[4];
    string nameDisparity = argv[5];
    string type = ".png";

    //Prepare the class stereocam and read the parameters
    StereoCam StereoRig;
    string filename ="/home/alex/Desktop/Alex stereo data/Extracted/StereoParamsAlex.yml";
    StereoRig.OpenParams(filename);

        // Load images
        //sequenceL >> ImageL;
        //sequenceR >> ImageR;
        Mat ImageL = imread(left_path,CV_LOAD_IMAGE_COLOR);
        Mat ImageR = imread(right_path,CV_LOAD_IMAGE_COLOR);
        ImageL.copyTo(StereoRig.ImageL);
        ImageR.copyTo(StereoRig.ImageR);

        // Rectify
        StereoRig.RectifyStereo();

        // Display
        //imshow("Rectified Left", StereoRig.ImageL_Rec);
        //imshow("Rectified Right", StereoRig.ImageR_Rec);
        //waitKey(1);

        //int aa = 6;
        // Save images
        /*
        vector<int> compression_params;
        compression_params.push_back(CV_IMWRITE_PNG_COMPRESSION);
        compression_params.push_back(9);

        ssLeft<<nameLeft<< setfill('0') << setw(5) << aa <<type;
        string filenameimwleft = ssLeft.str();
        ssLeft.str("");
        imwrite(filenameimwleft, StereoRig.ImageL_Rec, compression_params);

        ssRight<<nameRight<< setfill('0') << setw(5) << aa <<type;
        string filenameimwright = ssRight.str();
        ssRight.str("");
        imwrite(filenameimwright, StereoRig.ImageR_Rec, compression_params);
        */
        // Compute disparity
        /*
        Mat DispMap = StereoRig.ComputeDisparity(1,true);
        /*cv::rectangle(DispMap,cvPoint(457,281),
                      cvPoint(601,545),
                      CV_RGB(255,255,255), 5, 8);
        */
        //imshow("disp",DispMap);

        //save depth map
        /*
        vector<int> compression_params;
        compression_params.push_back(CV_IMWRITE_PNG_COMPRESSION);
        compression_params.push_back(9);
        ssLeft<<nameDisparity<< setfill('0') << setw(5) << aa <<type;
        string filenameimwdepth = ssLeft.str();
        imwrite(filenameimwdepth, DispMap, compression_params);

        // Depth from disparity
        StereoRig.ComputeDepthMap();
        /*double minDist = 100000;
        for (int i=457;i<=601;++i){
            for (int j=281;j<545;++j){
                minDist = min(minDist,StereoRig.DepthMap.at<double>(i,j));
            }
        }
        cout<< "Distance to the object = " << minDist << "m" << endl;
        */

        //write to file
        //cout<<"Startining writing to a file"<<endl;
        //FileStorage file("/home/alex/Desktop/Alex stereo data/Extracted/Rectified/depth6.yml", cv::FileStorage::WRITE);
        //file << "depth" << StereoRig.DepthMap;
        //cout<<"Writing to file is completed"<<endl;


        //new version for command line

        //QCoreApplication a(argc, argv);

        // input arguments
        string left_path = argv[1];
        string right_path = argv[2];
        string bboxPath = argv[3];
        string segMaskPath = argv[4];
        string pathDisp = argv[5];
        string pathDist = argv[6];
        string pathParam = argv[7];
        //string pathHW = argv[8];
        

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

            // Display
            //imshow("Rectified Left", StereoRig.ImageL_Rec);
            //imshow("Rectified Right", StereoRig.ImageR_Rec);
            //waitKey(1);


            // Save images
            /*
            vector<int> compression_params;
            compression_params.push_back(CV_IMWRITE_PNG_COMPRESSION);
            compression_params.push_back(9);
            /*
            ssLeft<<nameLeft<< setfill('0') << setw(5) << aa <<type;
            string filenameimwleft = ssLeft.str();
            ssLeft.str("");
            imwrite(filenameimwleft, StereoRig.ImageL_Rec, compression_params);

            ssRight<<nameRight<< setfill('0') << setw(5) << aa <<type;
            string filenameimwright = ssRight.str();
            ssRight.str("");
            imwrite(filenameimwright, StereoRig.ImageR_Rec, compression_params);
            */

            // Compute disparity

            Mat DispMap = StereoRig.ComputeDisparity(1,true);
            //save depth map
            vector<int> compression_params;
            compression_params.push_back(CV_IMWRITE_PNG_COMPRESSION);
            compression_params.push_back(9);
            imwrite(pathDisp, DispMap, compression_params);

            // Depth from disparity
            StereoRig.ComputeDepthMap();

            //read bounding boxes
            vector<vector<int> > bboxVector;
            ifstream myfile (bboxPath);
              if (myfile.is_open())
              {
                string line;
                while ( getline (myfile,line) )
                {
                  istringstream iss(line);
                  vector<int> temp;
                  for(string s; iss >> s; ){
                      temp.push_back(stoi(s));
                  }

                  bboxVector.push_back(temp);
                }
                myfile.close();
              }
              else cout << "Unable to open file";

            //read segmentation mask
            Mat segMask = imread(segMaskPath,CV_LOAD_IMAGE_UNCHANGED);
//cout<<segMask<<endl;
            //imshow("disp",segMask);
//cout<<"Supposed to print stuff"<<endl;

            //calculate distance to the object
            vector<double> distVector;
            vector<double> heightVector;
            vector<double> widthVector;
            for (int i =0; i<bboxVector.size();++i){
                double dist = computeDistance(bboxVector[i],segMask, StereoRig.DepthMap);
                distVector.push_back(dist);

                //calculate height and width of the object
                vector<double> tempHW = computeHW(bboxVector[i],dist,StereoRig.Intrinsics_Rec);
                heightVector.push_back(tempHW[0]);
                widthVector.push_back(tempHW[1]);
            }

            //write distance to the file
            ofstream myfile1;
            myfile1.open (pathDist);
            for (int i =0; i<distVector.size();++i)
                myfile1 << distVector[i] << " " << heightVector[i] << " " << widthVector[i] << endl;
            myfile1.close();

            //write height and width to the file
/*            
            myfile1;
            myfile1.open (pathHW);
            for (int i =0; i<heightVector.size();++i)
                myfile1 << heightVector[i] << " " << widthVector[i] << endl;
            myfile1.close();
*/
    return 0;
}


