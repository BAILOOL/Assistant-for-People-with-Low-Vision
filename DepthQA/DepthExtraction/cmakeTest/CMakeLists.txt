set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++14")
set(CMAKE_BUILD_TYPE Release)
cmake_minimum_required(VERSION 2.8.3)
project(Depth_extraction)

find_package( OpenCV REQUIRED )

FIND_PACKAGE(OpenMP)
  IF(OPENMP_FOUND)
    SET(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} ${OpenMP_C_FLAGS}")
    SET(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} ${OpenMP_CXX_FLAGS}")
  ENDIF()



include_directories(
    /usr/include/opencv
    /usr/include/eigen3 
)

set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -msse3")
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -O3")
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -fopenmp")
set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} ${OpenMP_C_FLAGS}")
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} ${OpenMP_CXX_FLAGS}")


## Depthmap
add_executable(Depth_extraction src/main.cpp
       src/stereocam.cpp
        src/MiscTool.cpp
        src/elasFiles/elas.cpp
        src/elasFiles/descriptor.cpp
        src/elasFiles/filter.cpp
        src/elasFiles/matrix.cpp
        src/elasFiles/triangle.cpp
        src/elasFiles/image.h
        src/elasFiles/timer.h)

 target_link_libraries(Depth_extraction
-fopenmp -L/usr/local/lib -L/usr/lib64 -lstdc++
${OpenCV_LIBS}
 )


