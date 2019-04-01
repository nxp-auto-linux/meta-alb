# If the GPU is added, we want to enable OpenCV to make use of it.
PACKAGECONFIG_append_s32v2xx = " ${@bb.utils.contains('DISTRO_FEATURES', 'gpu', 'opencl', '', d)}"

