import pandas as pd
import numpy as np
import sys
import random
import pdb
import StringIO
import csv
import fasttext

model = fasttext.load_model(PATH_TO_FASTTEXT_MODEL)

def word2vec(str):
	return model[str]

sentences = []
labels = []

with open('./LSTM_questions.csv', 'rb') as f:
	reader = csv.reader(f, delimiter=',')
	i = 1
	for row in reader:
		print i
		if row[0] == 'im2txt':
			continue
		if row[0] == '':
			break
		vec = word2vec(row[1])
		vec2 = word2vec(row[2])
		im_sentence = row[0].split(' ')
		qu_sentence = row[1].split(' ')
		em_sentence = row[2].split(' ')
		face_sentence = row[3].split(' ')
		text_sentence = row[4].split(' ')
		im_vect = []
		qu_vect = []
		em_vect = []
		face_vect = []
		text_vect = []
		for word in im_sentence:
			im_vect.append(word2vec(word))
		for word in qu_sentence:
			qu_vect.append(word2vec(word))
		for word in em_sentence:
			em_vect.append(word2vec(word))
		for word in face_sentence:
			face_vect.append(word2vec(word))
		for word in text_sentence:
			text_vect.append(word2vec(word))
		sentences.append(im_vect)
		labels.append(0)
		sentences.append(qu_vect)
		labels.append(1)
		sentences.append(em_vect)
		labels.append(2)
		if row[3] != '':
			sentences.append(face_vect)
			labels.append(3)
		if row[4] != '':
			sentences.append(text_vect)
			labels.append(4)
		i += 1

np.save(SAVE_PATH, sentences)
np.save(SAVE_PATH, labels)
print "Total: " + str(len(sentences))
print "Test: " + str(len(labels))
