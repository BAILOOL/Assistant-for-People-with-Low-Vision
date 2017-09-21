from __future__ import print_function
from flask import Flask, request, redirect, url_for, jsonify, send_from_directory
from time import time
import time
import hashlib
import os
import sys
import imp
import pdb
import cv2

from depthQuestionAnswering import DepthQuestionAnwering

UPLOAD_FOLDER = 'path to /uploads/Original/'
UPLOAD_FOLDER_LEFT = 'path to /uploads/Left/'
UPLOAD_FOLDER_RIGHT = 'path to /uploads/Right/'

ALLOWED_EXTENSIONS = set(['jpg', 'jpeg', 'JPG', 'JPEG', 'png', 'PNG'])

# global variables
app = Flask(__name__, static_url_path='')
app.config['UPLOAD_FOLDER'] = UPLOAD_FOLDER

# helpers
def setup():
    global depthQuestionAnswering

    # uploads
    if not os.path.exists(UPLOAD_FOLDER):
        os.makedirs(UPLOAD_FOLDER)
    
    depthQuestionAnswering = DepthQuestionAnwering()

def allowed_file(filename):
    return '.' in filename and filename.rsplit('.', 1)[1] in ALLOWED_EXTENSIONS


# routes
@app.route('/', methods=['GET'])
def index():
    return app.send_static_file('demo2.html')


@app.route('/api/upload_all', methods=['POST'])
def upload_all():
    file = request.files['image']
    if not file:
        return jsonify({'error': 'No file was uploaded.'})

    if not allowed_file(file.filename):
        return jsonify({'error': 'Please upload a JPG or PNG.'})

    file_hash = hashlib.md5(file.read()).hexdigest()
    #save_path = os.path.join(app.config['UPLOAD_FOLDER'], file_hash + '.png')
    save_path_original = UPLOAD_FOLDER + file_hash + '.png'
    file.seek(0)
    file.save(save_path_original)

    save_path_left = UPLOAD_FOLDER_LEFT + file_hash + '.png'
    save_path_right =UPLOAD_FOLDER_RIGHT + file_hash + '.png'
    orgImage = cv2.imread(save_path_original)
    crop_img_L = orgImage[0:orgImage.shape[0], 0:orgImage.shape[1]/2]
    cv2.imwrite(save_path_left,crop_img_L)
    crop_img_R = orgImage[0:orgImage.shape[0], orgImage.shape[1]/2:orgImage.shape[1]]
    cv2.imwrite(save_path_right,crop_img_R)

    question = request.form['request']
    #question = "How far is the car on the right"

    (depth,height,width,isFound) = depthQuestionAnswering.getFullResponse(save_path_left,save_path_right,question)

    #round distance measures
    depth = float("{0:.1f}".format(depth))
    height = float("{0:.1f}".format(height))
    width = float("{0:.1f}".format(width))

    #json = jsonify({'answer': 'Alex reply from server'})
    json = jsonify({'distance': str(depth), 'height': str(height), 'width' : str(width), 'isFound' : isFound})

    return json


if __name__ == '__main__':
    setup()
    app.run(host='0.0.0.0', port=5001, debug=False)
