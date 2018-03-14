#
# If we are asking for X11, we can't have wayland.
#
PACKAGECONFIG_remove_s32v = "${@bb.utils.contains("DISTRO_FEATURES", "x11", "wayland", "", d)}"

# We need the -DLINUX for the case of an unpatched Vivante driver which otherwise
# complains about the platform.
# If we have wayland, we should have EGL. This is currently not functional if GPU is enabled
# because of a limited GPU driver archive
CFLAGS_append_s32v = " -DLINUX \
                      ${@bb.utils.contains('DISTRO_FEATURES', 'x11', '', \
                                        bb.utils.contains('DISTRO_FEATURES', 'wayland', \
                                                      '-DEGL_API_FB -DEGL_API_WL',  '', d), d)}"
