from __future__ import print_function
from flask import Flask, request, redirect, url_for, jsonify, send_from_directory
from time import time
import time
import numpy as np
import hashlib
import os
import sys
sys.path.append("./questionAnswering") # path to question answering module

from QuestionAnswering import QuestionAnswering
from im2txt.imgCaptioning import imgCap
from recognition.recognition import FaceRecognition
from emotion.tf_emotion_class import EmotionPredictor
from detection.Detectface import DetectFaceClass
from LSTM.lstm import SentencePredictor
from menu_recognition.menu_recog import ReadText


import cv2

VIZ_FOLDER = './viz/'
UPLOAD_FOLDER = './uploads/'
ALLOWED_EXTENSIONS = set(['jpg', 'jpeg', 'JPG', 'JPEG', 'png', 'PNG'])

# global variables
app = Flask(__name__, static_url_path='')
app.config['UPLOAD_FOLDER'] = UPLOAD_FOLDER

im2txt = None
questionAnswering = None
recognizer = None
faceDetector = None
emotionDetector = None
sentencePredictor = None
textReader = None
feature_cache = {}

def format_image(image, face):
    image = image[face[1]:face[3], face[0]:face[2]]
    return image
# helpers
def setup():
    global im2txt
    global questionAnswering
    global recognizer
    global faceDetector
    global emotionDetector
    global sentencePredictor
    global textReader

    # uploads
    if not os.path.exists(UPLOAD_FOLDER):
        os.makedirs(UPLOAD_FOLDER)

    if not os.path.exists(VIZ_FOLDER):
        os.makedirs(VIZ_FOLDER)

    emotionDetector = EmotionPredictor()
    faceDetector = DetectFaceClass(1, '/home/richard/Desktop/emotion-recognition-neural-networks-master/detection/mxnet-face-fr50', 0, 0.3, 0.001, 600, 1000)
    

    
    im2txt = imgCap()
    
    sentencePredictor = SentencePredictor()
    questionAnswering = QuestionAnswering()
    textReader = ReadText()
    recognizer = FaceRecognition(1.0, faceDetector)


def allowed_file(filename):
    return '.' in filename and filename.rsplit('.', 1)[1] in ALLOWED_EXTENSIONS


# routes
@app.route('/', methods=['GET'])
def index():
    return app.send_static_file('demo2.html')


@app.route('/api/upload_all', methods=['POST'])
def upload_all():
    tick = time.clock()
    file = request.files['image']
    if not file:
        return jsonify({'error': 'No file was uploaded.'})

    if not allowed_file(file.filename):
        return jsonify({'error': 'Please upload a JPG or PNG.'})
    if request.form['request'] == "":
        return jsonify({'answer': "Please, ask a question!"})
    file_hash = hashlib.md5(file.read()).hexdigest()
    save_path = os.path.join(app.config['UPLOAD_FOLDER'], file_hash + '.jpg')
    file.seek(0)
    file.save(save_path)

    print("file:")
    print(save_path)

    question = request.form['request']
    
    lst = question.split()
    lst[0] = lst[0][0].upper() + lst[0][1:]

    question = " ".join(lst)
    
    result = np.squeeze(sentencePredictor.predict(question))
    

    maxval = 0
    idx = 0
    for i in range(len(result)):
        print(result[i])
        if result[i] > maxval:
            maxval = result[i]
            idx = i


    # handle image first
    print("question:")
    print(question)
    print( str(idx) + "th NETWORK")
    
    name = question[:question.find(" ")]
    print(name)
    if (name == 'name' or name == 'Name'):
        print("adding person")
        name = question[question.find(" ") + 1:]
        Image = cv2.imread(save_path)
        answer = recognizer.add(Image, name, file_hash + '.jpg')
        if answer != "success":
            print("could not save person " + name + ", because: " + answer)
        return jsonify({'answer': answer})

    # img captioning
    if idx == 0:

        answer = im2txt.feed_image(save_path)
        tock = time.clock()
        print(str(tock - tick) + "time used for iamge captioning")

        return jsonify({'answer': answer})

    
    # face
    if (idx == 3):
        try:
            print("path = " + save_path)
            answer = ""
            Image = cv2.imread(save_path)
            people = recognizer.recognize(Image)
            

            if len(people) == 0:
                answer = "I don't see anyone here!"
            elif (len(people) == 1):
                if people[0] == "I don't know :(":
                    answer = "I don't know :("
                else:
                    answer = "It is " + people[0]
            else:
                known_people = ""
                unknow_people = 0
                for person in people:
                    if person == "I don't know :(":
                        unknow_people += 1
                    else:
                        if known_people == "":
                            known_people = person
                        else:
                            known_people = known_people + ", " + person

                if known_people == "":
                    answer = "There are " + str(unknow_people) + " people. I don't know anyone here."
                else:
                    answer = "There are " + known_people
                    if unknow_people > 0:
                        answer = answer + ". There are " + str(unknow_people) + " people, which I don't know."
        except:
            tock = time.clock()
            print(str(tock - tick) + "time used for face")
            return jsonify({'answer': "I don't see anyone here!"})
        tock = time.clock()
        print(str(tock - tick) + "time used for face")
        return jsonify({'answer': answer})

    # emotion
    if (idx == 2):
        img = cv2.imread(save_path)
        faces = faceDetector.detect_Face(img)
        if len(faces) == 0:
            answer = "There are no people here!"
            return jsonify({'answer': answer})
        else:
            answer = []
        for face in faces:
            answer.append(emotionDetector.predict(img, face))
        tock = time.clock()
        print(str(tock - tick) + "time used for emotion")
        print(answer)
        return jsonify({'answer': ",".join(answer)})




    # question answering
    if idx == 1:

        feature = questionAnswering.img_handler(save_path)
        if feature is None:
            tock = time.clock()
            print(str(tock - tick) + "time used for qa")
            return jsonify({'error': 'Error reading image.'})

        # image + question
        img_ques_hash = hashlib.md5(file_hash + question).hexdigest()
        json = questionAnswering.get_answers(question, feature, save_path, img_ques_hash, VIZ_FOLDER)

        tock = time.clock()
        print(str(tock - tick) + "time used for qa")
        return jsonify(json)

    # text
    if idx == 4:
        json = textReader.read(save_path)
        
        print(type(json))
        if json == '':
            tock = time.clock()
            print(str(tock - tick) + "time used for text")
            return jsonify({'answer': "I don't see the text here!"})
        tock = time.clock()
        print(str(tock - tick) + "time used for text")
        return jsonify({'answer':json})

    else:
        tock = time.clock()
        print(str(tock - tick) + "time used for text")
        return jsonify({'answer':"Error text"})
if __name__ == '__main__':
    setup()
    app.run(host='0.0.0.0', port=5000, debug=False)
