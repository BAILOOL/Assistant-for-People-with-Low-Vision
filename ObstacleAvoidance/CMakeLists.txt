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


find_package( PythonLibs 2.7 REQUIRED )
include_directories( ${PYTHON_INCLUDE_DIRS} )
 
find_package( Boost COMPONENTS python REQUIRED )
include_directories( ${Boost_INCLUDE_DIR} )

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
add_library(Depth_extraction SHARED src/main.cpp
       src/stereocam.cpp
        src/MiscTool.cpp
        src/elasFiles/elas.cpp
        src/elasFiles/descriptor.cpp
        src/elasFiles/filter.cpp
        src/elasFiles/matrix.cpp
        src/elasFiles/triangle.cpp
        src/elasFiles/image.h
        src/elasFiles/timer.h)

target_include_directories(${PROJECT_NAME} PUBLIC
        ${Boost_INCLUDE_DIRS}
        ${OpenCV_INCLUDE_DIRS}
        ${PYTHON_INCLUDE_DIRS}
        )

target_link_libraries(${PROJECT_NAME}
        ${Boost_LIBRARIES}
        ${PYTHON_LIBRARIES}
        -fopenmp -L/usr/local/lib -L/usr/lib64 -lstdc++
        ${OpenCV_LIBRARIES}
)

SET_TARGET_PROPERTIES(${PROJECT_NAME} PROPERTIES PREFIX "")


