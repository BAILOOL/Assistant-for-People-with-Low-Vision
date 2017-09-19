import numpy as np
from keras.models import Sequential, Model
from keras.layers import Input
from keras.layers import Dense
from keras.layers import LSTM
from keras.layers import Dropout, Input
from keras.callbacks import ModelCheckpoint
from keras.preprocessing import sequence
import keras.backend.tensorflow_backend as KTF
import tensorflow as tf
import fasttext
import time
import pdb

FASTTEXT_MODEL = ""
DATASET_PATH = ""
CHECKPOINT_PATH = ""

class SentencePredictor:

	def __init__(self, B = None, T = 20, D = 300):
		with KTF.tf.device('/gpu:0'):
			gpu_options = tf.GPUOptions(per_process_gpu_memory_fraction = 0.5, allow_growth = False, visible_device_list = '0')
			session_config = tf.ConfigProto(allow_soft_placement = True, log_device_placement = False, gpu_options = gpu_options)
			KTF.set_session(tf.Session(config = session_config))
			self.model = Sequential()
			self.model.add(LSTM(32, batch_input_shape = (B, T, D), return_sequences = True))
			self.model.add(Dropout(0.3))
			self.model.add(LSTM(16))
			self.model.add(Dropout(0.3))
			self.model.add(Dense(5, activation='softmax'))
			print(self.model.summary())
			self.vector = fasttext.load_model(FASTTEXT_MODEL)	
		self.model.compile(loss='categorical_crossentropy', optimizer='adam', metrics=['accuracy'])
		self.load_model()

	def load_data(self):
		X_train = np.load(DATASET_PATH)
		Y_train = np.load(DATASET_PATH)
		Y_test = np.load(DATASET_PATH)
		X_test = np.load(DATASET_PATH)
		return X_train, Y_train, X_test, Y_test

	def load_model(self, filepath = CHECKPOINT_PATH):
		print "[+] Model loaded"
		self.model.load_weights(filepath)

	def start_training(self):
		X_train, Y_train, X_test, Y_test = self.load_data()
		checkpoints = "./model/checkpoint.hdf5"
		checkpointer = ModelCheckpoint(filepath=checkpoints, verbose = 0, save_best_only = False)
		clbks = [checkpointer]

		self.model.fit(X_train, Y_train, validation_data = (X_test, Y_test), nb_epoch=60, batch_size=284, callbacks = clbks, shuffle = True)

	def word2vec(self, str):
		return self.vector[str]

	def predict(self, input_str):
		string = input_str.split(' ')
		string_array = [[[0 for i in range(300)] for y in range(20)] for z in range(1)]
		for i in range(len(string)):
			string[i] = self.word2vec(string[i])
		for y in range(len(string)):
			string_array[0][y] = string[y]
		input_arr = np.array(string_array)
		prediction = self.model.predict(input_arr)
		print prediction
		return prediction

