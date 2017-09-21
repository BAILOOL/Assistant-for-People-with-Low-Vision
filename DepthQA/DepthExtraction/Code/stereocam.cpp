#include "stereocam.h"



StereoCam::StereoCam()
{
}

// Declare with Images
StereoCam::StereoCam(Mat ImageLT, Mat ImageRT)
{
    ImageL = ImageLT; ImageR = ImageRT;
    /*GaussianBlur( ImageL,ImageL, Size( 3, 3 ), 0, 0 );
    GaussianBlur( ImageR,ImageR, Size( 3, 3 ), 0, 0 );*/
}

StereoCam::StereoCam(Mat ImageLT, Mat ImageRT, StereoCam Prev_StereoT)
{
    // load images
    ImageL = ImageLT; ImageR = ImageRT;
    /*GaussianBlur( ImageL,ImageL, Size( 3, 3 ), 0, 0 );
    GaussianBlur( ImageR,ImageR, Size( 3, 3 ), 0, 0 );*/

    // Load cameras parameters
    IntrinsicsL = Prev_StereoT.IntrinsicsL; IntrinsicsR = Prev_StereoT.IntrinsicsR;
    DistorsionL = Prev_StereoT.DistorsionL; DistorsionR = Prev_StereoT.DistorsionR;
    Stereo_Rotation = Prev_StereoT.Stereo_Rotation; Stereo_Translation = Prev_StereoT.Stereo_Translation;
    baseline = Prev_StereoT.baseline; Stereo_imgSize = Prev_StereoT.Stereo_imgSize;
    Intrinsics_Rec = Prev_StereoT.Intrinsics_Rec;
    rmap[0][0] = Prev_StereoT.rmap[0][0]; rmap[1][0] = Prev_StereoT.rmap[1][0];
    rmap[0][1] = Prev_StereoT.rmap[0][1]; rmap[1][1] = Prev_StereoT.rmap[1][1];
}

// Open Intrinsic, distorsion and Extrinsic parameters
// The Stereo Parameters are stored in a single yml file
void StereoCam::OpenParams(string ParametersFile) {

    //set up a FileStorage object to read camera params from file
    FileStorage fs;
    fs.open(ParametersFile, FileStorage::READ);

    // read cameras matrices, distortions and extrinsic coefficients from file
    fs["Camera_MatrixL"] >> IntrinsicsL;
    fs["Distortion_CoefficientsL"] >> DistorsionL;
    fs["Camera_MatrixR"] >> IntrinsicsR;
    fs["Distortion_CoefficientsR"] >> DistorsionR;
    fs["Stereo_Rotation"] >> Stereo_Rotation;
    fs["Stereo_Translation"] >> Stereo_Translation;
    fs["image_Width"] >> Stereo_imgSize.width;
    fs["image_Height"] >> Stereo_imgSize.height;
    fs["baseline"] >> baseline;
    // close the input file
    fs.release();
}


// Open Intrinsic, distorsion and Extrinsic parameters
// The Stereo Parameters are stored in a single yml file
void StereoCam::DisplayStereo(string Which) {

    if (Which == "LR" || Which == "RL" ) // Display both images
    {
        namedWindow("ImageL");
        imshow("ImageL",ImageL);
        namedWindow("ImageR");
        imshow("ImageR",ImageR);
        waitKey(1);
    }
    else if (Which == "R") // Display only right image
    {
        namedWindow("ImageR");
        imshow("ImageR",ImageR);
        waitKey(1);
    }
    else if (Which == "L") // Display only left image
    {
        namedWindow("ImageL");
        imshow("ImageL",ImageL);
        waitKey(1);
    }
    else if (Which == "RecL") // Display only left rectified image
    {
        namedWindow("ImageLRec");
        imshow("ImageLRec",ImageL_Rec);
        waitKey(1);
    }
    else if (Which == "RecR") // Display only rectified image
    {
        namedWindow("ImageRRec");
        imshow("ImageRRec",ImageR_Rec);
        waitKey(1);
    }
    else if (Which == "RecLR" || Which == "RecRL") // Display both rectified images
    {
        namedWindow("ImageRRec");
        imshow("ImageRRec",ImageR_Rec);
        namedWindow("ImageLRec");
        imshow("ImageLRec",ImageL_Rec);
        waitKey(1);
    }
}

