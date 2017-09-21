import cv2
import caffe
import vqa_data_provider_layer

import os
import sys

from vqa_data_provider_layer import LoadVQADataProvider
import numpy as np
from skimage.transform import resize

import pdb
# constants
GPU_ID = 1
RESNET_MEAN_PATH = "/home/oleg/Desktop/QuestionAnswering/CAFFE_ROOT/preprocess/ResNet_mean.binaryproto"
RESNET_LARGE_PROTOTXT_PATH = "/home/oleg/Desktop/QuestionAnswering/CAFFE_ROOT/preprocess/ResNet-152-448-deploy.prototxt"
RESNET_CAFFEMODEL_PATH = "/home/oleg/Desktop/QuestionAnswering/CAFFE_ROOT/preprocess/ResNet-152-model.caffemodel"
EXTRACT_LAYER = "res5c"
EXTRACT_LAYER_SIZE = (2048, 14, 14)
TARGET_IMG_SIZE = 448
VQA_PROTOTXT_PATH = "/home/oleg/Desktop/QuestionAnswering/CAFFE_ROOT/multi_att_2_glove_pretrained/proto_test_batchsize1.prototxt"
VQA_CAFFEMODEL_PATH = "/home/oleg/Desktop/QuestionAnswering/CAFFE_ROOT/multi_att_2_glove_pretrained/_iter_190000.caffemodel"
VDICT_PATH = "/home/oleg/Desktop/QuestionAnswering/CAFFE_ROOT/multi_att_2_glove_pretrained/vdict.json"
ADICT_PATH = "/home/oleg/Desktop/QuestionAnswering/CAFFE_ROOT/multi_att_2_glove_pretrained/adict.json"

class QuestionAnswering(object):
    def __init__(self):
        self.vqa_data_provider = LoadVQADataProvider(VDICT_PATH, ADICT_PATH, batchsize=1, \
                                                mode='test', data_shape=EXTRACT_LAYER_SIZE)
        vqa_data_provider_layer.CURRENT_DATA_SHAPE = EXTRACT_LAYER_SIZE

        # mean substraction
        self.blob = caffe.proto.caffe_pb2.BlobProto()
        self.data = open(RESNET_MEAN_PATH, 'rb').read()
        self.blob.ParseFromString(self.data)
        self.resnet_mean = np.array(caffe.io.blobproto_to_array(self.blob)).astype(np.float32).reshape(3, 224, 224)
        self.resnet_mean = np.transpose(cv2.resize(np.transpose(self.resnet_mean, (1, 2, 0)), (448, 448)), (2, 0, 1))

        # resnet
        caffe.set_device(GPU_ID)
        caffe.set_mode_gpu()

        self.resnet_net = caffe.Net(RESNET_LARGE_PROTOTXT_PATH, RESNET_CAFFEMODEL_PATH, caffe.TEST)

        # our net
        self.vqa_net = caffe.Net(VQA_PROTOTXT_PATH, VQA_CAFFEMODEL_PATH, caffe.TEST)


    def trim_image(self, img):
        y, x, c = img.shape
        if c != 3:
            raise Exception('Expected 3 channels in the image')
        resized_img = cv2.resize(img, (TARGET_IMG_SIZE, TARGET_IMG_SIZE))
        transposed_img = np.transpose(resized_img, (2, 0, 1)).astype(np.float32)
        ivec = transposed_img - self.resnet_mean
        return ivec

    def make_rev_adict(self, adict):
        """
        An adict maps text answers to neuron indices. A reverse adict maps neuron
        indices to text answers.
        """
        rev_adict = {}
        for k, v in adict.items():
            rev_adict[v] = k
        return rev_adict

    def softmax(self, arr):
        e = np.exp(arr)
        dist = e / np.sum(e)
        return dist

    def downsample_image(self, img):
        img_h, img_w, img_c = img.shape
        img = resize(img, (448 * img_h / img_w, 448))
        return img

    def save_attention_visualization(self, source_img_path, att_map, dest_name, viz_folder):
        """
        Visualize the attention map on the image and save the visualization.
        """
        img = cv2.imread(source_img_path)  # cv2.imread does auto-rotate

        # downsample source image
        img = self.downsample_image(img)
        img_h, img_w, img_c = img.shape

        _, att_h, att_w = att_map.shape
        att_map = att_map.reshape((att_h, att_w))

        # upsample attention map to match original image
        upsample0 = resize(att_map, (img_h, img_w), order=3)  # bicubic interpolation
        upsample0 = upsample0 / upsample0.max()

        # create rgb-alpha
        rgba0 = np.zeros((img_h, img_w, img_c + 1))
        rgba0[..., 0:img_c] = img
        rgba0[..., 3] = upsample0

        path0 = os.path.join(viz_folder, dest_name + '.png')
        cv2.imwrite(path0, rgba0 * 255.0)

        return path0

    def img_handler(self, save_path):
        img = cv2.imread(save_path)
        if img is None:
            return None

        small_img = self.downsample_image(img)
        cv2.imwrite(save_path, small_img * 255.0)

        preprocessed_img = self.trim_image(img)
        self.resnet_net.blobs['data'].data[0, ...] = preprocessed_img
        self.resnet_net.forward()
        feature = self.resnet_net.blobs[EXTRACT_LAYER].data[0].reshape(EXTRACT_LAYER_SIZE)
        return (feature / np.sqrt((feature ** 2).sum()))

    def get_answers(self, question, feature, source_img_path = None, img_ques_hash = None, viz_folder = None):
        qvec, cvec, avec, glove_matrix = self.vqa_data_provider.create_batch(question)
        self.vqa_net.blobs['data'].data[...] = np.transpose(qvec, (1, 0))
        self.vqa_net.blobs['cont'].data[...] = np.transpose(cvec, (1, 0))
        self.vqa_net.blobs['img_feature'].data[...] = feature
        self.vqa_net.blobs['label'].data[...] = avec  # dummy
        self.vqa_net.blobs['glove'].data[...] = np.transpose(glove_matrix, (1, 0, 2))
        self.vqa_net.forward()
        scores = self.vqa_net.blobs['prediction'].data.flatten()

        # attention visualization
        if (not source_img_path is None):
            att_map = self.vqa_net.blobs['att_map0'].data.copy()[0]
            path0 = self.save_attention_visualization(source_img_path, att_map, img_ques_hash, viz_folder)

        scores = self.softmax(scores)
        top_indices = scores.argsort()[::-1][:5]
        top_answers = [self.vqa_data_provider.vec_to_answer(i) for i in top_indices]
        top_scores = [float(scores[i]) for i in top_indices]

        return {'answer': top_answers[0]}
        # 'answers': top_answers,
        # 'scores': top_scores,
        # 'viz': [path0]}
