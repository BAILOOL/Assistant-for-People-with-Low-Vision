#!/usr/bin/python
from build import Depth_extraction

paramPath = './StereoParams.yml'
leftPath = 'left.png'
rightPath = 'right.png'
dispPath = 'disp.jpg'

print(Depth_extraction.mainRoutine(leftPath, rightPath, paramPath, dispPath))
