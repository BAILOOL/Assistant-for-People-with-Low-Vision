import numpy as np
import matplotlib.pyplot as plt
import os
import sys

import gensim
import os
import subprocess
import sys
import cv2
import scipy.misc
import skimage.io
import time

caffe_root = '/home/oleg/Desktop/QuestionAnswering/CAFFE_ROOT'
os.chdir(caffe_root)
sys.path.append(caffe_root) 
sys.path.insert(0, 'python')
import caffe

from skimage.transform import resize
from google.protobuf import text_format
from caffe.proto import caffe_pb2
from nltk.tag import pos_tag

import httplib
import json

import pdb

sys.path.insert(0, 'python')

os.environ['GLOG_minloglevel'] = '1'
#os.chdir(caffe_root)

#constants
gradCamPrototxt='models/grad-cam/GoogleNetCOCO.prototxt'
gradCamCaffemodel='models/grad-cam/GoogleNetCOCO.caffemodel'
ssdPrototxt = 'models/VGGNet/coco/SSD_512x512/deploy.prototxt'
ssdCaffeModel = 'models/VGGNet/coco/SSD_512x512/VGG_coco_SSD_512x512_iter_360000.caffemodel'


dataFolder = caffe_root+'/data/demo/'
interComData = caffe_root+'/examples/InterFiles/'

