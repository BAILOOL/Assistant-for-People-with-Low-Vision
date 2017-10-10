# Obstacle Avoidance module

Description of important files in this directory:
```
src             		# source files with main logic
src/main.cpp			# main function to be called
StereoParams.yml                # dcalibration parameters of ZED camera
server_depth.py		   	# server for processing input from ZED camera
				# outputs closest object and side on which object is located
test.py				# example of the usage
```

# BUILD

Main logic is implemented in C++ to speed up execution. Communication between Python and C++ is implemented using Boost. Python module can be built using CMake

```
mkdir build
cd build
cmake ..
make
cp Depth_extraction.so ../
cd ..
```

Example of usage can be found in test.py
