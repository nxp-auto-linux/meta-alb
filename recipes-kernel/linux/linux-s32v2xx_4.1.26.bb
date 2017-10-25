require recipes-kernel/linux/linux-s32v2xx.inc

SRC_URI = "git://git.freescale.com/auto/linux.git;protocol=git;branch=master"

# If a different repo or revision is needed, use the SRC_URI and SRCREV override mechanism.
# See layer README.FIRST for details.
#SRC_URI = "git://sw-stash.freescale.net/scm/alb/linux.git;protocol=http;branch=develop"
#SRCREV = "${AUTOREV}"

# bsp15.0
SRCREV = "bf0a2d2a9c97f738d274f4b33094ed976c8a5448"

# LXC configuration
DELTA_KERNEL_DEFCONFIG_append = "${@base_contains('DISTRO_FEATURES', 'lxc', '${THISDIR}/files/containers_4.1.26.config', '', d)}"