// Rectify stereo Images and save new parameters
void StereoCam::RectifyStereo(){

    // Init Rectification (if LUT is empty)
    if (rmap[0][0].empty()) {
    Mat R1,R2,P1,P2,Q;
    SizeAfterRec = Stereo_imgSize;

    // Rectification parameters
    stereoRectify(IntrinsicsL, DistorsionL, IntrinsicsR, DistorsionR, Stereo_imgSize, Stereo_Rotation, Stereo_Translation, R1, R2, P1, P2, Q,CV_CALIB_ZERO_DISPARITY,0, SizeAfterRec);

    //Left
    initUndistortRectifyMap(IntrinsicsL, DistorsionL, R1, P1, SizeAfterRec, CV_16SC2 , rmap[0][0], rmap[0][1]);
    //Right
    initUndistortRectifyMap(IntrinsicsR, DistorsionR, R2, P2, SizeAfterRec, CV_16SC2 , rmap[1][0], rmap[1][1]);

    // Save new parameters
    Mat TK(3,3,CV_64F);
    TK.at<double>(0,0) = P1.at<double>(0,0); TK.at<double>(0,1) = P1.at<double>(0,1); TK.at<double>(0,2) = P1.at<double>(0,2);
    TK.at<double>(1,0) = P1.at<double>(1,0); TK.at<double>(1,1) = P1.at<double>(1,1); TK.at<double>(1,2) = P1.at<double>(1,2);
    TK.at<double>(2,0) = P1.at<double>(2,0); TK.at<double>(2,1) = P1.at<double>(2,1); TK.at<double>(2,2) = P1.at<double>(2,2);
    Intrinsics_Rec = TK.clone();

    // Display
    cout << "K" << Intrinsics_Rec << endl;

    Mat TTK(3,1,CV_64F);
    TTK.at<double>(0,0) = P2.at<double>(0,3); TTK.at<double>(1,0) = P2.at<double>(1,3);TTK.at<double>(2,0) = P2.at<double>(2,3);
    Mat Temp = TK.inv()*TTK;
    baseline = abs(Temp.at<double>(0,0));

    cout << "baseline" << baseline << endl;
    Stereo_imgSize = SizeAfterRec;

    // Rectify images
    remap(ImageL, ImageL_Rec, rmap[0][0], rmap[0][1], INTER_LINEAR);
    remap(ImageR, ImageR_Rec, rmap[1][0], rmap[1][1], INTER_LINEAR);
    }
    else {

    // Equalize image
    /*Mat1b ImageL_Gray_Ad, ImageR_Gray_Ad ;
    equalizeHist(ImageL_Gray, ImageL_Gray_Ad);
    equalizeHist(ImageR_Gray, ImageR_Gray_Ad);
    ImageL_Gray_Ad.copyTo(ImageL_Gray);
    ImageR_Gray_Ad.copyTo(ImageR_Gray);*/

    // Rectify images
    remap(ImageL, ImageL_Rec, rmap[0][0], rmap[0][1], INTER_LINEAR);
    remap(ImageL, ImageR_Rec, rmap[1][0], rmap[1][1], INTER_LINEAR);
    }
}

Mat StereoCam::ComputeDisparity(double scale, bool display){

    clock_t startTime = clock();
    // Prepare the data to be compatible with elasMP
    Mat lt,rt;
    if(ImageL_Rec.channels()==3){cvtColor(ImageL_Rec,lt,CV_BGR2GRAY);}
    else lt = ImageL_Rec.clone();
    if(ImageR_Rec.channels()==3)cvtColor(ImageR_Rec,rt,CV_BGR2GRAY);
    else rt = ImageR_Rec.clone();
    int bd =0; // border ??

    // Rescale
    int sx = lt.cols*scale; int sy = lt.rows*scale;
    Mat l(sy, sy, CV_8UC1 ); Mat r(sx, sy, CV_8UC1 );
    resize(lt, l, Size(sx,sy));
    resize(rt, r, Size(sx,sy));

    // border if needed
    Mat lb,rb;
    cv::copyMakeBorder(l,lb,0,0,bd,bd,cv::BORDER_REPLICATE);
    cv::copyMakeBorder(r,rb,0,0,bd,bd,cv::BORDER_REPLICATE);

    const cv::Size imsize = rb.size();
    const int32_t dims[3] = {imsize.width,imsize.height,imsize.width}; // bytes per line = width

    Elas::parameters param;
    param.postprocess_only_left = true;
    Elas elas(param);

    cv::Mat leftdpf = cv::Mat::zeros(imsize,CV_32F);
    cv::Mat rightdpf = cv::Mat::zeros(imsize,CV_32F);

    // disparity computation
    elas.process(lb.data,rb.data,leftdpf.ptr<float>(0),rightdpf.ptr<float>(0),dims);

    //Return values
    Mat disp, leftdisp, rightdisp;
    Mat(leftdpf(cv::Rect(bd,0,l.cols,l.rows))).copyTo(disp);
    disp.convertTo(leftdisp,CV_16S,16);

    // Rescale at original scale
    resize(disp*(1/scale), Disparity, Size(ImageL_Rec.cols,ImageL_Rec.rows));

    cout << "TIME disparity : " << double( clock() - startTime )*1000 / (double)CLOCKS_PER_SEC<< " milli seconds." << endl;

    if (display == true){
        // view
        Mat show;
        resize(leftdisp*(1/scale), leftdisp, Size(ImageL_Rec.cols,ImageL_Rec.rows));
        leftdisp.convertTo(show,CV_8U,1.0/8);
        //imshow("disp",show);
        //waitKey(1);
        return show;
    }
}

// Compute depth map from disparity
void StereoCam::ComputeDepthMap(){
    Disparity.convertTo(Disparity, CV_64F);
    DepthMap = Intrinsics_Rec.at<double>(0,0)*(baseline/Disparity);
    //DepthMap.convertTo(DepthMap, CV_64F);
    //cout << "DepthMap type = " << DepthMap.type() << endl;
}
