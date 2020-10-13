
# build kernel with gcc 7.5 compatibility, required for Ubuntu 18.04
DELTA_KERNEL_DEFCONFIG_append_ubuntu += "${@oe.utils.conditional("UBUNTU_TARGET_BASEVERSION", "18.04", 'gcc75compat.config', '', d)}"
SRC_URI += "\
    file://gcc75compat.config \
"
