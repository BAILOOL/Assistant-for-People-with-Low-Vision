#!/usr/bin/python
from build import Depth_extraction

paramPath = '/home/oleg/server_assembly/depth/StereoParams.yml'
leftPath = '/home/oleg/server_assembly/depth/left.png'
rightPath = '/home/oleg/server_assembly/depth/right.png'
dispPath = '/home/oleg/server_assembly/depth/disparity/disp.jpg'

print(Depth_extraction.mainRoutine(leftPath, rightPath, paramPath, dispPath))
