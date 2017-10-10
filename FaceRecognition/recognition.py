
from detection.Detectface import DetectFaceClass
from frontalization.frontalization import *
import cv2

import openface
import numpy as np
import pandas as pd

from operator import itemgetter
from sklearn.pipeline import Pipeline
from sklearn.lda import LDA
from sklearn.preprocessing import LabelEncoder
from sklearn.svm import SVC
from sklearn.grid_search import GridSearchCV
from sklearn.mixture import GMM
from sklearn.tree import DecisionTreeClassifier
from sklearn.naive_bayes import GaussianNB

import pdb

#example image
PATH = 'img.jpg'

class FaceRecognition(object):
	def __init__(self, threshold, detection_net):
		
		self.threshold = threshold
		fileDir = os.path.dirname(os.path.realpath(__file__))
		recognitionModelDir = os.path.join(fileDir, 'recognition', 'models', 'openface', 'nn4.small2.v1.t7')
		
		self.detectionNN = detection_net
		
		self.frontalization = Frontalization()
		self.recognitionNN = openface.TorchNeuralNet(recognitionModelDir, imgDim=96, cuda=True)

		self.labelsPath = os.path.join(fileDir, 'data','labels.csv')
		self.labels = pd.read_csv(self.labelsPath)
		self.embeddingsPath = os.path.join(fileDir, 'data','reps.csv')
		self.embeddings = np.genfromtxt(self.embeddingsPath, delimiter=",",usecols=np.arange(0,128))
		if(self.embeddings.size == 128):
			self.embeddings.shape = (1, 128)
		

	def recognize(self, img):
		# detect faces on the image
		boxes = self.detectionNN.detect_Face(img)
		faces = []

		# crop detected faces
		for box in boxes:
			faces.append(img[box[1]:box[3], box[0]:box[2]])

		labels = []
		
		for face in faces:
			# large images cause strange bug,
			# therefore, images should be resized (96 by 96).
			# we tend to save proportions of the image,
			# in order to avoid distortions in embeddings		
			smallFace = face
			resize_factor = 1
			if (smallFace.shape[0] > smallFace.shape[1]):
				resize_factor = 96. / smallFace.shape[1]
			else:
				resize_factor = 96. / smallFace.shape[0]
			smallFace = cv2.resize(smallFace, (int(smallFace.shape[1] * resize_factor), int(smallFace.shape[0] * resize_factor)))

			Frontalized = self.frontalization.frontalize(smallFace)

			cropedAlignedfaces = self.detectionNN.detect_Face(Frontalized)
			if len(cropedAlignedfaces) == 0:
				print(len(cropedAlignedfaces))
				continue
			box = cropedAlignedfaces[0]
			cropedAligned = Frontalized[box[1]:box[3], box[0]:box[2]]

			face96 = cv2.resize(cropedAligned, (96, 96))

			face96RGB = cv2.cvtColor(face96.astype(np.uint8), cv2.COLOR_BGR2RGB)

			rep = self.recognitionNN.forward(face96RGB)
			labels.append(self.match(rep))

		return labels

	def match(self, frep):
		# iterate over existing embeddings in database
		# and match the best one using L2 norm
		best = 10
		best_i = 0;
		for i, rep in enumerate(self.embeddings):
			val = np.linalg.norm(rep - frep)
			if val < best:
				best = val
				best_i = i

		if best > self.threshold:
			return "I don't know :("

		return self.labels['name'][best_i]


	def add(self, img, name, img_name):
		# adding people to database.
		# logic is similar to recognition
		faces = self.detectionNN.detect_Face(img)
		if (len(faces) > 1):
			return "too many people! must be 1!"

		if (len(faces) < 1):
			return "I don't see any one here :("
		
		box = faces[0]
		face = img[box[1]:box[3], box[0]:box[2]]
		smallFace = face

		resize_factor = 1

		if (smallFace.shape[0] > smallFace.shape[1]):
			resize_factor = 96. / smallFace.shape[1]
		else:
			resize_factor = 96. / smallFace.shape[0]

		smallFace = cv2.resize(smallFace, (int(smallFace.shape[1] * resize_factor), int(smallFace.shape[0] * resize_factor)))

		Frontalized = self.frontalization.frontalize(smallFace)
		cropedAlignedBox = self.detectionNN.detect_Face(Frontalized)[0]

		cropedAligned = Frontalized[cropedAlignedBox[1]:cropedAlignedBox[3], cropedAlignedBox[0]:cropedAlignedBox[2]]

		face96 = cv2.resize(cropedAligned, (96, 96))

		face96RGB = cv2.cvtColor(face96, cv2.COLOR_BGR2RGB)

		rep = self.recognitionNN.forward(face96RGB)

		fileName = os.path.join(os.path.dirname(os.path.realpath(__file__)), 'data', 'pitures', img_name)
		cv2.imwrite(fileName, cropedAligned)

		tmp = pd.DataFrame([[fileName, name]], columns=['picture', 'name'])
		if self.labels.empty:
			self.labels = tmp
		else:
			self.labels = pd.concat([self.labels, tmp], ignore_index=True)

		rep.shape = (1, rep.size)
		if self.embeddings.size == 0:
			self.embeddings = rep
		else:
			self.embeddings = np.append(self.embeddings, rep, axis=0)

		# save values
		self.labels.to_csv(self.labelsPath, columns=["picture", 'name'], index=False)
		np.savetxt(self.embeddingsPath, self.embeddings, delimiter=",")

		return "success"

if __name__ == "__main__":
	faceRecognizer = FaceRecognition(1.1)
	Image = cv2.imread(PATH)
	labels = faceRecognizer.recognize(Image)
	print(labels)
