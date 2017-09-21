#-------------------------------------------------
#
# Project created by QtCreator 2016-01-13T20:04:30
#
#-------------------------------------------------

QT       += core

QT       -= gui

TARGET = untitled
CONFIG   += console
CONFIG   -= app_bundle

QMAKE_CXXFLAGS += -fopenmp
QMAKE_LFLAGS +=  -fopenmp
QMAKE_CXXFLAGS += -msse3

TEMPLATE = app
INCLUDEPATH += /usr/include/opencv
INCLUDEPATH += /usr/include/eigen3
LIBS += `pkg-config opencv –cflags –libs`
LIBS += -lopencv_core
LIBS += -lopencv_imgproc
LIBS += -lopencv_highgui
LIBS += -lopencv_ml
LIBS += -lopencv_video
LIBS += -lopencv_features2d
LIBS += -lopencv_calib3d
LIBS += -lopencv_objdetect
LIBS += -lopencv_contrib
LIBS += -lopencv_legacy
LIBS += -lopencv_flann
LIBS += -lopencv_gpu
LIBS += -lopencv_nonfree

SOURCES += main.cpp \
    MiscTool.cpp \
    stereocam.cpp \
    elasFiles/matrix.cpp \
    elasFiles/descriptor.cpp \
    elasFiles/filter.cpp \
    elasFiles/triangle.cpp \
    elasFiles/elas.cpp

HEADERS += \
    MiscTool.h \
    stereocam.h \
    elasFiles/matrix.h \
    elasFiles/filter.h \
    elasFiles/image.h \
    elasFiles/timer.h \
    elasFiles/descriptor.h \
    elasFiles/triangle.h \
    elasFiles/elas.h