class DepthQuestionAnwering(object):
    def __init__(self):
        self.genSimModel = gensim.models.Word2Vec.load_word2vec_format(caffe_root+'/examples/GoogleNews-vectors-negative300.bin', binary=True) 
        print '\n Loaded Gensim model \n'

        # load PASCAL VOC labels
        labelmap_file = caffe_root+'/data/coco/labelmap_coco.prototxt'
        file = open(labelmap_file, 'r')
        self.labelmap = caffe_pb2.LabelMap()
        text_format.Merge(str(file.read()), self.labelmap)

        #load grad-cam caffe. It's not used in this experiment
        #caffe.set_device(0)
        #caffe.set_mode_gpu()
        #gradCamNet = caffe.Net(gradCamPrototxt, gradCamCaffemodel, caffe.TEST)
        #print '\n Loaded grad-cam caffe \n'

        #load SSD caffe
        caffe.set_device(0)
        caffe.set_mode_gpu()
        self.ssdNet = caffe.Net(ssdPrototxt, ssdCaffeModel,caffe.TEST)
        print '\n Loaded ssd caffe \n'

        # input preprocessing: 'data' is the name of the input blob == net.inputs[0]
        transformer = caffe.io.Transformer({'data': self.ssdNet.blobs['data'].data.shape})
        transformer.set_transpose('data', (2, 0, 1))
        transformer.set_mean('data', np.array([104,117,123])) # mean pixel
        transformer.set_raw_scale('data', 255)  # the reference model operates on images in [0,255] range instead of [0,1]
        transformer.set_channel_swap('data', (2,1,0))  # the reference model has channels in BGR order instead of RGB
        self.transformer = transformer

        # set net to batch size of 1
        image_resize = 512
        self.ssdNet.blobs['data'].reshape(1,3,image_resize,image_resize)

        #os.chdir('/home/bosch-server/Desktop/CurrentCode/')

    def get_labelname(self,labelmap, labels):
        num_labels = len(labelmap.item)
        labelnames = []
        if type(labels) is not list:
            labels = [labels]
        for label in labels:
            found = False
            for i in xrange(0, num_labels):
                if label == labelmap.item[i].label:
                    found = True
                    labelnames.append(labelmap.item[i].display_name)
                    break
            assert found == True
        return labelnames        

    #get relavant objects
    def get_rel_objects(self,words,classes,conf,all_dets):
        rel_dets = []
        rel_classes = []
        rel_conf = []
        max_acc = 0.0;
        #get best match accuracy
        for i in range(len(words)):
           for j in range(len(classes)):
              try:
                 sim = self.genSimModel.similarity(classes[j], words[i])
              except KeyError:
                 sim = -1
              max_acc = max(max_acc,sim)
        if max_acc<0.5: #make sure the highest similarity match is a strong match
           max_acc=0.0
             
        #words to class idx
        for i in range(len(words)):
           for j in range(len(classes)):
              try:
                 sim = self.genSimModel.similarity(classes[j],words[i])
              except KeyError:
                 sim = -1
              if sim==max_acc:
                 rel_dets.append(all_dets[j][0:4])                
                 rel_classes.append(classes[j])
                 rel_conf.append(conf[j])
              
        return (rel_conf,rel_classes,rel_dets)

                        
    def getDepthInfo(self,imgPathL, imgPathR, interComData,rel_dets,relSegMask):
        #writing bbox and segmenation to a file
        f = open(interComData+'Bbox.txt','w')
        for i in range(len(rel_dets)):
            f.write('%d %d %d %d\n' %(round(rel_dets[i][0]),round(rel_dets[i][1]),round(rel_dets[i][2]),round(rel_dets[i][3])))
        f.close()

        #saving segmantation mask
        scipy.misc.toimage(relSegMask, cmin=0.0, cmax=255.0).save(interComData+'SegMask.png')
        print "Finished saving bbox and segmentation mask"
        
        print "Start extracting depth map"
        leftImage = imgPathL
        rightImage = imgPathR
        BboxPath = interComData+'Bbox.txt'
        segmaskPath = interComData+'SegMask.png'
        disparityPath = interComData+'Disparity.jpg'
        distancePath = interComData+'Distance.txt'
        heightWidthPath = interComData+'HeightWidth.txt'
        paramPath = '/home/bosch-server/Desktop/CurrentCode/caffe/data/demo/'+'StereoParams.yml'
        #pdb.set_trace()
        subprocess.call("./examples/DepthExtraction/cmakeTest/Depth_extraction "+leftImage+" "+rightImage+" "+BboxPath+" "+segmaskPath+" "+disparityPath+" "+distancePath+" "+paramPath+" "+heightWidthPath, shell=True)
        print "Finish extracting depth map"
        
        #read distances
        distances = [];
        f = open(interComData+'Distance.txt','r')
        for line in f:
            data = line.split()
            distances.append((float(data[0]),float(data[1]),float(data[2])))
        f.close()
        
        disparity = skimage.io.imread(disparityPath)
        
        return (distances,disparity)

    def getRealSegmentation(self,image_path, interComData):
        subprocess.call("python getSegmentation.py -i "+image_path+" -o "+interComData+"SegMask.png", shell=True, cwd="/home/bosch-server/Desktop/CRFasRNN/crfasrnn/caffe/examples/python-scripts/")
        #testRequest = json.dumps({'inputPath': image_path, 'outputPath': interComData+'realSegMask.png'}, sort_keys=False, indent=4, separators=(',', ': '))
        #c = httplib.HTTPConnection('localhost', 8080)
        #c.request('POST', '/process', testRequest)
        #doc = c.getresponse().read()
        #print doc
        relSegMask = skimage.io.imread(interComData+'SegMask.png')
        #relSegMask = cv2.imread(interComData+'realSegMask.png')
        #relSegMask = cv2.cvtColor(relSegMask, cv2.COLOR_BGR2GRAY)
        return relSegMask

    def getSingleObject(self,rel_box,distances,index):
        if index == 0:
            left_idx = sorted(range(len(rel_box)), key=lambda k: rel_box[k][2], reverse=False)
            final_dist = [ distances[i] for i in left_idx]
            final_box = [ rel_box[i] for i in left_idx]
        elif index == 1:
            right_idx = sorted(range(len(rel_box)), key=lambda k: rel_box[k][2], reverse=True) #rightxcord
            final_dist = [ distances[i] for i in right_idx]
            final_box = [ rel_box[i] for i in right_idx]
        elif index==3:
            bottom_idx = sorted(range(len(rel_box)), key=lambda k: rel_box[k][3], reverse=True) #bottomycord
            final_dist = [ distances[i] for i in bottom_idx]
            final_box = [ rel_box[i] for i in bottom_idx]
        elif index==2:
            top_idx = sorted(range(len(rel_box)), key=lambda k: rel_box[k][1], reverse=False) #topycord
            final_dist = [ distances[i] for i in top_idx]
            final_box = [ rel_box[i] for i in top_idx]
        elif index==4:
            depth_idx = sorted(range(len(distances)), key=lambda k: distances[k][0], reverse=True) #front object
            final_dist = [ distances[i] for i in depth_idx]
            final_box = [ rel_box[i] for i in depth_idx]
        elif index==5:
            depth_idx = sorted(range(len(distances)), key=lambda k: distances[k][0], reverse=False) #back object
            final_dist = [ distances[i] for i in depth_idx]
            final_box = [ rel_box[i] for i in depth_idx]
        else:
            print 'GetSingleObject index is out of range'

        return (final_dist[0], final_box[0])    

    def getFullResponse(self, imgPathL, imgPathR,question):
        #loop through images
        start_time=time.time()
        image = caffe.io.load_image(imgPathL)
            
        transformed_image = self.transformer.preprocess('data', image)
        self.ssdNet.blobs['data'].data[...] = transformed_image
            
        # Forward pass.
        detections = self.ssdNet.forward()['detection_out']
            
        # Parse the outputs.
        det_label = detections[0,0,:,1]
        det_conf = detections[0,0,:,2]
        det_xmin = detections[0,0,:,3]
        det_ymin = detections[0,0,:,4]
        det_xmax = detections[0,0,:,5]
        det_ymax = detections[0,0,:,6]
            
        # Get detections with confidence higher than 0.6.
        top_indices = [i for i, conf in enumerate(det_conf) if conf >= 0.6]
            
        top_conf = det_conf[top_indices]
        top_label_indices = det_label[top_indices].tolist()
        top_labels = self.get_labelname(self.labelmap, top_label_indices)
        top_xmin = np.reshape(det_xmin[top_indices],(len(top_indices),1,))
        top_ymin = np.reshape(det_ymin[top_indices],(len(top_indices),1,))
        top_xmax = np.reshape(det_xmax[top_indices],(len(top_indices),1,))
        top_ymax = np.reshape(det_ymax[top_indices],(len(top_indices),1,))
            
        bbox = np.round(np.hstack((np.hstack((top_xmin*image.shape[1],top_ymin*image.shape[0])),np.hstack((top_xmax*image.shape[1],top_ymax*image.shape[0])))))
        bbox = bbox.astype(int)

        #preprocessing the question
        #left,right,back,front,bottom,top"
        locationHashTable = {'left': 0,'right' : 1,'top' : 2,'bottom' : 3, 'back' : 4,'front' : 5}
        
        tagged_sent = pos_tag(question.split())
        properNouns = [word for word,pos in tagged_sent if pos == 'NN']
        
        #get relevant to the question object in the scene
        (rel_conf, rel_label, rel_box) = self.get_rel_objects(properNouns,top_labels,top_conf,bbox)

        #get segmentation of the scene
        relSegMask = np.asarray(self.getRealSegmentation(imgPathL,interComData))
        #make all the values above zero 1
        relSegMask[np.where(relSegMask>0)] = 255

        #get depth to all relevant objects
        (distances,disparity) = self.getDepthInfo(imgPathL,imgPathR,interComData,rel_box,relSegMask)
                         
        #get single object that user is curious about
        location = -1;
        for i in range(len(properNouns)):
            if (properNouns[i] in locationHashTable):
                location = locationHashTable[properNouns[i]]
        
        if len(rel_box)>1:
            (final_dist, final_box) = self.getSingleObject(rel_box,distances,location)
        elif len(rel_box)==1:
            final_dist = distances 
            final_box = rel_box
        else:
            print "Nothing found"
                        
        elapsed_time=time.time()-start_time
                
        print "Time elapsed %.5f \n" % (elapsed_time)

        return (final_dist[0],final_dist[1],final_dist[2])

#z = DepthQuestionAnwering()
#question = "How far is the car on the left"
#test = z.getFullResponse(caffe_root+'/examples/camera_Left_000058.png',caffe_root+'/examples/camera_Right_000058.png',question)
#print "Got here"
#print test