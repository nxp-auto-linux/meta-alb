# We need to solve a conflict between building the image with
# the Vivante GPU driver or without. Without the driver, we
# want mesa to provide functionality that we can then use for
# framebuffer based setup. With the GPU, we can't really have
# both, so we let the GPU provide it for now.

PACKAGECONFIG_remove_s32v2xx = "${@bb.utils.contains("DISTRO_FEATURES", "gpu", "egl gles", "", d)}"
PROVIDES_remove_s32v2xx = "${@bb.utils.contains("DISTRO_FEATURES", "gpu", "virtual/libgles1 virtual/libgles2 virtual/egl", "", d)}"
