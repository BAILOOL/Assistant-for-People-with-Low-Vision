import numpy as np
import cv2
import mxnet as mx
import time
import os
import sys
sys.path.append("/home/richard/Desktop/emotion-recognition-neural-networks-master/detection/symbol")
from resnet import *
from config import config
from processing import bbox_pred, clip_boxes, nms
from detection import resize, ch_dev

import pdb

class DetectFaceClass:
	"""docstring for ClassName"""
	def __init__(self, gpu, prefix, epoch, thresh, nms_thresh, scale, maxscale):
		#super(ClassName, self).__init__()
		self.gpu = gpu
		self.prefix = prefix
		self.epoch = epoch
		self.thresh = thresh
		self.nms_thresh = nms_thresh
		self.scale = scale
		self.max_scale = maxscale
		self.ctx = mx.gpu(self.gpu)
		self._, self.arg_params, self.aux_params = mx.model.load_checkpoint(self.prefix, self.epoch)
		self.arg_params, self.aux_params = ch_dev(self.arg_params, self.aux_params, self.ctx)
		self.sym = resnet_50(num_class=2)

	def loadImg(self, img):
		#color = cv2.imread(img_path)
		#gray  = cv2.cvtColor(img, cv2.COLOR_BGR2RGB)
		gray, scale = resize(img.copy(), self.scale, self.max_scale)
		im_info = np.array([[gray.shape[0], gray.shape[1], scale]], dtype=np.float32)  # (h, w, scale)
		#gray = gray[:, scale]
		gray = np.swapaxes(gray, 0, 2)
		gray = np.swapaxes(gray, 1, 2)  # change to (c, h, w) order
		#pdb.set_trace()
		gray = gray[np.newaxis, :]  # extend to (n, c, h, w)
		ls = [gray, im_info, scale]
		return ls

	def loadResnet(self, img, im_info):
		self.arg_params["data"] = mx.nd.array(img, self.ctx)
		self.arg_params["im_info"] = mx.nd.array(im_info, self.ctx)
		exe = self.sym.bind(self.ctx, self.arg_params, args_grad=None, grad_req="null", aux_states = self.aux_params)
		return exe

	def detect_Face(self, img):
		gray, im_info, scale = self.loadImg(img)[0], self.loadImg(img)[1], self.loadImg(img)[2]
		exe = self.loadResnet(gray, im_info)
		tic = time.time()
		exe.forward(is_train=False)
		output_dict = {name: nd for name, nd in zip(self.sym.list_outputs(), exe.outputs)}
		rois = output_dict['rpn_rois_output'].asnumpy()[:, 1:]  # first column is index
		scores = output_dict['cls_prob_reshape_output'].asnumpy()[0]
		bbox_deltas = output_dict['bbox_pred_reshape_output'].asnumpy()[0]
		pred_boxes = bbox_pred(rois, bbox_deltas)
		pred_boxes = clip_boxes(pred_boxes, (im_info[0][0], im_info[0][1]))
		cls_boxes = pred_boxes[:, 4:8]
		cls_scores = scores[:, 1]
		keep = np.where(cls_scores >= self.thresh)[0]
		cls_boxes = cls_boxes[keep, :]
		cls_scores = cls_scores[keep]
		dets = np.hstack((cls_boxes, cls_scores[:, np.newaxis])).astype(np.float32)
		keep = nms(dets.astype(np.float32), self.nms_thresh)		
		dets = dets[keep, :]
		toc = time.time()

		print "time cost is:{}s".format(toc-tic)
		#pdb.set_trace()
		'''
		for i in range(dets.shape[0]):
		   	bbox = dets[i, :4]
		   	cv2.rectangle(img, (int(round(bbox[0]/scale)), int(round(bbox[1]/scale))),
		               (int(round(bbox[2]/scale)), int(round(bbox[3]/scale))),  (0, 0, 255), 10)
		'''
		faces = []
		for i in range(dets.shape[0]):
			bbox = dets[i, :4]
			y1 = int(round(bbox[1]/scale))
			y2 = int(round(bbox[3]/scale))
			x1 = int(round(bbox[0]/scale))
			x2 = int(round(bbox[2]/scale))
			if y2 - y1 > x2 - x1:
				new_x1 = x1 - (y2 - y1 - x2 + x1)/ 4
				new_x2 = x2 + (y2 - y1 - x2 + x1)/ 4
				new_y1 = y1 + (y2 - y1 - x2 + x1)/ 4
				new_y2 = y2 - (y2 - y1 - x2 + x1)/ 4
				if new_y2 - new_y1 > new_x2 - new_x1:
					new_x2 = new_y2 - new_y1 + new_x1
				else:
					new_y2 = new_x2 - new_x1 + new_y1
			else:
				new_x1 = x1 +(y2 - y1 - x2 + x1)/ 4
				new_x2 = x2 - (y2 - y1 - x2 + x1)/ 4
				new_y1 = y1 - (y2 - y1 - x2 + x1)/ 4
				new_y2 = y2 + (y2 - y1 - x2 + x1)/ 4
				if new_y2 - new_y1 > new_x2 - new_x1:
					new_x2 = new_y2 - new_y1 + new_x1
				else:
					new_y2 = new_x2 - new_x1 + new_y1

			face = (new_x1, new_y1, new_x2, new_y2)
			faces.append(face)
		#test if it works
		#cv2.imshow("Image", img)  
		#cv2.waitKey(0)
		
		# if you need rectangle coordinates return bbox
		return faces
