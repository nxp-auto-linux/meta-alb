require recipes-kernel/linux/linux-s32.inc

SRC_URI = "git://source.codeaurora.org/external/autobsps32/linux;protocol=https;branch=alb-4.14"
SRCREV = "e7f9da79d35b3ca7ff087f881346355001ab081f"

DELTA_KERNEL_DEFCONFIG_append_s32v234pcie += " \
    blueboxconfig_s32v234pcie_4.14 \
"
DELTA_KERNEL_DEFCONFIG_append_s32v234pciebcm += " \
    blueboxconfig_s32v234pcie_4.14 \
"
DELTA_KERNEL_DEFCONFIG_append_s32v234bbmini += " \
    blueboxconfig_s32v234pcie_4.14 \
"

# LXC configuration
DELTA_KERNEL_DEFCONFIG_append = " ${@bb.utils.contains('DISTRO_FEATURES', 'lxc', 'containers_4.1.26.config', '', d)}"

# Docker configuration
DELTA_KERNEL_DEFCONFIG_append += "${@bb.utils.contains('DISTRO_FEATURES', 'docker', 'docker.config', '', d)}"

SRC_URI += "\
    file://build/blueboxconfig_s32v234pcie_4.14 \
    file://build/containers_4.1.26.config \
    file://build/docker.config \
"
