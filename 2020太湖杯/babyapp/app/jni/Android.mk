LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)
# 导出的so库名字
LOCAL_MODULE := Encrypto
# 对应的c代码
LOCAL_SRC_FILES := jni/com_example_babyapp_Encrypto.c
include $(BUILD_SHARED_LIBRARY)